package com.secl.kafka.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class Sender {

    //private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private KafkaTemplate<String, Student> kafkaTemplate;

    @Autowired
	private Environment env;
    private String topic = null;
    
    @PostConstruct
    private void init(){
    	topic = env.getProperty("kafka.topic");
    }

    public void sendMessage(Student message) {
    	//kafkaTemplate.send(topic, message);
    	//System.out.println("Message = "+message);
    	
    	// the KafkaTemplate provides asynchronous send methods returning a
        // Future
        ListenableFuture<SendResult<String, Student>> future = kafkaTemplate.send(topic, message.getName(), message);

        // you can register a callback with the listener to receive the result
        // of the send asynchronously
        future.addCallback(
                new ListenableFutureCallback<SendResult<String, Student>>() {

                    @Override
                    public void onSuccess(SendResult<String, Student> result) {
                    	System.out.println("sent message = "+message+" with offset = "+result.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                    	System.err.println("unable to send message= "+message+" "+ex.getMessage());
                    }
                });
         
        // alternatively, to block the sending thread, to await the result,
        // invoke the future's get() method
    }
}