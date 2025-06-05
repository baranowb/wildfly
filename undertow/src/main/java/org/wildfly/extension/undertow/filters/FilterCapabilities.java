/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.extension.undertow.filters;

import static org.wildfly.extension.undertow.Capabilities.CAPABILITY_FILTER;
import static org.wildfly.extension.undertow.Capabilities.CAPABILITY_HOST_FILTER_REF;
import static org.wildfly.extension.undertow.Capabilities.CAPABILITY_LOCATION_FILTER_REF;

import java.util.function.Function;

import org.jboss.as.clustering.controller.Capability;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.capability.QuaternaryCapabilityNameResolver;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.capability.TernaryCapabilityNameResolver;
import org.jboss.as.controller.capability.UnaryCapabilityNameResolver;
import org.wildfly.extension.undertow.UndertowFilter;

/**
 * @author Paul Ferraro
 * @author baranowb
 */
enum FilterCapabilities implements Capability {

    FILTER_CAPABILITY(CAPABILITY_FILTER, PredicateHandlerWrapper.class, false),
    FILTER_HOST_REF_CAPABILITY(CAPABILITY_HOST_FILTER_REF, UndertowFilter.class, true, TernaryCapabilityNameResolver.GRANDPARENT_PARENT_CHILD),
    FILTER_LOCATION_REF_CAPABILITY(CAPABILITY_LOCATION_FILTER_REF, UndertowFilter.class, true,  QuaternaryCapabilityNameResolver.GREATGRANDPARENT_GRANDPARENT_PARENT_CHILD);

    private final RuntimeCapability<Void> definition;

    FilterCapabilities(String name, Class<?> serviceValueType, boolean allowMultiple) {
        this.definition = RuntimeCapability.Builder.of(name, true, serviceValueType)
                .setDynamicNameMapper(UnaryCapabilityNameResolver.DEFAULT)
                .setAllowMultipleRegistrations(allowMultiple)
                .build();
    }

    FilterCapabilities(String name, Class<?> serviceValueType, boolean allowMultiple, Function<PathAddress, String[]> nameResolver) {
        this.definition = RuntimeCapability.Builder.of(name, true, serviceValueType)
                .setAllowMultipleRegistrations(allowMultiple)
                .setDynamicNameMapper(nameResolver).build();
    }

    @Override
    public RuntimeCapability<Void> getDefinition() {
        return this.definition;
    }
}
