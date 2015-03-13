package com.acme.scala.mysql

import java.text.SimpleDateFormat

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, deserializationError}

object Date {

  object JsonProtocol extends DefaultJsonProtocol {

    val dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

    implicit object DateJsonFormat extends RootJsonFormat[java.util.Date] {
      def write(date: java.util.Date) =
        JsString(dateFormat.format(date))

      def read(value: JsValue) = value match {
        case JsString(dateString) => dateFormat.parse(dateString)
        case _ => deserializationError("Date expected")
      }
    }

  }

}