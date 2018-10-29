//package com.sparks.sparkscala.config
//
//import java.util
//
//import akka.stream.ActorMaterializer
//import org.springframework.data.annotation.Id
//import org.springframework.data.mongodb.core.mapping.Document
//import org.springframework.data.mongodb.repository.ReactiveMongoRepository
//import org.springframework.stereotype.{Repository, Service}
//import org.reactivestreams.Publisher
//import akka.stream.scaladsl.{Sink, Source}
//
//
//import scala.beans.BeanProperty
//import scala.collection.JavaConverters
//
//@Document
//case class Author (@BeanProperty @Id handler: String)
//
//case class HashTag(@BeanProperty @Id tag: String)
//
//@Document
//case class Tweet (@BeanProperty @Id content: String, @BeanProperty author: Author) {
//  @BeanProperty
//  val hashTags: util.Set[HashTag] =
//    JavaConverters.setAsJavaSet(
//      content.split(" ")
//        .collect {
//          case t if t.startsWith("#") => HashTag(t.replaceAll("[^#\\w]", "").toLowerCase())
//        }.toSet
//    )
//}
//
//@Repository
//trait TweetRepositry extends ReactiveMongoRepository[Tweet, String]
//
//@Service
//class TweetService (tr: TweetRepositry, am: ActorMaterializer) {
//  def hashTags(): Publisher[HashTag] = {
//    Source.fromPublisher(tweets())
//      .map(t => JavaConverters.asScalaSet(t.hashTags).toSet)
//      .reduce((x,y) => x ++ y)
//      .mapConcat(identity).runWith(Sink.asPublisher(true)) {
//        am
//      }
//  }
//
//  def tweets(): Publisher[Tweet] = tr.findAll()
//}
