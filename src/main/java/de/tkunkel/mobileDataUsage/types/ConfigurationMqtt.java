package de.tkunkel.mobileDataUsage.types;

public class ConfigurationMqtt {
    public String url;
    public String topic;
    public String publishId;
    public String mqttUser;
    public String mqttPassword;

    @Override
    public String toString() {
        return "ConfigurationMqtt{" +
                "url='" + url + '\'' +
                ", topic='" + topic + '\'' +
                ", publishId='" + publishId + '\'' +
                ", mqttUser='" + mqttUser + '\'' +
                ", mqttPassword='" + mqttPassword + '\'' +
                '}';
    }
}
