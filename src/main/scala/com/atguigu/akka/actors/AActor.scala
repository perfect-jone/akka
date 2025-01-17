package com.atguigu.akka.actors

import akka.actor.{Actor, ActorRef}

class AActor(actorRef:ActorRef) extends Actor {
  val bActorRef: ActorRef = actorRef
  override def receive: Receive = {
    case "start" => {
      println("AActor 出招了,start ok")
        self ! "我打" //发给自己}
    }
    case "我打" => {
      //给BActor发消息，需要持有BActor的引用BActorRef
      println("AActor(黄飞鸿) 厉害 看我佛山无影脚")
      Thread.sleep(1000)
      bActorRef ! "我打"
    }
  }
}
