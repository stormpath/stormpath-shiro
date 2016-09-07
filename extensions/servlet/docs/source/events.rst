.. _events:

Events
======

The |project| will trigger events when interesting things happen.  You can listen for these events and implement custom behavior when desired.

Subscribe to Events
-------------------

Subscribing to events simply requires annotating a one or more methods with `@Subscribe <http://shiro.apache.org/static/current/apidocs/org/apache/shiro/event/Subscribe.html>`_
and adding the class to the ``[main]`` section of your ``shiro.ini``.  This method must only contain a single parameter of the event type you are listening for.
For example, the configuration to listen for the ``SuccessfulAuthenticationRequestEvent`` would look like this:

The event listner class:

.. code-block:: java

    public class MyEventListener {
        @Subscribe
        public void onEvent(SuccessfulAuthenticationRequestEvent event) {
            // Do something with event
        }
    }

Your shiro.ini:

.. code-block:: ini

    ...
    [main]
    myListener = com.mybiz.MyEventListener

Events
------

The events currently published by the plugin are:

======================================== ==============================================================================
Event Class                              Published when processing an HTTP request that:
======================================== ==============================================================================
``SuccessfulAuthenticationRequestEvent`` successfully authenticates an ``Account``
``FailedAuthenticationRequestEvent``     attempts to authenticate an ``Account`` but the authentication attempt failed
``RegisteredAccountRequestEvent``        results in a newly registered ``Account``.  If the newly registered account
                                         requires email verification before it can login,
                                         ``event.getAccount().getStatus() == AccountStatus.UNDEFINED`` will be ``true``
``VerifiedAccountRequestEvent``          verifies an account's email address.  The event's associated account is
                                         considered verified and may login to the application.
``LogoutRequestEvent``                   will logout the request's associated ``Account``.  After the request is
                                         complete, the account will be logged out.
======================================== ==============================================================================

Listener Best Practices
-----------------------

Events are sent and consumed *synchronously* during the HTTP request that triggers them.

To ensure requests are responded to quickly, ensure your event listener methods return quickly or dispatch work asynchronously to another thread or ``ExecutorService`` (for example).