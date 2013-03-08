package uk.bl.monitrix.bdt.server

import unfiltered.filter.Plan
import unfiltered.request.{GET, Path}
import unfiltered.response.{PlainTextContent, ResponseString}
import unfiltered.jetty.Http
import unfiltered.response.ResponseString
import unfiltered.request.Seg
import java.net.URLDecoder

class Frontend(messageQueue: MessageQueue) extends Plan {
  
  def intent = {
    
    case GET(Path("/")) =>
      PlainTextContent ~> ResponseString("Nothing to see here. Move along.")
      
    case GET(Path(Seg("ping" :: url :: Nil))) => {
      // TODO verify/sanitize the URL
      val unescaped = URLDecoder.decode(url)
      messageQueue.publishURL(unescaped)
      PlainTextContent ~> ResponseString("Queueing URL " + unescaped)      
    }
  }
}
 
object Frontend {
  def main(args: Array[String]) {
    // Get a handle on the message queue
    val messageQueue = new MessageQueue("62.218.164.156", "ukwa_block_detection")
    
    // Start the frontend in an embedded Jetty
    Http.local(80).filter(new Frontend(messageQueue)).run
  }
}