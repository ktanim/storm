package com.secl.kafka.spring;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.support.RetryTemplate;

@EnableKafka
@Configuration
@PropertySource(value = { "classpath:application.properties" }, ignoreResourceNotFound = false)
public class ReceiverConfig {

    @Autowired
	private Environment env;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("kafka.bootstrap.servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.group"));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, Student> consumerFactory() {
    	JsonDeserializer<Student> jsonDeserializer = new JsonDeserializer<Student>(Student.class);
		return new DefaultKafkaConsumerFactory<String, Student>(consumerConfigs(), new StringDeserializer(), jsonDeserializer);
    }

	@Bean
    public ConcurrentKafkaListenerContainerFactory<String, Student> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Student> factory = new ConcurrentKafkaListenerContainerFactory<String, Student>();
        factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(3);
		factory.getContainerProperties().setPollTimeout(3000);
		factory.getContainerProperties().setIdleEventInterval(60000L);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
		/*factory.setRetryTemplate(new RetryTemplate());
		factory.setRecordFilterStrategy(new RecordFilterStrategy<String, Student>() {
			@Override
			public boolean filter(ConsumerRecord<String, Student> consumerRecord) {
				return consumerRecord.value().getName().contains("10");
			}
		});*/
        return factory;
    }
	
    @Bean
    public Receiver receiver() {
        return new Receiver();
    }
	
}

