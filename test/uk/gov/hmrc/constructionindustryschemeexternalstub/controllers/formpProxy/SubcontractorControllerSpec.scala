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
import org.mockito.Mockito._
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.FakeAuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{EmployerReference, SoleTrader}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.CreateAndUpdateSubcontractorRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.response.{GetSubcontractorListResponse, Subcontractor}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class SubcontractorControllerSpec extends SpecBase {

  private val cisId                  = "1"
  private val updateSubcontractorUrl = "/cis/subcontractor/create-and-update"

  ".createAndUpdateSubcontractor" - {

    "returns 204 NoContent with valid payload" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest(
          cisId = cisId,
          subcontractorType = SoleTrader,
          tradingName = Some("trading name")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 502 BadGateway with message when taxOfficeNumber = 502" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest(
          cisId = cisId,
          subcontractorType = SoleTrader,
          tradingName = Some("trading name")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 InternalServerError with message when taxOfficeNumber = 500" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest(
          cisId = cisId,
          subcontractorType = SoleTrader,
          tradingName = Some("trading name")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 403 Forbidden with message when enrolments are missing" in new Setup {
      val json: JsValue = Json.toJson(
        CreateAndUpdateSubcontractorRequest(
          cisId = cisId,
          subcontractorType = SoleTrader,
          tradingName = Some("trading name")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.createAndUpdateSubcontractor()(req)

      status(res) mustBe FORBIDDEN
      (contentAsJson(res) \ "message").as[String].toLowerCase must include("enrolments")
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

    val cisId                   = "cis-123"
    val getSubcontractorListUrl = s"/cis/subcontractors/$cisId"

    "returns 200 get with subcontractor list on success" in new Setup {

      val subcontractorUTR: Seq[String] = Seq("1111111111", "2222222222")

      val response = GetSubcontractorListResponse(
        subcontractors = List(
          Subcontractor(
            subcontractorId = 1L,
            subbieResourceRef = 10,
            `type` = "soletrader",
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

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(response).toString)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(cisId)(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(response)
    }

    "propagates UpstreamErrorResponse for taxOfficeNumber = 502" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(cisId)(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")

    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(cisId)(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 with error message when no contractor enrolment found" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, getSubcontractorListUrl)
      val res: Future[Result]                      = controller.getSubcontractorList(cisId)(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }

  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new SubcontractorController(auth, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, url)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
