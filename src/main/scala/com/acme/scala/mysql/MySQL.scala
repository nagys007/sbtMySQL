package com.acme.scala.mysql

import java.sql.{Connection, DriverManager}

import spray.json._

import scala.collection.mutable.ListBuffer
import scala.util.Try

object MySQL extends App {
  val host = Try(System.getProperty("host").toString).toOption.getOrElse("localhost")
  val database = Try(System.getProperty("database").toString).toOption.getOrElse("db")
  val mysqlUrl = s"""jdbc:mysql://${host}/${database}"""
  val mysqlDriver = "com.mysql.jdbc.Driver"
  val username = Try(System.getProperty("username").toString).toOption.getOrElse("guest")
  val mysqlUsername = username
  var mysqlConnection: Connection = _
  println(s"Loading $mysqlDriver...")
  Class.forName(mysqlDriver).newInstance
  println(s"Connecting to $mysqlUrl as $mysqlUsername...")
  mysqlConnection = DriverManager.getConnection(mysqlUrl, mysqlUsername, "")

  def getWaypoints(trackId: Long) = {
    val waypointStatement = mysqlConnection.createStatement
    val waypointColumns = "*"
    val waypointQuery = s"""SELECT $waypointColumns FROM ${Waypoint.TABLE_NAME} WHERE waypoint = "$trackId" ORDER BY "id" LIMIT 100"""
    //println(s"Executing '$waypointQuery'...")
    val waypointResultSet = waypointStatement.executeQuery(waypointQuery)
    val waypointBuffer = ListBuffer.empty[Waypoint]
    while (waypointResultSet.next()) {
      waypointBuffer += Waypoint(
        waypointId = waypointResultSet.getLong("id"),
        timestamp = Option(waypointResultSet.getDate("timestampStore")),
        point = Point(
          lat = waypointResultSet.getDouble("latitude"),
          long = waypointResultSet.getDouble("longitude")
        ),
        isFaulty = Option(waypointResultSet.getBoolean("faultyWaypoint"))
      )
    }
    waypointResultSet.close()
    waypointStatement.close()
    waypointBuffer.toList
  }

  try {

    val trackStatement = mysqlConnection.createStatement

    val trackColumns = "*"
    val trackQuery =
      s"""SELECT $trackColumns FROM ${Track.TABLE_NAME}
          |WHERE trackStatus = "${Track.EVALUATION_DONE}" AND markedAsFaulty = 0
                                                          |LIMIT 100""".stripMargin
    println(s"Executing '$trackQuery'...")
    try {
      val trackResultSet = trackStatement.executeQuery(trackQuery)
      try {
        while (trackResultSet.next()) {
          val id = trackResultSet.getLong("id")
          println(id)
          val track = Track(
            trackId = id,
            startPoint = Point(
              lat = trackResultSet.getDouble("startPointLatitude"),
              long = trackResultSet.getDouble("startPointLongitude") ),
            endPoint = Point(
              lat = trackResultSet.getDouble("endPointLatitude"),
              long = trackResultSet.getDouble("endPointLongitude") ),
            km = KM(
              distance = Option(trackResultSet.getDouble("distanceKM")),
              highway = Option(trackResultSet.getDouble("kmHighway")),
              rural = Option(trackResultSet.getDouble("kmRural")),
              urban = Option(trackResultSet.getDouble("kmUrban")) ),
            waypoints = getWaypoints(id)
          )

          import Track.JsonProtocol._
          println(track.toJson.compactPrint)
        }
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        trackResultSet.close()
      }
    } catch {
      case e:Exception => e.printStackTrace()
    } finally {
      trackStatement.close()
    }
  } catch {
    case e:Exception => e.printStackTrace()
  } finally {
    mysqlConnection.close()
  }


}
