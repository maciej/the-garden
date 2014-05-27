The Garden
==========

Scala common code

We're opinionated. We use certain libraries and avoid others. What we use?

* tests -- [ScalaTest](http://www.scalatest.org/)
* date, time -- [Joda Time](http://www.joda.org/joda-time/)
...

Lifecycle (experimental)
------------------------

It is often the case that some of your application services will acquire non-memory resources that need to be
closed at a certain point of running the application (either at shutdown or when leaving a scope).
`lifecycle` package tries to address this.
It works best with [MacWire](https://github.com/adamw/macwire) type modules.

In order to add lifecycle support to your application module mixin `DefaultLifeCycleManagerModule` into your
modules and wrap creation of closeables resources with `withLifeCycle`.

````scala
trait UserModule extends MacWire with DefaultLifeCycleManagerModule {

    lazy val service : UserService = withLifeCycle(wire[UserService]) {
        transferManager => transferManager.shutdownNow()
    }
    
}
````

If the `UserService` implements `com.softwaremill.thegarden.lawn.lifecycle.Closeable` you can skip the close function block.
