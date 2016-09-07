.. _access-control:

Access Control
==============

The |project| uses both the `Stormpath Java Servlet Plugin <https://docs.stormpath.com/java/servlet-plugin/>`_ and `Apache Shiro's web <http://shiro.apache.org/web.html#Web-WebINIconfiguration>`_ module.
The Stormpath filters will execute before the Shiro filters, this is done to make use of Stormpath's login and registration workflows (including `Social Login <social.rst>`_ and ID Site), and still allow
defining Shiro's authentication an authorization filters to function as expected.

Authentication & Authorization
------------------------------

Web applications using Apache Shiro typically define access permissions in a ``[urls]`` section in a ``shiro.ini`` file.

.. code-block:: ini

    ...
    [urls]

    /index.html = anon
    /user/** = authc
    /admin/** = authc, roles[administrator]

In this example, anybody is able to view the ``index.html`` page, any logged in user can access all resources under ``/user/``, and finally any logged in user with the 'administrator' role
can access all resources under ``/admin/``.

For more details on this topic take a look at the `Apache Shiro Documentation <http://shiro.apache.org/web.html#Web-WebINIconfiguration>`_

Groups are Roles
----------------

Out of the box, groups assigned to a Stormpath account are mapped directly to Apache Shiro roles using the group's `href`.  This behavior is configurable, one or more of
the following modes can be used:

+------+--------------------------------------------+------------------------------------------------------------+
| Mode | Behavior                                   | Example                                                    |
+======+============================================+============================================================+
| HREF | Returns the Group's fully qualified HREF   | https://api.stormpath.com/v1/groups/upXiExAmPlEfA5L1G5ZaSQ |
|      | as the role name                           |                                                            |
+------+--------------------------------------------+------------------------------------------------------------+
| ID   | Returns Group's globally unique identifier | upXiExAmPlEfA5L1G5ZaSQ                                     |
|      | as the role name                           |                                                            |
+------+--------------------------------------------+------------------------------------------------------------+
| NAME | Returns Group's name as the role name      | administrators                                             |
+------+--------------------------------------------+------------------------------------------------------------+

This configuration can be set in the `main` section of your `shiro.ini`

.. code-block:: ini

    [main]
    ...
    stormpathRealm.groupRoleResolver.modeNames = href, id

In the above configuration, each Group translates into two Shiro role names: one role name is the raw href itself,
the other role name is the Group ID. You can specify one or more modes to translate into one or more role names respectively. Mode names are case-insensitive.

**NOTE:** Group Names, while easier to read in code, can change at any time via a REST API call or by using the Stormpath UI Console.
It is strongly recommended to use only the HREF or ID modes as these values will never change. Acquiring group names might also incur a REST server call, whereas the the HREF is guaranteed to be present.

Permissions as Custom User Data
===============================

Shiro `permission strings <http://shiro.apache.org/permissions.html>`_ can be stored via custom user data attached to a group or account.  Custom user data can be added
programmatically or by using the Stormpath Admin Console.

.. code-block:: java

   Account account = getAccount();
   new CustomDataPermissionsEditor(account.getCustomData())
       .append("someResourceType:anIdentifier:anAction")
       .append("anotherResourceType:anIdentifier:*")
       .remove("oldPermission");
   account.save();

In the above example the permission string 'oldPermission' is being removed while 'someResourceType:anIdentifier:anAction'
and 'anotherResourceType:anIdentifier:\*' are being added.

These permissions can then be used in your code by calling:

.. code-block:: java

    subject.isPermitted("someResourceType:anIdentifier:anAction")

Or mapped to a path in the `[urls]` section in your shiro.ini file:

.. code-block:: ini

    ...
    [urls]
    /application/path = perms["someResourceType:anIdentifier:anAction"]

