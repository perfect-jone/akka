package com.atguigu.akka.sparkmasterworker.common

import java.text.SimpleDateFormat

class MessageProtocol {

}

//worker注册信息
case class RegisterWorkerInfo(id: String, cpu: Int, ram: Int)

//这个信息将来要保存到master的hashMap中,该hashMap是用来管理worker的
class WorkerInfo(val id: String, val cup: Int, val ram: Int){
  var lastHeartBeat: Long = System.currentTimeMillis()
}

//当worker注册成功，服务器会返回一个RegisterdWorkerInfo对象
case object RegisterdWorkerInfo

//worker每隔一段时间由定时器发给自己的一个消息
case object SentHeartBeat

//worker每隔一段时间由定时器触发，发给服务器的协议消息
case class HeartBeat(id: String)

//master给自己发送一个触发检查超时worker的信息
case object StartTimeOutWorker

//master给自己发消息，检测worker,对于心跳超时的
case object RemoveTimeOutWorker