/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.as.naming.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.naming.CompositeName;
import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NamingException;

/**
 * Name parser used by the NamingContext instances.  Relies on composite name instances.
 *
 * @author John E. Bailey
 */
public class NameParser implements javax.naming.NameParser {

    public static final NameParser INSTANCE = new NameParser();
    public static final String PATTERN = "[a-zA-Z]*://.*";
    public static final String ESCAPE = "\\";
    public static final String TO_REPLACE = "://";
    public static final String REPLACE_WITH = ":"+ESCAPE+"/"+ESCAPE+"/";
    private static final Properties syntax = new Properties();
    static{
        syntax.put("jndi.syntax.direction", "left_to_right");
        syntax.put("jndi.syntax.ignorecase", "false");
        syntax.put("jndi.syntax.separator", "/");
        syntax.put("jndi.syntax.escape", ESCAPE);
    }
    private NameParser() {
    }

    /**
     * Parse the string name into a {@code javax.naming.Name} instance.
     *
     * @param name The name to parse
     * @return The parsed name.
     * @throws NamingException
     */
    public Name parse(final String name) throws NamingException {
        String toParse = name;
        if(Pattern.matches(PATTERN, name)){
            toParse = toParse.replace(TO_REPLACE, REPLACE_WITH);
            return new InnerCompositeName(new CompoundName(toParse, syntax).getAll());
        } else {
            return new CompositeName(toParse);
        }
    }

    static class InnerCompositeName extends CompositeName{

        private static final long serialVersionUID = -1861862759030023651L;

        public InnerCompositeName(Enumeration<String> comps) {
            super(comps);
        }
    }
}
