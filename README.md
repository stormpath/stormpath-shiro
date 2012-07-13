# Stormpath Shiro Integration #

The `stormpath-shiro` project allows an [Apache Shiro](http://shiro.apache.org)-enabled application
use the [Stormpath](http://www.stormpath.com) cloud Identity Management service for all authentication and
access control needs.

Pairing Shiro with Stormpath gives you a full application security system complete with immediate user account
support, authentication, account registration and password reset workflows, password security and more -
with little to no coding on your part.

## Usage ##

1. Add the stormpath-shiro .jars to your application using Maven, Ant+Ivy, Grails, SBT or whatever
   maven-compatible tool you prefer:
    <dependency>
        <groupId>com.stormpath.shiro</groupId>
        <artifactId>stormpath-shiro-core</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.stormpath.sdk</groupId>
        <artifactId>stormpath-sdk-httpclient</artifactId>
        <version>0.3.0</version>
        <scope>runtime</scope>
    </dependency>
2. Ensure you [have an API Key](http://www.stormpath.com/docs/quickstart/connect) so your application can communicate
   with Stormpath.  Store your API Key file somewhere secure (readable only by you), for example:
    /home/myhomedir/.stormpath/apiKey.properties
3. Configure `shiro.ini` with the Stormpath `ApplicationRealm`:
    [main]
    ...
    stormpathClient = com.stormpath.shiro.client.ClientFactory
    # Replace this value with the file location from #2 above:
    stormpathClient.builder.apiKeyFileLocation = /home/myhomedir/.stormpath/apiKey.properties

    stormpathRealm = com.stormpath.shiro.realm.ApplicationRealm
    stormpathRealm.client = $stormpathClient
    stormpathRealm.applicationRestUrl = REPLACE_ME_WITH_YOUR_STORMPATH_APP_REST_URL

    securityManager.realm = $stormpathRealm
4. Replace the `stormpathRealm.applicationRestUrl` value above with your
   [Application's Stormpath-specific REST URL](http://www.stormpath.com/docs/libraries/application-rest-url), for
   example:

    stormpathRealm.applicationRestUrl = https://api.stormpath.com/v1/applications/someRandomIdHereReplaceMe

And you're done!

Add, remove, enable users in Stormpath and use them to log in to your application immediately!




