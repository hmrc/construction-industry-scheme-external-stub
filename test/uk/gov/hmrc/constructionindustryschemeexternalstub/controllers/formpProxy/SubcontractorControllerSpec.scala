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
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.FakeAuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{EmployerReference, SoleTrader}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.CreateAndUpdateSubcontractorRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper

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

  private trait Setup {
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new SubcontractorController(auth, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, url)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
