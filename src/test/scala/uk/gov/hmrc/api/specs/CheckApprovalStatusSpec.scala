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
import play.api.libs.ws.JsonBodyReadables.readableAsJson

class CheckApprovalStatusSpec extends BaseSpec {

  Scenario("Approval Status request returns as Approved - POST") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 200")
    val response = postCheckApprovalStatus("GBVA0000200DS")
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

  Scenario("Approval Status request returns not found") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 404")
    val response = postCheckApprovalStatusNotFound
    response.status shouldBe 404
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
    val response = postCheckApprovalStatus("GBVA0000401DS")
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
    val response = postCheckApprovalStatus("GBVA0000403DS")
    response.status shouldBe 503
    Then("Response should be 503")
    response.body   shouldBe Json.obj(
      "code"    -> "SERVICE_UNAVAILABLE",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns Business Error") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 422")
    val response = postCheckApprovalStatus("GBVA0000422DS")
    response.status shouldBe 422
    Then("Response should Unprocessable Entity")
    response.body   shouldBe Json.obj(
      "code"    -> "UNPROCESSABLE_ENTITY",
      "message" -> "The request has returned a business logic error.",
      "errors"  -> Seq("001")
    )
  }

  Scenario("Approval Status request returns Business Error with 003") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 422")
    val response = postCheckApprovalStatus("GBVA2000422DS")
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
    val response = postCheckApprovalStatus("GBVA0000500DS")
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
    val response = postCheckApprovalStatus("GBVA1000503DS")
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
    val response = postCheckApprovalStatus("GBVA0000503DS")
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
    val response = postCheckApprovalStatus("GBVA2000503DS")
    response.status shouldBe 503
    Then("Response should be 503")
    response.body   shouldBe Json.obj(
      "code"    -> "SERVICE_UNAVAILABLE",
      "message" -> "Error has occurred in downstream service"
    )
  }
}
