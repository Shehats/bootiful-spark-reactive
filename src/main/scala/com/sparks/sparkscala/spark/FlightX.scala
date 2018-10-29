package com.sparks.sparkscala.spark

import java.util.{Date, Optional}

import org.apache.spark.sql.{DataFrame, Dataset, Encoders, SparkSession}
import org.springframework.stereotype.Component
import org.apache.spark.graphx._
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux

import scala.beans.BeanProperty

case class Flight(var date: String, delay: Long, distance: Long, origin: String, destination: String) {
  def getDate(): String = {
    val arr = date.split("/")
    val curn = new Date()
    new Date(if (curn.getMonth >= arr(1).toInt) curn.getYear else curn.getYear + 1, arr(1).toInt,
      arr(2).split(" ")(0).toInt,
      arr(2).split(" ")(1).split(":")(0).toInt, arr(2).split(" ")(1).split(":")(1).toInt).toString
  }
}

case class Route(@BeanProperty origin: Long, @BeanProperty destination: Long, @BeanProperty distance: Long, @BeanProperty date: String) {
  def getEdge(): Edge[(Long, String)] = Edge(origin, destination, (distance, date))
}
case class Airport(@BeanProperty name: String, @BeanProperty id: Long) {
  def isAirport(x: Long): Boolean = id.equals(x)
}

case class FlightData(@BeanProperty orgin: Airport, @BeanProperty destination: Airport, @BeanProperty date: String)


@Component
class FlightX (@Autowired val sparkSession: SparkSession) {
  var airportMap: Map[Long, Airport] = _
  var graph: Graph[String, (Long, String)] = generateGraph()
  var flights: Dataset[Flight] = _

  def getData (): Dataset[Flight] = {
    val df: DataFrame = sparkSession.read.json("src/main/resources/flights-2k.json")
    val ds: Dataset[Flight] = df.as[Flight](Encoders.product[Flight])
    ds.cache()
    ds
  }

  def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = if (a._2 > b._2) a else b

  def getFlightData(): Flux[FlightData] = Flux.fromArray(this.graph.edges.collect().map{
    x => FlightData(this.airportMap(x.srcId), this.airportMap(x.dstId), x.attr._2)
  })

  def getMostIncomingFlights(): Array[Airport] = this.graph.inDegrees.collect()
    .sortWith(_._2 > _._2).map{
    x => this.airportMap(x._1.toLong)
  }

  def getMostIncomingFlight(): Airport = getMostIncomingFlights().take(1)(0)

  def getMostOutgoingFlights(): Array[Airport] = this.graph.outDegrees.collect()
    .sortWith(_._2>_._2).map{
    x => this.airportMap(x._2.toLong)
  }

  def getMostOutgoingFlight(): Airport = getMostOutgoingFlights().take(1)(0)

  def generateGraph(): Graph[String, (Long, String)] = {
    var flights: Dataset[Flight] = getData()
//    val airports: Dataset[Airport] = ds.map(flight => Airport(flight.org_id, flight.origin))(Encoders.product[Airport]).distinct()
    val airports: Dataset[Airport] = flights.map(flights => Airport(flights.origin, flights.origin.hashCode))(Encoders.product[Airport])
          .union(flights.map(flights => Airport(flights.destination, flights.destination.hashCode))(Encoders.product[Airport])).distinct()
    airports.cache
    this.airportMap = airports.collect().map(x => x.id -> x).toMap
    // Defining a default vertex called nowhere
    val nowhere = "nowhere"
    val routes: Dataset[Route] = flights.map(flight => Route(flight.origin.hashCode, flight.destination.hashCode, flight.distance, flight.getDate()))(Encoders.product[Route]).distinct()
    routes.cache
    val edges = routes.map(_.getEdge)(Encoders.product[Edge[(Long, String)]]).rdd
    edges.cache
    val vertecies = airports.map(x => (x.id, x.name))(Encoders.product[(Long, String)]).rdd
    val graph: Graph[String, (Long, String)]= Graph(vertecies, edges, nowhere)
    graph.cache
  }
}
//object Runner {
//  def main(args: Array[String]): Unit = {
//    val sparkConfig = new SparkConfig()
//    val flightX = new FlightX(sparkConfig.getSparkSession())
//    flightX.generateGraph()
//  }
//}
