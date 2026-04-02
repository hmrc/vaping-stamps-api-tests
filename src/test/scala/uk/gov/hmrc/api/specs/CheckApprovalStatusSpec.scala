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

  Scenario("Approval Status returns as Approved") {
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
}
