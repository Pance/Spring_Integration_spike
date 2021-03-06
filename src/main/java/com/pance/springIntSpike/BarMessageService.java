package com.pance.springIntSpike;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class BarMessageService {

	@ServiceActivator(inputChannel="bar", outputChannel="quu")
	public String getMessage(String message) {
		System.out.println("Bar: " + message);
		return message;
	}
}