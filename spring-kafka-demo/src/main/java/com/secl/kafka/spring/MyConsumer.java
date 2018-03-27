package com.secl.kafka.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyConsumer {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(ReceiverConfig.class);
		System.out.println("Consumer is running...");
	}
}
