

# Domain objects #

Grails domain classes can be used in resource methods as return types and parameters out-of-the box. The grails-jaxrs plugin is able to convert domain objects to and from XML and JSON representations. These conversions are done by dedicated [domain object providers](#Domain_object_providers.md). This section gives some examples how to use domain classes in resource methods.

## Single domain objects ##

Let's assume there's a `Person` domain class and a `PersonResource.update` method  that responds to HTTP PUT operations. The `Person` domain class has two properties `firstName` and `lastName`.

```
package hello

class Person {
    String firstName
    String lastName
}
```

The `PersonResource.update` method

  * defines a `Person` parameter for receiving person data from a client
  * defines a `Person` return type for sending person data to the client.
  * has annotations that constrain valid `Person` representation formats to `application/xml` or `application/json`.

```
package hello

import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.PUT

class PersonResource {
        
    @PUT
    @Consumes(['application/xml','application/json'])
    @Produces(['application/xml','application/json'])
    Person update(Person person) {
        Person result = ... // make some database changes
        return result
    }

    // ...

}
```

Depending on the `Content-Type` request header the client must either send an XML representation

```
PUT /hello/api/person/1 HTTP/1.1
Content-Type: application/xml
Accept: application/xml
Host: localhost:8080
Content-Length: 82

<person>
  <firstName>Sam</firstName>
  <lastName>Hill</lastName>
</person>
```

or a JSON representation

```
PUT /hello/api/person/2 HTTP/1.1
Content-Type: application/json
Accept: application/json
Host: localhost:8080
Content-Length: 58

{"class":"Person","firstName":"Fabien","lastName":"Barel"}
```

In both cases the plugin binds the representation to the `person` parameter of the `update` method. The representation of the response entity depends on the `Accept` request header. If the client sends an `Accept=application/xml` request header, the `Person` object returned from the `update` method is converted to XML. With an `Accept=application/json` request header, a conversion to JSON is done.

Instead of declaring a `Person` return type one could also declare a `javax.ws.rs.core.Response` return type and set the response entity during the response building process, as shown in the following example:

```
    @PUT
    @Consumes(['application/xml','application/json'])
    @Produces(['application/xml','application/json'])
    Response update(Person person) {
        Person result = ... // make some database changes
        return Response.ok(result)
    }
```

This gives better control over e.g. response header creation and still allows for content negotiation.

## Domain object collections ##

The grails-jaxrs plugin can also handle domain object collections that are returned from resource methods. In the following example, the `readAll` resource method returns a list of all `Person` objects in the database.

```
package hello

import java.util.List
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.Path

@Path('/api/person')
class PersonCollectionResource {

    @GET
    @Produces(['application/xml','application/json'])
    List<Person> readAll() {
        Person.findAll()
    }

    // ...

}
```

Depending on the `Accept` request header either an XML or JSON representation is returned to the client. The generic return type (`List<Person>`) makes it explicit which domain objects are contained in the collection. But one could equally write:

```
    @GET
    @Produces(['application/xml','application/json'])
    List readAll() {
        Person.findAll()
    }
```

or

```
    @GET
    @Produces(['application/xml','application/json'])
    def readAll() {
        Person.findAll()
    }
```

The plugin's [domain object writer](#Domain_object_providers.md), however, can be configured to only convert domain objects returned from methods that declare a generic collection return type by adding the following line to `grails-app/conf/Config.groovy`:

```
org.grails.jaxrs.dowriter.require.generic.collections=true
```

This is useful in situations where resource methods return collections that contain objects other than Grails domain objects (which cannot be handled by the domain object writer). Alternatively, one could [disable](#Configuration.md) the plugin's domain object writer completely and write a [custom one](#Custom_providers.md).

As with resource methods that return single domain objects, one can also declare a `javax.ws.rs.core.Response` return type and set the domain object collection as response entity.

```
    @GET
    @Produces(['application/xml','application/json'])
    Response readAll() {
        Response.ok(Person.findAll())
    }
```

Using domain object collections for method parameters is not supported (yet).

# Entity providers #

Entity providers bind representation formats to Java classes e.g. an XML representation  to a `Person` class as shown in the [previous](#Domain_objects.md) section. They are used to factor out marshalling and unmarshalling code from resource classes.

The grails-jaxrs plugin provides some default entity providers that are presented in the following subsections. The [custom providers](#Custom_providers.md) section explains how to implement custom entity providers.

## Domain object providers ##

Domain object providers convert between Grails domain classes and their XML or JSON representations and support content negotiation. This has already been shown in the sections [scaffolding](GettingStarted#Scaffolding.md) and [domain objects](#Domain_objects.md).

  * Converting an XML or JSON representation to a domain object is done by the `org.grails.jaxrs.provider.DomainObjectReader`. This provider is used whenever a Grails domain class is used as resource method parameter.
  * Converting a domain object to an XML or JSON representation is done by the `org.grails.jaxrs.provider.DomainObjectWriter`. This provider is used whenever a Grails domain object (collection) is returned from a resource method.

The behaviour of domain object providers can be customized as described in the [custom providers](#Custom_providers.md) section.

## XML providers ##

XML providers are superseded by [domain object providers](#Domain_object_providers.md) since grails-jaxrs version 0.3.

  * `org.grails.jaxrs.provider.XMLReader` converts an XML representation of a domain object into a `java.util.Map`. This map can be used for constructing domain objects.
  * `org.grails.jaxrs.provider.XMLWriter` converts an `grails.converters.XML` object into an XML representation.

Usage example:

```
import grails.converters.XML
...

class PersonResource {
        
    @PUT
    @Consumes('application/xml')
    @Produces('application/xml')
    XML update(Map dto) {
        Person person = new Person(map)
        // ... do something with person
        return person as XML
    }

}
```

## JSON providers ##

JSON providers are superseded by [domain object providers](#Domain_object_providers.md) since grails-jaxrs version 0.3.

  * `org.grails.jaxrs.provider.JSONReader` converts a JSON representation of a domain object into a `java.util.Map`. This map can be used for constructing domain objects.
  * `org.grails.jaxrs.provider.JSONWriter` converts an `grails.converters.JSON` object into a JSON representation.

Usage example:

```
import grails.converters.JSON
...

class PersonResource {
        
    @PUT
    @Consumes('application/json')
    @Produces('application/json')
    JSON update(Map dto) {
        Person person = new Person(map)
        // ... do something with person
        return person as JSON
    }

}
```

## Custom providers ##

Applications can implement their own entity providers by placing them into the `grails-app/providers` directory. In order to be auto-detected by grails-jaxrs they

  * must be annotated with `@javax.ws.rs.ext.Provider`
  * must have a file name matching `*Reader.groovy` if the corresponding class implements `javax.ws.rs.ext.MessageBodyReader`
  * must have a file name matching `*Writer.groovy` if the corresponding class implements `javax.ws.rs.ext.MessageBodyWriter`

### Custom domain object providers ###

For customizing the conversion between Grails domain objects and their XML or JSON representations, one has to disable the default [domain object providers](#Domain_object_providers.md) first. To disable the default domain object reader and writer, the following entries must be added to `grails-app/conf/Config.groovy`.

```
org.grails.jaxrs.doreader.disable=true
org.grails.jaxrs.dowriter.disable=true
```

In the following example a custom domain object writer is implemented, therefore, only the default domain object writer needs to be disabled. A custom XML creation should be done for the `Person` domain class (see [scaffolding](GettingStarted#Scaffolding.md) example), for all other classes the default XML creation should occur. Here's the custom provider.

```
package hello

import javax.ws.rs.Produces
import javax.ws.rs.ext.Provider
import groovy.xml.MarkupBuilder
import org.grails.jaxrs.support.DomainObjectWriterSupport

@Provider
@Produces(['text/xml', 'application/xml', 'text/x-json', 'application/json'])
class CustomDomainObjectWriter extends DomainObjectWriterSupport {

    protected Object writeToXml(Object obj, OutputStream entityStream, String charset) {
        if (obj instanceof Person) {
            def writer = new OutputStreamWriter(entityStream, charset)
            def builder = new MarkupBuilder(writer) 
            builder.person {
                id(obj.id)
                fullName("${obj.firstName} ${obj.lastName}")
            }
        } else {
            super.writeToXml(obj, entityStream, charset)
        }
    }
    
}
```

The custom provider overrides the `writeToXml` method and generates custom XML using a `MarkupBuilder`. To test this provider, create an application as described in the [scaffolding](GettingStarted#Scaffolding.md) example, create a folder `grails-app/provider/hello` and place this custom provider there. The plugin will auto-detect the provider. To create a new person object in the database, send the following request:

```
POST /hello/api/person HTTP/1.1
Content-Type: application/xml
Accept: application/xml
Host: localhost:8080
Content-Length: 83

<person>
  <firstName>Custom</firstName>
  <lastName>Tester</lastName>
</person>
```

The response entity is a custom XML representation created by the custom provider:

```
HTTP/1.1 201 Created
Content-Type: application/xml
Location: http://localhost:8080/hello/api/person/3
Transfer-Encoding: chunked
Server: Jetty(6.1.14)

<person>
  <id>3</id>
  <fullName>Custom Tester</fullName>
</person>
```

There are several other protected `DomainObjectWriterSupport` methods for customizing the domain object marshalling, for example `writeToJson` to create custom JSON representations or `isWriteable` to narrow the set of domain classes that a custom domain object writer accepts. Refer to the the API docs for details.

### Further entity provider support ###

For simple use cases, the grails-jaxrs plugin additionally provides the abstract classes

  * `org.grails.jaxrs.support.MessageBodyReaderSupport<T>`
  * `org.grails.jaxrs.support.MessageBodyWriterSupport<T>`

These base classes can also be used for classes other than domain classes. Implementors define the supported Java type with a type parameter. For example, the following class is a `MessageBodyWriter` that supports conversions for a `Note` class.

```
@Provider
@Produces('application/xml')
class NoteWriter extends MessageBodyWriterSupport<Note> {
    
    void writeTo(Note entity, MultivaluedMap httpHeaders, OutputStream entityStream) {
        def builder = new MarkupBuilder(new OutputStreamWriter(entityStream)) 
        builder.note {
            // create custom XML here ...
        }    
    }
   
}
```

For details about the `MessageBodyWriterSupport` and `MessageBodyReaderSupport` classes refer to the API docs.

Alternatively, you may of course write JAX-RS providers from scratch by using the JAX-RS API directly.

# Using GORM #

This section explains what's happening behind the scenes of the [scaffolding](GettingStarted#Scaffolding.md) example and how [GORM](http://grails.org/doc/1.3.1/guide/5.%20Object%20Relational%20Mapping%20(GORM).html) inside JAX-RS resource classes.

## `PersonCollectionResource.groovy` ##

Here's the source code for `PersonCollectionResource.groovy`.

```
package hello

import static org.grails.jaxrs.response.Responses.*

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.core.Response

@Path('/api/person')
@Consumes(['application/xml','application/json'])
@Produces(['application/xml','application/json'])
class PersonCollectionResource {

    @POST
    Response create(Person dto) {
        created dto.save()
    }

    @GET
    Response readAll() {
        ok Person.findAll()
    }
    
    @Path('/{id}')
    PersonResource getResource(@PathParam('id') String id) {
        new PersonResource(id:id)
    }
        
}
```

It is based based on JSR 311 classes and annotations and uses static methods from `org.grails.jaxrs.response.Responses`. This is a helper class provided by the plugin that implements a very simple DSL consisting of elements `created` and `ok`. Supported content types for requests and responses are `application/xml` and `application/json`. This is given by the class-level `@Consumes` and `@Produces` annotations.

The `PersonCollectionResource` class responds to HTTP operations that are releated to person lists. The URL of the person list is `http://localhost:8080/hello/api/person` where the `/api/person` path is defined by the class-level `@Path('/api/person')` annotation.

  * The `create` method responds to POST requests by storing a new `Person` object in the database using [GORM](http://grails.org/doc/1.3.1/guide/5.%20Object%20Relational%20Mapping%20(GORM).html). The XML or JSON request entity is converted by the plugin to a `Person` domain object and passed to the method via a `dto` parameter. The conversion is done by a [domain object provider](#Domain_object_providers.md). The persisted domain object is passed to the `created` method which creates a response from it using the JAX-RS API (see [source code](http://grails-jaxrs.googlecode.com/svn/trunk/jaxrs/src/groovy/org/grails/jaxrs/response/Responses.groovy) for details). The `created` method constructs a `URI` for the `Location` response header from the domain object id. The `Person` object is set to the `Response` entity.
  * The `readAll` method responds to GET requests and returns a person list. Again we use [GORM](http://grails.org/doc/1.3.1/guide/5.%20Object%20Relational%20Mapping%20(GORM).html) to get all person objects from the database and pass that list as argument to the `ok` method. This method uses the JAX-RS API to create a response (see [link source code](http://grails-jaxrs.googlecode.com/svn/trunk/jaxrs/src/groovy/org/grails/jaxrs/response/Responses.groovy) for details).
  * The `getResource` method creates another JAX-RS resource whenever a request to the URI template `http://localhost:8080/hello/api/person/{id} ` is made. The `id` path parameter in the template is bound to the `id` parameter of the `getResource` method. The created JAX-RS `PersonResource` is then used by the JAX-RS runtime to handle the request to the person with the given id.

## `PersonResource.groovy` ##

Here's the source code for `PersonResource.groovy`.

```
package hello

import static org.grails.jaxrs.response.Responses.*

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.PUT
import javax.ws.rs.core.Response

import org.grails.jaxrs.provider.DomainObjectNotFoundException

@Consumes(['application/xml','application/json'])
@Produces(['application/xml','application/json'])
class PersonResource {
    
    def id
    
    @GET
    Response read() {
        def obj = Person.get(id)
        if (!obj) {
            throw new DomainObjectNotFoundException(Person.class, id)
        }
        ok obj
    }
    
    @PUT
    Response update(Person dto) {
        def obj = Person.get(id)
        if (!obj) {
            throw new DomainObjectNotFoundException(Person.class, id)
        }
        obj.properties = dto.properties 
        ok obj
    }
    
    @DELETE
    void delete() {
        def obj = Person.get(id)
        if (obj) { 
            obj.delete()
        }
    }
    
}
```

The `id` property is set during construction of the resource and is used for database operations. This class implements the methods `read`, `update` and `delete` to handle GET, PUT and DELETE requests, respectivly. It also uses [GORM](http://grails.org/doc/1.3.1/guide/5.%20Object%20Relational%20Mapping%20(GORM).html) for database operations and relies on helper methods of `org.grails.jaxrs.response.Responses` to create repsonses via the JAX-RS API.

If there's no person with given `id` in the database, a `DomainObjectNotFoundException` is thrown. This exception class generates a custom `404` response using the JAX-RS API (see [source code](http://grails-jaxrs.googlecode.com/svn/trunk/jaxrs/src/groovy/org/grails/jaxrs/provider/DomainObjectNotFoundException.groovy) for details).

# Applying filters #

[Grails filters](http://grails.org/doc/1.3.1/guide/6.%20The%20Web%20Layer.html#6.6%20Filters) can be applied to JAX-RS resources as well. For example, to add a filter for the `/api/test/**` URL pattern, create a file `TestFilters.groovy` under `grails-app/conf` with a content like

```
class TestFilters {

    def filters = {
            
        testUris(uri:'/api/test/**') {
            before = {
                // do some preprocessing
            }
            after = {
                // do some postprocessing
            }
            
        }   
    }
}
```

# Service injection #

Services can be auto-injected into resource and [provider](#Entity_providers.md) objects by name. Assuming we have a service class named `TestService.groovy` in `grails-app/services`

```
package hello

class TestService {

     String greet(String name) {
         'Hello ' + (name ? name : 'unknown')
     }
    
}
```

then we can auto-inject it by defining a `testService` property like in the following resource class.

```
package hello

// imports omitted ...

@Path('/api/test')
class TestResource {

    def testService // injected
    
    @GET
    @Produces('text/plain')
    String getTestRepresentation(@QueryParam('name') String name) {
        testService.greet(name)
    }
    
}
```

# Google App Engine #

This section describes how to get the [hello world](GettingStarted#Hello_world.md) example running on Google App Engine.

  * Install the Grails [app-engine](http://grails.org/plugin/app-engine) plugin.
  * Create a Grails application and JAX-RS resource as described in the [hello world](GettingStarted#Hello_world.md) example.
  * Add the following entries to `grails-app/conf/Config.groovy`
```
org.grails.jaxrs.provider.name='restlet'
// replace <application-name> with the 
// actual App Engine application name
google.appengine.application='<application-name>'
```
  * Open a shell at the root directory of the hello world application.
  * Set the application version to 1 with
```
grails set-version 1
```
  * Run the application locally with `grails app-engine run`.
  * Enter the URL `http://localhost:8080/test?name=Tester` into your browser and the browser window should display `Hello Tester`.
  * Package the plugin with `grails app-engine package`.
  * Deploy the application with the `appcfg` command-line tool from your [App Engine SDK](http://code.google.com/appengine/downloads.html).
    * On Linux, enter
```
$APPENGINE_HOME/bin/appcfg.sh update ./target/war
```
    * On Windows, enter
```
%APPENGINE_HOME%\bin\appcfg.cmd update .\target\war
```
  * When prompted enter email and password to authenticate at Google App Engine.
  * Once deployment is done go enter the URL `http://<application-name>.appspot.com/test?name=Tester` into your browser and the browser window should display `Hello Tester`. It may take 10-20 seconds for Google App Engine to initialize the Grails application the first time. Subsequent requests are served much faster.

**Please note:** Using [scaffolding](GettingStarted#Scaffolding.md) together with the [gorm-jpa](http://www.grails.org/plugin/gorm-jpa) plugin is not supported at the moment. A [related feature request](http://code.google.com/p/grails-jaxrs/issues/detail?id=15) has already been added to the issue tracker.

# Configuration #

## URL mappings ##

When the grails-jaxrs plugin is installed, requests paths matching `/api/**` are forwarded to a `JaxrsController` which is part of the plugin. This default URL mapping can be changed via the `org.grails.jaxrs.url.mappings` property in `grails-app/conf/Config.groovy`. For example, setting this property to

  * `org.grails.jaxrs.url.mappings=['/abc', '/xyz']`

will create the following URL mappings for the `JaxrsController`:

  * `"/abc"(controller:"jaxrs")`
  * `"/abc/**"(controller:"jaxrs")`
  * `"/xyz"(controller:"jaxrs")`
  * `"/xyz/**"(controller:"jaxrs")`

`@Path` annotation values in JAX-RS resource classes must be set accordingly e.g.

```
@Path('/abc/test1')
class TestResource1 {
  ...    
}
```

or

```
@Path('/xyz/test2')
class TestResource2 {
  ...    
}
```

Custom mappings replace the default `/api/**` mapping. If the default mapping should apply in addition to custom mappings it must be added to the `org.grails.jaxrs.url.mappings` list.

  * `org.grails.jaxrs.url.mappings=['/abc', '/xyz', '/api']`

## JAX-RS implementation ##

The grails-jaxrs plugin allows to choose between [Jersey](https://jersey.dev.java.net/) (version 1.2) and [Restlet](http://www.restlet.org/) (version 2.0-RC3) as JAX-RS implementations. By default, Jersey is used. If you want to use Restlet instead, add the following line to `grails-app/conf/Config.groovy`.

```
org.grails.jaxrs.provider.name='restlet'
```

Restlet was added because it supports deployments to [Google App Engine](http://code.google.com/appengine/) (GAE). For instructions how to deploy a grails-jaxrs application to Google App Engine refer to the [Google App Engine](#Google_App_Engine.md) section.

The main obstacle for deploying Jersey to Google App Engine was missing support for JAXB in the past. This has been resolved with the App Engine SDK 1.2.8. However, attempts to deploy Jersey to Google App Engine revealed other issues. We plan to resolve these issues in upcoming versions of this plugin.

## JAX-RS resource scope ##

By default, JAX-RS resource classes are instantiated with every request which corresponds to the following entry in `grails-app/conf/Config.groovy`.

```
org.grails.jaxrs.resource.scope='prototype'
```

Since this is the default you can omit this entry as well. On the other hand, if you want that your JAX-RS resources are singletons, add the following configuration entry.

```
org.grails.jaxrs.resource.scope='singleton'
```

## Domain object providers ##

From version 0.3 onwards the grails-jaxrs plugin comes with JAX-RS providers for converting between Grails domain objects and XML/JSON representations. Domain object providers are explained in detail in the [domain object providers](#Domain_object_providers.md) section. Domain object readers and writers can be disabled by adding the following entries to `grails-app/conf/Config.groovy`.

```
org.grails.jaxrs.doreader.disable=true
org.grails.jaxrs.dowriter.disable=true
```

This is useful in situations where applications implement [custom providers](#Custom_providers.md). Another domain object provider configuration property, `org.grails.jaxrs.dowriter.require.generic.collections`, is explained in the [domain object collections](#Domain_object_collections.md) section.

## Additional providers ##

By default the grails-jaxrs plugin scans the `grails-app/providers` directory for custom providers. JAX-RS provider implementations located elsewhere (e.g. in 3rd party libraries) are ignored. This can be changed by defining _extra paths_ where the plugin should scan for additional providers. For example by adding the following to `grails-app/conf/Config.groovy`

```
org.grails.jaxrs.provider.extra.paths='com.foo;com.bar'
```

the plugin additionally scans the packages `com.foo` and `com.bar` for providers. Please note that this feature is only available when the plugin is configured to use Jersey. You can also define extra paths by setting the corresponding init parameter (see also [next section](#Init_parameters.md)).

```
org.grails.jaxrs.provider.init.parameters=['com.sun.jersey.config.property.packages':'com.foo;com.bar']
```

## Init parameters ##

Init parameters for the servlet of the underlying JAX-RS implementation can be set via the `org.grails.jaxrs.provider.init.parameters` configuration property in `grails-app/conf/Config.groovy`, as in the following example.

```
org.grails.jaxrs.provider.init.parameters=[
  'com.sun.jersey.config.property.packages':'com.foo;com.bar',
  'another.key':'another.value'
]
```

# Integration testing #

For integration testing JAX-RS resources and providers, you should extend the plugin's [IntegrationTestCase](https://github.com/krasserm/grails-jaxrs/blob/master/src/groovy/org/grails/jaxrs/itest/IntegrationTestCase.groovy) class. The following example is an integration test for the JAX-RS resources created in the [scaffolding](GettingStarted#Scaffolding.md) section. This test

  * creates a new `Person` by POSTing a JSON representation of that person to `/api/person` and then
  * retrieves a list of persons in JSON format by sending a GET request to `/api/person`

It is located under `test/integration` in package `hello`.

```
package hello

import org.grails.jaxrs.itest.IntegrationTestCase
import org.junit.Test

import static org.junit.Assert.*

class PersonIntegrationTest extends IntegrationTestCase {

    @Test
    void testPostAndGet() {
        def headers = ['Content-Type':'application/json', 'Accept':'application/json']
        def content = '{"class":"hello.Person","firstName":"Sam","lastName":"Hill"}'
        def expected = '{"class":"hello.Person","id":1,"firstName":"Sam","lastName":"Hill"}'
        
        // create new person
        sendRequest('/api/person', 'POST', headers, content.bytes)
        
        assertEquals(201, response.status)
        assertEquals(expected, response.contentAsString)
        assertTrue(response.getHeader('Content-Type').startsWith('application/json'))
        
        // get list of persons
        sendRequest('/api/person', 'GET', headers)
        
        assertEquals(200, response.status)
        assertEquals("[${expected}]".toString(), response.contentAsString)
        assertTrue(response.getHeader('Content-Type').startsWith('application/json'))
    }
    
}
```

For sending requests, the `sendRequest` method, defined in `IntegrationTestCase`, is used. The returned response is accessible via the `response` variable. Query parameters can be set with

```
controller.request.queryString='param=value'
```

prior to calling `sendRequest`.

Tests that extend `IntegrationTestCase` inherit the following behavior.

  * Initialization and start of a grails-jaxrs application for integration testing with (optional) auto-detection of user-defined JAX-RS resources and providers as well as domain classes.
  * JAX-RS resources in `grails-app/resources` are automatically detected and available for integration testing. Auto-detection can be turned off by overriding the `isAutoDetectJaxrsClasses` method to return `false`.
  * JAX-RS providers in `grails-app/providers` are automatically detected and available for integration testing. Auto-detection can be turned off by overriding the `isAutoDetectJaxrsClasses` method to return `false`.

Additional configuration options include.

  * JAX-RS resources and providers that are not auto-detected can be added to integration tests by overriding the `getJaxrsClasses` method.
  * Additional Spring application context XML files can be added to the integration test by overriding the `getContextLocations` method.
  * By default, Jersey is used as JAX-RS implementation. Other JAX-RS implementations can be specified by overriding the `getJaxrsImplementation` method.

The grails-jaxrs plugin itself is using this test framework for self-testing, such as in [JaxrsControllerIntegrationTests](https://github.com/krasserm/grails-jaxrs/blob/master/test/integration/org/grails/jaxrs/itest/JaxrsControllerIntegrationTests.groovy).