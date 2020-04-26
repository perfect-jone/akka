package com.atguigu.akka.sparkmasterworker.worker

import java.util.UUID

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common.{RegisterWorkerInfo, RegisterdWorkerInfo}
import com.typesafe.config.ConfigFactory

class SparkWorker(masterHost: String, masterPort: Int) extends Actor {
  var masterProxy: ActorSelection = _
  val id = UUID.randomUUID().toString

  override def preStart(): Unit = {

    //初始化masterProxy
    masterProxy = context
      .actorSelection(s"akka.tcp://SparkMaster@${masterHost}:${masterPort}/user/SparkMaster-01")
    println("masterProxy=" + masterProxy)

  }

  override def receive: Receive = {
    case "start" => {
      println("Worker客户端启动了...")
      masterProxy ! RegisterWorkerInfo(id, 16, 16)
    }
    case RegisterdWorkerInfo => println("workerid="+ id + "注册成功")
  }
}

object SparkWorker extends App {
  val workerHost = "127.0.0.1"
  val workerPort = 10001

  val masterHost = "127.0.0.1"
  val masterPort = 10005

  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=127.0.0.1
       |akka.remote.netty.tcp.port=10001
            """.stripMargin)

  //创建worker的ActorSystem
  private val sparkWorkerSystem = ActorSystem("SparkWorker", config)

  //创建worker的ActorRef
  private val sparkWorkerRef: ActorRef = sparkWorkerSystem.
    actorOf(Props(new SparkWorker(masterHost, masterPort)), "SparkWorker-01")

  //启动SparkWorker
  sparkWorkerRef ! "start"

}
