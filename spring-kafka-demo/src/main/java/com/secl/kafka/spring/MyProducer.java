package com.secl.kafka.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyProducer {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SenderConfig.class);
		Sender helloWorld = ctx.getBean(Sender.class);
		int i = 1;
		while(true){
			if(i > 1000){
				break;
			}
			helloWorld.sendMessage(new Student(String.valueOf(i), "Tanim-"+i));
			//Thread.sleep(1000*1);
			Thread.sleep(10*1);
			i++;
		}

	}
}
