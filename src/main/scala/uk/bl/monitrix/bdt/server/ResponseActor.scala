package uk.bl.monitrix.bdt.server

import akka.actor.Actor
import akka.event.Logging
import com.rabbitmq.client.QueueingConsumer
import scala.concurrent.Future
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Seq

class ResponseActor(consumer: QueueingConsumer) extends Actor {

  import context.dispatcher
  
  val log = Logging(context.system, this)
  
  var requestLog: ListBuffer[String] = ListBuffer.empty[String]
  
  override def preStart() {
    val infiniteLoop = Future {
      try {
	    while (true) {
	      val delivery = consumer.nextDelivery
	      val reply = new String(delivery.getBody)
	      requestLog.append(reply)
	      log.info("Got reply: " + reply)
	    }
      } catch {
        case t: Throwable => {
          t.printStackTrace
          log.info("ResponseActor loop terminated")
        }
      }
    }
  }
    
  def receive = {
    case "logs" => {
    	sender ! requestLog.toList
    }
    
    case msg => log.warning("Received message: " + msg)
  }

}