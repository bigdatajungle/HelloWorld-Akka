package com.akka.tutorial

import akka.actor.{ActorSystem, Props, Actor}
import akka.actor.SupervisorStrategy.Stop
import com.akka.tutorial.MusicController.Play
import com.akka.tutorial.MusicPlayer.{StartMusic, StopMusic}

/**
  * Created by rahul on 14/02/16.
  */
//Music controller Messages
object MusicController {

  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object stop extends ControllerMsg


  def props = Props[MusicController]
}

//Music Controller
class MusicController extends Actor {
  def receive = {
    case Play => println("Music Started !!")
    case Stop => println("Music Stopped !!")
  }

}

//Music Player messages
object MusicPlayer {
  sealed trait PlayMsg
  case object StopMusic extends PlayMsg
  case object StartMusic extends PlayMsg

}

//Music Player
class MusicPlayer extends Actor {
  def receive = {
    case StopMusic => println("I don't want to stop the music !!")
    case StartMusic =>
      val controller = context.actorOf(MusicController.props,"music-controller")
      controller ! Play
    case _ => println("Unknown message !!")
  }
}

object Creation extends App {

  //Create Actor System
  val system = ActorSystem("Creation")

  //Create the Music Player Actor

  val player = system.actorOf(Props[MusicPlayer],"music-player")

  //Send StartMusic message to Actor

  player ! StartMusic

}