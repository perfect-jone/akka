package com.atguigu.akka.actors

import akka.actor.{ActorSystem, Props}

object ActorGame {
  def main(args: Array[String]): Unit = {
    //创建ActorSystem
    val actorFactory = ActorSystem("actorFactory")
    //先创建BActor的引用
    val bActorRef = actorFactory.actorOf(Props[BActor],"bActor")
    //创建AActor的引用
    val aActorRef = actorFactory.actorOf(Props(new AActor(bActorRef)),"aActor")

    aActorRef ! "start"
  }
}
