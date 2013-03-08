package uk.bl.monitrix.bdt.server

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer
import java.util.UUID
import com.rabbitmq.client.AMQP.BasicProperties
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await

class MessageQueue(queueHost: String, queueName: String) {
  
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
  
  val factory = new ConnectionFactory
  factory.setHost(queueHost)
  
  val connection = factory.newConnection 
  
  val channel = connection.createChannel
  channel.queueDeclare(queueName, false, false, false, null)
  
  val replyQueueName = channel.queueDeclare.getQueue
  val consumer = new QueueingConsumer(channel)
  channel.basicConsume(replyQueueName, true, consumer)
  
  val responseActor = ActorSystem().actorOf(Props(new ResponseActor(consumer)), "response-actor")
  
  def publishURL(url: String) = {    
    val props = new BasicProperties.Builder()
      .replyTo(replyQueueName)
      .build
      
    channel.basicPublish("", queueName, props, url.getBytes) 
  }
  
  def getLog: List[String]  = {
    implicit val timeout = Timeout(5000)
    val future = { ask( responseActor, "logs") }
    Await.result(future, timeout.duration).asInstanceOf[List[String]]
  }
  
  def close = {
    channel.close
    connection.close
  }

}