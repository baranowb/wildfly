package org.jboss.as.test.integration.management.base;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.shared.RetryTaskExecutor;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;

/**
 * Class that is extended by management tests that can use resource injection to get the management client
 *
 * @author Stuart Douglas
 */
public abstract class ContainerResourceMgmtTestBase extends AbstractMgmtTestBase {

    private static final int RETRY_COUNT = 200;
    private static final int RETRY_DELAY = 500;

    @ContainerResource
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

    protected int getWaitRetryCount(){
        return RETRY_COUNT;
    }

    protected int getWaitRetryDelay(){
        return RETRY_DELAY;
    }

    /**
     * Create default reload operation. If there is need for custom reload operation, this method should be overridden.
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
}
