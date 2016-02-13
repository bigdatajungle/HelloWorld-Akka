package com.akka.tutorial

import akka.actor.{Props, ActorSystem, Actor}

/**
  * Created by rahul on 14/02/16.
  */

//Define Actor Message
case class WhoToGreet(who: String)
//Define Greeter Actor
class Greeter extends Actor {
  def receive ={
    case WhoToGreet(who) => println(s"Hello $who")
  }
}
object HelloAkka extends App{

  // Create the Hello Akka actor system
  val system = ActorSystem("Hello-Akka")

  // Create the greet Actor
  val greeter = system.actorOf(Props[Greeter], "greeter")

  //Send WhoToGreet message to actor

  greeter ! WhoToGreet("Akka")

}
