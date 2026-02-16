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

package uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{SoleTrader, SubcontractorType}

class CreateAndUpdateSubcontractorRequestSpec extends AnyWordSpec with Matchers {

  "CreateAndUpdateSubcontractorRequest JSON format" should {

    "round-trip (write then read) with all fields populated" in {
      val model = CreateAndUpdateSubcontractorRequest(
        cisId = "CIS-123",
        subcontractorType = SoleTrader,
        firstName = Some("Jane"),
        secondName = Some("Q"),
        surname = Some("Doe"),
        tradingName = Some("ABC Ltd"),
        addressLine1 = Some("10 Downing Street"),
        addressLine2 = Some("Westminster"),
        addressLine3 = Some("London"),
        addressLine4 = Some("UK"),
        country = Some("GB"),
        postcode = Some("SW1A 2AA"),
        nino = Some("AB123456C"),
        utr = Some("1234567890"),
        worksReferenceNumber = Some("WRN-001"),
        emailAddress = Some("jane.doe@example.com"),
        phoneNumber = Some("0123456789")
      )

      val json = Json.toJson(model)
      val back = json.as[CreateAndUpdateSubcontractorRequest]

      back mustEqual model

      // spot-check a couple of fields
      (json \ "cisId").as[String] mustBe "CIS-123"
      (json \ "subcontractorType").isDefined mustBe true // exact value depends on your enum format
      (json \ "postcode").as[String] mustBe "SW1A 2AA"
    }

    "read minimal valid JSON (only required fields)" in {
      val json =
        Json.parse(
          s"""
             |{
             |  "cisId": "CIS-999",
             |  "subcontractorType": "${SoleTrader.toString}"
             |}
             |""".stripMargin
        )

      val result = json.validate[CreateAndUpdateSubcontractorRequest]
      result.isSuccess mustBe true

      val model = result.get
      model.cisId mustBe "CIS-999"
      model.subcontractorType mustBe SoleTrader
      model.firstName mustBe None
      model.tradingName mustBe None
      model.postcode mustBe None
    }

    "fail to read when 'cisId' is missing" in {
      val json =
        Json.parse(
          s"""
             |{
             |  "subcontractorType": "${SoleTrader.toString}"
             |}
             |""".stripMargin
        )

      val result = json.validate[CreateAndUpdateSubcontractorRequest]
      result.isError mustBe true

      val errors = result.asEither.swap.getOrElse {
        fail("Expected validation errors but JSON validated successfully")
      }

      errors.map(_._1.toString()) must contain("/cisId")
    }

    "fail to read when 'subcontractorType' is missing" in {
      val json =
        Json.parse(
          s"""
             |{
             |  "cisId": "CIS-123"
             |}
             |""".stripMargin
        )

      val result = json.validate[CreateAndUpdateSubcontractorRequest]
      result.isError mustBe true

      val errors = result.asEither.swap.getOrElse {
        fail("Expected validation errors but JSON validated successfully")
      }
      errors.map(_._1.toString()) must contain("/subcontractorType")
    }

    "write should omit None fields" in {
      val model = CreateAndUpdateSubcontractorRequest(
        cisId = "CIS-omit-nones",
        subcontractorType = SoleTrader
      )

      val json = Json.toJson(model).as[JsObject]
      json.keys must contain allOf ("cisId", "subcontractorType")
      json.keys must not contain "firstName"
      json.keys must not contain "emailAddress"
      json.keys must not contain "postcode"
    }

    "ignore unknown JSON fields (forward compatibility)" in {
      val json =
        Json.parse(
          s"""
             |{
             |  "cisId": "CIS-unknown-ok",
             |  "subcontractorType": "${SoleTrader.toString}",
             |  "unexpectedField": "will be ignored",
             |  "anotherOne": 123
             |}
             |""".stripMargin
        )

      val result = json.validate[CreateAndUpdateSubcontractorRequest]
      result.isSuccess mustBe true
      result.get.cisId mustBe "CIS-unknown-ok"
    }
  }
}
