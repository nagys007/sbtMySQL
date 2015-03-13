package com.acme.scala.mysql

import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._

case class Point(lat: Double,
                 long: Double)

object Point {

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val pointFormat = jsonFormat2(Point.apply)
  }

}
