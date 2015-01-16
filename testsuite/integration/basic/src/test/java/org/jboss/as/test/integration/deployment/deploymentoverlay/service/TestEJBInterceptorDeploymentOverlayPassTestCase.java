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

import javax.ejb.EJB;
import javax.naming.Context;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author baranowb
 * 
 */
@RunWith(Arquillian.class)
@ServerSetup({SetupModuleServerSetupTask.class})
public class TestEJBInterceptorDeploymentOverlayPassTestCase {

    // @ContainerResource
    // private ManagementClient managementClient;

     @EJB(mappedName=Constants.EJB_JNDI_REGISTER)
     private InvocationRegister register;

     @EJB(mappedName=Constants.EJB_JNDI_OVERLAYED)
     private Overlayed overlayed;

     @ContainerResource
     private Context context;
//
     @ArquillianResource
     Deployer deployer;
    @Deployment(name = Constants.NAME_EJB_TESTER_DU, order = 0, managed = true, testable = true)
    public static Archive<?> getTestArchive() throws Exception {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, Constants.NAME_EJB_TESTER);
        jar.addClass(TestEJBInterceptorDeploymentOverlayPassTestCase.class);
        jar.addClass(SetupModuleServerSetupTask.class);
        jar.addClass(InvocationRegisterEJB.class);
        jar.addClass(Constants.class);
        jar.addAsManifestResource(new StringAsset("Dependencies: org.jboss.as.controller-client, org.jboss.dmr, "
                + Constants.TEST_MODULE_NAME_FULL + "\n"), "MANIFEST.MF");
        return jar;
    }

    @Deployment(name = Constants.NAME_TESTED_JAR_DU, order = 1, managed = false, testable = false)
    public static Archive<?> getTestedArchive() throws Exception {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, Constants.NAME_TESTED_JAR);
        archive.addClass(InterceptorBase.class);
        archive.addClass(DeployedInterceptor.class);
        archive.addClass(OverlayedInterceptor.class);
        archive.addClass(OverlayedEJB.class);
        archive.addClass(Constants.class);
        archive.addAsManifestResource(new StringAsset("Dependencies: org.jboss.as.controller-client, org.jboss.dmr, "
                + Constants.TEST_MODULE_NAME_FULL + "\n"), "MANIFEST.MF");
        // archive.addAsManifestResource(new StringAsset(DeployedInterceptor.class.getName()),
        // "services/org.jboss.ejb.client.EJBClientInterceptor");
        return archive;
    }

    @Test
    public void test() throws Exception {
        try {
            deployer.deploy(Constants.NAME_TESTED_JAR_DU);
            System.err.println(">>"+overlayed);
            System.err.println(">>"+register);
            System.err.println(context.lookup(Constants.EJB_JNDI_REGISTER));
            System.err.println(context.lookup(Constants.EJB_JNDI_OVERLAYED));
            overlayed.testMethod();
            createOverlay();
        } finally {
            deployer.undeploy(Constants.NAME_TESTED_JAR_DU);
            removeOverlay();
        }
        Assert.assertTrue("" + register, false);
    }

    /**
     * 
     */
    private void removeOverlay() {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    private void createOverlay() {
        // TODO Auto-generated method stub

    }

}
