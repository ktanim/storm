package com.secl.kafka;

import org.s1p.test.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
@SpringBootApplication
@Import({ ProducerConfiguration.class, ConfigProperties.class })
public class MyProducer {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(MyProducer.class)
			.web(false)
			.run(args);
		TestBean testBean = context.getBean(TestBean.class);
		int i = 1;
		while(true){
			if(i == 1000){
				break;
			}
			testBean.send("foo-"+i);
			Thread.sleep(1000*1);
			i++;
		}
		context.close();
	}

	@Bean
	public TestBean test() {
		return new TestBean();
	}

	public static class TestBean {

		@Autowired
		private ConfigProperties configProperties;

		@Autowired
		private KafkaTemplate<String, String> template;

		public void send(String foo) {
			System.out.println(foo);
			this.template.send(this.configProperties.getTopic(), foo);
			//this.template.flush();
		}

	}

}
