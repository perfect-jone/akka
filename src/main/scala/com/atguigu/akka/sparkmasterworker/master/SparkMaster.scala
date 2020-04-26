package com.atguigu.akka.sparkmasterworker.master

import java.text.SimpleDateFormat

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, RegisterdWorkerInfo, WorkerInfo}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable

class SparkMaster extends Actor {

  //定义一个hashMap，用于管理worker
  val workers = new mutable.HashMap[String, WorkerInfo]()
  val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  override def receive: Receive = {
    case "start" => println("Master服务器启动了...")
    case RegisterWorkerInfo(id, cpu, ram) => {
      //接收到work的注册信息
      if (!workers.contains(id)) {
        val workerInfo = new WorkerInfo(id, cpu, ram)
        workers += (id -> workerInfo)
        //workers += ((id,workerInfo))
        println("服务器的workers=" + workers)
        //注册成功后，回复客户端一个消息
        sender() ! RegisterdWorkerInfo
      }
    }
    case HeartBeat(id) => {
      //更新对应worker的心跳时间
      //从workers中取出workerInfo
      val workerInfo = workers(id)
      workerInfo.lastHeartBeat = df.format(System.currentTimeMillis())
      println("master更新了 " + id + " 心跳时间..." + workerInfo.lastHeartBeat)
    }
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
  val sparkMasterSystem = ActorSystem("SparkMaster", config)

  //创建master的ActorRef
  val sparkMasterRef: ActorRef = sparkMasterSystem.actorOf(Props[SparkMaster], "SparkMaster-01")

  //启动SparkMaster
  sparkMasterRef ! "start"


}
