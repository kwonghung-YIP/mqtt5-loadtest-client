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
@Profile("case6")
public class BroadcastQinOdds {

	@Autowired
	private MqttAsyncClient client;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${qin-odds.f1:12}")
	private int f1;
	
	@Value("${qin-odds.f2:12}")
	private int f2;
	
	@Value("${qin-odds.topic-raw:public/push/odds/qin-raw}") 
	private String qinOddsRawTopic;

	@Value("${qin-odds.topic-zip:public/push/odds/qin-zip}") 
	private String qinOddsZipTopic;
	
	private long countraw = 1;
	private long countzip = 1;
	
	@Scheduled(fixedRateString = "${qin-odds.fixed-rate:1000}")
	public void broadcastQinOddsRaw() {
		
		//OddsInfo oddsInfo = readOddsInfoFromFile();
		FullOdds fullOdds = genf1xf2QinOdds(countraw++,f1,f2);
		
		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
		props.setMessageExpiryInterval(60*2l);
			
		MqttMessage msg = new MqttMessage();
		msg.setRetained(true);
		msg.setQos(1);
		msg.setProperties(props);
		
		try {
			msg.setPayload(objectMapper.writeValueAsBytes(fullOdds));
		} catch (JsonProcessingException e) {
			log.error("", e);
		}
		
		final String topic = qinOddsRawTopic;
		
		try {
			IMqttToken token = client.publish(topic, msg);
			token.waitForCompletion();
		} catch (MqttException e) {
			log.error("fail to publish message",e);
		}	
	}

	@Scheduled(fixedRateString = "${qin-odds.fixed-rate:1000}")
	public void broadcastQinOddsZip() {
		
		//OddsInfo oddsInfo = readOddsInfoFromFile();
		FullOdds fullOdds = genf1xf2QinOdds(countzip++,f1,f2);
		
		MqttProperties props = new MqttProperties();
		props.setContentType("application/gzip");
		props.setMessageExpiryInterval(60*2l);
			
		MqttMessage msg = new MqttMessage();
		msg.setRetained(true);
		msg.setQos(1);
		msg.setProperties(props);
		
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
			objectMapper.writeValue(gzipOut,fullOdds);
			msg.setPayload(byteOut.toByteArray());
		} catch (IOException e) {
			log.error("", e);
		}
		
		final String topic = qinOddsZipTopic;
		
		try {
			IMqttToken token = client.publish(topic, msg);
			token.waitForCompletion();
		} catch (MqttException e) {
			log.error("fail to publish message",e);
		}	
	}
	
	/*private OddsInfo readOddsInfoFromFile() {
		OddsInfo oddsInfo = null;
		try (InputStream in=jsonFile.getInputStream()) {
			oddsInfo = objectMapper.readValue(in, OddsInfo.class);
		} catch (IOException e) {
			log.error("", e);
		}
		return oddsInfo;
	}*/
	
	private FullOdds genf1xf2QinOdds(long count2,int f1,int f2) {
		
		//Random rand = new Random();
		//IntStream intStream = rand.ints(1,999);
		//intStream.iterator().ne
		
		//OddsInfo oddsInfo = new OddsInfo();
		FullOdds fullOdds = new FullOdds();
		
		CombinationOdds[] odds = new CombinationOdds[f1*f2];
		int n=0;
		int noOfHf = 0;
		int noOfODrp = 0;
		for (int i=0;i<f1;i++) {
			for (int j=0;j<f2;j++) {
				long randOdds = Math.round(Math.random()*999);
				odds[n] = new CombinationOdds();
				odds[n].setCmbStr(String.format("%02d,%02d", i+1, j+1));
				odds[n].setScrOrd(n+1);
				odds[n].setCmbSt("Defined");
				//odds[n].setWP(99999.9);
				odds[n].setOdds(String.valueOf(randOdds));
				if (noOfHf <= 0) {
					odds[n].setHf(true);
					noOfHf++;
				}
				if (noOfODrp <= 10) {
					odds[n].setODrp(30);
					noOfODrp++;
				}
				n++;
			}
		}
		fullOdds.setCmb(odds);
		fullOdds.setColSt("Final");
		fullOdds.setUpdAt(new Date());
		
		//oddsInfo.setFullodds(fullOdds);
		//oddsInfo.setCount(count2);
		//oddsInfo.setSentTime(LocalDateTime.now());
		
		return fullOdds;
	}
}
