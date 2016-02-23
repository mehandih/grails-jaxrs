# Release notes for grails-jaxrs version 0.2 #

In version 0.2 the way how JAX-RS resources and providers are auto-detected has changed. In version 0.1 plain Spring mechanisms were used (`<context:component-scan />`, `@Component` annotation, etc.) whereas in version 0.2 the plugin follows Grails conventions how to detect and manage resources. There's no need any more to provide a custom Spring application context. The grails-jaxrs plugin is now making these changes behind the scenes.

Similar changes have been introduced for injection of other beans into JAX-RS resources and providers. In version 0.1 plain Spring mechanisms were necessary such as the `@Autowired` annotation whereas in version 0.2 other beans are auto-injected by name as you know it from Grails controllers, for example.

The easiest way to get familiar with these changes is to work through the [getting started guide](GettingStarted.md).