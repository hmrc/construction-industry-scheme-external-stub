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

package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

import java.time.Instant

final class ContractorSchemeSpec extends PlaySpec {

  "ContractorScheme" should {

    val model = ContractorScheme(
      schemeId = 123,
      instanceId = "abc-123",
      accountsOfficeReference = "123PA00123456",
      taxOfficeNumber = "123",
      taxOfficeReference = "AB456",
      utr = Some("1234567890"),
      name = Some("Test Contractor Ltd"),
      emailAddress = Some("contractor@example.com"),
      displayWelcomePage = Some("Y"),
      prePopCount = Some(1),
      prePopSuccessful = Some("Y"),
      subcontractorCounter = Some(2),
      verificationBatchCounter = Some(3),
      createDate = Some(Instant.parse("2026-06-15T10:00:00Z")),
      lastUpdate = Some(Instant.parse("2026-06-15T10:05:00Z")),
      version = Some(1)
    )

    val json = Json.parse(
      """
        |{
        |  "schemeId": 123,
        |  "instanceId": "abc-123",
        |  "accountsOfficeReference": "123PA00123456",
        |  "taxOfficeNumber": "123",
        |  "taxOfficeReference": "AB456",
        |  "utr": "1234567890",
        |  "name": "Test Contractor Ltd",
        |  "emailAddress": "contractor@example.com",
        |  "displayWelcomePage": "Y",
        |  "prePopCount": 1,
        |  "prePopSuccessful": "Y",
        |  "subcontractorCounter": 2,
        |  "verificationBatchCounter": 3,
        |  "createDate": "2026-06-15T10:00:00Z",
        |  "lastUpdate": "2026-06-15T10:05:00Z",
        |  "version": 1
        |}
        |""".stripMargin
    )

    "serialize to JSON" in {
      Json.toJson(model) mustBe json
    }

    "deserialize from JSON" in {
      json.validate[ContractorScheme] mustBe JsSuccess(model)
    }

    "deserialize when optional fields are missing" in {
      val minimalJson = Json.parse(
        """
          |{
          |  "schemeId": 123,
          |  "instanceId": "abc-123",
          |  "accountsOfficeReference": "123PA00123456",
          |  "taxOfficeNumber": "123",
          |  "taxOfficeReference": "AB456"
          |}
          |""".stripMargin
      )

      val expected = ContractorScheme(
        schemeId = 123,
        instanceId = "abc-123",
        accountsOfficeReference = "123PA00123456",
        taxOfficeNumber = "123",
        taxOfficeReference = "AB456"
      )

      minimalJson.validate[ContractorScheme] mustBe JsSuccess(expected)
    }

    "fail to deserialize when required fields are missing" in {
      Json
        .obj("schemeId" -> 123)
        .validate[ContractorScheme]
        .isError mustBe true
    }
  }
}
