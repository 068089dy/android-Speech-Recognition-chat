package com.example.dy.group_demo6.util;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;



/**
 * Created by dy on 17-3-5.
 */

public class mqtt_method {

    public static MqttClient connect(String userName,String passWord,String clientid,String host,String[] TOPIC)throws Exception{
        //MemoryPersistence设置clientid的保存形式，默认为以内存保存
        MqttClient client = new MqttClient(host, clientid, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {}

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    // subscribe后得到的消息会执行到这里面
                    /*
                    System.out.println("接收消息主题:"+s);
                    System.out.println("接收消息Qos:"+mqttMessage.getQos());
                    System.out.println("接收消息内容:"+new String(mqttMessage.getPayload()));
                    */
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
            });
            client.connect(options);
            //topic = client.getTopic(TOPIC);
            //订阅消息
            int[] Qos  = {1};
            client.subscribe(TOPIC, Qos);
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void publish(String message, MqttTopic topic,int Qos) throws MqttException {

        MqttMessage msg = new MqttMessage();
        msg.setQos(Qos);
        msg.setRetained(true);
        msg.setPayload(message.getBytes());


        MqttDeliveryToken token = topic.publish(msg);
        token.waitForCompletion();
        System.out.println("发布完成！"+token.isComplete());
    }

    public static void subscribe(MqttClient client,String[] topic,int[] Qos)throws Exception{
        client.subscribe(topic,Qos);
    }

    public static void unsubscribe(MqttClient client,String[] topic)throws Exception{
        client.unsubscribe(topic);
    }

}
