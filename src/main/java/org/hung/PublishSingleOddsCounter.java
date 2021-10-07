package org.hung;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("case1")
public class PublishSingleOddsCounter {

	@Autowired
	private MqttAsyncClient client;
	
	//@Autowired
	//private ObjectMapper objectMapper;
	
	@Value("${odds.win.topic}") 
	private String winOddsTopic;

	@Value("${odds.pla.topic}") 
	private String plaOddsTopic;
	
	@Value("${odds.update-which-horse:4}")
	private int updateWhichHorse;
	
	private Random random = new Random();
	
	private int[] winOddsCounter;
	private int[] plaOddsCounter;
	
	public PublishSingleOddsCounter(@Value("${odds.no-of-horse:11}") int noOfHorse) {
		winOddsCounter = new int[noOfHorse];
		plaOddsCounter = new int[noOfHorse];		
	}
	
	@Scheduled(fixedRateString="${odds.win.fixed-rate}")
	public void broadcastWinOdds() {

		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.TEXT_PLAIN_VALUE);
		
		int horseIdx = updateWhichHorse;
		winOddsCounter[horseIdx]++;
		
		MqttMessage msg = new MqttMessage();
		msg.setRetained(true);
		msg.setQos(0);
		msg.setProperties(props);
		
		msg.setPayload(String.valueOf(winOddsCounter[horseIdx]).getBytes(StandardCharsets.UTF_8));
		
		String topic = winOddsTopic + horseIdx;
		log.info("broadcast WIN odds to topic {}...",topic);
		
		try {
			IMqttToken token = client.publish(topic, msg);
			token.waitForCompletion();
		} catch (MqttException e) {
			log.error("fail to publish message",e);
		}	

	}

	@Scheduled(fixedRateString="${odds.pla.fixed-rate}")
	public void broadcastPlaOdds() {
		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.TEXT_PLAIN_VALUE);
		
		int horseIdx = updateWhichHorse;
		plaOddsCounter[horseIdx]++;
		
		MqttMessage msg = new MqttMessage();
		msg.setRetained(true);
		msg.setQos(0);
		msg.setProperties(props);
		
		msg.setPayload(String.valueOf(plaOddsCounter[horseIdx]).getBytes(StandardCharsets.UTF_8));
		
		String topic = plaOddsTopic + horseIdx;
		log.info("broadcast PLA odds to topic {}...",topic);
		
		try {
			IMqttToken token = client.publish(topic, msg);
			token.waitForCompletion();
		} catch (MqttException e) {
			log.error("fail to publish message",e);
		}	
	}	
}
