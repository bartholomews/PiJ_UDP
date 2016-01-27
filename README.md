# UDP
BBK Programming in Java 2014/15 - Assignment 5

It is necessary to run the Client(s) with security.policy in order to make the multicastSocket work
(i.e. being able to receive packets).
On IntelliJ it doesn't seem to work, even editing "VM Options" for runtime configuration
(adding `-Djava.security.policy on Run/Edit_Configurations/VM options`).

It is tested successfully with command line, but still you need to add the security.policy in runtime configuration.

Also on OS X it is possible to get a `java.netSocketException: Can't assign requested address`
for a RECEIVER Client trying to join the MulticastSocket.
This might be resolved either starting JVM with `-Djava.net.preferIPv4Stack=true`
or turning off the wireless (solution found on [this stackoverflow answer][1])

So,
to start the Server: `java.main.Server` (it works from an IDE - tested with IntelliJ - as well as from command line)
to start the Client(s): `java -Djava.security.policy=../client.policy main.ClientLauncher` (from command line)
OS X it might need: `java -Djava.net.preferIPv4Stack=true -Djava.security.policy=../client.policy main.ClientLauncher`
(or wireless off)

[1]: http://stackoverflow.com/questions/18747134/getting-cant-assign-requested-address-java-net-socketexception-using-ehcache
