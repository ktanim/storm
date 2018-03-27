package org.s1p.test;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public class ConfigProperties {

	private String brokerAddress;

	private String topic;

	private String fooTopic;

	public String getBrokerAddress() {
		return this.brokerAddress;
	}

	public void setBrokerAddress(String brokerAddress) {
		this.brokerAddress = brokerAddress;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getFooTopic() {
		return this.fooTopic;
	}

	public void setFooTopic(String fooTopic) {
		this.fooTopic = fooTopic;
	}

}
