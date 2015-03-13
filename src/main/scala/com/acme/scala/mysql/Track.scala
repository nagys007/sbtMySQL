package com.acme.scala.mysql

import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._

case class Track(trackId: Long,
                 startPoint: Point,
                 endPoint: Point,
                 km: KM,
                 waypoints: Seq[Waypoint])

object Track {

  val TABLE_NAME = "trackpersistence"

  val EVALUATION_DONE = "EVALUATION_DONE"

  object JsonProtocol extends DefaultJsonProtocol {
    import com.acme.scala.mysql.KM.JsonProtocol._
    import com.acme.scala.mysql.Point.JsonProtocol._
    import com.acme.scala.mysql.Waypoint.JsonProtocol._
    implicit val TrackFormat = jsonFormat5(Track.apply)

  }

}
