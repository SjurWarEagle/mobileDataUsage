package de.tkunkel.mobileDataUsage.mqtt;

import de.tkunkel.mobileDataUsage.types.Configuration;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MqttSender {
    @Autowired
    private Configuration configuration;
    private IMqttClient publisher;

    @PostConstruct
    public void postCreate() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setUserName(configuration.mqtt.mqttUser);
        options.setPassword(configuration.mqtt.mqttPassword.toCharArray());

        publisher = new MqttClient(configuration.mqtt.url, configuration.mqtt.publishId);
        publisher.connect(options);
    }

    public void sendUsageDate(int usedMemoryInMB, int contractMemoryInMB) throws MqttException {
        String data = ("{\"usedMemoryInMB\":" + usedMemoryInMB + ",\"contractMemoryInMB\":" + contractMemoryInMB + "}");
        MqttMessage msg = new MqttMessage(data.getBytes());
        msg.setQos(0);
        msg.setRetained(true);
        publisher.publish(configuration.mqtt.topic, msg);
    }

    public static void main(String[] args) throws MqttException {
        MqttSender mqttSender = new MqttSender();
        mqttSender.postCreate();
        mqttSender.sendUsageDate(10, 20);
        mqttSender.finish();
    }

    private void finish() throws MqttException {
        publisher.disconnect();
    }
}
