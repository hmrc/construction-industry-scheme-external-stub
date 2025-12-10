/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.*

class CreateContractorSchemeParamsSpec extends AnyWordSpec with Matchers {

  "CreateContractorSchemeParams OFormat" should {

    "serialise to JSON and back (round-trip)" in {
      val original = CreateContractorSchemeParams(
        instanceId = "inst-123",
        accountsOfficeReference = "AOR123",
        taxOfficeNumber = "123",
        taxOfficeReference = "AB123",
        utr = Some("1234567890"),
        name = Some("Test Name"),
        emailAddress = Some("test@example.com"),
        displayWelcomePage = Some("Y"),
        prePopCount = Some(5),
        prePopSuccessful = Some("Y")
      )

      val json: JsValue = Json.toJson(original)
      val parsed        = json.as[CreateContractorSchemeParams]

      parsed shouldBe original
    }

    "handle missing optional fields using defaults" in {
      val json = Json.obj(
        "instanceId"              -> "inst-456",
        "accountsOfficeReference" -> "AOR456",
        "taxOfficeNumber"         -> "456",
        "taxOfficeReference"      -> "CD456"
      )

      val parsed = json.as[CreateContractorSchemeParams]

      parsed.instanceId              shouldBe "inst-456"
      parsed.accountsOfficeReference shouldBe "AOR456"
      parsed.taxOfficeNumber         shouldBe "456"
      parsed.taxOfficeReference      shouldBe "CD456"

      parsed.utr                shouldBe None
      parsed.name               shouldBe None
      parsed.emailAddress       shouldBe None
      parsed.displayWelcomePage shouldBe None
      parsed.prePopCount        shouldBe None
      parsed.prePopSuccessful   shouldBe None
    }
  }
}
