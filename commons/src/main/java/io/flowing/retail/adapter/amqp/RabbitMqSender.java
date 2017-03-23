package io.flowing.retail.adapter.amqp;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.flowing.retail.adapter.ChannelSender;


public class RabbitMqSender extends ChannelSender {

  private Connection connection;
  private Channel channel;

  public void doSend(String eventString) {
    System.out.println("Sending event via RabbitMQ: " + eventString);
    try {
      channel.basicPublish(RabbitMqConsumer.EXCHANGE_NAME, "", null, eventString.getBytes("UTF-8"));
    } catch (Exception ex) {
      throw new RuntimeException("Could not send message with RabbitMQ: " + ex.getMessage(), ex);
    }
  }  

  public void connect() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    connection = factory.newConnection();
    channel = connection.createChannel();

    channel.exchangeDeclare(RabbitMqConsumer.EXCHANGE_NAME, "fanout", true); // publish/subscribe model

    System.out.println("Connected to RabbitMQ");
  }

  public void disconnect() throws IOException, TimeoutException {
    channel.close();
    connection.close();
  }

}
