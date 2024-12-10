import com.phidget22._
import org.json._
import scala.concurrent._
import scala.util.{Try, Success, Failure}
import java.time.{LocalDateTime. Instant}
import akka.actor.{Actor, ActorSystem, Props}

import ExecutionContext.Implicits.global



/**
  * this class handles all the data generation and communication to the server
  */
class WorkoutDataManagement {



    val rfidReader = new RFID
    rfidReader.open(PhidgetBase.DEFAULT_TIMEOUT) 



    val appEnv = Option(System.getenv("IP_MQTT")).getOrElse("localhost")
    val brokerUrl = s"tcp://$appEnv:1883"
    val clientId = "AkkaMqttClient"
    val topic = "basic_frite/machine/0/data"

    val system = ActorSystem("MqttSystem")
    val mqttActor = system.actorOf(Props(new MqttActor(brokerUrl, clientId)), "mqttActor")


    /**
      * we have 3 types of data we send to the server
      * START: a user has just started a workout set on the machine
      * DATA: values of that user using the machine
      * END: user has just finished his set on the machine
      * 
      * format and examples of these messages can be found in jsonexamples.json in the archive folder
      */

    def createStartJson(weight: Int): Try[JSONObject] = {
        var userTag = Try(rfidReader.getLastTag)

        userTag match {
            case Success(tag) => {
                var startJSON = new JSONObject
                startJSON.put("type", "START")
                startJSON.put("user", tag.tagString)
                startJSON.put("time", Instant.now.toString)
                startJSON.put("weight", weight)
                Success(startJSON)
            }
            case Failure(e) => {
                Failure(e)
            }
        }
    }



    def createDataJson(distance: Int, timerValue: Float): Try[JSONObject] = {
        var userTag = Try(rfidReader.getLastTag)

        userTag match {
            case Success(tag) => {
                var dataJSON = new JSONObject
                dataJSON.put("type", "DATA")
                dataJSON.put("user", tag.tagString)
                dataJSON.put("distance", distance)
                dataJSON.put("timer", timerValue)
                Success(dataJSON)
            }
            case Failure(e) => {
                Failure(e)
            }
        }
    }



    def createEndJson(reps: Int): Try[JSONObject] = {
        var userTag = Try(rfidReader.getLastTag)

        userTag match {
            case Success(tag) => {
                var endJSON = new JSONObject
                endJSON.put("type", "END")
                endJSON.put("user", tag.tagString)
                endJSON.put("reps", reps)
                endJSON.put("time", Instant.now.toString)
                Success(endJSON)
            }
            case Failure(e) => {
                Failure(e)
            }
        }
    }



    def sendJSON(json: Try[JSONObject]): Future[scala.Unit] = Future {
        json match {
            case Success(json) => {
                println(json.toString)
                mqttActor ! Publish(topic, json.toString)
            }
            case Failure(e) => {
                // println("no RFID registered")
                ()
            }
        }
    }
}
