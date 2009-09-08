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
package org.grails.jaxrs.provider

import static org.grails.jaxrs.provider.ConverterUtils.getDefaultEncoding;
import static org.grails.jaxrs.provider.ConverterUtils.xmlToMap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware;

/**
 * Very experimental provider to convert from XML streams to Grails domain 
 * objects. 
 * 
 * @author Martin Krasser
 */
@Provider
class DomainObjectReader implements MessageBodyReader<Object>, GrailsApplicationAware {

    GrailsApplication grailsApplication
    
    boolean isReadable(Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        grailsApplication.isDomainClass(type);
    }

    Object readFrom(Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        
        // TODO: obtain encoding from HTTP header and/or XML document
        String encoding = getDefaultEncoding(grailsApplication);

        // Construct domain object from xml map obtained from entity stream
        type.metaClass.invokeConstructor(xmlToMap(entityStream, encoding))
    }

}
