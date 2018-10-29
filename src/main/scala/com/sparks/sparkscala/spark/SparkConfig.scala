package com.sparks.sparkscala.spark

import org.apache.spark.sql.SparkSession
import org.springframework.context.annotation.{Bean, Configuration}
import org.apache.spark.{SparkConf}

@Configuration
class SparkConfig {
  @Bean def getSparkSession(): SparkSession = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("Flight")
    val spark = SparkSession.builder().appName("Flight").config(conf).getOrCreate()
    spark
  }
}
