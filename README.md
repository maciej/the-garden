The Garden
==========

Scala common code

We're opinionated. We use certain libraries and avoid others. What we use?

* tests -- [ScalaTest](http://www.scalatest.org/)
* date, time -- [Joda Time](http://www.joda.org/joda-time/)
...

Shutdownables
------------------------

It is often the case that some of your application services will acquire non-memory resources that need to be
shutdown at a certain point of running the application (either at termination or when leaving a scope).
`shutdownables` package tries to address this.
It works best with [MacWire](https://github.com/adamw/macwire) type modules.

In order to add shutdownables support to your application module mixin `DefaultShutdownHandlerModule` into your
modules and add `onShutdown` after the wiring.

````scala
trait UserModule extends MacWire with DefaultShutdownHandlerModule {

    lazy val service : UserService = wire[UserService] onShutdown {
        _.shutdownNow()
    }
    
}
````

If the `UserService` implements the `Shutdownable` interface you can add `withShutdownHandled()` instead of `onShutdown`.

If you want to register a Runtime Shutdown hook mixin `ShutdownOnJVMTermination` trait as well.
