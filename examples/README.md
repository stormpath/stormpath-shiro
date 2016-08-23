Stormpath Shiro Examples
========================

The following examples will help show common usages of Stormpath's Apache Shiro integration.

If you are unfamiliar with Apache Shiro, you may want to start with the [10 minute tutorial](http://shiro.apache.org/10-minute-tutorial.html)

[quickstart](./quickstart/README.md) - A basic example for configuring Stormpath's Realm via a shiro.ini file.
[servlet](./servlet/README.md) - A war based example, that uses out of the box UI for login and registering users.
[spring-boot](./spring-boot/README.md) - A spring-boot cli example using which requires minimal configuration.
[spring-boot-web](./spring-boot-web/README.md) - A web based example to get you started using Stormpath + Shiro + Spring Boot.


All of these examples require you to configure your Stormpath API Key info and your application href (only if you have more then one application).
If you do not already have a Stormpath account (free) or API Key, visit [this page](http://docs.stormpath.com/java/quickstart/#get-an-api-key) for more instructions.

You can either save your apiKey.properties to the default location of `$HOME/.stormpath/apiKey.properties` (recomended), or set them \
as java system properties (`stormpath.client.apiKey.id` and `stormpath.client.apiKey.secret`) or environment variables (`STORMPATH_CLIENT_APIKEY_ID` and `STORMPATH_CLIENT_APIKEY_SECRET`).

Your application href can also be a java system property (`stormpath.application.href`) or environment variable (`STORMPATH_APPLICATION_HREF`).

When using `stormpath-shiro-servlet-plugin` any of these properties can also be set in your `shiro.ini` file in the `[stormpath]` section.