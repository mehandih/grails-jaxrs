# Release notes for grails-jaxrs version 0.4 #

## Download ##

  * See [installation instructions](InstallationInstructions.md)

## Documentation ##

  * [Getting started](GettingStarted.md)
  * [Advanced features](AdvancedFeatures.md)

## What's new ##

This release is mainly a bugfix and maintainance release.

### Additions ###
  * [Automated generation of WADL documents](GettingStarted#Generate_WADL.md) (with some [known limitations](http://code.google.com/p/grails-jaxrs/issues/detail?id=17))
  * Upgrade to Grails 1.3.1
  * Upgrade to Jersey 1.2
  * Upgrade to Restlet 2.0-RC3

### Fixes ###
  * [Object ids in XML or JSON requests are not set on the domain object](http://code.google.com/p/grails-jaxrs/issues/detail?id=18).
  * [Unmarshalling from JSON (and XML) to nested domain objects doesn't work properly](http://code.google.com/p/grails-jaxrs/issues/detail?id=21)

## Known issues ##

  * There's a bug ([issue 971](http://restlet.tigris.org/issues/show_bug.cgi?id=971)) in Restlet 2.0-RC3 that forces implementors of `MessageBodyReader` and `MessageBodyWriter` to **directly** implement these interfaces. Extending a class that implements these interfaces doesn't work. Restlet will ignore the provider in this case.

## Upgrade notes ##

If you upgrade from grails-jaxrs 0.2, see also the [release notes of the grails-jaxrs 0.3 release](ReleaseNotes_0_3.md).