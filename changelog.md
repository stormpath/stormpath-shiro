## Change Log ##

### 0.6.0

- Upgraded Shiro dependency to latest stable release of 1.2.3
- Upgraded Stormpath SDK dependency to latest released version: 1.0.RC2
- [Issue 6](https://github.com/stormpath/stormpath-shiro/issues/6): Fixed bug that prevented Authentication data to be removed from cache after a successful logout.

### 0.5.0

- Upgraded Stormpath SDK dependency to latest stable release of 0.9.1
- Added Permission support!  It is now possible to assign Shiro permissions to Stormpath Accounts or Groups by leveraging Stormpath's newly released [CustomData](http://docs.stormpath.com/rest/product-guide/#custom-data) feature.  You can add and remove permission to an Account or Group by modifying that account or group's CustomData resource.  For example:

```java
Account account = getAccount(); //lookup account

//edit the permisssions assigned to the Account:
new CustomDataPermissionsEditor(account.getCustomData())
    .append("user:1234:edit")
    .append("document:*")
    .remove("printer:*:print");

//persist the account's permission changes:
account.save();
```

The same `CustomDataPermissionsEditor` can be used to assign permissions to Groups as well, and assumes 'transitive association': any permissions assigned to a Group are also 'inherited' to the Accounts in the Group.

In other words, an account's total assigned permissions are any permissions assigned directly to the account, plus, all of the permissions assigned to any Group that contains the account.

The `CustomDataPermissionsEditor` will save the permissions as a JSON list in the CustomData resource, under the default `apacheShiroPermissions` field name, for example:

```json
{
    ... any other of your own custom data properties ...,

    "apacheShiroPermissions": [
        "perm1",
        "perm2",
        ...,
        "permN"
    ]
}
```
If you would like to change the default field name, you can call the `setFieldName` method:

```java
new CustomDataPermissionsEditor(account.getCustomData())
    .setFieldName("whateverYouWantHere")
    .append("user:1234:edit")
    .append("document:*")
    .remove("printer:*:print");
```

But you'll also need to update your `ApplicationRealm`'s configuration to reflect the new name so it can function - the realm reads the same `CustomData` field, so they must be identical to ensure both read and write scenarios access the same field.  For example, if using `shiro.ini`:

    stormpathRealm.groupPermissionResolver.customDataFieldName = whateverYouWantHere
    stormpathRealm.accountPermissionResolver.customDataFieldName = whateverYouWantHere

- The `ApplicationRealm` implementation now has a default `groupPermissionResolver` and `accountPermissionResolver` properties that leverage respective group or account `CustomData` to support permissions as described above.  Prior to this 0.5.0 release, there were no default implementations of these properties - you had to implement the interfaces yourself to support permissions.  Now Permissions are built in by default (although you could still provide your own custom implementations if you have custom needs of course).

### 0.4.0

- Upgraded Stormpath SDK dependency to latest stable release of 0.8.1
- Added CacheManager/Cache bridging support.  This allows the Stormpath SDK to use the same caching mechanism that you're already using for Shiro, simplifying cache configuration/setup.  For example:

```ini
[main]

cacheManager = my.shiro.CacheManagerImplementation
securityManager.cacheManager = $cacheManager

# Stormpath integration:
stormpathClient = com.stormpath.shiro.client.ClientFactory
# etc...
stormpathClient.cacheManager = $cacheManager
```

If for some reason you *don't* want the Stormpath SDK to use Shiro's caching mechanism, you can configure the `stormpathCacheManager` property (instead of the expected Shiro-specific `cacheManager` property), which accepts a `com.stormpath.sdk.cache.CacheManager` instance instead:

```ini
# ...
stormpathCacheManager = my.com.stormpath.sdk.cache.CacheManagerImplementation
# etc...
stormpathClient.stormpathCacheManager = $stormpathCacheManager
```
But note this approach requires you to set-up/configure two separate caching mechanisms.

See ClientFactory `setCacheManager` and `setStormpathCacheManager` JavaDoc for more.

### 0.3.1

0.3.1 is a minor dependency fix: the Stormpath Java SDK dependency has been upgraded to reflect its latest 0.8.0 release.  This is the only change - no additional features/changes have been made otherwise.