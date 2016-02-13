package com.akka.tutorial

import akka.actor.{Props, ActorSystem, ActorRef, Actor}
import com.akka.tutorial.Checker.{WhiteUser, CheckUser, BlackUser}
import com.akka.tutorial.Recorder.NewUser
import com.akka.tutorial.Storage.AddUser
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.sys.Prop

/**
  * Created by rahul on 14/02/16.
  */

case class User(username: String, email:String)

object Recorder {
  sealed trait RecorderMsg
  //Record Message

  case class NewUser(user: User) extends RecorderMsg

  def props(checker: ActorRef, storage: ActorRef) =
    Props(new Recorder(checker, storage))

}

object Checker {
  sealed trait CheckerMsg
  //Checker Message
  case class CheckUser(user: User) extends CheckerMsg

  sealed trait CheckerResponse
  //Checker Response
  case class BlackUser(user: User) extends CheckerMsg
  case class WhiteUser(user: User) extends CheckerMsg

}

object Storage {
  sealed trait StorageMsg
  //Storage Message

  case class AddUser(user: User) extends StorageMsg

}

class Storage extends Actor {
  var users = List.empty[User]
  def receive = {
    case AddUser(user) =>
    println(s"Storage : $user added")
      users = user :: users
  }

}

class Checker extends Actor {
  val blackList = List(
    User("Rahul","rahul@mail.com"))

    def receive = {
      case CheckUser(user) if blackList.contains(user) =>
        println(s"Checker : $user is in BlackList")
        sender() ! BlackUser(user)
      case CheckUser(user) =>
        println(s"Checker : $user is not in BlackList")
        sender() ! WhiteUser(user)
    }
}

class Recorder(checker: ActorRef,storage: ActorRef) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5 seconds)

  def receive =  {
    case NewUser(user) =>
      checker ? CheckUser(user) map {
        case WhiteUser(user) =>
          storage ! AddUser(user)
        case BlackUser(user) =>
          println(s"Recoder : $user in the blacklist")
      }
  }


}

object TalkToActor extends App {

  val system = ActorSystem("TalkToActor")

  val checker = system.actorOf(Props[Checker],"checker")
  val storage = system.actorOf(Props[Storage],"storage")

  val recorder = system.actorOf(Recorder.props(checker,storage),"recorder")

  recorder ! Recorder.NewUser(User("Rahul", "rahul@mail.com"))

  Thread.sleep(100)

  //shutdown system
  system.terminate()
}