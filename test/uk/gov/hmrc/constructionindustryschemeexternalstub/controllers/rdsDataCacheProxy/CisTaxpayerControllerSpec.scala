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
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{CisTaxpayer, EmployerReference}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class CisTaxpayerControllerSpec extends SpecBase with MockitoSugar {

  "CisTaxpayerController" - {

    ".getCisTaxpayerByTaxReference" - {

      "must return Ok response with contractor details for an unknown taxOfficeNumber /  taxOfficeReference" in new Setup {

        val taxpayer: CisTaxpayer = mkTaxpayer(ton = "200", tor = "123456")

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("200", "123456")))

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(Json.toJson(taxpayer).toString)

        val req: FakeRequest[JsValue] = requestWithEmployeeReferenceJsonPayload("200", "123456")
        val res: Future[Result]       = controller.getCisTaxpayerByTaxReference(req)
        status(res) mustBe OK
//        contentType(res) mustBe Some(JSON)
        contentAsJson(res) mustBe Json.toJson(taxpayer)
      }

      "must return Ok response with uniqueId 800 when taxOfficeReference is EZ10800" in new Setup {

        val baseTaxpayer: CisTaxpayer = mkTaxpayer(ton = "200", tor = "EZ10800")

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("200", "EZ10800")))

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(Json.toJson(baseTaxpayer).toString)

        val req: FakeRequest[JsValue] =
          requestWithEmployeeReferenceJsonPayload("200", "EZ10800")

        val res: Future[Result] =
          controller.getCisTaxpayerByTaxReference(req)

        status(res) mustBe OK

        val expectedJson: JsObject = Json
          .toJson(baseTaxpayer)
          .as[JsObject]
          .deepMerge(
            Json.obj(
              "uniqueId"        -> "800",
              "taxOfficeNumber" -> "200",
              "taxOfficeRef"    -> "EZ10800"
            )
          )

        contentAsJson(res) mustBe expectedJson

        (contentAsJson(res) \ "uniqueId").as[String] mustBe "800"
        (contentAsJson(res) \ "taxOfficeNumber").as[String] mustBe "200"
        (contentAsJson(res) \ "taxOfficeRef").as[String] mustBe "EZ10800"
      }

      "return 404 with NOT FOUND message for taxOfficeNumber 404" in new Setup {

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("404", "123456")))

        val req: FakeRequest[JsValue] = requestWithEmployeeReferenceJsonPayload("404", "123456")
        val res: Future[Result]       = controller.getCisTaxpayerByTaxReference(req)
        status(res) mustBe NOT_FOUND
        (contentAsJson(res) \ "message").as[String] mustBe "CIS taxpayer not found for TON=404, TOR=123456"

      }

      "returns 400 when request JSON is an empty object" in new Setup {
        val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj())
        val res: Future[Result]       = controller.getCisTaxpayerByTaxReference(req)
        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
      }

      "returns 400 when taxOfficeNumber is missing from request" in new Setup {
        val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("taxOfficeReference" -> "test111"))
        val res: Future[Result]       = controller.getCisTaxpayerByTaxReference(req)
        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
      }

      "returns 400 when taxOfficeReference is missing from request" in new Setup {
        val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("taxOfficeNumber" -> "111"))
        val res: Future[Result]       = controller.getCisTaxpayerByTaxReference(req)
        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
      }

    }

  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val controller = new CisTaxpayerController(fakeAuthAction, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue): FakeRequest[JsValue] =
      FakeRequest(POST, "/cis-taxpayer")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)

    def requestWithEmployeeReferenceJsonPayload(ton: String = "111", tor: String = "test111"): FakeRequest[JsValue] =
      makeJsonRequest(Json.obj("taxOfficeNumber" -> ton, "taxOfficeReference" -> tor))
  }
}
