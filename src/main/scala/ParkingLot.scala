import akka.actor.{Props, ActorSystem, Actor}
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

object ParkingLot {

  val system = ActorSystem("ParkingLot")
  val car = system.actorOf(Props[Car], "Car")
  val attendant = system.actorOf(Props[Attendant], "Attendant")
  val monitor = system.actorOf(Props[SlotMonitor], "SlotMonitor")

  def main(args: Array[String]) {
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    car ! "Find Parking"
    Thread.sleep(100)
    car ! "Take Away 3"
    Thread.sleep(100)
    car ! "Find Parking"
    car ! "Find Parking"
    system.shutdown()
  }

  val list: ListBuffer[Boolean] = ListBuffer(false, false, false, false, false, false, false, false, false, false)

  def leaveParking(takeAway: String): Unit = {
    val index = takeAway.charAt(takeAway.length-1).asDigit
    println("Car is removed from parking " + (index+1))
    list(index) = false
  }

  def findSlot(list: ListBuffer[Boolean]): Int = {
    val slot = list.indexOf(false)
    if (slot != -1) {
      list(slot) = true
      slot + 1
    }
    else {
      slot
    }
  }

  def getSlot: Int = {
    findSlot(list)
  }

}

class Car extends Actor {

  def receive = {
    case "Find Parking" => ParkingLot.attendant ! "Let Me Park"
    case takeAway => {
      ParkingLot.leaveParking(takeAway.toString)
    }
  }
}

class SlotMonitor extends Actor {
  def receive = {
    case "findEmptySlot" => sender ! ParkingLot.getSlot
  }
}

class Attendant extends Actor {

  implicit val timeout = Timeout(5.seconds)

  def receive = {
    case "Let Me Park" =>
      val slot = Await.result((ParkingLot.monitor ? "findEmptySlot").mapTo[Int], 5.seconds)
      if (slot != -1) {
        println(s"Parking available at $slot")
      }
      else {
        println("Sorry!! Parking Not Available. Parking Full.")
      }
  }

}
