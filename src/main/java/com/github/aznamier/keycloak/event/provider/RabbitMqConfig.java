package com.github.aznamier.keycloak.event.provider;


import org.keycloak.Config.Scope;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RabbitMqConfig {

	public static final ObjectMapper rabbitMqObjectMapper = new ObjectMapper();

	private String hostUrl;
	private Integer port;
	private String username;
	private String password;
	private String vhost;

	private String exchange;

	public static String writeAsJson(Object object, boolean isPretty) {
		String messageAsJson = "unparsable";
		try {
			if(isPretty) {
				messageAsJson = RabbitMqConfig.rabbitMqObjectMapper
						.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			} else {
				messageAsJson = RabbitMqConfig.rabbitMqObjectMapper.writeValueAsString(object);
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return messageAsJson;
	}


	public static RabbitMqConfig createFromScope(Scope config) {
		RabbitMqConfig cfg = new RabbitMqConfig();

		cfg.hostUrl = resolveConfigVar(config, "url", "20.93.191.202");
		cfg.port = Integer.valueOf(resolveConfigVar(config, "port", "5672"));
		cfg.username = resolveConfigVar(config, "username", "guest");
		cfg.password = resolveConfigVar(config, "password", "guest");
		cfg.vhost = resolveConfigVar(config, "vhost", "/");

		cfg.exchange = resolveConfigVar(config, "exchange", "leagueshare-exchanger");
		return cfg;

	}

	private static String resolveConfigVar(Scope config, String variableName, String defaultValue) {

		String value = defaultValue;
		if(config != null && config.get(variableName) != null) {
			value = config.get(variableName);
		} else {
			//try from env variables eg: KK_TO_RMQ_URL:
			String envVariableName = "KK_TO_RMQ_" + variableName.toUpperCase();
			if(System.getenv(envVariableName) != null) {
				value = System.getenv(envVariableName);
			}
		}
		System.out.println("keycloak-to-rabbitmq configuration: " + variableName + "=" + value);
		return value;

	}
	public String getHostUrl() {
		return hostUrl;
	}
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVhost() {
		return vhost;
	}
	public void setVhost(String vhost) {
		this.vhost = vhost;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
}
