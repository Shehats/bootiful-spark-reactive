package com.sparks.sparkscala

import org.springframework.boot.{ApplicationRunner, SpringApplication}
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class SparksApp extends SpringBootServletInitializer{
}

object ReactiveApplication  {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[SparksApp], args: _*)
  }
}
