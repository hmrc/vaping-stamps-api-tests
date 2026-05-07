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

  Scenario("Approval Status returns as Approved - GET") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 200")
    val response = getCheckApprovalStatus("GBVA0000200DS")
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

  Scenario("Approval Status returns as Approved - POST") {
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
      "contactEmail"    -> JsString("email@test.com"),
      "postCode"        -> JsString("SW1A 1AA"),
      "contactName"     -> JsString("Jane Smith"),
      "telephoneNumber" -> JsString("+44 20 7946 0123"),
      "stampsThreshold" -> JsNumber(500000)
    )
  }

  Scenario("Approval Status returns bad request") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 400")
    val response = postCheckApprovalStatus("INVALID_ID")
    response.status shouldBe 400
    Then("Response should be bad request")
    response.body   shouldBe Json.obj(
      "datetime"     -> "2021-12-17T09:30:47Z",
      "errorCode"    -> Seq("001", "002", "010"),
      "errorMessage" -> "The request payload is invalid or malformed."
    )
  }

  Scenario("Approval Status returns not found") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 404")
    val response = postCheckApprovalStatus("GBVA0000404DS")
    response.status shouldBe 404
    Then("Response should be not found")
    response.body   shouldBe Json.obj(
      "datetime"     -> "2021-12-17T09:30:47Z",
      "errorCode"    -> Seq("001"),
      "errorMessage" -> "The requested approval could not be found."
    )
  }

  Scenario("Approval Status returns unauthorized") {
    Given("User is not authenticated")
    When("Make request to CheckApprovalStatus API returns 401")
    val response = postCheckApprovalStatus("GBVA0000401DS")
    response.status shouldBe 401
    Then("Response should be unauthorized")
    response.body   shouldBe Json.obj(
      "datetime"     -> "2021-12-17T09:30:47Z",
      "errorCode"    -> Seq("001"),
      "errorMessage" -> "Authentication credentials are missing or invalid."
    )
  }

  Scenario("Approval Status returns internal server error") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 500")
    val response = postCheckApprovalStatus("GBVA0000500DS")
    response.status shouldBe 500
    Then("Response should be internal server error")
    response.body   shouldBe Json.obj(
      "datetime" -> "2021-12-17T09:30:47Z",
      "message"  -> "An unexpected error occurred while processing the request."
    )
  }
}
