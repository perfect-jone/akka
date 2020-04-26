package com.atguigu.akka.sparkmasterworker.common

class MessageProtocol {

}

//worker注册信息
case class RegisterWorkerInfo(id: String, cpu: Int, ram: Int)

//这个信息将来要保存到master的hashMap中,该hashMap是用来管理worker的
class WorkerInfo(val id: String, val cup: Int, val ram: Int)

//当worker注册成功，服务器会返回一个RegisterdWorkerInfo对象
case object RegisterdWorkerInfo
