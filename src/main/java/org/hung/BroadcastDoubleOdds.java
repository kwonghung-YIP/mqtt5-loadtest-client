package org.hung;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
@Profile("case4")
public class BroadcastDoubleOdds {

	@Autowired
	private MqttAsyncClient client;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${dbl-odds.topic:public/odds/dbl}") 
	private String dblOddsTopic;
	
	@Value("file:/c:/projects/mqtt-client/dbl-odds-14x14.json")
	private Resource jsonFile;
	
	@Scheduled(fixedRateString = "${dbl-odds.fixed-rate:300000}")
	public void broadcastDoubleOdds() {
		
		//OddsInfo oddsInfo = readOddsInfoFromFile();
		OddsInfo oddsInfo = genf1xf2DblOdds(24,24);
		
		MqttProperties props = new MqttProperties();
		props.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
		props.setMessageExpiryInterval(60*60l);
			
		MqttMessage msg = new MqttMessage();
		msg.setRetained(true);
		msg.setQos(1);
		msg.setProperties(props);
		
		try {
			msg.setPayload(objectMapper.writeValueAsBytes(oddsInfo));
		} catch (JsonProcessingException e) {
			log.error("", e);
		}
		
		final String topic = dblOddsTopic;
		
		try {
			IMqttToken token = client.publish(topic, msg);
			token.waitForCompletion();
		} catch (MqttException e) {
			log.error("fail to publish message",e);
		}	
	
	}
	
	private OddsInfo readOddsInfoFromFile() {
		OddsInfo oddsInfo = null;
		try (InputStream in=jsonFile.getInputStream()) {
			oddsInfo = objectMapper.readValue(in, OddsInfo.class);
		} catch (IOException e) {
			log.error("", e);
		}
		return oddsInfo;
	}
	
	private OddsInfo genf1xf2DblOdds(int f1,int f2) {
		OddsInfo oddsInfo = new OddsInfo();
		FullOdds fullOdds = new FullOdds();
		
		
		CombinationOdds[] odds = new CombinationOdds[f1*f2];
		int n=0;
		for (int i=0;i<f1;i++) {
			for (int j=0;j<f2;j++) {
				odds[n] = new CombinationOdds();
				odds[n].setCmbStr(String.format("%02d/%02d", f1, f2));
				odds[n].setScrOrd(n+1);
				odds[n].setCmbSt("Defined");
				odds[n].setWP(99999.9);
				odds[n].setOdds("999");
				n++;
			}
		}
		fullOdds.setCmb(odds);
		fullOdds.setColSt("Final");
		fullOdds.setUpdAt(new Date());
		
		oddsInfo.setFullodds(fullOdds);
		
		return oddsInfo;
	}
}
