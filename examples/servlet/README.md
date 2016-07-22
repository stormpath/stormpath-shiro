Stormpath Shiro Servlet Example
===============================

This simple example is based on Apache Shiro's `web` example, and serves as a starting point to introduce the `stormpath-shiro-servlet` and `stormpath-shiro-servlet-plugin` modules.

All that is needed to run this example is your Stormpath application href, and api key information.

You can either save your apiKey.properties to the default location of `$HOME/.stormpath/apiKey.properties` (recomended), or set them \
as java system properties (`stormpath.client.apiKey.id` and `stormpath.client.apiKey.secret`) or environment variables (`STORMPATH_CLIENT_APIKEY_ID` and `STORMPATH_CLIENT_APIKEY_SECRET`).

Your application href can also be a java system property (`stormpath.application.href`) or environment variable (`STORMPATH_APPLICATION_HREF`).
 
Any of these properties can also be set in your `shiro.ini` file in the `[stormpath]` section.

To run the example, use the following Apache Maven command:
```
mvn jetty:run -Dstormpath.application.href=https://api.stormpath.com/v1/applications/<YOUR_APPLICAION_ID>
```