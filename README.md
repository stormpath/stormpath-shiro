[![Build Status](https://api.travis-ci.org/stormpath/stormpath-shiro.png?branch=master)](https://travis-ci.org/stormpath/stormpath-shiro)

# Apache Shiro plugin for Stormpath #

Copyright &copy; 2013 Stormpath, Inc. and contributors. This project is open-source via the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).  

The `stormpath-shiro` plugin allows an [Apache Shiro](http://shiro.apache.org)-enabled application
use the [Stormpath](http://www.stormpath.com) User Management & Authentication service for all authentication and access control needs.

Pairing Shiro with Stormpath gives you a full application security system complete with immediate user account
support, authentication, account registration and password reset workflows, password security and more -
with little to no coding on your part.  

Usage documentation [is in the wiki](https://github.com/stormpath/stormpath-shiro/wiki).

### Build Instructions ###

This project requires Maven 3 to build.  Run the following from a command prompt:

`mvn install`

## Change Log

### 0.3.1

0.3.1 is a minor dependency fix: the Stormpath Java SDK dependency has been upgraded to reflect its latest 0.8.0 release.  This is the only change - no additional features/changes have been made otherwise.


## Change Log

### 0.3.1

0.3.1 is a minor dependency fix: the Stormpath Java SDK dependency has been upgraded to reflect its latest 0.8.0 release.  This is the only change - no additional features/changes have been made otherwise.

### 0.4.0

- Upgraded Stormpath SDK dependency to latest stable release of 0.8.1
- Added CacheManager/Cache bridging support.  This allows the Stormpath SDK to use the same caching mechanism that you're already using for Shiro, simplifying cache configuration/setup.  For example:

```ini
[main]

cacheManager = my.shiro.CacheManagerImplementation
securityManager.cacheManager = $cacheManager

# Stormpath integration:
stormpathClient = com.stormpath.shiro.client.ClientFactory
...
stormpathClient.cacheManager = $cacheManager
```

If for some reason you *don't* want the Stormpath SDK to use Shiro's caching mechanism, you can configure the `stormpathCacheManager` property (instead of the expected Shiro-specific `cacheManager` property), which accepts a `com.stormpath.sdk.cache.CacheManager` instance instead:

```
...
stormpathCacheManager = my.com.stormpath.sdk.cache.CacheManagerImplementation

...
stormpathClient.stormpathCacheManager = $stormpathCacheManager
```
But note this approach requires you to set-up/configure two separate caching mechanisms.

See ClientFactory `setCacheManager` and `setStormpathCacheManager` JavaDoc for more.







