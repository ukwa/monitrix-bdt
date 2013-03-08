package uk.bl.monitrix.bdt.receiver

import akka.actor.Actor
import akka.event.Logging
import akka.dispatch.Futures
import com.rabbitmq.client.{Channel, Connection, QueueingConsumer}
import dispatch.Http
import scala.concurrent.{ExecutionContext, Future}
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * The PingActor sequentially picks messages from the message queue and 'pings' the 
 * URL contained in each message. After the HTTP request is done, the actor
 * picks the next message from the queue.
 * 
 * The actor will start automatically, will ignore all messages sent to it, and
 * continue until it is terminated from the outside.
 */
class PingActor(connection: Connection, queueName: String ) extends Actor {

  import context.dispatcher
  
  val log = Logging(context.system, this)
  
  val channel = connection.createChannel
  channel.basicQos(1)
  channel.queueDeclare(queueName, false, false, false, null)
  
  val consumer = new QueueingConsumer(channel)
  channel.basicConsume(queueName, true, consumer)
  
  override def preStart() {
    val infiniteLoop = Future {
      try {
	    while (true) {
	      val delivery = consumer.nextDelivery
	      val message = new String(delivery.getBody)
	      
	      val startTime = System.currentTimeMillis
	      val request = dispatch.url(message)
	      Http(request).onSuccess {
	        case r =>  {
	          val response = "HTTP " + r.getStatusCode()  + " -- " + (System.currentTimeMillis - startTime) + "ms -- " + message
	          channel.basicPublish("", delivery.getProperties.getReplyTo, null, response.getBytes)
	          log.info(response)
	        }
	      }
	    }
      } catch {
        case t: Throwable => {
          t.printStackTrace
          log.info("PingActor loop terminated")
        }
      }
    }
  }
    
  def receive = {
    case msg => log.warning("Received message: " + msg)
  }
  
  override def postStop() = channel.close

}