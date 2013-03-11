# monitrix: Blocking Detection Tool

monitrix-bdt is a tool in the spirit of [Down For Everyone Or Just Me](http://www.downforeveryoneorjustme.com/). If you 
encounter a URL that is blocked from your location, you can post this URL to monitrix-bdt, and it will try to reach the URL
from a second (physically separated) server. If the URL is not reachable for montrix-bdt either, there's a good chance
the URL is actually down. Otherwise, it's likely that your location is being blocked by the host.

The montrix-bdt project consists of two parts:

* A simple __HTTP frontend__, which allows you to submit a URL for blocking detection.
* A __worker app__ which carries out the actual blocking detection using a pool of actors.

Frontend and Worker App communicate via RabbitMQ. Typically, you would install frontend and RabbitMQ at your institution,
while the worker App can sit somewhere in the cloud.

## Getting Started

montrix-bdt is built with [SBT](http://www.scala-sbt.org/). Use `sbt eclipse` to create an Eclipse project. 

To launch the frontend, use the following command:

    sbt "run-main uk.bl.monitrix.bdt.server.Frontend localhost 9080 100"
   
The last three parameters are optional. They define the host name (or IP address) of the RabbitMQ server (default = localhost),
the HTTP port to bind the frontend to (default = 8080) and the number of log lines to maintain in memory (default = 20).

To launch the worker app, use the following command

    sbt "run-main uk.bl.monitrix.bdt.receiver.Receiver localhost 200"
    
The last two parameters are optional. They define the host name (or IP address) of the RabbitMQ server (default = localhost),
and the number of worker actors to start.

* You can pass URLs to 'ping' to the HTTP frontend like so: [http://localhost:8080/ping/http%3A%2F%2Fwww.example.com](http://localhost:8080/ping/http%3A%2F%2Fwww.example.com)
* You can get the log of the last N pings like so: [http://localhost:8080/log](http://localhost:8080/log)

## RabbitMQ Cheat Sheet

* Enabling RabbitMQ Management Plugin: `rabbitmq-plugins enable rabbitmq_management` ([Details](http://www.rabbitmq.com/management.html)).
  The Web UI is located at [http://127.0.0.1:55672/](http://127.0.0.1:55672/).
