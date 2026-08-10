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

import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import play.api.libs.json.Json

import java.time.LocalDateTime

class SubcontractorCurrentVerificationSpec extends SpecBase {
  "SubcontractorCurrentVerification" - {
    "serialize to JSON correctly" in {
      val subcontractors = SubcontractorCurrentVerification(
        subcontractorId = 1L,
        subbieResourceRef = Some(10L),
        firstName = Some("John"),
        secondName = None,
        surname = Some("Smith"),
        tradingName = Some("ACME"),
        utr = Some("1111111111"),
        nino = Some("AA123456A"),
        crn = Some("AC012345"),
        partnerUtr = Some("5860920998"),
        partnershipTradingName = Some("ACME Trading"),
        subcontractorType = Some("soletrader"),
        addressLine1 = None,
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        country = None,
        postcode = None,
        emailAddress = Some("john.smith@test.com"),
        phoneNumber = Some("01911234567"),
        mobilePhoneNumber = Some("07123456789"),
        worksReferenceNumber = Some("WR001"),
        matched = Some("Y"),
        autoVerified = Some("Y"),
        verified = Some("Y"),
        verificationNumber = Some("V123456789"),
        taxTreatment = Some("0"),
        verificationDate = Some(LocalDateTime.parse("2026-08-07T10:00:00")),
        version = Some(1),
        updatedTaxTreatment = None,
        lastMonthlyReturnDate = Some(LocalDateTime.parse("2026-07-31T00:00:00")),
        pendingVerifications = Some(0)
      )
      val json           = Json.toJson(subcontractors)

      (json \ "subcontractorType").as[String] mustBe "soletrader"
      (json \ "emailAddress").as[String] mustBe "john.smith@test.com"
      (json \ "phoneNumber").as[String] mustBe "01911234567"
      (json \ "mobilePhoneNumber").as[String] mustBe "07123456789"
      (json \ "worksReferenceNumber").as[String] mustBe "WR001"
      (json \ "matched").as[String] mustBe "Y"
      (json \ "autoVerified").as[String] mustBe "Y"
      (json \ "verified").as[String] mustBe "Y"
      (json \ "verificationNumber").as[String] mustBe "V123456789"
      (json \ "taxTreatment").as[String] mustBe "0"
      (json \ "verificationDate").as[String] mustBe "2026-08-07T10:00:00"
      (json \ "version").as[Int] mustBe 1
      (json \ "updatedTaxTreatment").toOption mustBe None
      (json \ "lastMonthlyReturnDate").as[String] mustBe "2026-07-31T00:00:00"
      (json \ "pendingVerifications").as[Int] mustBe 0
    }
    "deserialize from JSON correctly" in {
      val json   = Json.parse(
        """
          |{
          |  "subcontractorId": 1,
          |  "subbieResourceRef": 10,
          |  "subcontractorType": "soletrader",
          |  "firstName": "John",
          |  "surname": "Smith",
          |  "secondName": "Paul",
          |  "tradingName": "ACME",
          |  "utr": "1111111111",
          |  "nino": "AA123456A",
          |  "crn": "AC012345",
          |  "partnerUtr": "5860920998",
          |  "partnershipTradingName": "ACME Trading",
          |  "emailAddress": "john.smith@test.com",
          |  "phoneNumber": "01911234567",
          |  "mobilePhoneNumber": "07123456789",
          |  "worksReferenceNumber": "WR001",
          |  "matched": "Y",
          |  "autoVerified": "Y",
          |  "verified": "Y",
          |  "verificationNumber": "V123456789",
          |  "taxTreatment": "0",
          |  "verificationDate": "2026-08-07T10:00:00",
          |  "version": 1,
          |  "updatedTaxTreatment": null,
          |  "lastMonthlyReturnDate": "2026-07-31T00:00:00",
          |  "pendingVerifications": 0
          |}
          |""".stripMargin
      )
      val result = json.as[SubcontractorCurrentVerification]
      result.subcontractorType mustBe Some("soletrader")
      result.emailAddress mustBe Some("john.smith@test.com")
      result.phoneNumber mustBe Some("01911234567")
      result.mobilePhoneNumber mustBe Some("07123456789")
      result.worksReferenceNumber mustBe Some("WR001")
      result.matched mustBe Some("Y")
      result.autoVerified mustBe Some("Y")
      result.verified mustBe Some("Y")
      result.verificationNumber mustBe Some("V123456789")
      result.taxTreatment mustBe Some("0")
      result.verificationDate mustBe Some(LocalDateTime.parse("2026-08-07T10:00:00"))
      result.version mustBe Some(1)
      result.updatedTaxTreatment mustBe None
      result.lastMonthlyReturnDate mustBe Some(LocalDateTime.parse("2026-07-31T00:00:00"))
      result.pendingVerifications mustBe Some(0)
    }
    "round-trip serialize and deserialize correctly" in {
      val subcontractors = SubcontractorCurrentVerification(
        subcontractorId = 1L,
        subbieResourceRef = Some(10L),
        firstName = Some("John"),
        secondName = None,
        surname = Some("Smith"),
        tradingName = Some("ACME"),
        utr = Some("1111111111"),
        nino = Some("AA123456A"),
        crn = Some("AC012345"),
        partnerUtr = Some("5860920998"),
        partnershipTradingName = Some("ACME Trading"),
        subcontractorType = Some("soletrader"),
        addressLine1 = None,
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        country = None,
        postcode = None,
        emailAddress = Some("john.smith@test.com"),
        phoneNumber = Some("01911234567"),
        mobilePhoneNumber = Some("07123456789"),
        worksReferenceNumber = Some("WR001"),
        matched = Some("Y"),
        autoVerified = Some("Y"),
        verified = Some("Y"),
        verificationNumber = Some("V123456789"),
        taxTreatment = Some("0"),
        verificationDate = Some(LocalDateTime.parse("2026-08-07T10:00:00")),
        version = Some(1),
        updatedTaxTreatment = None,
        lastMonthlyReturnDate = Some(LocalDateTime.parse("2026-07-31T00:00:00")),
        pendingVerifications = Some(0)
      )
      val json           = Json.toJson(subcontractors)
      val result         = json.as[SubcontractorCurrentVerification]
      result mustBe subcontractors
    }
  }
}
