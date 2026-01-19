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

import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.freespec.AnyFreeSpec
import play.api.http.Status.CREATED
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.FakeAuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{EmployerReference, SoleTrader}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.{CreateSubcontractorRequest, UpdateSubcontractorRequest}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class SubcontractorControllerSpec extends SpecBase {
  val schemeId          = 1
  val subbieResourceRef = 10
  ".createSubcontractor" - {

    val createSubcontractorUrl = "/cis/subcontractor/create"

    "returns 201 Created with subbieResourceRef on valid payload" in new Setup {

      val json: JsValue = Json.toJson(
        CreateSubcontractorRequest(
          schemeId = schemeId,
          subcontractorType = SoleTrader,
          version = 0
        )
      )

      val responseJson: JsObject = Json.obj("subbieResourceRef" -> subbieResourceRef)

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString)

      val req: FakeRequest[JsValue] = makeJsonRequest(json, createSubcontractorUrl)
      val res: Future[Result]       = controller.createSubcontractor()(req)

      status(res) mustBe CREATED
      contentAsJson(res) mustBe responseJson
    }

    "propagates UpstreamErrorResponse for taxOfficeNumber = 502" in new Setup {

      val json: JsValue = Json.toJson(
        CreateSubcontractorRequest(
          schemeId = schemeId,
          subcontractorType = SoleTrader,
          version = 0
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, createSubcontractorUrl)
      val res: Future[Result]       = controller.createSubcontractor()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")

    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      val json: JsValue = Json.toJson(
        CreateSubcontractorRequest(
          schemeId = schemeId,
          subcontractorType = SoleTrader,
          version = 0
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, createSubcontractorUrl)
      val res: Future[Result]       = controller.createSubcontractor()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }
  }

  ".updateSubContractor" - {

    val updateSubcontractorUrl = "/cis/subcontractor/update"

    "returns 200 Update with valid payload" in new Setup {

      val json: JsValue = Json.toJson(
        UpdateSubcontractorRequest(
          schemeId = schemeId,
          subbieResourceRef = 10,
          tradingName = Some("trading name")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.updateSubcontractor()(req)

      status(res) mustBe NO_CONTENT
    }

    "propagates UpstreamErrorResponse for taxOfficeNumber = 502" in new Setup {

      val json: JsValue = Json.toJson(
        UpdateSubcontractorRequest(
          schemeId = schemeId,
          subbieResourceRef = 10,
          tradingName = Some("trading name")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.updateSubcontractor()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")

    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      val json: JsValue = Json.toJson(
        UpdateSubcontractorRequest(
          schemeId = schemeId,
          subbieResourceRef = 10,
          tradingName = Some("trading name")
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubcontractorUrl)
      val res: Future[Result]       = controller.updateSubcontractor()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new SubcontractorController(auth, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, "/cis/subcontractor/create")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
