/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.api.specs

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsArray, JsNumber, JsString, Json}
import play.api.libs.ws.DefaultBodyWritables.writeableOf_String
import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.apitestrunner.http.HttpClient

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers with HttpClient {

  val bearerToken: String = authenticate.header("Authorization").getOrElse("Unable to get Bearer token")

  def authenticate: StandaloneWSRequest#Response =
    Await.result(
      mkRequest("http://localhost:8585/application/session/login")
        .withHttpHeaders("Content-Type" -> "application/json")
        .post(
          Json.stringify(
            Json.obj(
              "clientId"        -> JsString("clientId"),
              "authProvider"    -> JsString("StandardApplication"),
              "applicationId"   -> JsString("applicationId"),
              "applicationName" -> "VapingStamps",
              "enrolments"      -> JsArray(),
              "ttl"             -> JsNumber(5000)
            )
          )
        ),
      10.seconds
    )

  def getCheckApprovalStatus(id: String): StandaloneWSRequest#Response =
    Await.result(
      mkRequest(s"http://localhost:7011/status/$id/summary")
        .withHttpHeaders("Authorization" -> bearerToken)
        .get(),
      10.seconds
    )

  def postCheckApprovalStatus(id: String): StandaloneWSRequest#Response =
    Await.result(
      mkRequest("http://localhost:7011/status")
        .withHttpHeaders(
          "Authorization" -> bearerToken,
          "Content-Type"  -> "application/json"
        )
        .post(
          Json.stringify(
            Json.obj(
              "contactEmail"  -> JsString("email@test.com"),
              "vdsApprovalId" -> JsString(id)
            )
          )
        ),
      10.seconds
    )
}
