package com.atguigu.akka.sparkmasterworker.master

import java.text.SimpleDateFormat
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common._
import com.typesafe.config.ConfigFactory
import scala.collection.mutable
import scala.concurrent.duration._

class SparkMaster extends Actor {

  //定义一个hashMap，用于管理worker
  val workers = new mutable.HashMap[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" => {
      println("Master服务器启动了...")
      //给自己发送一个触发检查超时worker的信息
      self ! StartTimeOutWorker
    }
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
      workerInfo.lastHeartBeat = System.currentTimeMillis()
      println("master更新了 " + id + " 心跳时间..." + workerInfo.lastHeartBeat)
    }
    case StartTimeOutWorker => {
      println("开始了定时检测心跳的任务")
      import context.dispatcher
      //1. 0 millis 表示不延时，立即执行
      //2. 9000 millis 表示每隔9秒执行一次
      //3. self 表示发送给自己
      //4. RemoveTimeOutWorker 发送的内容
      context.system.scheduler.schedule(0 millis, 9000 millis, self, RemoveTimeOutWorker)
    }
    case RemoveTimeOutWorker => {
      val workInfos = workers.values
      val now = System.currentTimeMillis()
      //简写形式：workInfos.filter(now - _.lastHeartBeat > 6000).foreach(workerInfo => workers.remove(workerInfo.id))
      //(workerInfo:WorkerInfo) => now - workerInfo.lastHeartBeat > 6000  匿名函数
      //(workerInfo:WorkerInfo) => workers.remove(workerInfo.id) 匿名函数
      workInfos.filter(workerInfo => (now - workerInfo.lastHeartBeat) > 6000).foreach(workerInfo => workers.remove(workerInfo.id))
      println("当前有" + workers.size + "个worker是存活的")
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
