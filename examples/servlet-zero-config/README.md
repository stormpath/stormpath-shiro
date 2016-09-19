Stormpath Shiro Servlet Example
===============================

This examples differs slightly from the [servlet](../servlet/README.md) in that a `shiro.ini` file is NOT used, only the `stormpath-shiro-servlet-plugin` dependency is added.

```xml
<dependency>
    <groupId>com.stormpath.shiro</groupId>
    <artifactId>stormpath-shiro-servlet-plugin</artifactId>
</dependency>
```

By default all paths (i.e. '/**') require a logged in user. 

If your Stormpath apiKey.properties file is not already setup, start [here](../README.md).

To run the example, use the following Apache Maven command:
```
mvn jetty:run
```