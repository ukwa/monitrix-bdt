package uk.bl.monitrix.bdt.server

import unfiltered.filter.Plan
import unfiltered.request.{GET, Path}
import unfiltered.response.{PlainTextContent, ResponseString}
import unfiltered.jetty.Http
import unfiltered.response.ResponseString
import unfiltered.request.Seg
import java.net.URLDecoder
import scala.util.Properties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A simple HTTP frontend that accepts request in the form
 * 
 * http://<host>/ping/<URL-encoded-URI-to-ping>
 * 
 * The frontend exposes the 'logs' of the most recent ping requests at
 * 
 * http://<host>/log
 * 
 */
class Frontend(messageQueue: MessageQueue, maxLogSize: Int) extends Plan {
  
  def intent = { 
    case GET(Path("/")) =>
      PlainTextContent ~> ResponseString("Nothing to see here. Move along.")
      
    case GET(Path(Seg("ping" :: url :: Nil))) => {
      // TODO verify/sanitize the URL
      val unescaped = URLDecoder.decode(url)
      messageQueue.publishURL(unescaped)
      PlainTextContent ~> ResponseString("Queueing URL " + unescaped)      
    }
    
    case GET(Path("/log")) => {
      PlainTextContent ~> ResponseString(messageQueue.getLog.mkString("\n"))
    }
  }
}

/**
 * A simple App to start the HTTP frontend. Takes three command-line arguments:
 * 1. the hostname to bind to (default = localhost)
 * 2. the port number for the HTTP process (default = 8080)
 * 3. the size of the ping log to maintain in memory (default = 20)
 */
object Frontend extends App {  
  
  val hostname = if (args.length > 0) args(0) else "localhost"   
  println("Binding to " + hostname)
  
  val port = if (args.length > 1) args(1).toInt else 8080
  println("Starting on port " + port)
  
  val maxLogSize = if (args.length > 2) args(2).toInt else 20
  println("Log size is " + maxLogSize)
  
  val messageQueue = new MessageQueue(hostname, maxLogSize)  
  Http.local(port).filter(new Frontend(messageQueue, maxLogSize)).run
  
}