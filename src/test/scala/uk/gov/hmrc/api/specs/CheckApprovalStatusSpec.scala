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

import play.api.libs.json.{JsNumber, JsString, Json}
import play.api.libs.ws.DefaultBodyWritables.writeableOf_String
import play.api.libs.ws.JsonBodyReadables.readableAsJson

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class CheckApprovalStatusSpec extends BaseSpec {

  Scenario("Approval Status request returns as Approved - POST") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 200")
    val response = postCheckApprovalStatus("GBVC0000200DS")
    response.status shouldBe 200
    Then("ApprovalStatus should be APPROVED")
    response.body   shouldBe Json.obj(
      "approvalStatus"  -> JsString("APPROVED"),
      "businessName"    -> JsString("Example Trading Ltd"),
      "addressLine1"    -> JsString("10 Example Street"),
      "addressLine2"    -> JsString("London"),
      "postCode"        -> JsString("SW1A 1AA"),
      "contactName"     -> JsString("Jane Smith"),
      "telephoneNumber" -> JsString("+44 20 7946 0123"),
      "stampsThreshold" -> JsNumber(500000)
    )
  }

  Scenario("Approval Status request from NI returns as Approved - POST") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 200")
    val response = postCheckApprovalStatus("XIVC0000200DS")
    response.status shouldBe 200
    Then("ApprovalStatus should be APPROVED")
    response.body   shouldBe Json.obj(
      "approvalStatus"  -> JsString("APPROVED"),
      "businessName"    -> JsString("Example Trading Ltd"),
      "addressLine1"    -> JsString("10 Example Street"),
      "addressLine2"    -> JsString("Belfast"),
      "postCode"        -> JsString("BT1 1AA"),
      "contactName"     -> JsString("Jane Smith"),
      "telephoneNumber" -> JsString("+44 20 7946 0123"),
      "stampsThreshold" -> JsNumber(500000)
    )
  }

  Scenario("Approval Status request returns as Not Approved - POST") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 200")
    val response = postCheckApprovalStatus("GBVE0000266DS")
    response.status shouldBe 200
    Then("ApprovalStatus should be NOT_APPROVED")
    response.body   shouldBe Json.obj("approvalStatus" -> JsString("NOT_APPROVED"))
  }

  Scenario("Approval Status request from NI returns as Not Approved - POST") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 200")
    val response = postCheckApprovalStatus("XIVF0000266DS")
    response.status shouldBe 200
    Then("ApprovalStatus should be NOT_APPROVED")
    response.body   shouldBe Json.obj("approvalStatus" -> JsString("NOT_APPROVED"))
  }

  Scenario("Approval Status request returns BAD_REQUEST with invalid and 001, 005, 007 sequence of errors") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 400")
    val response =
      Await.result(
        mkRequest("http://localhost:7011/status")
          .withHttpHeaders(
            "Authorization" -> bearerToken,
            "Content-Type"  -> "application/json"
          )
          .post(
            Json.stringify(
              Json.obj(
              )
            )
          ),
        10.seconds
      )
    response.status shouldBe 400
    response.body shouldBe Json.obj(
      "code"    -> "BAD_REQUEST",
      "message" -> "The request is invalid",
      "errors"  -> Seq("005", "007")
    )
  }

  Scenario("Approval Status request returns BAD_REQUEST with invalid and 002, 004, 006, 008 sequence of errors") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 400")
    val response =
      Await.result(
        mkRequest("http://localhost:7011/status")
          .withHttpHeaders(
            "Accept"        -> "application/vnd",
            "Authorization" -> bearerToken,
            "Content-Type"  -> "text/json"
          )
          .post(
            Json.stringify(
              Json.obj(
                "vdsEmail"              -> JsString("em@ail@te@st.com"),
                "stampsReferenceNumber" -> JsString("GBVC0000AAA200DS")
              )
            )
          ),
        10.seconds
      )
    response.status shouldBe 400
    response.body shouldBe Json.obj(
      "code"    -> "BAD_REQUEST",
      "message" -> "The request is invalid",
      "errors"  -> Seq("002", "004", "006", "008")
    )
  }

  Scenario("Approval Status request returns BAD_REQUEST with invalid response and error 009") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 400")
    val response =
      Await.result(
        mkRequest("http://localhost:7011/status")
          .withHttpHeaders(
            "Accept"        -> "application/vnd.hmrc.1.0+json",
            "Authorization" -> bearerToken,
            "Content-Type"  -> "application/json"
          )
          .post(
            Json.stringify(
              Json.obj(
                "vdsEmail"              -> JsString(
                  "0234567890123456789022345678903234567890423456789052345678906234@0234567890123456789022345678903234567890423456789052345678906234.0234567890123456789022345678903234567890423456789052345678901"
                ),
                "stampsReferenceNumber" -> JsString("GBVC0000200DS")
              )
            )
          ),
        10.seconds
      )
    response.status shouldBe 400
    response.body shouldBe Json.obj(
      "code"    -> "BAD_REQUEST",
      "message" -> "The request is invalid",
      "errors"  -> Seq("009")
    )
  }

  Scenario("Approval Status request returns unauthorized") {
    Given("User is not authenticated")
    When("Make request to CheckApprovalStatus API returns 502 when 401 is returned by EIS")
    val response = postCheckApprovalStatusUnauthorized
    response.status shouldBe 401
    Then("Response should be unauthorized")
    response.body   shouldBe Json.obj(
      "statusCode" -> 401,
      "message"    -> "Invalid bearer token"
    )
  }

  Scenario("Approval Status request returns service unavailable - 401 EIS response") {
    Given("User is not authenticated")
    When("Make request to CheckApprovalStatus API returns 503 when 401 is returned by EIS")
    val response = postCheckApprovalStatus("GBVC0000401DS")
    response.status shouldBe 503
    Then("Response should be 503")
    response.body   shouldBe Json.obj(
      "code"    -> "SERVICE_UNAVAILABLE",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns service unavailable - 403 EIS response") {
    Given("User is not authenticated")
    When("Make request to CheckApprovalStatus API returns 503 when 403 is returned by EIS")
    val response = postCheckApprovalStatus("GBVC0000403DS")
    response.status shouldBe 503
    Then("Response should be 503")
    response.body   shouldBe Json.obj(
      "code"    -> "SERVICE_UNAVAILABLE",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns not found") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 404")
    val response = postCheckApprovalStatusNotFound
    response.status shouldBe 404
  }

  Scenario("Approval Status request returns Unsupported Media Type") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 415")
    val response =
      Await.result(
        mkRequest("http://localhost:7011/status")
          .withHttpHeaders(
            "Accept"        -> "application/vnd.hmrc.1.0+json",
            "Authorization" -> bearerToken
          )
          .post(
            Json.stringify(
              Json.obj(
                "vdsEmail"              -> JsString("email@test.com"),
                "stampsReferenceNumber" -> JsString("GBVC0000200DS")
              )
            )
          ),
        10.seconds
      )
    response.status shouldBe 415
    response.body shouldBe Json.obj(
      "statusCode" -> 415,
      "message" -> "Expecting text/json or application/json body"
    )
  }

  Scenario("Approval Status request returns Business Error with 001") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 422")
    val response = postCheckApprovalStatus("GBVC0000422DS")
    response.status shouldBe 422
    Then("Response should Unprocessable Entity")
    response.body   shouldBe Json.obj(
      "code"    -> "UNPROCESSABLE_ENTITY",
      "message" -> "The request has returned a business logic error.",
      "errors"  -> Seq("001")
    )
  }

  Scenario("Approval Status request returns Business Error with 002") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 422")
    val response = postCheckApprovalStatus("GBVC1000422DS")
    response.status shouldBe 422
    Then("Response should Unprocessable Entity")
    response.body   shouldBe Json.obj(
      "code"    -> "UNPROCESSABLE_ENTITY",
      "message" -> "The request has returned a business logic error.",
      "errors"  -> Seq("002")
    )
  }

  Scenario("Approval Status request returns Business Error with 003") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 422")
    val response = postCheckApprovalStatus("GBVC2000422DS")
    response.status shouldBe 422
    Then("Response should Unprocessable Entity")
    response.body   shouldBe Json.obj(
      "code"    -> "UNPROCESSABLE_ENTITY",
      "message" -> "The request has returned a business logic error.",
      "errors"  -> Seq("003")
    )
  }

  Scenario("Approval Status request returns internal server error") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 500")
    val response = postCheckApprovalStatus("GBVC0000500DS")
    response.status shouldBe 500
    Then("Response should be internal server error")
    response.body   shouldBe Json.obj(
      "code"    -> "INTERNAL_SERVER_ERROR",
      "message" -> "Success response received invalid JSON response"
    )
  }

  Scenario("Approval Status request returns service unavailable - 500 EIS response") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 503 when 500 is returned by EIS")
    val response = postCheckApprovalStatus("GBVC1000503DS")
    response.status shouldBe 503
    Then("Response should be 503")
    response.body   shouldBe Json.obj(
      "code"    -> "SERVICE_UNAVAILABLE",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns service unavailable - 502 EIS response") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 503 when 502 is returned by EIS")
    val response = postCheckApprovalStatus("GBVC0000503DS")
    response.status shouldBe 503
    Then("Response should be 503")
    response.body   shouldBe Json.obj(
      "code"    -> "SERVICE_UNAVAILABLE",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns service unavailable - 503 EIS response") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 503 when 503 is returned by EIS")
    val response = postCheckApprovalStatus("GBVC2000503DS")
    response.status shouldBe 503
    Then("Response should be 503")
    response.body   shouldBe Json.obj(
      "code"    -> "SERVICE_UNAVAILABLE",
      "message" -> "Error has occurred in downstream service"
    )
  }
}
