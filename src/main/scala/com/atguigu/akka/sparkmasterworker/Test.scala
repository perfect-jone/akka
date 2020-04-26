package com.atguigu.akka.sparkmasterworker

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class Test {



}
object Test{
  def main(args: Array[String]): Unit = {
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val curTime = df.format(new Date())
    val curTime2 = df.format(System.currentTimeMillis())
    val curTime3 = LocalDate.now()

    println(curTime)
    println(curTime2)
    println(curTime3)
  }
}
