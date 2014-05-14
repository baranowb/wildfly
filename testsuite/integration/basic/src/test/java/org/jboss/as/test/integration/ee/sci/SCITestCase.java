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

package org.jboss.as.test.integration.ee.sci;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test sci count.
 * 
 * @author baranowb
 * 
 */
@RunWith(Arquillian.class)
public class SCITestCase {
    @Deployment
    public static WebArchive deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "sci-test.war");

        JavaArchive lib = ShrinkWrap.create(JavaArchive.class, "lib.jar");
        lib.addClass(SCITestInitializer.class);
        JavaArchive sciArchive1 = ShrinkWrap.create(JavaArchive.class, "jar1.jar");
        JavaArchive sciArchive2 = ShrinkWrap.create(JavaArchive.class, "jar2.jar");
        enableSCI(sciArchive1);
        enableSCI(sciArchive2);

        war.addAsLibraries(lib, sciArchive1, sciArchive2);
        return war;
    }

    private static void enableSCI(final JavaArchive arch) {
        arch.addAsServiceProvider("javax.servlet.ServletContainerInitializer",
                "org.jboss.as.test.integration.ee.sci.SCITestInitializer");
    }
    @Test
    public void testDeployment() throws Exception{
        //noop
    }
}
