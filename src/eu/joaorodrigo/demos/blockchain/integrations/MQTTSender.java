package eu.joaorodrigo.demos.blockchain.integrations;

import eu.joaorodrigo.demos.blockchain.BlockchainDemo;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.UUID;

public class MQTTSender {

    private static MqttClient client;

    static {
        try {
            client = new MqttClient("tcp://10.42.0.1:1883", "java-" + UUID.randomUUID().hashCode(), new MemoryPersistence());
            MqttConnectionOptions options = new MqttConnectionOptionsBuilder().username("chaintracer").password("chaintracer".getBytes()).build();

            System.out.println("MQTT-Connecting to broker: "+client.getServerURI());
            client.connect(options);

            client.setCallback(new MqttCallback() {
                @Override
                public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {

                }

                @Override
                public void mqttErrorOccurred(MqttException e) {

                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if(topic.equals("sys/fuzzy")) {
                        System.out.println("Content: " +  new String(message.getPayload()));
                        BlockchainDemo.sendNewValue(new String(message.getPayload()));
                    }
                }

                @Override
                public void deliveryComplete(IMqttToken iMqttToken) {

                }

                @Override
                public void connectComplete(boolean b, String s) {

                }

                @Override
                public void authPacketArrived(int i, MqttProperties mqttProperties) {

                }

                public void connectionLost(Throwable cause) {
                    System.out.println("connectionLost: " + cause.getMessage());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("deliveryComplete: " + token.isComplete());
                }
            });
            client.subscribe("sys/fuzzy", 0);
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
