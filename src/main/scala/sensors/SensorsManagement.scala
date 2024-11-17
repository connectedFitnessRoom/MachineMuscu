package sensors

import com.phidget22._
import scala.concurrent._
import java.util.{Scanner, Queue, LinkedList}
import rx.lang.scala.Observable

object SensorsManagement {
  // Distance sensor fetch
  val distancesensor0 = new DistanceSensor()
  distancesensor0.open(5000)
  distancesensor0.setDeviceSerialNumber(1)
  distancesensor0.setHubPort(1)
  distancesensor0.setChannel(0)
  distancesensor0.setDataRate(5)
  println("opened sensor 0")

  val distancesensor1  = new DistanceSensor()
  distancesensor1.open(5000)
  distancesensor1.setDeviceSerialNumber(0)
  distancesensor1.setHubPort(0)
  distancesensor1.setChannel(0)
  distancesensor1.setDataRate(5)
  println("opened sensor 1")
  
  
  
  var exercising: Boolean = false
  var reps: Int = 0
  var repCounted: Boolean = false



  val weight_step_size: Float = 100f
  val weight_bottom: Float = 20f
  var weight_setting = 1
  


  // get the general movement of the machine 
  // true = up, false = down
  // Used for counting reps at the machine
  def getMovementDirection(history: LinkedList[Int]): Boolean = {

    if (history.size < 3) {
      return true
    }

    val difference = history.getLast - history.get(history.size - 3)

    if (difference > 0){
      return true
    } else {
      return false
    }
  }



  // tells if the sensor is detecting meaningful movement based on recent reading history
  // true if we are moving, false if not
  def detectMovement_bis(history: LinkedList[Int]): Boolean = {
    val difference: Int = Math.abs(history.get(4) - history.getFirst)

    if (difference < 20) return false
    else return true
  }

  def detectMovement(readingHistory: LinkedList[Int]): Boolean = {
    var max: Int = -9999
    var min: Int = 9999

    readingHistory.forEach {
      case reading => {
        if (reading > max) max = reading 
        if (reading < min) min = reading 
      }
    }

    val difference = Math.abs(max - min)

    if (difference < 30) {
      return false
    } else {
      return true
    }
  }



  // observable type to be able to run the sensor readings asynchronously
  // no  onComplete method since it should be a continuous stream and the observer should be
  // the one to command when the reading is complete or not
  def fetchDistanceValue(sensor: DistanceSensor): Observable[LinkedList[Int]] = {

    exercising = true

    Observable(
      subscriber => {
        new Thread(new Runnable() {
          def run(): scala.Unit = {

            // FIFO Queue to store the last few readings we had to determine the general movement of the machine (up or down)
            val readingHistory: LinkedList[Int] = new LinkedList

            var reading: Int = 0

            while(true) {

              // if subscriber unsubscribes from observer, shutdown thread
              if (subscriber.isUnsubscribed) {
                return
              }

              // try catch expression to catch any out of bound exception, do nothing with them since they aren't errors
              try {
                reading = sensor.getDistance

                // keep the reading history size at a certain maximum
                if (readingHistory.size >= 10) {
                  readingHistory.remove
                }

                readingHistory.add(reading)

                // send updated reading history to subscriber
                subscriber.onNext(readingHistory)
              } 
              catch {
                case t: Throwable => println("Out of bound")
              }
              
              // sleep 200 ms to match the speed of the sensor (5 Hz)
              Thread.sleep(200)
            }
          }
        }).start()
      }
    )
  }



  def handleReading(readingHistory: LinkedList[Int]): scala.Unit = {
    
    exercising = checkIfExercising(readingHistory)
    if (exercising) {
      updateRepCount(readingHistory)
    }
  }



  def checkIfExercising(readingHistory: LinkedList[Int]): Boolean = {
    if (readingHistory.size > 2) {
      if (detectMovement(readingHistory)) return true
      else {
        reps = 0
        return false
      }
    } else {
      return true
    }
  }



  def updateRepCount(readingHistory: LinkedList[Int]): scala.Unit = {
    if (getMovementDirection(readingHistory)) {
      println("We are going up!")
      repCounted = false
    } else {
      println("We are coming down...")
      
      if (!repCounted) {
        reps = reps + 1
        repCounted = true
      }
      println("Reps: " + reps.toString)
    }
  }



  def getUpdatedWeightSetting(): Int = {
    if (!exercising) {
      try {
        weight_setting = ((distancesensor1.getDistance.toFloat - weight_bottom) / weight_step_size).toInt
        println("Changed weight setting to " + weight_setting.toString)
      } catch {
        case t: Throwable => return weight_setting
      }
      
    }
    return weight_setting 
  }



  def main(args: Array[String]): scala.Unit = {
    println("this is phidget!")

    var sensor0_reading: Int = 0

    fetchDistanceValue(distancesensor0).subscribe(
      reading => {
        handleReading(reading)
        getUpdatedWeightSetting
      },
      e => println("Error: " + e.getMessage)
    )
    
    println("Thread is running!")
    System.in.read
    return 
  }

}