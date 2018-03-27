package com.secl.kafka.spring;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public class Receiver {

	private ExecutorService executor;
	
	public Receiver(){
		executor = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	//@KafkaListener(topics = "${kafka.topic}", group = "${kafka.group}")
    @KafkaListener(id = "Consumer-Tanim", topics = "${kafka.topic}", group = "${kafka.group}", containerFactory = "kafkaListenerContainerFactory")
    //@KafkaListener(topicPartitions = { @TopicPartition(topic = "${kafka.topic}", partitionOffsets = { @PartitionOffset(partition = "0", initialOffset = "0") }) })
    //@KafkaListener(topicPartitions = { @TopicPartition(topic = "${kafka.topic}", partitions = { "0" }), @TopicPartition(topic = "${kafka.topic}", partitions = "0", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")) })
	public void receiveMessage(@Payload Student message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
	        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, 
	        Acknowledgment acknowledgment) throws Exception {
        //System.out.println("received message = "+message);
		executor.submit(new ConsumerThreadHandler(message, topic, key, partition));
        //Thread.sleep(1000*5);
		acknowledgment.acknowledge();
    }

}