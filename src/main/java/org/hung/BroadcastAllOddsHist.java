package org.hung;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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
@Profile("case2")
public class BroadcastAllOddsHist {

	@Autowired
	private MqttAsyncClient client;
	
	//@Autowired
	//private ObjectMapper objectMapper;
	
	@Value("${odds.win.topic}") 
	private String winOddsTopic;

	@Value("${odds.pla.topic}") 
	private String plaOddsTopic;
	
	@Data
	@Component
	@ConfigurationProperties("odds-hist")
	public class OddsHist {
		private int winCnt;
		private Map<Integer,String>[] win;
		
		private int plaCnt;
		private Map<Integer,String>[] pla;
	}
	
	@Autowired
	private OddsHist oddsHist;
	
	
	@Scheduled(initialDelayString="${odds-all.win.init-delay}",fixedRateString="${odds-all.win.fixed-rate}")
	public void broadcastAllWinOdds() {
		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.TEXT_PLAIN_VALUE);
		
		Map<Integer,String> odds = oddsHist.win[oddsHist.winCnt];
		odds.forEach((k,v) -> {
			
			MqttMessage msg = new MqttMessage();
			msg.setRetained(true);
			msg.setQos(0);
			msg.setProperties(props);
			
			msg.setPayload(v.getBytes(StandardCharsets.UTF_8));
			
			final String topic = winOddsTopic + k;
			log.info("broadcast WIN odds to topic {} {}...",v,topic);
			
			try {
				IMqttToken token = client.publish(topic, msg);
				token.waitForCompletion();
			} catch (MqttException e) {
				log.error("fail to publish message",e);
			}	
		});
		
		oddsHist.winCnt++;
		if (oddsHist.winCnt >= oddsHist.win.length) {
			oddsHist.winCnt = 0;
		}
	}

	@Scheduled(initialDelayString="${odds-all.pla.init-delay}",fixedRateString="${odds-all.pla.fixed-rate}")
	public void broadcastAllPlaOdds() {
		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.TEXT_PLAIN_VALUE);
		
		Map<Integer,String> odds = oddsHist.pla[oddsHist.plaCnt];
		odds.forEach((k,v) -> {
			
			MqttMessage msg = new MqttMessage();
			msg.setRetained(true);
			msg.setQos(0);
			msg.setProperties(props);
			
			msg.setPayload(v.getBytes(StandardCharsets.UTF_8));
			
			final String topic = plaOddsTopic + k;
			log.info("broadcast PLA odds to topic {} {}...",v,topic);
			
			try {
				IMqttToken token = client.publish(topic, msg);
				token.waitForCompletion();
			} catch (MqttException e) {
				log.error("fail to publish message",e);
			}	
		});
		
		oddsHist.plaCnt++;
		if (oddsHist.plaCnt >= oddsHist.pla.length) {
			oddsHist.plaCnt = 0;
		}		
	}
	
}

