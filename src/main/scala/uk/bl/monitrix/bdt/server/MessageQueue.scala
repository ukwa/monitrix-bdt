package uk.bl.monitrix.bdt.server

import com.rabbitmq.client.ConnectionFactory

class MessageQueue(queueHost: String, queueName: String) {
  
  val factory = new ConnectionFactory
  factory.setHost(queueHost)
  
  val connection = factory.newConnection
  
  val channel = connection.createChannel
  channel.queueDeclare(queueName, false, false, false, null)
  
  def publishURL(url: String) = channel.basicPublish("", queueName, null, url.getBytes)
  
  def close = {
    channel.close
    connection.close
  }

}