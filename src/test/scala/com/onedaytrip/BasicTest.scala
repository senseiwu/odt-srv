package com.onedaytrip

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}
//import org.specs2.matcher.Matchers
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
 * Created by tomek on 7/11/15.
 */
class BasicTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("MySpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Echo actor" must {

    "send back messages unchanged" in {
      val echo = system.actorOf(TestActors.echoActorProps)
      echo ! "hello world"
      expectMsg("hello world")
    }

  }
}