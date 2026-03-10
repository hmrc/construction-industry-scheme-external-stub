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

import org.scalatest.EitherValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{Company, Partnership, SoleTrader}

class CreateAndUpdateSubcontractorRequestSpec extends AnyWordSpec with Matchers with EitherValues {

  "CreateAndUpdateSubcontractorRequest JSON format" should {

    "round-trip (write then read) for SoleTraderRequest with fields populated" in {
      val model: CreateAndUpdateSubcontractorRequest =
        CreateAndUpdateSubcontractorRequest.SoleTraderRequest(
          cisId = "CIS-123",
          utr = Some("1234567890"),
          nino = Some("AB123456C"),
          firstName = Some("Jane"),
          secondName = Some("Q"),
          surname = Some("Doe"),
          tradingName = Some("ABC Ltd"),
          addressLine1 = Some("10 Downing Street"),
          addressLine2 = Some("Westminster"),
          city = Some("London"),
          county = Some("Greater London"),
          country = Some("United Kingdom"),
          postcode = Some("SW1A 2AA"),
          emailAddress = Some("jane.doe@example.com"),
          phoneNumber = Some("0123456789"),
          mobilePhoneNumber = Some("07123456789"),
          worksReferenceNumber = Some("WRN-001")
        )

      val json = Json.toJson(model)
      val back = json.as[CreateAndUpdateSubcontractorRequest]

      back mustEqual model

      (json \ "cisId").as[String] mustBe "CIS-123"
      (json \ "subcontractorType").as[String] mustBe SoleTrader.toString
      (json \ "postcode").as[String] mustBe "SW1A 2AA"
      (json \ "county").as[String] mustBe "Greater London"
      (json \ "firstName").as[String] mustBe "Jane"
      (json \ "secondName").as[String] mustBe "Q"
      (json \ "surname").as[String] mustBe "Doe"
      (json \ "country").as[String] mustBe "United Kingdom"
    }

    "round-trip (write then read) for CompanyRequest with fields populated" in {
      val model: CreateAndUpdateSubcontractorRequest =
        CreateAndUpdateSubcontractorRequest.CompanyRequest(
          cisId = "CIS-456",
          utr = Some("1234567890"),
          crn = Some("CRN123"),
          tradingName = Some("ACME LTD"),
          addressLine1 = Some("1 Company Street"),
          city = Some("London"),
          county = Some("Greater London"),
          country = Some("United Kingdom"),
          postcode = Some("EC1A 1AA"),
          emailAddress = Some("company@example.com"),
          phoneNumber = Some("02000000000"),
          mobilePhoneNumber = Some("07111111111"),
          worksReferenceNumber = Some("WRN-999")
        )

      val json = Json.toJson(model)
      val back = json.as[CreateAndUpdateSubcontractorRequest]

      back mustEqual model
      (json \ "subcontractorType").as[String] mustBe Company.toString
      (json \ "crn").as[String] mustBe "CRN123"
    }

    "round-trip (write then read) for PartnershipRequest with fields populated" in {
      val model: CreateAndUpdateSubcontractorRequest =
        CreateAndUpdateSubcontractorRequest.PartnershipRequest(
          cisId = "CIS-789",
          utr = Some("1234567890"),
          partnerUtr = Some("9999999999"),
          partnershipTradingName = Some("My Partnership"),
          tradingName = Some("Nominated Partner"),
          addressLine1 = Some("1 Partnership Lane"),
          city = Some("London"),
          county = Some("Greater London"),
          country = Some("United Kingdom"),
          postcode = Some("SE1 1AA"),
          emailAddress = Some("partner@example.com"),
          phoneNumber = Some("02011111111"),
          mobilePhoneNumber = Some("07222222222"),
          worksReferenceNumber = Some("WRN-123")
        )

      val json = Json.toJson(model)
      val back = json.as[CreateAndUpdateSubcontractorRequest]

      back mustEqual model
      (json \ "subcontractorType").as[String] mustBe Partnership.toString
      (json \ "partnerUtr").as[String] mustBe "9999999999"
      (json \ "partnershipTradingName").as[String] mustBe "My Partnership"
    }

    "read minimal valid JSON (only required fields) for SoleTraderRequest" in {
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

      model match {
        case st: CreateAndUpdateSubcontractorRequest.SoleTraderRequest =>
          st.tradingName mustBe None
          st.postcode mustBe None
          st.utr mustBe None
          st.mobilePhoneNumber mustBe None
        case other                                                     =>
          fail(s"Expected SoleTraderRequest but got: ${other.getClass.getSimpleName}")
      }
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

      val errors = result.asEither.swap.getOrElse(fail("Expected validation errors but JSON validated successfully"))
      errors.map(_._1.toString()) must contain("/cisId")
    }

    "fail to read when 'subcontractorType' is missing" in {
      val json = Json.parse("""{ "cisId": "CIS-123" }""")
      json.validate[CreateAndUpdateSubcontractorRequest].isError mustBe true
    }

    "should fail to read when subcontractorType is unsupported" in {
      val json = Json.parse("""
                              |{
                              |  "cisId": "CIS-123",
                              |  "subcontractorType": "banana"
                              |}
                              |""".stripMargin)

      val result = json.validate[CreateAndUpdateSubcontractorRequest]
      result.isError mustBe true

      val errs = result match {
        case JsError(e) => e
        case _          => fail("Expected JsError")
      }

      val msg = errs.flatMap(_._2).flatMap(_.messages).mkString(" | ")
      msg must include("Invalid SubcontractorType value")
    }

    "write should omit None fields (SoleTraderRequest)" in {
      val model: CreateAndUpdateSubcontractorRequest =
        CreateAndUpdateSubcontractorRequest.SoleTraderRequest(
          cisId = "CIS-omit-nones"
        )

      val json = Json.toJson(model).as[JsObject]

      json.keys must contain allOf ("cisId", "subcontractorType")

      json.keys must not contain "utr"
      json.keys must not contain "partnerUtr"
      json.keys must not contain "crn"
      json.keys must not contain "emailAddress"
      json.keys must not contain "postcode"
      json.keys must not contain "mobilePhoneNumber"
      json.keys must not contain "worksReferenceNumber"
      json.keys must not contain "firstName"
      json.keys must not contain "secondName"
      json.keys must not contain "surname"
      json.keys must not contain "country"
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
