import com.phidget22.*
import java.util.Scanner
import scala.rxscala.Observable

object Phidget_Test {
  // Distance sensor fetch
  val distancesensor0 = new DistanceSensor()
  distancesensor0.open(5000)
  distancesensor0.setDeviceSerialNumber(0)
  distancesensor0.setHubPort(0)
  distancesensor0.setChannel(0)
  distancesensor0.setDataRate(5)
  println("opened sensor 0")
  
  
  val distancesensor1 = new DistanceSensor()
  distancesensor1.open(5000)
  distancesensor1.setDeviceSerialNumber(1)
  distancesensor1.setHubPort(1)
  distancesensor1.setChannel(0)
  distancesensor1.setDataRate(5)
  println("opened sensor 1")
  

  val listener = new DistanceSensorDistanceChangeListener() {
    def onDistanceChange(e: DistanceSensorDistanceChangeEvent): Observable[Int] = {
      println("Sensor " + e.getSource().getHubPort().toString() + " Distance: " + e.getDistance())
      return e.getDistance()
    }
  }

  distancesensor0.addDistanceChangeListener(listener)

  distancesensor1.addDistanceChangeListener(listener)
  
  def main() = {
    println("this is phidget!")

    System.in.read()
  }

}