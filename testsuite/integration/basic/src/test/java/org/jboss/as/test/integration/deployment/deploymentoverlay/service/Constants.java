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

/**
 * @author baranowb
 * 
 */
public interface Constants {

    String NAME_TESTED_JAR_DU = "test";
    // String NAME_TESTED_JAR = NAME_TESTED_JAR_DU + ".war";
    String NAME_TESTED_JAR = NAME_TESTED_JAR_DU + ".jar";
    String NAME_EJB_DU = "test";
    String NAME_EJB = NAME_EJB_DU + ".jar";
    String NAME_EJB_TESTER_DU = "tester";
    String NAME_EJB_TESTER = NAME_EJB_TESTER_DU + ".jar";
    String NAME_LIB = "library.jar";

    String NAME_EJB_SINGLETON = "";
    String NAME_OVERLAY = NAME_TESTED_JAR_DU;

    String TEST_MODULE_NAME = "Overlayed";
    String TEST_MODULE_NAME_FULL = "test." + TEST_MODULE_NAME;

    String EJB_JNDI_OVERLAYED = "java:global/test/OverlayedEJB!org.jboss.as.test.integration.deployment.deploymentoverlay.service.Overlayed";
    String EJB_JNDI_REGISTER = "java:global/tester/InvocationRegisterEJB!org.jboss.as.test.integration.deployment.deploymentoverlay.service.InvocationRegister";
}
