package com.atguigu.akka.sparkmasterworker.worker

import java.util.UUID
import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, RegisterdWorkerInfo, SentHeartBeat}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

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
    case RegisterdWorkerInfo => {
      println("workerid=" + id + "注册成功")
      //注册成功后，就定义一个定时器，每隔一段时间，发送SentHeartBeat给自己
      import context.dispatcher
      //1. 0 millis 表示不延时，立即执行
      //2. 3000 millis 表示每隔3秒执行一次
      //3. self 表示发送给自己
      //4. SentHeartBeat 发送的内容
      context.system.scheduler.schedule(0 millis, 3000 millis, self, SentHeartBeat)
    }
    case SentHeartBeat => {
      println("worker= " + id + " 给服务器发送心跳")
      masterProxy ! HeartBeat(id)
    }
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
