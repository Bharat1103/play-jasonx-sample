package jsonx.sample

import ai.x.play.json.Jsonx
import play.api.libs.json.{Format, Json}

import scala.util.{Failure, Success, Try}

sealed trait Pet

case class Cat(name: String) extends Pet

object Cat {
  implicit lazy val jsonFormat: Format[Cat] = Jsonx.formatCaseClass[Cat]
}

case class Dog(name: String) extends Pet

object Dog {
  implicit lazy val jsonFormat: Format[Dog] = Jsonx.formatCaseClass[Dog]
}

case object Turtle extends Pet

object Pet {

  import ai.x.play.json.SingletonEncoder.simpleName // required for formatSingleton
  import ai.x.play.json.implicits.formatSingleton // required if trait has object children
  implicit lazy val jsonFormat: Format[Pet] = Jsonx.formatSealed[Pet]
}

object Main {
  def main(args: Array[String]): Unit = {
    //does not work as intended, it does not hold any type hint ?
    println(Json.toJson(Cat("Tom")))

    //Always convert below jason in an instance of Cat, since its defined first in order
    //change the order of declaring case classes and parsing will change too
    //this is caused since type hint is missing from the generated json
    println(Json.parse("""{"name":"Tom"}""").as[Pet])

    //prints "Turtle" as json output, but same cannot be parsed back in the next line
    println(Json.toJson(Turtle).toString())

    //Not able to parse case object directly, unlike as shown in the docs
    Try(Json.parse("""Turtle""").as[Pet]) match {
      case Success(value) =>
        println(s"case object parsing success => $value")
      case Failure(ex) =>
        println(s"case object parsing failed => $ex") // throws JsonParseException
    }
  }
}
