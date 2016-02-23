

# Prerequisites #

Using the grails-jaxrs plugin requires a Grails 1.1.1 (or higher) installation. For instructions how to download and install Grails refer to the [Grails reference documentation section 2.1](http://grails.org/doc/1.1.1/guide/2.%20Getting%20Started.html#2.1%20Downloading%20and%20Installing). The following table shows which grails-jaxrs version to use for which Grails version


| **Grails version** | **grails-jaxrs version** |
|:-------------------|:-------------------------|
|1.1.1 or higher 1.x |0.1 - 0.3                 |
|1.2.0 or higher 1.x |0.4                       |
|1.3.7 or higher 1.x |0.5-m1                    |
|2.0.0 or higher 2.x |0.6                       |
|2.1.1 or higher 2.1.x (2.2.x will be supported soon)|0.7                       |
|2.0.0 or higher 2.1.x (2.2.x will be supported soon)|0.8 development snapshot  |

  * The latest stable version is 0.7 and can be installed [from the Grails plugin repository](#From_the_Grails_plugin_repository.md)
  * The latest development version is 0.8 and can be obtained by [checking out the sources and building the plugin](#Sources.md). The plugin must then be [installed manually](#From_a_local_plugin_binary.md).

# Getting the plugin #

**Note:** If you want to install the plugin directly from the [Grails Plugin Repository](http://www.grails.org/plugin/home) you can skip this section and go directly to the section [Installing the plugin](#Installing_the_plugin.md).

## Binaries ##

The plugin binaries can be downloaded from the project's [download section](http://code.google.com/p/grails-jaxrs/downloads/list).

## Sources ##

If you want to build the plugin from its sources clone the [grails-jaxrs repository](http://github.com/krasserm/grails-jaxrs) with

```
git clone git://github.com/krasserm/grails-jaxrs.git
```

and change to the created `grails-jaxrs` directory. Optionally, run the unit and integration tests by entering

```
grails test-app
```

on the command line. To create a binary package of the plugin enter

```
grails package-plugin
```

The filename of the created plugin is `grails-jaxrs-<version>.zip` where `<version>` is the current development version. To build older versions of the plugin from its sources use one of the [grails-jaxrs tags](http://grails-jaxrs.googlecode.com/svn/tags).

# Installing the plugin #

If you want to use the grails-jaxrs plugin in a Grails application you need to install it first. The plugin can be either be installed directly from the Grails plugin repository or from a local package that has been download before or built from the sources.

## From the Grails plugin repository ##

To install the plugin from the Grails plugin repository go to your project's root directory and enter

```
grails install-plugin jaxrs
```

This will install version 0.7.

## From a local plugin binary ##

If you downloaded or built the plugin as described in [Getting the plugin](#Getting_the_plugin.md) go to your project's root directory and enter

```
grails install-plugin /path/to/grails-jaxrs-<version>.zip
```

where `/path/to` needs to be replaced by the absolute or relative path to the `grails-jaxrs-<version>.zip` file.