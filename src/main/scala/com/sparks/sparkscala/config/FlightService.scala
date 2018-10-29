package com.sparks.sparkscala.config

import akka.stream.ActorMaterializer
import com.sparks.sparkscala.spark.{FlightData, FlightX}
import org.springframework.stereotype.Service
import org.reactivestreams.Publisher
import akka.stream.scaladsl.{Sink, Source}
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.{GetMapping, RestController}
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.{Flux, Mono}
import org.springframework.web.reactive.function.server.RequestPredicates._
import org.springframework.web.reactive.function.server.RouterFunctions.{route, _}
import org.springframework.web.reactive.function.server.ServerResponse._
import org.springframework.web.reactive.function.BodyInserters.fromPublisher

@Service
class FlightService (val flightX: FlightX, val am: ActorMaterializer) {
  def getFlights(): Publisher[FlightData] = Source.fromPublisher(
    flights()
  ).runWith(Sink.asPublisher(true)) {
    am
  }

  def getAll(): Mono[ServerResponse] = ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(getFlights(), classOf[FlightData])

  def flights(): Publisher[FlightData] = flightX.getFlightData()
}

//@RestController
//class FlightController(val flightService: FlightService) {
//  @GetMapping(value = Array("/flights"),produces = Array(MediaType.))
//  def getAll(): Publisher[FlightData] = flightService.getFlights()
//}
