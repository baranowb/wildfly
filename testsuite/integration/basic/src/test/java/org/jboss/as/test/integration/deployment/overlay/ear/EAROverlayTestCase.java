/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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

package org.jboss.as.test.integration.deployment.overlay.ear;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.deployment.overlay.jar.RemoteOverlayCheck;
import org.jboss.as.test.integration.deployment.overlay.jar.ContentTestEJB;
import org.jboss.as.test.integration.deployment.overlay.jar.EJBOverlayTestCase;
import org.jboss.as.test.integration.deployment.overlay.jar.OverlayUtils;
import org.jboss.as.test.integration.deployment.overlay.jar.RemoteOverlayCheck;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author baranowb
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EAROverlayTestCase {
    @ArquillianResource
    protected ManagementClient managementClient;
    private static final Logger logger = Logger.getLogger(EJBOverlayTestCase.class);

    @Deployment(name = RemoteOverlayCheck.DEPLOYMENT_NAME_EAR, order = 0, managed = true, testable = false)
    public static Archive getTestedArchive() {
        final Archive archive = createOverlayedArchive();
        logger.info(archive.toString(true));
        return archive;
    }

    public static Archive<?> createOverlayedArchive() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, RemoteOverlayCheck.DEPLOYMENT_JAR_NAME_OVERLAYED);
        jar.addClass(ContentTestEJB.class);
        jar.addClasses(RemoteOverlayCheck.class);
        jar.addAsResource(new StringAsset(RemoteOverlayCheck.VALUE_ORIGINAL), RemoteOverlayCheck.OVERLAY_RESOURCE_JAR);
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, RemoteOverlayCheck.DEPLOYMENT_EAR_NAME_OVERLAYED);
        ear.addAsModule(jar);
        return ear;
    }

    @Before
    public void setup() throws Exception {
        OverlayUtils.setupOverlay(managementClient, RemoteOverlayCheck.DEPLOYMENT_EAR_NAME_OVERLAYED, RemoteOverlayCheck.OVERLAY,
                RemoteOverlayCheck.OVERLAY_RESOURCE_EAR, RemoteOverlayCheck.VALUE_OVERLAYED);
    }

    @After
    public void cleanup() throws Exception {
        OverlayUtils.removeOverlay(managementClient, RemoteOverlayCheck.DEPLOYMENT_EAR_NAME_OVERLAYED, RemoteOverlayCheck.OVERLAY,
                RemoteOverlayCheck.OVERLAY_RESOURCE_EAR);
    }

    @Test
    public void testOverlay() throws Exception {
        RemoteOverlayCheck check = (RemoteOverlayCheck) getInitialContext().lookup(RemoteOverlayCheck.EJB_LOOKUP_NAME_EAR);
        Assert.assertNotNull(check);
        Assert.assertEquals(RemoteOverlayCheck.VALUE_OVERLAYED, check.getTestedContent());
    }

    protected InitialContext getInitialContext() throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.as.naming.InitialContextFactory");
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final InitialContext context = new InitialContext(jndiProperties);
        return context;
    }
}
