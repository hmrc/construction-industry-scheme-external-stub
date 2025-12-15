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
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.*

class UpdateContractorSchemeParamsSpec extends AnyWordSpec with Matchers {

  "UpdateContractorSchemeParams" should {

    "serialise and deserialise to/from JSON" in {
      val params = UpdateContractorSchemeParams(
        schemeId = 123,
        instanceId = "instance-1",
        accountsOfficeReference = "AOR123",
        taxOfficeNumber = "456",
        taxOfficeReference = "789",
        utr = Some("1234567890"),
        name = Some("Test Name"),
        emailAddress = Some("test@test.com"),
        displayWelcomePage = Some("Y"),
        prePopCount = Some(10),
        prePopSuccessful = Some("Y"),
        version = Some(1)
      )

      val json = Json.toJson(params)
      json.as[UpdateContractorSchemeParams] mustBe params
    }

    "deserialise when optional fields are missing" in {
      val json = Json.obj(
        "schemeId"                -> 123,
        "instanceId"              -> "instance-1",
        "accountsOfficeReference" -> "AOR123",
        "taxOfficeNumber"         -> "456",
        "taxOfficeReference"      -> "789",
        "version"                 -> 1
      )

      val result = json.as[UpdateContractorSchemeParams]

      result.schemeId mustBe 123
      result.instanceId mustBe "instance-1"
      result.accountsOfficeReference mustBe "AOR123"
      result.taxOfficeNumber mustBe "456"
      result.taxOfficeReference mustBe "789"
      result.version mustBe Some(1)

      result.utr mustBe None
      result.name mustBe None
      result.emailAddress mustBe None
      result.displayWelcomePage mustBe None
      result.prePopCount mustBe None
      result.prePopSuccessful mustBe None
    }
  }
}
