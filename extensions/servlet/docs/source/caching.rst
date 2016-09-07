.. _caching:

Caching
=======

The |project| delegates to an SDK Client, which supports caching to reduce round-trips to the Stormpath API servers and to improve performance.

The plugin enables a single (non-clustered) in-process memory cache for the SDK Client by default.  This behavior is relevant for web applications that are deployed to a single JVM only.  If your web application is deployed on multiple web hosts/nodes simultaneously (i.e. a striped or clustered application), then you will likely want to :ref:`enable a shared cache <shared cache>` instead to ensure that cached data remains coherent across all web application nodes.


Caching Options
---------------

The Stormpath SDK Client by default is configured to use `Stormpath's cache manager`_.  You can also configure the client to use `Apache Shiro's cache manager`_ instead by adding following lines to your shiro.ini.

.. code-block:: ini

    ...
    [main]
    # this could be any Shiro CacheManager implementation.
    cacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager

    securityManager.cacheManager = $cacheManager
    stormpathClient.cacheManager = $cacheManager

.. _shared cache:

Shared Cache
------------

In the example above each web application instance would have its *own* private in-process cache as described above.

However, if your web application .war is deployed on multiple JVMs - for example, you load balance requests across multiple identical web application nodes - you may experience data cache inconsistency problems if the default cache remains enabled: separate private cache instances are often not desirable because each web app instance could see its own 'version' of the cached data.

For example, if a user sends a request that is directed to web app instance A and then a subsequent request is directed to web app instance B, and the two instances do not agree on the same cached data, this could cause data integrity problems in many applications. This can be solved by using a shared or distributed cache to ensure cache consistency, also known as `cache coherence`_.

If you need cache coherency, you will want to specify a cache manager implementation that can communicate with a shared or distributed cache system, like `Hazelcast`_, `Ehcache`_, etc.

You can do this by specifying the ``stormpath.client.cacheManager`` configuration property, for example:

.. code-block:: properties

   stormpath.client.cacheManager = your.fully.qualified.implementation.of.CacheManager


.. _cache coherence: http://en.wikipedia.org/wiki/Cache_coherence
.. _Stormpath's cache manager: https://docs.stormpath.com/java/servlet-plugin/caching.html
.. _Apache Shiro's cache manager: http://shiro.apache.org/caching.html
.. _Hazelcast: https://stormpath.com/blog/hazelcast-support-apache-shiro
.. _Ehcache: http://shiro.apache.org/session-management.html#SessionManagement-EhcacheTerracotta
