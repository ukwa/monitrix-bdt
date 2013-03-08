# monitrix: Block Detection Tool

## Getting Started

montrix-bdt is built with [Gradle](http://www.gradle.org/)

* Type `gradle eclipse` to generate an Eclipse project
* __Temporary:__ launch `uk.bl.monitrix.bdt.server.Frontend` to fire up the HTTP frontend in an embedded Jetty.
* __Temporary:__ launch `uk.bl.monitrix.bdt.receiver.Receiver` to fire up a pool of worker actors. Configure the 
  number of actors to use in parallel via the `NUMBER_OF_ACTORS` variable.
* You can pass URLs to 'ping' to the HTTP frontend like so: `curl http://localhost:8080/ping/http%3A%2F%2Fwww.example.com`

## RabbitMQ Cheat Sheet

* Enabling RabbitMQ Management Plugin: `rabbitmq-plugins enable rabbitmq_management` ([Details](http://www.rabbitmq.com/management.html)).
  The Web UI is located at [http://127.0.0.1:55672/](http://127.0.0.1:55672/).
