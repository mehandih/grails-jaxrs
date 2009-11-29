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

import static org.codehaus.groovy.grails.web.converters.configuration.ConvertersConfigurationHolder.getConverterConfiguration
import static org.grails.jaxrs.provider.ProviderUtils.*

import java.io.IOException
import java.io.OutputStream
import java.lang.annotation.Annotation
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.io.OutputStreamWriterimport javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider

import grails.converters.JSON
import grails.converters.XML

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.codehaus.groovy.grails.web.converters.configuration.ConvertersConfigurationHolder;

/**
 * A message body writer that converts Grails domain objects to XML or JSON 
 * entity streams. The expected response content type is specified by the
 * <code>Accept</code> request header. Supported content types by this 
 * provider are <code>text/xml</code>, <code>application/xml</code>, 
 * <code>text/x-json</code> or <code>application/json</code>. Assuming 
 * <code>Note</code> is a Grails domain class. This provider supports usage of 
 * resource methods such as:
 * 
 * <pre>
 * &#064;Path('/notes')
 * class NotesResource {
 * 
 *      &#064;GET
 *      &#064;Produces(['application/xml','application/json'])
 *      Note getNote(...) {
 *          Note note = ... // e.g. load a note object from database.
 *          return note
 *      }
 *      
 *      &#064;GET
 *      &#064;Produces(['application/xml','application/json'])
 *      Collection&lt;Note&gt; getNotes(...) {
 *          def notes = ... // e.g. load list of note objects from database.
 *          return notes
 *      }
 *      
 *      &#064;GET
 *      &#064;Produces(['application/xml','application/json'])
 *      def getNotes(...) {
 *          def notes = ... // e.g. load list of note objects from database.
 *          return notes
 *      }
 *      
 * }
 * 
 * 
 * </pre>
 * 
 * This provider can be disabled by setting 
 * <code>org.grails.jaxrs.dowriter.disable</code> to <code>true</code> in the 
 * application config. If this provider should only support resource methods
 * that declare a generic collection as response type, where the generic type
 * must be a Grails domain class, then set
 * <code>org.grails.jaxrs.dowriter.require.generic.collections</code> to
 * <code>true</code> in the application config. 
 * 
 * @author Martin Krasser
 */
@Provider
@Produces(['text/xml', 'application/xml', 'text/x-json', 'application/json'])
class DomainObjectWriter implements MessageBodyWriter<Object>, GrailsApplicationAware {

    private static final Log LOG = LogFactory.getLog(DomainObjectWriter.class);

    public static final String DEFAULT_CHARSET = "UTF-8";

    GrailsApplication grailsApplication;
    
    long getSize(Object t, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        -1
    }

    /**
     * Returns <code>true</code> if <code>type</code> is a Grails domain class 
     * or domain class collection and the <code>mediaType</code> is one of 
     * <code>text/xml</code>, <code>application/xml</code>, 
     * <code>text/x-json</code> or <code>application/json</code>.
     */
    boolean isWriteable(Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        
        if (ConfigurationHolder.config.org.grails.jaxrs.dowriter.disable) {
            return false
        }
        
        boolean compatibleMediaType = isXmlType(mediaType) || isJsonType(mediaType)
        boolean requireGenericCollections =
            ConfigurationHolder.config.org.grails.jaxrs.dowriter.require.generic.collections

        if (Collection.class.isAssignableFrom(type) && !requireGenericCollections) {
            // A potential domain object collection. This check ignores
            // eventual type parameter declarations for reasons of 
            // simplicity. It is expected that returned collections
            // from resource methods always contains domain objects.
            return compatibleMediaType
        } else if (genericType instanceof ParameterizedType) {
            return isDomainObjectCollectionType(genericType)
        } else if (grailsApplication.isDomainClass(type)) {
            return compatibleMediaType
        }
        return false;
    }

     /**
      * Creates anXML or JSON response entity stream from a Grails domain 
      * object.
      */
    void writeTo(Object t, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream entityStream) 
            throws IOException, WebApplicationException {
        def writer = null
        def converter = null

        if (isXmlType(mediaType)) {
            writer = newWriter(entityStream, getConverterConfiguration(XML.class).encoding)
            converter = new XML(t)
            converter.render(writer)
            writer.flush()
        } else { // JSON
            writer = newWriter(entityStream, getConverterConfiguration(JSON.class).encoding)
            converter = new JSON(t)
            converter.render(writer)
        }
    }
 
    private boolean isDomainObjectCollectionType(ParameterizedType genericType) {
        if (!Collection.class.isAssignableFrom(genericType.rawType)) {
            return false
        }
        if (genericType.actualTypeArguments.length == 0) {
            return false
        }
        return grailsApplication.isDomainClass(genericType.actualTypeArguments[0])
    }
    
    private Writer newWriter(def stream, def charset) {
        if (charset) {
            return new OutputStreamWriter(stream, charset)
        } else {
            return new OutputStreamWriter(stream, DEFAULT_CHARSET)
        }
    }
    
}
