package eu.joaorodrigo.demos.blockchain.integrations;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
public class MQTTSender {

    private static MqttClient client;

    static {
        try {
            client = new MqttClient("tcp://broker.emqx.io:1883", "JavaSample", new MemoryPersistence());
            System.out.println("MQTT-Connecting to broker: "+client.getServerURI());
            client.connect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public static void send(String b) {
        try {

            if(!client.isConnected()) client.connect();

            System.out.println("MQTT-Publishing message: "+ b);
            MqttMessage message = new MqttMessage(b.getBytes());
            message.setQos(0);
            client.publish("sys/ct2", message);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
