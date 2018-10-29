package com.sparks.sparkscala.config

import org.springframework.context.annotation.{Bean, Configuration}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.sparks.sparkscala.spark.FlightData
import org.springframework.web.reactive.function.server.RequestPredicates._
import org.springframework.web.reactive.function.server.RouterFunctions.{route, _}
import org.springframework.web.reactive.function.server.ServerResponse._
import org.springframework.web.reactive.function.BodyInserters.fromPublisher

@Configuration
class AkkaConfiguration {
  @Bean
  def actorSystem () = ActorSystem.create("reactive")
  @Bean
  def actorMaterializer () = ActorMaterializer.create(actorSystem())
}

//@Configuration
//class RoutingConfiguration(fs: FlightService) {
//  @Bean
//  def routes() = {
//    route(GET("/flights"), _ => fs.getAll())
////      .andRoute(GET("/hashtags/unique"), _ => ok().body(ts.hashTags(), classOf[HashTag]))
//  }
//}
