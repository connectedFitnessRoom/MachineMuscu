import java.time.{LocalDateTime, Duration}
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit



/* 
 * This class implements various time features necessary to communicate
 * workout information with the server
 */
class WorkoutTimer {


    var timerStart: LocalDateTime = LocalDateTime.now



    def getCurrentTime(): String = {
        return LocalDateTime.now.toString
    }



    def startTimer(): Unit = {
        timerStart = LocalDateTime.now
    }



    def millisToSecondsDecimal(timeInMillis: Long): Float = {
        return timeInMillis.toFloat / 1000F
    }



    def getTimerValue(): Float = {
        var duration: Duration = Duration.between(timerStart, LocalDateTime.now)

        var durationInMilliseconds: Long = duration.toMillis
        return millisToSecondsDecimal(durationInMilliseconds)

    }   
}
