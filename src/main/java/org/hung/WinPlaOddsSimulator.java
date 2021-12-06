package org.hung;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.hung.pojo.odds.FullOdds;
import org.hung.pojo.odds.FullOdds.CombinationOdds;
import org.hung.pojo.odds.OddsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("case5")
public class WinPlaOddsSimulator {

	@Autowired
	private MqttAsyncClient client;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${odds.noOfHorse:14}")
	private int noOfHorse;
	
	@Value("${win-odds.topic:hk/d/prdt/wager/evt/01/upd/racing/20201006/st/01/win/odds/full}") 
	private String winOddsTopic;

	@Value("${win-odds.compress:true}")
	private boolean winOddsCompress;
	
	@Value("${pla-odds.topic:hk/d/prdt/wager/evt/01/upd/racing/20201006/st/01/pla/odds/full}") 
	private String plaOddsTopic;
	
	@Value("${pla-odds.compress:true}")
	private boolean plaOddsCompress;	
	
	@Scheduled(fixedRateString = "${win-odds.fixed-rate:30000}")
	public void broadcastWinOdds() {
		
		FullOdds fullOdds = genFullOdds(noOfHorse);
			
		MqttMessage msg = createFullOddsMsg(fullOdds, winOddsCompress);
		
		final String topic = winOddsTopic;
		
		try {
			IMqttToken token = client.publish(topic, msg);
			token.waitForCompletion();
		} catch (MqttException e) {
			log.error("fail to publish WIN odds",e);
		}	
	}

	@Scheduled(fixedRateString = "${pla-odds.fixed-rate:30000}")
	public void broadcastPlaOdds() {
		
		FullOdds fullOdds = genFullOdds(noOfHorse);
		
		MqttMessage msg = createFullOddsMsg(fullOdds, plaOddsCompress);
		
		final String topic = plaOddsTopic;
		
		try {
			IMqttToken token = client.publish(topic, msg);
			token.waitForCompletion();
		} catch (MqttException e) {
			log.error("fail to publish message",e);
		}	
	}

	private FullOdds genFullOdds(int noOfHorse) {
		
		FullOdds fullOdds = new FullOdds();
		
		CombinationOdds[] odds = new CombinationOdds[noOfHorse];
		//int n=0;
		int noOfHf = 0;
		for (int i=0;i<noOfHorse;i++) {
			long randOdds = Math.round(Math.random()*999);
			odds[i] = new CombinationOdds();
			odds[i].setCmbStr(String.format("%02d", i+1));
			odds[i].setScrOrd(i+1);
			odds[i].setCmbSt("Defined");
			//odds[n].setWP(99999.9);
			odds[i].setOdds(String.valueOf(randOdds));
			if (Math.random()>0.7 && noOfHf<=0) {
				odds[i].setHf(true);
				noOfHf++;
			}
			if (Math.random()>0.9) {
				odds[i].setODrp(30);
			}
		}
		fullOdds.setCmb(odds);
		fullOdds.setColSt("Interim");
		fullOdds.setUpdAt(new Date());
		
		return fullOdds;
	}
	
	private MqttMessage createFullOddsMsg(FullOdds fullOdds,boolean compress) {
		
		MqttProperties props = new MqttProperties();
		props.setContentType(compress?"application/gzip":MimeTypeUtils.APPLICATION_JSON_VALUE);
		props.setMessageExpiryInterval(60*2l);
		
		MqttMessage msg = new MqttMessage();
		msg.setRetained(true);
		msg.setQos(1);
		msg.setProperties(props);
		
		if (!compress) {
			try {
				msg.setPayload(objectMapper.writeValueAsBytes(fullOdds));
			} catch (JsonProcessingException e) {
				log.error("", e);
			}		
		} else {
			try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
				GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
				objectMapper.writeValue(gzipOut,fullOdds);
				msg.setPayload(byteOut.toByteArray());
			} catch (IOException e) {
				log.error("", e);
			}			
		}
		
		return msg;
	}
	
}
