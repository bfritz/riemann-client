[![Build Status](https://travis-ci.org/mgodave/riemann-client.png)](https://travis-ci.org/mgodave/riemann-client)

About
-----

This is a simple Java client for [Riemann](https://github.com/aphyr/riemann). It is based on [Netty](http://netty.io) and uses
[Guava](http://code.google.com/p/guava-libraries/) futures for results. The code is also JSR305 compatible; I prefer to use Guice.

Sample Usage
------------

```java
package org.robobninjas.riemann;

import com.aphyr.riemann.Proto;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.Closeables.closeQuietly;
import static makeClient;

public class SampleClient {

  public static void main(String[] args) {

    final Client client = makeTcpClient("localhost");
    Connection connection = null;

    try {

      connection = client.makeConnection();
      final Future<Boolean> isOk = connection.sendEvent(
          Proto.Event
              .newBuilder()
              .setMetricF(1000000)
              .setService("thing")
              .build());

      isOk.get(1, TimeUnit.SECONDS);

    } catch (Throwable t) {
      propagate(t);
    } finally {
      closeQuietly(connection);
      client.shutdown();
    }

  }

}
```

Clients
-------

There are two client implementations, both of which implement the Client interface, these are the TcpClient 
and the UdpClient. Both clients have constructors which take a Netty channel factory, remote address, and port.
For simple usage, a Clients class with static factory methods is made available. By default the Clients factory
methods construct implementations which use old school blocking IO.

Guice
-----

Both of the client implementations have constructors annotated with the @Inject annotation. These constructors are
also make use the Guice's AssistedInject extension and annotate the appropriate constructor arguments.  In both cases
the approrpriate arguments are the address and port fields.


