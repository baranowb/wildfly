package org.jboss.as.test.integration.messaging.jms.topic;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

@Startup
@Singleton
@JMSDestinationDefinitions(
        value =  {
                @JMSDestinationDefinition(
                        name="java:module/topic/EventTopic1",
                        interfaceName="javax.jms.Topic",
                        destinationName="myTopic1"
                )
        }
)
public class JmsSubscriber {
 
	@Inject 
	private ConnectionFactory connectionFactory;

	@Inject 
	private Topic topic;
 
	private Connection connection;
	private Session session;
	
	@PostConstruct
	private void init() {
		start();
	}
	
	public void start(){
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("Injected topic :" +topic);
			MessageProducer messageProducer = session.createProducer(topic); // <----- exception thrown ----->//
			
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

	}
 
}
