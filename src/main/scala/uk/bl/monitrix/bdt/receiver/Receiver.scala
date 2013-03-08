package uk.bl.monitrix.bdt.receiver

import com.rabbitmq.client.ConnectionFactory
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import com.rabbitmq.client.QueueingConsumer
import akka.actor.PoisonPill
import uk.bl.monitrix.bdt.Constants

/**
 * The Receiver does nothing but start a pool of PingActors.
 */
class Receiver(hostname: String, numberOfActors: Int) {

  val factory = new ConnectionFactory
  factory.setHost(hostname)
  
  val connection = factory.newConnection
  
  val system = ActorSystem()
  val actors = Seq.range(0, numberOfActors).map(index => 
    system.actorOf(Props(new PingActor(connection, Constants.FORWARD_QUEUE_NAME)), "actor-" + index))
  
  def stop() {
    actors.foreach(actorRef => {
      actorRef.tell(PoisonPill)
    })
  }
  
}

/**
 * A simple App to start the Receiver. Takes two command-line arguments:
 * 1. the hostname to bind to (default = localhost)
 * 2. the number of actors to spawn (default = 5)
 */
object Receiver extends App {
  
  val hostname = if (args.length > 0) args(0) else "localhost"   
  println("Binding to " + hostname)

  val numberOfActors = if (args.length > 1) args(1).toInt else 5
  println("Starting " + numberOfActors + " actors")
  
  val r = new Receiver(hostname, numberOfActors)
  
  println("Hit any key to stop")
  readLine
  r.stop
  println("Stopping.")

}