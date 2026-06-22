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
    val response = postCheckApprovalStatus("XIVA0000200DS")
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
    val response = postCheckApprovalStatus("GBVA0000266DS")
    response.status shouldBe 200
    Then("ApprovalStatus should be NOT_APPROVED")
    response.body   shouldBe Json.obj("approvalStatus" -> JsString("NOT_APPROVED"))
  }

  Scenario("Approval Status request from NI returns as Not Approved - POST") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 200")
    val response = postCheckApprovalStatus("XIVA0000266DS")
    response.status shouldBe 200
    Then("ApprovalStatus should be NOT_APPROVED")
    response.body   shouldBe Json.obj("approvalStatus" -> JsString("NOT_APPROVED"))
  }

  Scenario("Approval Status request returns bad request") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 400")
    val response = postCheckApprovalStatus("INVALID_ID")
    response.status shouldBe 400
    Then("Response should be bad request")
    response.body   shouldBe Json.obj(
      "code"    -> "BAD_REQUEST",
      "message" -> "The request is invalid",
      "errors"  -> Seq("006")
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

  Scenario("Approval Status request returns bad gateway - 401 EIS response") {
    Given("User is not authenticated")
    When("Make request to CheckApprovalStatus API returns 502 when 401 is returned by EIS")
    val response = postCheckApprovalStatus("GBVA0000401DS")
    response.status shouldBe 502
    Then("Response should be unauthorized")
    response.body   shouldBe Json.obj(
      "code"    -> "BAD_GATEWAY",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns bad gateway - 403 EIS response") {
    Given("User is not authenticated")
    When("Make request to CheckApprovalStatus API returns 502 when 403 is returned by EIS")
    val response = postCheckApprovalStatus("GBVA0000403DS")
    response.status shouldBe 502
    Then("Response should be 502")
    response.body   shouldBe Json.obj(
      "code"    -> "BAD_GATEWAY",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns bad gateway - 404 EIS response") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 502 when 404 is returned by EIS")
    val response = postCheckApprovalStatus("GBVA0000404DS")
    response.status shouldBe 502
    Then("Response should be 502")
    response.body   shouldBe Json.obj(
      "code"    -> "BAD_GATEWAY",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns Business Error") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 422")
    val response = postCheckApprovalStatus("GBVA0000422DS")
    response.status shouldBe 422
    Then("Response should be not found")
    response.body   shouldBe Json.obj(
      "code"    -> "UNPROCESSABLE_ENTITY",
      "message" -> "The request has returned a business logic error.",
      "errors"  -> Seq("001")
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

  Scenario("Approval Status request returns bad gateway - 500 EIS response") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 502 when 500 is returned by EIS")
    val response = postCheckApprovalStatus("GBVA1000502DS")
    response.status shouldBe 502
    Then("Response should be 502")
    response.body   shouldBe Json.obj(
      "code"    -> "BAD_GATEWAY",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns bad gateway - 502 EIS response") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 502 when 502 is returned by EIS")
    val response = postCheckApprovalStatus("GBVA0000502DS")
    response.status shouldBe 502
    Then("Response should be 502")
    response.body   shouldBe Json.obj(
      "code"    -> "BAD_GATEWAY",
      "message" -> "Error has occurred in downstream service"
    )
  }

  Scenario("Approval Status request returns bad gateway - 503 EIS response") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 502 when 503 is returned by EIS")
    val response = postCheckApprovalStatus("GBVA2000502DS")
    response.status shouldBe 502
    Then("Response should be 502")
    response.body   shouldBe Json.obj(
      "code"    -> "BAD_GATEWAY",
      "message" -> "Error has occurred in downstream service"
    )
  }
}
