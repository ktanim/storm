package com.secl.kafka.spring;

public class ConsumerThreadHandler implements Runnable {

	private Student message;
	private int partition;
	private String topic;
	private String key;

	public ConsumerThreadHandler(Student message, String topic, String key, int partition) {
		this.message = message;
		this.partition = partition;
		this.topic = topic;
		this.key = key;
	}

	public void run() {
		System.out.println("Message : " + message.getName() + ", topic: "+topic+", partition: " + partition + ", key: "+key+", By ThreadID: " + Thread.currentThread().getId());
	}
	
}
