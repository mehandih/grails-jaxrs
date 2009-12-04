/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.jaxrs

import java.io.ByteArrayInputStream
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.ext.MessageBodyReader
import javax.ws.rs.ext.Provider
import grails.test.*

/**
 * @author Martin Krasser
 */
class JaxrsClassesTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testIsJaxrsResource() {        assertTrue(JaxrsClasses.isJaxrsResource(A.class))
        assertTrue(JaxrsClasses.isJaxrsResource(B.class))
        assertTrue(JaxrsClasses.isJaxrsResource(C.class))
        assertFalse(JaxrsClasses.isJaxrsResource(D.class))
        assertFalse(JaxrsClasses.isJaxrsResource(E.class))
    }
    
    void testIsJaxrsResourceInherit() {
        assertTrue(JaxrsClasses.isJaxrsResource(H1B.class))
        assertFalse(JaxrsClasses.isJaxrsResource(H2B.class))
        assertTrue(JaxrsClasses.isJaxrsResource(H1B.class))
    }
    
}

@Path('/a') class A { @GET String a() {'a'} }@Path('/b') class B { String b() {'b'} }class C { @GET String c() {'c'} }class D { String d() {'d'} }
abstract class E implements MessageBodyReader { @GET String e() {'e'} }

@Path('/a') class H1A {}
class H1B extends H1A {}

class H2A {}
class H2B extends H2A {}

@Path('/a') interface H3A {}
class H3B implements H3A {}
