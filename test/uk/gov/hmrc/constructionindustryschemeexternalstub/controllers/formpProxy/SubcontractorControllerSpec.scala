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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.formpProxy

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.FakeAuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests._
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.ContractorScheme
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.response._
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class SubcontractorControllerSpec extends SpecBase {

  private val cisId                                                         = "1"
  private val updateSubcontractorUrl                                        = "/cis/subcontractor/create-and-update"
  private val getListCisId: String                                          = "cis-123"
  private val getSubcontractorListUrl: String                               = s"/cis/subcontractors/$getListCisId"
  private val updateExistingSubcontractorUrl: String                        = "/cis/subcontractor/update"
  private val editExistingSubcontractorUrl: String                          = "/cis/subcontractor/edit"
  private val sampleSubcontractorListResponse: GetSubcontractorListResponse =
    GetSubcontractorListResponse(
      subcontractors = List(
        Subcontractor(
          subcontractorId = 1L,
          subbieResourceRef = Some(10L),
          subcontractorType = Some("soletrader"),
          utr = Some("1234567890"),
          pageVisited = Some(2),
          partnerUtr = None,
          crn = None,
          firstName = Some("John"),
          nino = Some("AA123456A"),
          secondName = None,
          surname = Some("Smith"),
          partnershipTradingName = None,
          tradingName = Some("ACME"),
          addressLine1 = Some("1 Main Street"),
          addressLine2 = None,
          addressLine3 = None,
          addressLine4 = None,
          country = Some("GB"),
          postcode = Some("AA1 1AA"),
          emailAddress = None,
          phoneNumber = None,
          mobilePhoneNumber = None,
          worksReferenceNumber = None,
          version = Some(1),
          taxTreatment = None,
          updatedTaxTreatment = None,
          verificationNumber = None,
          createDate = None,
          lastUpdate = None,
          matched = None,
          verified = None,
          autoVerified = None,
          verificationDate = None,
          lastMonthlyReturnDate = None,
          pendingVerifications = Some(0)
        )
      )
    )

  private val getSubcontractorCisId             = "individual-123"
  private val getSubcontractorSubbieResourceRef = 3L
  private val getSubcontractorUrl: String       =
    s"/cis/subcontractor/$getSubcontractorCisId/$getSubcontractorSubbieResourceRef"

  private val sampleGetSubcontractorResponse: GetSubcontractorResponse =
    GetSubcontractorResponse(
      scheme = Some(
        ContractorScheme(
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
          subcontractorCounter = Some(1),
          verificationBatchCounter = Some(1),
          version = Some(1)
        )
      ),
      subcontractor = Some(
        Subcontractor(
          subcontractorId = 30303L,
          subbieResourceRef = Some(3L),
          subcontractorType = Some("soletrader"),
          utr = Some("3333333333"),
          pageVisited = Some(1),
          partnerUtr = None,
          crn = None,
          firstName = Some("John"),
          nino = Some("AA123456A"),
          secondName = Some("Q"),
          surname = Some("Smith"),
          partnershipTradingName = None,
          tradingName = Some("John Smith Trading"),
          addressLine1 = Some("1 Main Street"),
          addressLine2 = Some("Flat 2"),
          addressLine3 = Some("London"),
          addressLine4 = None,
          country = Some("GB"),
          postcode = Some("AA1 1AA"),
          emailAddress = Some("subcontractor@example.com"),
          phoneNumber = Some("01234567890"),
          mobilePhoneNumber = Some("07123456789"),
          worksReferenceNumber = Some("WR-123"),
          version = Some(3),
          taxTreatment = Some("NET"),
          updatedTaxTreatment = Some("NET"),
          verificationNumber = Some("V123456"),
          createDate = None,
          lastUpdate = None,
          matched = Some("Y"),
          verified = Some("Y"),
          autoVerified = Some("N"),
          verificationDate = None,
          lastMonthlyReturnDate = None,
          pendingVerifications = Some(0)
        )
      )
    )

  private val deleteSubcontractorUrl: String =
    "/cis/subcontractor/delete"

  ".createAndUpdateSubcontractor" - {

    "returns 204 NoContent with valid SoleTrader payload" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest.SoleTraderRequest(
          cisId = cisId,
          tradingName = Some("trading name"),
          utr = Some("1234567890"),
          nino = Some("AA123456A"),
          firstName = Some("John"),
          surname = Some("Smith"),
          country = Some("United Kingdom")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NoContent with valid Company payload" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest.CompanyRequest(
          cisId = cisId,
          tradingName = Some("ACME LTD"),
          utr = Some("1234567890"),
          crn = Some("CRN123"),
          country = Some("United Kingdom")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NoContent with valid Partnership payload" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest.PartnershipRequest(
          cisId = cisId,
          utr = Some("1234567890"),
          partnerUtr = Some("9999999999"),
          partnershipTradingName = Some("My Partnership"),
          partnerTradingName = Some("Nominated Partner"),
          country = Some("United Kingdom")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NoContent with valid Trust payload" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest.TrustRequest(
          cisId = cisId,
          utr = Some("1234567890"),
          trustTradingName = Some("The Big Trust"),
          addressLine1 = Some("1 Trust Street"),
          city = Some("London"),
          county = Some("Greater London"),
          country = Some("United Kingdom"),
          postcode = Some("SW1A 1AA"),
          emailAddress = Some("trust@example.com"),
          phoneNumber = Some("02000000000"),
          mobilePhoneNumber = Some("07111111111"),
          worksReferenceNumber = Some("WRN-TRUST-1")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 400 BadRequest with validation errors when payload is invalid" in new Setup {
      val invalidJson: JsValue = Json.obj(
        "cisId"       -> cisId,
        "tradingName" -> "name-only"
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(invalidJson, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
      (contentAsJson(res) \ "errors").isDefined mustBe true
    }
  }

  ".getSubcontractorList" - {

    "returns 200 get with subcontractor list on success" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(sampleSubcontractorListResponse).toString)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(getListCisId)(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(sampleSubcontractorListResponse)
    }

    "returns 200 with subcontractor list when contractor enrolment is missing but agent enrolment is present" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("agent-123"))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(sampleSubcontractorListResponse).toString)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(getListCisId)(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(sampleSubcontractorListResponse)
    }

    "returns 502 BadGateway for taxOfficeNumber = 502" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(getListCisId)(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(getListCisId)(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 200 with subcontractor list when no enrolments are present" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(sampleSubcontractorListResponse).toString)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(getListCisId)(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(sampleSubcontractorListResponse)
    }
  }

  ".getSubcontractorForDelete" - {

    val deleteStatusCisId = "cis-123"

    "returns subcontractor details with canBeDeleted = true for normal subcontractor references" in new Setup {

      val req =
        FakeRequest(
          GET,
          s"/cis/subcontractor/$deleteStatusCisId/10/delete-status"
        )

      val res =
        controller.getSubcontractorForDelete(deleteStatusCisId, 10L)(req)

      status(res) mustBe OK

      contentAsJson(res) mustBe Json.obj(
        "subcontractorName"         -> "Test Subcontractor",
        "subcontractorCanBeDeleted" -> true
      )
    }

    "returns subcontractor details with canBeDeleted = false for resource ref 7" in new Setup {

      val req =
        FakeRequest(
          GET,
          s"/cis/subcontractor/$deleteStatusCisId/27/delete-status"
        )

      val res =
        controller.getSubcontractorForDelete(deleteStatusCisId, 7L)(req)

      status(res) mustBe OK

      contentAsJson(res) mustBe Json.obj(
        "subcontractorName"         -> "Delta Trust",
        "subcontractorCanBeDeleted" -> false
      )
    }
  }

  ".getSubcontractor" - {

    "returns verified individual response" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(sampleGetSubcontractorResponse).toString)

      val res =
        controller.getSubcontractor("individual-123", getSubcontractorSubbieResourceRef)(
          FakeRequest(GET, getSubcontractorUrl)
        )

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(sampleGetSubcontractorResponse)

      (contentAsJson(res) \ "scheme" \ "schemeId").as[Int] mustBe 123
      (contentAsJson(res) \ "subcontractor" \ "subcontractorId").as[Long] mustBe 30303L
      (contentAsJson(res) \ "subcontractor" \ "utr").as[String] mustBe "3333333333"
    }

    "returns 200 with subcontractor response when contractor enrolment is missing but agent enrolment is present" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("agent-123"))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(sampleGetSubcontractorResponse).toString)

      val res =
        controller.getSubcontractor("individual-123", getSubcontractorSubbieResourceRef)(
          FakeRequest(GET, getSubcontractorUrl)
        )

      status(res) mustBe OK
    }

    "returns 502 BadGateway for taxOfficeNumber = 502" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val res =
        controller.getSubcontractor("individual-123", getSubcontractorSubbieResourceRef)(
          FakeRequest(GET, getSubcontractorUrl)
        )

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] mustBe "formp failed"
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val res =
        controller.getSubcontractor("individual-123", getSubcontractorSubbieResourceRef)(
          FakeRequest(GET, getSubcontractorUrl)
        )

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 200 with subcontractor response when no enrolments are present" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(sampleGetSubcontractorResponse).toString)

      val res =
        controller.getSubcontractor("individual-123", getSubcontractorSubbieResourceRef)(
          FakeRequest(GET, getSubcontractorUrl)
        )

      status(res) mustBe OK
    }
  }

  ".deleteSubcontractor" - {

    "returns 201 Created for a valid request body" in new Setup {
      val requestBody = DeleteSubcontractorRequest(
        instanceId = "abc-123",
        subbieResourceRef = 10L
      )

      val request: FakeRequest[DeleteSubcontractorRequest] =
        FakeRequest(POST, deleteSubcontractorUrl)
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(requestBody)

      val result: Future[Result] = controller.deleteSubcontractor(request)

      status(result) mustBe NO_CONTENT
    }
  }

  ".updateSubcontractor" - {

    val validUpdateJson: JsValue =
      Json.parse(
        """
          |{
          |  "cisId": "abc-123",
          |  "subcontractor": {
          |    "subcontractorId": 999,
          |    "subbieResourceRef": 10,
          |    "utr": "1234567890",
          |    "pageVisited": 1,
          |    "firstName": "John",
          |    "nino": "AA123456A",
          |    "secondName": "Q",
          |    "surname": "Smith",
          |    "tradingName": "John Smith Trading",
          |    "subcontractorType": "soletrader",
          |    "addressLine1": "1 Main Street",
          |    "addressLine2": "Flat 2",
          |    "addressLine3": "London",
          |    "country": "GB",
          |    "postcode": "AA1 1AA",
          |    "matched": "Y",
          |    "autoVerified": "N",
          |    "verified": "Y",
          |    "verificationNumber": "V123456",
          |    "taxTreatment": "NET",
          |    "updatedTaxTreatment": "NET",
          |    "version": 5,
          |    "pendingVerifications": 0
          |  }
          |}
          |""".stripMargin
      )

    "returns 200 with incremented version for contractor enrolment" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req: FakeRequest[JsValue] =
        makeJsonRequest(validUpdateJson, updateExistingSubcontractorUrl)

      val res: Future[Result] =
        controller.updateSubcontractor(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(UpdateSubcontractorResponse(6))
      (contentAsJson(res) \ "version").as[Int] mustBe 6
    }

    "returns 200 with incremented version when contractor enrolment is missing but agent enrolment is present" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("agent-123"))

      val req: FakeRequest[JsValue] =
        makeJsonRequest(validUpdateJson, updateExistingSubcontractorUrl)

      val res: Future[Result] =
        controller.updateSubcontractor(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.obj("version" -> 6)
    }

    "returns 400 BadRequest when payload is invalid" in new Setup {
      val invalidJson: JsValue =
        Json.obj("bad" -> "payload")

      val req: FakeRequest[JsValue] =
        makeJsonRequest(invalidJson, updateExistingSubcontractorUrl)

      val res: Future[Result] =
        controller.updateSubcontractor(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "errors").isDefined mustBe true
    }

    "returns 502 BadGateway for taxOfficeNumber = 502" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[JsValue] =
        makeJsonRequest(validUpdateJson, updateExistingSubcontractorUrl)

      val res: Future[Result] =
        controller.updateSubcontractor(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] mustBe "formp failed"
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] =
        makeJsonRequest(validUpdateJson, updateExistingSubcontractorUrl)

      val res: Future[Result] =
        controller.updateSubcontractor(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 when no contractor enrolment and no agent enrolment found" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[JsValue] =
        makeJsonRequest(validUpdateJson, updateExistingSubcontractorUrl)

      val res: Future[Result] =
        controller.updateSubcontractor(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Missing enrolments"
    }
  }

  ".updateSubcontractorForEdit" - {

    val validUpdateJson: JsValue =
      Json.parse(
        """
          |{
          |  "cisId": "abc-123",
          |  "subcontractor": {
          |    "subcontractorId": 999,
          |    "subbieResourceRef": 10,
          |    "utr": "1234567890",
          |    "pageVisited": 1,
          |    "firstName": "John",
          |    "nino": "AA123456A",
          |    "secondName": "Q",
          |    "surname": "Smith",
          |    "tradingName": "John Smith Trading",
          |    "subcontractorType": "soletrader",
          |    "addressLine1": "1 Main Street",
          |    "addressLine2": "Flat 2",
          |    "addressLine3": "London",
          |    "country": "GB",
          |    "postcode": "AA1 1AA",
          |    "matched": "Y",
          |    "autoVerified": "N",
          |    "verified": "Y",
          |    "verificationNumber": "V123456",
          |    "taxTreatment": "NET",
          |    "updatedTaxTreatment": "NET",
          |    "version": 5,
          |    "pendingVerifications": 0
          |  }
          |}
          |""".stripMargin
      )

    "returns 200 with incremented version for contractor enrolment" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req =
        makeJsonRequest(
          validUpdateJson,
          editExistingSubcontractorUrl
        )

      val res =
        controller.updateSubcontractorForEdit(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe
        Json.toJson(
          UpdateSubcontractorResponse(6)
        )

      (contentAsJson(res) \ "version")
        .as[Int] mustBe 6
    }

    "returns 200 with incremented version when contractor enrolment is missing but agent enrolment is present" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("agent-123"))

      val req =
        makeJsonRequest(
          validUpdateJson,
          editExistingSubcontractorUrl
        )

      val res =
        controller.updateSubcontractorForEdit(req)

      status(res) mustBe OK

      contentAsJson(res) mustBe
        Json.obj(
          "version" -> 6
        )
    }

    "returns 400 BadRequest when payload is invalid" in new Setup {

      val invalidJson =
        Json.obj(
          "bad" -> "payload"
        )

      val req =
        makeJsonRequest(
          invalidJson,
          editExistingSubcontractorUrl
        )

      val res =
        controller.updateSubcontractorForEdit(req)

      status(res) mustBe BAD_REQUEST

      (contentAsJson(res) \ "errors").isDefined mustBe true
    }

    "returns 502 BadGateway for taxOfficeNumber = 502" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(
          Some(
            EmployerReference("502", "")
          )
        )

      val req =
        makeJsonRequest(
          validUpdateJson,
          editExistingSubcontractorUrl
        )

      val res =
        controller.updateSubcontractorForEdit(req)

      status(res) mustBe BAD_GATEWAY

      (contentAsJson(res) \ "message")
        .as[String] mustBe "formp failed"
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(
          Some(
            EmployerReference("500", "")
          )
        )

      val req =
        makeJsonRequest(
          validUpdateJson,
          editExistingSubcontractorUrl
        )

      val res =
        controller.updateSubcontractorForEdit(req)

      status(res) mustBe INTERNAL_SERVER_ERROR

      (contentAsJson(res) \ "message")
        .as[String] mustBe "Unexpected error"
    }

    "returns 500 when no contractor enrolment and no agent enrolment found" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req =
        makeJsonRequest(
          validUpdateJson,
          editExistingSubcontractorUrl
        )

      val res =
        controller.updateSubcontractorForEdit(req)

      status(res) mustBe INTERNAL_SERVER_ERROR

      (contentAsJson(res) \ "message")
        .as[String] mustBe "Missing enrolments"
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new SubcontractorController(auth, mockResourceHelper, mockEnrolmentsHelper, cc)()

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, url)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
