package com.atguigu.akka.sparkmasterworker.master

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class SparkMaster extends Actor{
  override def receive: Receive = {
    case "start" => print("Master服务器启动了...")
  }
}

object SparkMaster extends App {
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=127.0.0.1
       |akka.remote.netty.tcp.port=10005
            """.stripMargin)

  //创建master的ActorSystem
  val sparkMasterSystem = ActorSystem("SparkMaster",config)

  //创建master的ActorRef
  val sparkMasterRef: ActorRef = sparkMasterSystem.actorOf(Props[SparkMaster],"SparkMaster-01")

  //启动SparkMaster
  sparkMasterRef ! "start"


}
