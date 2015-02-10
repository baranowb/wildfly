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

package org.jboss.as.test.integration.deployment.overlay.jar;

import javax.ejb.Remote;

/**
 * @author baranowb
 *
 */
@Remote
public interface RemoteOverlayCheck {
    String DEPLOYMENT_NAME_JAR = "overlayed";
    String DEPLOYMENT_JAR_NAME_OVERLAYED = DEPLOYMENT_NAME_JAR + ".jar";
    String DEPLOYMENT_NAME_WAR = DEPLOYMENT_NAME_JAR+"-war";
    String DEPLOYMENT_WAR_NAME_OVERLAYED = DEPLOYMENT_NAME_WAR + ".war";
    String DEPLOYMENT_NAME_EAR= DEPLOYMENT_NAME_JAR+"-ear";
    String DEPLOYMENT_EAR_NAME_OVERLAYED = DEPLOYMENT_NAME_EAR + ".ear";
    
    String OVERLAY="FINE_COAT";
    String VALUE_ORIGINAL = "ImOldValue";
    String VALUE_OVERLAYED = "ImNewWhatIsGoingOn?";
    String FILE_NAME="tester.txt";
    String OVERLAY_RESOURCE_JAR="org/jboss/as/test/integration/deployment/overlay/jar/"+FILE_NAME;
    String OVERLAY_RESOURCE_WAR="WEB-INF/lib/"+DEPLOYMENT_JAR_NAME_OVERLAYED+"/org/jboss/as/test/integration/deployment/overlay/jar/"+FILE_NAME;
    String OVERLAY_RESOURCE_EAR=DEPLOYMENT_JAR_NAME_OVERLAYED+"/org/jboss/as/test/integration/deployment/overlay/jar/"+FILE_NAME;

    //JAR&WAR - no APP_NAME, hence it starts with /
    String EJB_LOOKUP_NAME_JAR = "ejb:"+                    "/"+DEPLOYMENT_NAME_JAR+"/ContentTestEJB!org.jboss.as.test.integration.deployment.overlay.jar.RemoteOverlayCheck";
    String EJB_LOOKUP_NAME_WAR = "ejb:"+                    "/"+DEPLOYMENT_NAME_WAR+"/ContentTestEJB!org.jboss.as.test.integration.deployment.overlay.jar.RemoteOverlayCheck";
    String EJB_LOOKUP_NAME_EAR = "ejb:"+DEPLOYMENT_NAME_EAR+"/"+DEPLOYMENT_NAME_JAR+"/ContentTestEJB!org.jboss.as.test.integration.deployment.overlay.jar.RemoteOverlayCheck";

    String getTestedContent() throws Exception;
}
