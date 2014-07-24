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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import javax.naming.InitialContext;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.as.test.integration.management.base.AbstractMgmtServerSetupTask;
import org.jboss.as.test.integration.management.base.AbstractMgmtTestBase;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.as.test.shared.RetryTaskExecutor;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author baranowb
 * 
 */
@RunWith(Arquillian.class)
@ServerSetup(JCAReloadTestCase.JmsQueueSetup.class)
public class JCAReloadTestCase extends ContainerResourceMgmtTestBase {
    private static final String QUEUE_NAME = "queue/callerPrincipal";
    private static final String TEST_DU = "test";
    private static final String TEST_ARCHIVE = TEST_DU + ".jar";
    private static final String MDB_DU = "mdb";
    private static final String MDB_ARCHIVE = MDB_DU + ".jar";

    @ArquillianResource
    Deployer deployer;

    @ArquillianResource
    private InitialContext initialContext;
    private static final int RETRY_COUNT = 200;
    private static final int RETRY_DELAY = 500;

    @ArquillianResource
    private ManagementClient managementClient;

    public ManagementClient getManagementClient() {
        return managementClient;
    }

    public void setManagementClient(ManagementClient managementClient) {
        this.managementClient = managementClient;
    }

    @Override
    protected ModelControllerClient getModelControllerClient() {
        return managementClient.getControllerClient();
    }

    protected int getWaitRetryCount() {
        return RETRY_COUNT;
    }

    protected int getWaitRetryDelay() {
        return RETRY_DELAY;
    }

    /**
     * Create default reload operation. If there is need for custom reload operation, this method should be overridden.
     * 
     * @return
     */
    protected ModelNode getReloadOperation() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set("reload");
        return operation;
    }

    protected boolean waitForServer() {
        RetryTaskExecutor<Boolean> rte = new RetryTaskExecutor<Boolean>();
        try {
            return rte.retryTask(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    boolean available = managementClient.isServerInRunningState();
                    if (!available)
                        throw new Exception("Server not available.");
                    return available;
                }
            }, getWaitRetryCount(), getWaitRetryDelay());
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Provide reload operation on server
     * 
     * @throws Exception
     */
    protected void reload() throws Exception {
        ModelNode operation = getReloadOperation();

        ModelNode result = executeOperation(operation, false);
        Assert.assertTrue(result.toString(), Operations.isSuccessfulOutcome(result));
        if (!waitForServer())
            throw new Exception("Server reloading failed");
    }

    public static class JmsQueueSetup extends AbstractMgmtServerSetupTask {

        @Override
        protected void doSetup(final ManagementClient managementClient) throws Exception {
            System.err.println("SETUP");
            createQueue(QUEUE_NAME);
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            destroyQueue(QUEUE_NAME);
        }

        private void createQueue(String queueName) throws Exception {
            ModelNode addJmsQueue = getQueueAddr(queueName);
            addJmsQueue.get(ClientConstants.OP).set("add");
            addJmsQueue.get("entries").add("java:jboss/" + queueName);
            executeOperation(addJmsQueue);
        }

        private void destroyQueue(String queueName) throws Exception {
            ModelNode removeJmsQueue = getQueueAddr(queueName);
            removeJmsQueue.get(ClientConstants.OP).set("remove");
            executeOperation(removeJmsQueue);
        }
    }

    private static ModelNode getQueueAddr(String name) {
        final ModelNode queueAddr = new ModelNode();
        queueAddr.get(ClientConstants.OP_ADDR).add("subsystem", "messaging");
        queueAddr.get(ClientConstants.OP_ADDR).add("hornetq-server", "default");
        queueAddr.get(ClientConstants.OP_ADDR).add("jms-queue", name);
        return queueAddr;
    }

    @Deployment(managed = true, testable = true, name = TEST_DU, order = 0)
    public static Archive<?> getTestDeployment() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, TEST_ARCHIVE);
        jar.addClasses(JCAReloadTestCase.class, JmsQueueSetup.class);
        jar.addClasses(JcaMgmtBase.class, ContainerResourceMgmtTestBase.class, AbstractMgmtTestBase.class);
        jar.addPackage(AbstractMgmtTestBase.class.getPackage());
        jar.addClasses(MgmtOperationException.class, XMLElementReader.class, XMLElementWriter.class,ManagementOperations.class, RetryTaskExecutor.class);
        jar.addAsManifestResource(new StringAsset(
                "Dependencies: org.jboss.as.controller-client,org.jboss.dmr,org.jboss.as.cli,org.jboss.as.connector \n"),
                "MANIFEST.MF");
        return jar;
    }

    @Deployment(managed = false, testable = false, name = MDB_DU)
    public static Archive<?> getMDBDeployment() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, MDB_ARCHIVE);
        jar.addClass(MessageReceiverBean.class);
        jar.addAsManifestResource(new StringAsset(
                "Dependencies: org.jboss.as.controller-client,org.jboss.dmr,org.jboss.as.cli,org.jboss.as.connector \n"),
                "MANIFEST.MF");
        return jar;
    }

    @Test
    public void testReloadAndMDB() throws Exception {
        deployer.deploy(MDB_DU);
        //deployer.undeploy(MDB_DU);
        reload();
        //deployer.deploy(MDB_DU);
    }
}
