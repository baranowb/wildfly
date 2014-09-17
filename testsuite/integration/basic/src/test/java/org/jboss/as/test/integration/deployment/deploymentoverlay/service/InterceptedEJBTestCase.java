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

package org.jboss.as.test.integration.deployment.deploymentoverlay.service;

import java.io.File;
import java.io.FileOutputStream;

import javax.ejb.EJB;
import javax.naming.InitialContext;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * 
 * @author baranowb
 */
@RunWith(Arquillian.class)
@ServerSetup({ SetupModuleServerSetupTask.class })
public class InterceptedEJBTestCase {

    private static final Logger logger = Logger.getLogger(InterceptedEJBTestCase.class);

    @ArquillianResource
    Deployer deployer;

    @EJB(mappedName = Constants.SINGLETON_EJB)
    private InvocationRegister register;

    @Deployment(name = Constants.DEPLOYMENT_NAME_TESTER, order = 0, managed = true, testable = true)
    public static Archive<?> getTestArchive() throws Exception {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, Constants.DEPLOYMENT_JAR_NAME_COUNTER);
        jar.addClass(InterceptionSingletonEJB.class);
        jar.addClass(InterceptedEJBTestCase.class);
        jar.addClasses(SetupModuleServerSetupTask.class);
        jar.addAsManifestResource(new StringAsset("Dependencies: org.jboss.as.controller-client, org.jboss.dmr, "
                + Constants.TEST_MODULE_NAME_FULL + "\n"), "MANIFEST.MF");
        return jar;
    }

    @Deployment(name = Constants.DEPLOYMENT_NAME_OVERLAYED, order = 1, managed = false, testable = false)
    public static Archive getInterceptedArchive() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, Constants.DEPLOYMENT_JAR_NAME_OVERLAYED);
        jar.addClass(InterceptedEJB.class);
        jar.addClass(DeployedInterceptor.class);
        jar.addAsManifestResource(new StringAsset("Dependencies: org.jboss.as.controller-client, org.jboss.dmr, "
                + Constants.TEST_MODULE_NAME_FULL + "\n"), "MANIFEST.MF");
        jar.addAsManifestResource(new StringAsset(DeployedInterceptor.class.getName()),
                "services/org.jboss.ejb.client.EJBClientInterceptor");
        logger.info(jar.toString(true));
        return jar;
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testINterception() throws Exception {
        this.deployer.deploy(Constants.DEPLOYMENT_NAME_OVERLAYED);
        Assert.assertNotNull("" + register, register);
        Assert.assertNotNull(new InitialContext().lookup(Constants.SINGLETON_OVERLAYED_EJB));
        Intercepted intercepted = (Intercepted) new InitialContext().lookup(Constants.SINGLETON_OVERLAYED_EJB);
        intercepted.testMethod();
        this.deployer.undeploy(Constants.DEPLOYMENT_NAME_OVERLAYED);
        Assert.assertNotEquals(0, register.getInvocationCount());
    }
}
