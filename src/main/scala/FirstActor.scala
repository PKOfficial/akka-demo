import akka.actor.{Props, ActorSystem, Actor}
import akka.pattern.ask
import akka.util.Timeout
import akka.util.Timeout._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure,Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by prabhat on 15/3/16.
  */
class FirstActor extends Actor {

  def receive = {
    case "Good Morning" => sender ! "Good Morning"
    case "Good Evening" => sender ! "Good Evening"
    case _ => sender ! "Actor: What????"
  }

}
object SomeMain {

  val system = ActorSystem("Some")
  val actor = system.actorOf(Props[FirstActor], "Greetings")

  def send(msg: String): Unit = {
    println(s"Me: $msg")
    val futureResult = (actor ask  msg)(Timeout(5.seconds)).mapTo[String]
    val result = Await.result(futureResult,5.seconds)
    println("Result: "+result)
    futureResult onComplete {
      case Success(newResult) => println("On Complete: "+newResult)
      case Failure(ex) => println(ex)
    }
    Thread.sleep(100)
  }

  def main(args: Array[String]): Unit ={
    send("Good Morning")
    send("Good Evening")
    send("Hello")
    system.shutdown()
  }
}


