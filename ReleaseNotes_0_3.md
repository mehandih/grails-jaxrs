# Release notes for grails-jaxrs version 0.3 #

## Download ##

  * See [installation instructions](InstallationInstructions.md)

## Documentation ##

  * [Getting started](GettingStarted.md)
  * [Advanced features](AdvancedFeatures.md)

## What's new ##

  * Domain object providers
    * Convert between Grails [domain objects](AdvancedFeatures#Domain_objects.md) and XML or JSON representations
    * Allow usage of Grails [domain classes](AdvancedFeatures#Domain_objects.md) in resource method signatures
    * Support for [content negotiation](AdvancedFeatures#Domain_objects.md) using the `Accept` request header
    * Can be disabled by means of [configuration](AdvancedFeatures#Configuration.md)
  * [Custom entity provider support](AdvancedFeatures#Custom_providers.md)
    * Base classes for custom domain object providers
      * `org.grails.jaxrs.support.DomainObjectReaderSupport`
      * `org.grails.jaxrs.support.DomainObjectWriterSupport`
    * Base classes for more general entity providers (improved)
      * `org.grails.jaxrs.support.MessageBodyReaderSupport`
      * `org.grails.jaxrs.support.MessageBodyWriterSupport`
  * [Scaffolding enhancements](GettingStarted#Scaffolding.md)
    * XML and JSON representations
    * Content negotiation support
  * Auto-detection of JAX-RS resource classes better aligned with the JAX-RS specification (incl. support for annotation inheritance)
  * Auto-detection of JAX-RS provider classes better aligned with the JAX-RS specification
  * Default URL mapping for `JaxrsController` [changed](#Upgrade_notes.md)
  * Upgrade to Grails 1.1.2
  * Upgrade to Jersey 1.1.4.1
  * Upgrade to Restlet 2.0-M6

## Known issues ##

  * There's a bug ([issue 971](http://restlet.tigris.org/issues/show_bug.cgi?id=971)) in Restlet 2.0-M6 that forces implementors of `MessageBodyReader` and `MessageBodyWriter` to **directly** implement these interfaces. Extending a class that implements these interfaces doesn't work. Restlet will ignore the provider in this case.

## Upgrade notes ##

The default URL mapping changed in version 0.3. In version 0.2 all URLs were mapped to the `JaxrsController`. In version 0.3 the default URL mapping changed to

  * `"/api"    (controller:"jaxrs")`
  * `"/api/**" (controller:"jaxrs")`

Refer to the [URL mappings](AdvancedFeatures#URL_mappings.md) section for instructions how to change the default URL mapping.

The auto-detection criteria for

  * JAX-RS resource classes under `grails-app/resources` and
  * JAX-RS provider classes under `grails-app/provider`

are now better aligned with the JAX-RS specification 1.0, sections 3.1 and 4.2.