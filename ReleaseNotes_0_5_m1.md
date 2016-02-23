# Release notes for grails-jaxrs version 0.5-m1 #

## Download ##

  * [grails-jaxrs-0.5-m1-p1](http://code.google.com/p/grails-jaxrs/downloads/detail?name=grails-jaxrs-0.5-m1-p1.zip)
  * See also [installation instructions](InstallationInstructions.md)

## Documentation ##

  * [Getting started](GettingStarted.md)
  * [Advanced features](AdvancedFeatures.md)

## What's new ##

### Additions ###
  * [New integration test framework](http://code.google.com/p/grails-jaxrs/wiki/AdvancedFeatures#Integration_testing)

### Enhancements ###
  * [Support for configuring Jersey with additional provider paths](http://code.google.com/p/grails-jaxrs/issues/detail?id=22)
  * [Switch to Grails' dependency management mechanism](http://code.google.com/p/grails-jaxrs/issues/detail?id=32)
  * [JaxrsController moved to package org.grails.jaxrs](http://code.google.com/p/grails-jaxrs/issues/detail?id=33)

### Updates ###
  * Upgrade to Grails 1.3.7
  * Upgrade to Jersey 1.5

### Fixes ###
  * [Responses with Content-Type text/html eaten by Grails](http://code.google.com/p/grails-jaxrs/issues/detail?id=26&)
  * [Service injection into resources doesn't work in integration tests](http://code.google.com/p/grails-jaxrs/issues/detail?id=37)

## Known issues ##

  * There's a bug ([issue 971](http://restlet.tigris.org/issues/show_bug.cgi?id=971)) in Restlet 2.0-RC3 that forces implementors of `MessageBodyReader` and `MessageBodyWriter` to **directly** implement these interfaces. Extending a class that implements these interfaces doesn't work. Restlet will ignore the provider in this case. An upgrade to the current Restlet 2.0.5 release is planned for the next milestone or release candidate.

## Upgrade notes ##

If you upgrade from grails-jaxrs 0.3, see also the [release notes of the grails-jaxrs 0.4 release](ReleaseNotes_0_4.md).