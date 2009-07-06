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

import groovy.util.GroovyTestCase

import javax.servlet.Servlet

import org.grails.jaxrs.test.integration.TestEnvironment
import org.grails.jaxrs.web.JaxrsUtils

import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockHttpServletRequest

/**
 * @author Martin Krasser
 */
public class JaxrsControllerIntegrationTests extends GroovyTestCase {

    static transactional = false

    def controller
    
    void setUp() {
        controller = new JaxrsController()
        JaxrsUtils.setJaxrsServlet(controller.servletContext, TestEnvironment.instance.jaxrsServlet)
    }
    
    void testGetTest01() {
        controller.request.method = 'GET'
        JaxrsUtils.setRequestUriAttribute(controller.request, '/test/01')
        controller.handle()
        assertEquals('test01', controller.response.contentAsString)
        assertEquals('text/plain', controller.response.getHeader('Content-Type'))
    }

    void testPostTest02() {
        controller.request.method = 'POST'
        controller.request.content = 'hello'.bytes
        controller.request.addHeader('Content-Type', 'text/plain')
        JaxrsUtils.setRequestUriAttribute(controller.request, '/test/02')
        controller.handle()
        assertEquals('response:hello', controller.response.contentAsString)
        assertEquals('text/plain', controller.response.getHeader('Content-Type'))
    }
    
}
