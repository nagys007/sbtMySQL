package com.acme.scala.mysql

import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._

case class Waypoint(waypointId: Long,
                    timestamp: Option[java.util.Date],
                    point: Point,
                    isFaulty: Option[Boolean] )

object Waypoint {

  val TABLE_NAME = "waypointpersistence"

  object JsonProtocol extends DefaultJsonProtocol {
    import Date.JsonProtocol._
    import Point.JsonProtocol._
    implicit val WaypointFormat = jsonFormat4(Waypoint.apply)
  }
}