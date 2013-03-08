package uk.bl.monitrix.bdt.receiver

import com.rabbitmq.client.ConnectionFactory
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import com.rabbitmq.client.QueueingConsumer
import akka.actor.PoisonPill

class Receiver(numberOfActors: Int) {

  val factory = new ConnectionFactory
  factory.setHost("62.218.164.156")
  
  val connection = factory.newConnection
  
  val system = ActorSystem()
  val actors = Seq.range(0, numberOfActors).map(index => 
    system.actorOf(Props(new PingActor(connection, "ukwa_block_detection")), "actor-" + index))
  
  def stop() {
    actors.foreach(actorRef => {
      actorRef.tell(PoisonPill)
    })
  }
  
}

object Main extends App {
  
  val NUMBER_OF_ACTORS = 5
   
  println("Starting " + NUMBER_OF_ACTORS + " actors")
  val r = new Receiver(NUMBER_OF_ACTORS)
  println("Hit any key to stop")
  
  readLine
  r.stop
  println("Stopping.")
  
}