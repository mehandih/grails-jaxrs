# Release notes for grails-jaxrs version 0.6 #

## Installation ##

```
grails install-plugin jaxrs
```

## Download ##

  * [grails-jaxrs-0.6](http://code.google.com/p/grails-jaxrs/downloads/detail?name=grails-jaxrs-0.6.zip)
  * See also [installation instructions](InstallationInstructions.md)

## Documentation ##

  * [Getting started](GettingStarted.md)
  * [Advanced features](AdvancedFeatures.md)

## What's new ##

### Upgrades ###
  * Upgrade to Grails 2.0.0
  * Upgrade to Jersey 1.8

### Enhancements ###
  * [Allow applications to configure init parameters for the JerseyServlet](http://code.google.com/p/grails-jaxrs/issues/detail?id=38)
  * [Separate service class used by generated resources](http://code.google.com/p/grails-jaxrs/issues/detail?id=43)
  * [Transaction boundaries in generated resource code](http://code.google.com/p/grails-jaxrs/issues/detail?id=48)
  * [Support deep object conversion in domain object providers](http://code.google.com/p/grails-jaxrs/issues/detail?id=49)


### Fixes ###
  * [Support for alphanumeric domain object identifiers](http://code.google.com/p/grails-jaxrs/issues/detail?id=42)
  * [POST method doesn't work](http://code.google.com/p/grails-jaxrs/issues/detail?id=45)
  * [java.lang.IllegalStateException: getOutputStream() has already been called for this response](http://code.google.com/p/grails-jaxrs/issues/detail?id=51)