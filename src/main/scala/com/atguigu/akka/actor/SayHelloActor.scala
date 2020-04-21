package com.atguigu.akka.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

//当继承了Actor后，就是一个Actor,核心方法receive，MailBox(实现了Runnable接口)会推送消息到receive方法
class SayHelloActor extends Actor {

  //type Receive = scala.PartialFunction[scala.Any, scala.Unit] 偏函数
  override def receive: Receive = {
    case "hello" => println("收到hello")
    case "world" => println("收到world")
    case "exit" => println("退出")
    case _ => println("匹配不到")
  }
}

//流程：
//1.Actor实例交给了它的引用ActorRef
//2.ActorRef把消息发给Dispatcher消息分发器
//3.Dispatcher消息分发器发送到ActorRef对应的Actor的MailBox中，MailBox内部是一个队列，可以存放多个消息
//4.MailBox把消息推送给Actor,本质是调用Actor的receive方法
object SayHelloActorDemo {
  //创建actorFactory，可以创建和管理Actor
  private val actorFactory = ActorSystem("actorFactory")
  //Props[SayHelloActor]用反射创建SayHelloActor的一个实例,sayHelloActor给actor取名字，actorRef是sayHelloActor的引用
  private val actorRef: ActorRef = actorFactory.actorOf(Props[SayHelloActor],"sayHelloActor")

  def main(args: Array[String]): Unit = {
    //给SayHelloActor发消息
    actorRef ! "hello"
    actorRef ! "world"
    actorRef ! "hello"
  }
}