package com.secl.kafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.s1p.test.ConfigProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;

@SpringBootApplication
@Import({ ConsumerConfiguration.class, ConfigProperties.class })
@EnableKafka
public class MyConsumer {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(MyConsumer.class)
			.web(false)
			.run(args);
		context.getBean(Listener.class).latch.await(60, TimeUnit.SECONDS);
		context.close();
	}

	@Bean
	public Listener listener() {
		return new Listener();
	}

	@Bean
	public KafkaMessageListenerContainer<String, String> container(
			ConsumerFactory<String, String> consumerFactory,
			ConfigProperties config) {
		ContainerProperties containerProperties = new ContainerProperties(config.getTopic());
		containerProperties.setMessageListener(listener());
		containerProperties.setAckMode(AckMode.MANUAL_IMMEDIATE);
		return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
	}

	public class Listener implements AcknowledgingMessageListener<String, String> {

		private final CountDownLatch latch = new CountDownLatch(1);

		@Override
		public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
			System.out.println("Received: " + record.value());
			acknowledgment.acknowledge();
			this.latch.countDown();
		}

	}

}
