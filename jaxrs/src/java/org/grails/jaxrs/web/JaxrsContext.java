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
package org.grails.jaxrs.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The JAX-RS context used by applications to interact with a JAX-RS
 * implementation.
 * 
 * @author Martin Krasser
 * @author David Castro
 */
public class JaxrsContext {
    
    private static final Log LOG = LogFactory.getLog(JaxrsContext.class);

    /**
     * Name of the Jersey JAX-RS implementation. 
     */
    public static final String JAXRS_PROVIDER_NAME_JERSEY = "jersey";

    /**
     * Name of the Restlet JAX-RS implementation. 
     */
    public static final String JAXRS_PROVIDER_NAME_RESTLET = "restlet";
    
    /**
     * Name of the JAX-RS servlet.
     */
    public static final String SERVLET_NAME = "org.grails.jaxrs.servlet.name";

    private volatile Servlet jaxrsServlet;
    private volatile ServletContext jaxrsServletContext;
    private volatile JaxrsConfig jaxrsConfig;
    private volatile String jaxrsProviderName;
    private volatile String jaxrsProviderExtraPaths;
    
    private JaxrsService jaxrsService;

    /**
     * Creates a new {@link JaxrsContext} using Jersey as JAX-RS implementation.
     */
    public JaxrsContext() {
        this.jaxrsConfig = new JaxrsConfig();
        this.jaxrsService = new JaxrsServiceImpl();
        this.jaxrsProviderName = JAXRS_PROVIDER_NAME_JERSEY;
    }

    /**
     * Returns the JAX-RS configuration.
     * 
     * @return the JAX-RS configuration.
     */
    public JaxrsConfig getJaxrsConfig() {
        return jaxrsConfig;
    }
    
    /**
     * Returns a service instance for processing HTTP requests.
     * 
     * @return a {@link JaxrsService} instance.
     */
    public JaxrsService getJaxrsService() {
        return jaxrsService;
    }

    /**
     * Set the name of the JAX-RS provider to use. If this context is already
     * initialized, a call to {@link JaxrsContext#refresh()} must follow for
     * changing the JAX-RS implementation.
     * 
     * @param jaxrsProviderName
     *            name of the JAX-RS implementation.
     * @see #JAXRS_PROVIDER_NAME_JERSEY
     * @see #JAXRS_PROVIDER_NAME_RESTLET
     */
    public void setJaxrsProviderName(String jaxrsProviderName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setting provider name: " + jaxrsProviderName);
        }
        this.jaxrsProviderName = jaxrsProviderName;
    }

    /**
     * Set the extra paths to pass to the JAX-RS implementation to search
     * for providers.
     * 
     * @param jaxrsProviderExtraPaths extra paths to pass to the JAX-RS
     *            implementation to search for providers
     */
    public void setJaxrsProviderExtraPaths(String jaxrsProviderExtraPaths) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setting provider extra paths: " + jaxrsProviderExtraPaths);
        }
        this.jaxrsProviderExtraPaths = jaxrsProviderExtraPaths;
    }
    
    /**
     * Reloads the JAX-RS configuration defined by Grails applications and
     * re-initializes the JAX-RS runtime.
     * 
     * @throws ServletException
     * @throws IOException
     */
    public void refresh() throws ServletException, IOException {
        if (jaxrsServlet != null) {
            destroy();
            init();
        }
    }
    
    void init() throws ServletException {
        if (jaxrsProviderName.equals(JAXRS_PROVIDER_NAME_RESTLET)) {
            init(new RestletServlet(jaxrsConfig));
        } else if (jaxrsProviderName.equals(JAXRS_PROVIDER_NAME_JERSEY)) {
            init(new JerseyServlet());
        } else {
            throw new ServletException("Illegal provider name: " + jaxrsProviderName + ". either use "
                    + JAXRS_PROVIDER_NAME_JERSEY + " or " 
                    + JAXRS_PROVIDER_NAME_RESTLET + "."); 
        }
    }
    
    void init(Servlet jaxrsServlet) throws ServletException {
        this.jaxrsServlet = jaxrsServlet;
        this.jaxrsServlet.init(new Config());
    }
    
    void destroy() {
        if (jaxrsServlet != null) {
            jaxrsServlet.destroy();
            jaxrsServlet = null;
        }
    }
    
    void setJaxrsServletContext(ServletContext jaxrsServletContext) {
        this.jaxrsServletContext = jaxrsServletContext;
    }
    
    private class JaxrsServiceImpl implements JaxrsService {

        public void process(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            jaxrsServlet.service(request, response);
        }
        
    }
    
    class Config implements ServletConfig {
        Hashtable<String, String> params = new Hashtable<String, String>();
 
        public String getJaxrsProviderExtraPaths() {
            return jaxrsProviderExtraPaths;
        }
        
        public Hashtable<String, String> getInitParameters() {
            return params;
        }
        
        public String getInitParameter(String name) {
            return params.get(name);
        }

        public Enumeration<String> getInitParameterNames() {
            return params.keys();
        }

        public ServletContext getServletContext() {
            return jaxrsServletContext;
        }

        public String getServletName() {
            return SERVLET_NAME;
        }
        
    }
    
}
