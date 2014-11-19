package org.jboss.as.test.integration.messaging.jms.topic;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;

public class ResourceProducer {

	@Produces
	@Resource(mappedName ="java:/JmsXA")
	private ConnectionFactory connectionFactory;

	@Produces
	@Resource(mappedName = "topic/EventTopic")
	private Topic topic;
	
	
}