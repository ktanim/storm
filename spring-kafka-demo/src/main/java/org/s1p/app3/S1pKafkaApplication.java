/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.s1p.app3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.s1p.test.CommonConfiguration;
import org.s1p.test.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * @author Gary Russell
 *
 */
@SpringBootApplication
@Import({ CommonConfiguration.class, ConfigProperties.class })
@EnableKafka
public class S1pKafkaApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(S1pKafkaApplication.class)
			.web(false)
			.run(args);
		TestBean testBean = context.getBean(TestBean.class);
		/*for (int i = 0; i < 10; i++) {
			testBean.send("foo-"+i);
		}*/
		int i = 1;
		while(true){
			if(i == 1000){
				break;
			}
			testBean.send("foo-"+i);
			//Thread.sleep(1000*1);
			i++;
		}
		context.getBean(Listener.class).latch.await(1, TimeUnit.SECONDS);

		Thread.sleep(1000);
		new CountDownLatch(1).await();
		context.close();
	}

	@Bean
	public TestBean test() {
		return new TestBean();
	}

	@Bean
	public Listener listener() {
		return new Listener();
	}

	public static class TestBean {

		@Autowired
		private ConfigProperties configProperties;

		@Autowired
		private KafkaTemplate<String, String> template;

		public void send(String foo) {
			this.template.send(this.configProperties.getTopic(), foo);
			this.template.flush();
		}

	}

	public static class Listener {

		private final CountDownLatch latch = new CountDownLatch(10);

		@KafkaListener(topics = "${kafka.topic}")
		public void listen(@Payload String foo, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
			System.out.println("Received: " + foo + " (partition: " + partition + ")");
			this.latch.countDown();
		}

	}

}
