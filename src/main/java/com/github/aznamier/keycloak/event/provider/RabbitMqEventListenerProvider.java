package com.github.aznamier.keycloak.event.provider;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMqEventListenerProvider implements EventListenerProvider {

	private RabbitMqConfig cfg;
	private ConnectionFactory factory;
	private static final Logger log = LoggerFactory.getLogger(RabbitMqEventListenerProvider.class);


	public RabbitMqEventListenerProvider(RabbitMqConfig cfg) {
		this.cfg = cfg;

		this.factory = new ConnectionFactory();
		this.factory.setUsername(cfg.getUsername());
		this.factory.setPassword(cfg.getPassword());
		this.factory.setVirtualHost(cfg.getVhost());
		this.factory.setHost(cfg.getHostUrl());
		this.factory.setPort(cfg.getPort());
	}

	@Override
	public void close() {

	}

	@Override
	public void onEvent(Event event) {
		if(event.getType() == EventType.LOGIN){
			EventClientNotificationMqMsg msg = EventClientNotificationMqMsg.create(event);
			String routingKey = MessagingConfig.routingKey;
			String messageString = RabbitMqConfig.writeAsJson(msg, true);
			this.publishNotification(messageString, routingKey);
		}
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
		EventAdminNotificationMqMsg msg = EventAdminNotificationMqMsg.create(event);
		String routingKey = MessagingConfig.routingKey;
		String messageString = RabbitMqConfig.writeAsJson(msg, true);
		this.publishNotification(messageString, routingKey);
	}


	private void publishNotification(String messageString, String routingKey) {
		try {
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			log.info("TestSendMessage "+ routingKey + messageString + cfg.getExchange());
			log.info(MessagingConfig.queueName + MessagingConfig.topicExchangeName + MessagingConfig.routingKey);
			channel.exchangeDeclare(cfg.getExchange(), "topic", true);
			channel.queueDeclare(MessagingConfig.queueName, true, false, false, null);

			channel.basicPublish(cfg.getExchange(), routingKey, false, false, null, messageString.getBytes());


			System.out.println("keycloak-to-rabbitmq SUCCESS sending message: " + routingKey);

			channel.queueBind(MessagingConfig.queueName, cfg.getExchange(), MessagingConfig.routingKey);
			channel.close();
			conn.close();

		} catch (Exception ex) {
			System.err.println("keycloak-to-rabbitmq ERROR sending message: " + routingKey);
			ex.printStackTrace();
		}
	}
}
