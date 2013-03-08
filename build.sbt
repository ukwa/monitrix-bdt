import com.typesafe.sbt.SbtStartScript

name := "monitrix-bdt"

version := "0.1"

scalaVersion := "2.10.0"

seq(SbtStartScript.startScriptForClassesSettings: _*)

mainClass in (Compile, run) := Some("uk.bl.monitrix.bdt.server.Frontend")

libraryDependencies ++= Seq(
	"com.rabbitmq" % "amqp-client" % "3.0.3",
	"com.typesafe.akka" % "akka-actor_2.10" % "2.1.1",
	"net.databinder.dispatch" % "dispatch-core_2.10" % "0.9.5",
	"net.databinder" % "unfiltered-filter_2.10" % "0.6.7",
	"net.databinder" % "unfiltered-jetty_2.10" % "0.6.7",
	"javax.servlet" % "servlet-api" % "2.5"
)