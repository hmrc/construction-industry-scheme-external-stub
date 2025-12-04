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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.rdsDataCacheProxy

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class SchemePrepopControllerSpec extends SpecBase with MockitoSugar {

  "SchemePrepopController" - {

    ".getSchemePrepopByKnownFacts" - {

      "must return Ok response with scheme pre-pop details for a normal request" in new Setup {

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("123", "AB456")))

        val stubJson = Json.obj(
          "taxOfficeNumber"    -> "TEMPLATE_TON",
          "taxOfficeReference" -> "TEMPLATE_TOR",
          "aoRef"              -> "TEMPLATE_AO",
          "utr"                -> "1123456789",
          "schemeName"         -> "PAL-355 Scheme"
        )

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(stubJson.toString)

        val req: FakeRequest[JsValue] =
          requestWithSchemePrepopKnownFactsJsonPayload("123", "AB456", "123PA12345678")

        val res: Future[Result] = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe OK

        val json = contentAsJson(res)
        (json \ "taxOfficeNumber").as[String] mustBe "123"
        (json \ "taxOfficeReference").as[String] mustBe "AB456"
        (json \ "aoRef").as[String] mustBe "123PA12345678"
        (json \ "utr").as[String] mustBe "1123456789"
        (json \ "schemeName").as[String] mustBe "PAL-355 Scheme"
      }

      "must return 404 with NOT FOUND message for taxOfficeNumber 404" in new Setup {

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("404", "123456")))

        val req: FakeRequest[JsValue] =
          requestWithSchemePrepopKnownFactsJsonPayload("404", "123456", "AO123")

        val res: Future[Result] = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe NOT_FOUND
        (contentAsJson(res) \ "message").as[String] mustBe
          "No CIS scheme pre-pop data found for TON=404, TOR=123456, AO=AO123"
      }

      "must return 500 with InternalServerError for taxOfficeNumber 500" in new Setup {

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("500", "123456")))

        val req: FakeRequest[JsValue] =
          requestWithSchemePrepopKnownFactsJsonPayload("500", "123456", "AO123")

        val res: Future[Result] = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe INTERNAL_SERVER_ERROR
        (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
      }

      "must return 500 when no enrolments are found" in new Setup {

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(None)

        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(None)

        val req: FakeRequest[JsValue] =
          requestWithSchemePrepopKnownFactsJsonPayload("123", "AB456", "123PA12345678")

        val res: Future[Result] = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe INTERNAL_SERVER_ERROR
      }

      "must return 400 when request JSON is an empty object" in new Setup {
        val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj())
        val res: Future[Result]       = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
      }

      "must return 400 when taxOfficeNumber is missing from request" in new Setup {
        val req: FakeRequest[JsValue] =
          makeJsonRequest(Json.obj("taxOfficeReference" -> "AB456", "aoRef" -> "123PA12345678"))
        val res: Future[Result]       = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
      }

      "must return 400 when taxOfficeReference is missing from request" in new Setup {
        val req: FakeRequest[JsValue] =
          makeJsonRequest(Json.obj("taxOfficeNumber" -> "123", "aoRef" -> "123PA12345678"))
        val res: Future[Result]       = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
      }

      "must return 400 when aoRef is missing from request" in new Setup {
        val req: FakeRequest[JsValue] =
          makeJsonRequest(Json.obj("taxOfficeNumber" -> "123", "taxOfficeReference" -> "AB456"))
        val res: Future[Result]       = controller.getSchemePrepopByKnownFacts(req)

        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
      }
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val controller =
      new SchemePrepopController(fakeAuthAction, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue): FakeRequest[JsValue] =
      FakeRequest(POST, "/cis/prepop-contractor")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)

    def requestWithSchemePrepopKnownFactsJsonPayload(
      taxOfficeNumber: String,
      taxOfficeReference: String,
      aoRef: String
    ): FakeRequest[JsValue] =
      makeJsonRequest(
        Json.obj(
          "taxOfficeNumber"    -> taxOfficeNumber,
          "taxOfficeReference" -> taxOfficeReference,
          "aoRef"              -> aoRef
        )
      )
  }
}
