/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.test.integration.jca.reload;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnectionFactory;

import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 * @author baranowb
 *
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/queue/callerPrincipal")
})
@ResourceAdapter(value = "hornetq-ra.rar") // based on documentation to as 7
public class MessageReceiverBean implements MessageDrivenBean, MessageListener{
    @Resource(mappedName = "java:/ConnectionFactory")
    private QueueConnectionFactory qFactory;

    private MessageDrivenContext msgContext;
    @Override
    public void onMessage(Message arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void ejbRemove() throws EJBException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMessageDrivenContext(MessageDrivenContext arg0) throws EJBException {
        msgContext = arg0;
    }

}
