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
      "approvalStatus"            -> JsString("APPROVED"),
      "businessName"              -> JsString("Example Trading Ltd"),
      "registeredBusinessAddress" -> JsString("10 Example Street, London, SW1A 1AA"),
      "correspondenceAddress"     -> JsString("PO Box 123, London, SW1A 2AB"),
      "contactName"               -> JsString("Jane Smith"),
      "contactTelephone"          -> JsString("+44 20 7946 0123"),
      "contactEmail"              -> JsString("jane.smith@example.com"),
      "approvalNumber"            -> JsString("GBVA0000200DS"),
      "stampThreshold"            -> JsNumber(500000)
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
      "approvalStatus"            -> JsString("APPROVED"),
      "businessName"              -> JsString("Example Trading Ltd"),
      "registeredBusinessAddress" -> JsString("10 Example Street, London, SW1A 1AA"),
      "correspondenceAddress"     -> JsString("PO Box 123, London, SW1A 2AB"),
      "contactName"               -> JsString("Jane Smith"),
      "contactTelephone"          -> JsString("+44 20 7946 0123"),
      "contactEmail"              -> JsString("jane.smith@example.com"),
      "approvalNumber"            -> JsString("GBVA0000200DS"),
      "stampThreshold"            -> JsNumber(500000)
    )
  }

  Scenario("Approval Status returns successful with no payload") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 204")
    val response = postCheckApprovalStatus("GBVA0000204DS")
    response.status shouldBe 204
    Then("Response should be no content")
    response.body shouldBe Json.obj()
  }

  Scenario("Approval Status returns bad request") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 400")
    val response = postCheckApprovalStatus("GBVA0000400DS")
    response.status shouldBe 400
    Then("Response should be bad request")
    response.body shouldBe Json.obj(
      "code"    -> "INVALID_REQUEST",
      "message" -> "The request payload is invalid or malformed."
    )
  }

  Scenario("Approval Status returns not found") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 404")
    val response = postCheckApprovalStatus("GBVA0000404DS")
    response.status shouldBe 404
    Then("Response should be not found")
    response.body shouldBe Json.obj(
      "code"    -> "NOT_FOUND",
      "message" -> "The requested approval could not be found."
    )
  }

  Scenario("Approval Status returns unauthorized") {
    Given("User is not authenticated")
    When("Make request to CheckApprovalStatus API returns 401")
    val response = postCheckApprovalStatus("GBVA0000401DS")
    response.status shouldBe 401
    Then("Response should be unauthorized")
    response.body shouldBe Json.obj(
      "code" -> "UNAUTHORISED",
      "message" -> "Authentication credentials are missing or invalid."
    )
  }

  Scenario("Approval Status returns conflict") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 409")
    val response = postCheckApprovalStatus("GBVA0000409DS")
    response.status shouldBe 409
    Then("Response should be conflict")
    response.body shouldBe Json.obj(
      "code" -> "CONFLICT",
      "message" -> "The request conflicts with the current state of the resource."
    )
  }

  Scenario("Approval Status returns internal server error") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 500")
    val response = postCheckApprovalStatus("GBVA0000500DS")
    response.status shouldBe 500
    Then("Response should be internal server error")
    response.body shouldBe Json.obj(
      "code" -> "INTERNAL_SERVER_ERROR",
      "message" -> "An unexpected error occurred while processing the request."
    )
  }

  Scenario("Approval Status returns service unavailable error") {
    Given("User is authenticated")
    authenticate
    When("Make request to CheckApprovalStatus API returns 503")
    val response = postCheckApprovalStatus("GBVA0000503DS")
    response.status shouldBe 503
    Then("Response should be service unavailable error")
    response.body shouldBe Json.obj(
      "code" -> "SERVICE_UNAVAILABLE",
      "message" -> "The service is temporarily unavailable. Please try again later."
    )
  }
}



