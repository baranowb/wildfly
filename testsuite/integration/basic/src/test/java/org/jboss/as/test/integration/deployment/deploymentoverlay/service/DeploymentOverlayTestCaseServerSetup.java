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

package org.jboss.as.test.integration.deployment.deploymentoverlay.service;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.as.test.shared.FileUtils;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;

/**
 * @author baranowb
 *
 */
public class DeploymentOverlayTestCaseServerSetup implements ServerSetupTask {

    @Override
    public void setup(ManagementClient managementClient, String containerId) throws Exception {
        ModelNode op = new ModelNode();
        op.get(ModelDescriptionConstants.OP_ADDR).set(ModelDescriptionConstants.DEPLOYMENT_OVERLAY, Constants.NAME_OVERLAY);
        op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.ADD);
        ModelNode result = ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
        Assert.assertTrue("Failed on overlay creation: "+result,Operations.isSuccessfulOutcome(result));
        
        op = new ModelNode();
        op.get(ModelDescriptionConstants.OP_ADDR).set(new ModelNode());
        op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.UPLOAD_DEPLOYMENT_BYTES);
        op.get(ModelDescriptionConstants.BYTES).set(FileUtils.readFile(DeploymentOverlayTestCaseServerSetup.class, "override.xml").getBytes());
        result = ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
    
        Assert.assertTrue("Failed on overlay creation: "+result,Operations.isSuccessfulOutcome(result));
    }

    @Override
    public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
