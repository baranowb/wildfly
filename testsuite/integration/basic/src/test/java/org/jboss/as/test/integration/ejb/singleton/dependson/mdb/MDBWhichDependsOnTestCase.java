/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.as.test.integration.ejb.singleton.dependson.mdb;

import java.io.File;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.integration.ejb.mdb.JMSMessagingUtil;
import org.jboss.as.test.integration.weld.modules.ModuleUtils;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * WFLY-2732 - test if MDB can access @DependsOn ejbs in @PostConstruct and @PreDestroy annotated methods.
 * 
 * @author baranowb
 */
@RunWith(Arquillian.class)
@ServerSetup({ MDBWhichDependsOnTestCase.JmsQueueSetup.class })
public class MDBWhichDependsOnTestCase {

    @ArquillianResource
    InitialContext ctx;

    @ArquillianResource
    Deployer deployer;
    private static final Logger logger = Logger.getLogger(MDBWhichDependsOnTestCase.class);

    @EJB(mappedName = Constants.EJB_JMS_NAME)
    private JMSMessagingUtil jmsUtil;

    @EJB
    private CallCounterInterface counter;
    
    @Resource(mappedName = Constants.QUEUE_JNDI_NAME)
    private Queue queue;
    
    @Resource (mappedName = Constants.QUEUE_REPLY_JNDI_NAME)
    private Queue replyQueue;


    static class JmsQueueSetup implements ServerSetupTask {

        private JMSOperations jmsAdminOperations;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            jmsAdminOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsAdminOperations.createJmsQueue(Constants.QUEUE_NAME, Constants.QUEUE_JNDI_NAME);
            jmsAdminOperations.createJmsQueue(Constants.QUEUE_REPLY_NAME, Constants.QUEUE_REPLY_JNDI_NAME);
            jmsAdminOperations.setSystemProperties(Constants.SYS_PROP_KEY, Constants.SYS_PROP_VALUE);
            // module
            ModuleUtils.createSimpleTestModule(Constants.TEST_MODULE_NAME, CallCounterInterface.class);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            if (jmsAdminOperations != null) {
                jmsAdminOperations.removeJmsQueue(Constants.QUEUE_NAME);
                jmsAdminOperations.removeJmsQueue(Constants.QUEUE_REPLY_NAME);
                jmsAdminOperations.removeSystemProperties();
                jmsAdminOperations.close();
            }
            ModuleUtils.deleteRecursively(new File(ModuleUtils.getModulePath(), "test"));
        }
    }

    @Deployment(name = "callcounter", order = 0, managed = true, testable = true)
    public static Archive<?> getTestArchive() throws Exception {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "callcounter.jar");
        jar.addClass(CallCounterSingleton.class);
        jar.addClass(MDBWhichDependsOnTestCase.class);
        jar.addClass(Constants.class);
        jar.addClass(JMSMessagingUtil.class);
        jar.addClass(JmsQueueSetup.class);
        jar.addAsManifestResource(new StringAsset("Dependencies: org.jboss.as.controller-client, org.jboss.dmr, test."
                + Constants.TEST_MODULE_NAME + "\n"), "MANIFEST.MF");
        return jar;
    }

    @Deployment(name = "mdb", order = 1, managed = false, testable = false)
    public static Archive getMDBArchive() {

        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "mdb.jar");
        jar.addPackage(JMSOperations.class.getPackage());
        jar.addClass(JMSMessagingUtil.class);
        jar.addClass(MDBWhichDependsOn.class);
        jar.addClass(Constants.class);
        jar.addClass(CallCounterProxy.class);
        jar.addAsManifestResource(new StringAsset("Dependencies: org.jboss.as.controller-client, org.jboss.dmr, test."
                + Constants.TEST_MODULE_NAME + "\n"), "MANIFEST.MF");
        logger.info(jar.toString(true));
        return jar;
    }

    /**
     * Test an annotation based MDB with properties substitution
     * 
     * @throws Exception
     */
    @Test
    public void testAnnoBasedMDB() throws Exception {
        this.deployer.deploy("mdb");
        this.jmsUtil.sendTextMessage("Say Nihao to new message!", this.queue, this.replyQueue);
        final Message reply = this.jmsUtil.receiveMessage(replyQueue, 5000);
        Assert.assertNotNull("Reply message was null on reply queue: " + this.replyQueue, reply);
        this.deployer.undeploy("mdb");
        Assert.assertTrue("PostConstruct not called!", counter.isPostConstruct());
        Assert.assertTrue("Message not called!", counter.isMessage());
        Assert.assertTrue("PreDestroy not called!", counter.isPreDestroy());
    }
}
