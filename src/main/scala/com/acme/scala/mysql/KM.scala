package com.acme.scala.mysql

import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._

case class KM(distance: Option[Double],
              highway: Option[Double],
              rural: Option[Double],
              urban: Option[Double])

object KM {

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val kmFormat = jsonFormat4(KM.apply)
  }

}
