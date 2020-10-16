package com.demo.ActivitiDemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaSendService {
	
	@Autowired
	private KafkaTemplate kafkaTemplate;
	
	public boolean send(String message){
		ListenableFuture listenableFuture = kafkaTemplate.send("test",message);
		
		kafkaTemplate.send("dddd", "dddddd");
		listenableFuture.addCallback(o -> System.out.println("消息发送成功,{}"+o.toString()),
                throwable -> System.out.println("消息发送失败,{}" + throwable.getMessage()));
        return true;
    }

}
