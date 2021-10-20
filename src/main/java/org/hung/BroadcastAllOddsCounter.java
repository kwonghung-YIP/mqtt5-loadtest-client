package org.hung;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("case3")
public class BroadcastAllOddsCounter {

	@Autowired
	private MqttAsyncClient client;
	
	//@Autowired
	//private ObjectMapper objectMapper;
	
	@Value("${odds.win.topic}") 
	private String winOddsTopic;

	@Value("${odds.pla.topic}") 
	private String plaOddsTopic;
	
	private int noOfHorse;
	private int counter = 1;
	private Random random = new Random();
	
	public BroadcastAllOddsCounter(@Value("${odds.no-of-horse:11}") int noOfHorse) {
		this.noOfHorse = noOfHorse;	
	}
	
	@Scheduled(initialDelayString="${odds-all.win.init-delay}",fixedRateString="${odds-all.win.fixed-rate}")
	public void broadcastAllWinOdds() {

		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.TEXT_PLAIN_VALUE);
		
		for (int horseIdx=0;horseIdx<noOfHorse;horseIdx++) {
			
			MqttMessage msg = new MqttMessage();
			msg.setRetained(true);
			msg.setQos(0);
			msg.setProperties(props);
			
			msg.setPayload(String.valueOf(counter).getBytes(StandardCharsets.UTF_8));
			
			final String topic = winOddsTopic + horseIdx;
			log.info("broadcast WIN odds to topic {} {}...",counter,topic);
			counter++;
			
			try {
				IMqttToken token = client.publish(topic, msg);
				token.waitForCompletion();
			} catch (MqttException e) {
				log.error("fail to publish message",e);
			}	
		}
	}
	
	@Scheduled(initialDelayString="${odds-all.pla.init-delay}",fixedRateString="${odds-all.pla.fixed-rate}")
	public void broadcastAllPlaOdds() {

		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.TEXT_PLAIN_VALUE);
		
		for (int horseIdx=0;horseIdx<noOfHorse;horseIdx++) {
			
			MqttMessage msg = new MqttMessage();
			msg.setRetained(true);
			msg.setQos(0);
			msg.setProperties(props);
			
			msg.setPayload(String.valueOf(counter).getBytes(StandardCharsets.UTF_8));
			
			final String topic = plaOddsTopic + horseIdx;
			log.info("broadcast PLA odds to topic {} {}...",counter,topic);
			counter++;
			
			try {
				IMqttToken token = client.publish(topic, msg);
				token.waitForCompletion();
			} catch (MqttException e) {
				log.error("fail to publish message",e);
			}	
		}
	}	
}

