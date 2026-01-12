package com.social

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val registerScenario = scenario("Register Users")
    .repeat(100) {
      exec(
        http("Register User")
          .post("/auth/register")
          .body(StringBody("""{"username":"user${randomUUID()}","password":"password123"}"""))
          .check(status.is(200))
          .check(jsonPath("$.token").saveAs("token"))
      )
    }

  val loginScenario = scenario("Login Users")
    .repeat(50) {
      exec(
        http("Login")
          .post("/auth/login")
          .body(StringBody("""{"username":"testuser","password":"password123"}"""))
          .check(status.is(200))
          .check(jsonPath("$.token").saveAs("token"))
      )
    }

  val feedScenario = scenario("Fetch Feed")
    .repeat(200) {
      exec(
        http("Get Feed")
          .get("/feed?userId=user123&limit=10")
          .header("Authorization", "Bearer ${token}")
          .check(status.is(200))
      )
    }

  setUp(
    registerScenario.inject(rampUsers(50).during(30.seconds)),
    loginScenario.inject(rampUsers(30).during(20.seconds)),
    feedScenario.inject(rampUsers(100).during(60.seconds))
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(5000),
      global.successfulRequests.percent.gt(95)
    )
}
