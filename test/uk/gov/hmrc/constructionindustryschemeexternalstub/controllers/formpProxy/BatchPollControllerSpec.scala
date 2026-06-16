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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.formpProxy

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class BatchPollControllerSpec extends AnyFreeSpec with Matchers with MockitoSugar {

  ".getBatchPollSubmissions" - {

    "returns 200 with batch poll submissions for contractor enrolment" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(nonEmptyResponse.toString)

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe nonEmptyResponse
    }

    "returns 200 with empty batch poll submissions when contractor taxOfficeNumber is 000" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("000", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(emptyResponse.toString)

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe emptyResponse
    }

    "returns 502 when contractor taxOfficeNumber is 502" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 when contractor taxOfficeNumber is 500" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 200 with batch poll submissions for agent enrolment" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("AGT200"))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(nonEmptyResponse.toString)

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe nonEmptyResponse
    }

    "returns 200 with empty batch poll submissions when agent enrolment is AGT000" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("AGT000"))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(emptyResponse.toString)

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe emptyResponse
    }

    "returns 502 when agent enrolment is AGT502" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("AGT502"))

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 when agent enrolment is AGT500" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("AGT500"))

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 when no valid enrolment is found" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[JsValue] = makeJsonRequest()
      val res: Future[Result]       = controller.getBatchPollSubmissions()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Missing enrolment"
    }
  }

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    private val parsers: PlayBodyParsers = cc.parsers

    private def fakeAuth: AuthAction = new FakeAuthAction(parsers)

    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val controller =
      new BatchPollController(fakeAuth, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(): FakeRequest[JsValue] =
      FakeRequest(POST, "/cis/get-batchpoll-submissions")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(Json.obj())

    val nonEmptyResponse: JsValue =
      Json.obj(
        "verificationSubmissions"  -> Json.arr(
          Json.obj(
            "submissionId"                 -> 90001,
            "submissionType"               -> "CISVERIFY",
            "agentId"                      -> "A123456",
            "taxOfficeNumber"              -> "123",
            "taxOfficeReference"           -> "ABC123",
            "instanceId"                   -> "instance-verification-001",
            "status"                       -> "SUBMITTED",
            "verificationBatchResourceRef" -> 70001
          )
        ),
        "monthlyReturnSubmissions" -> Json.arr(
          Json.obj(
            "submissionId"       -> 90002,
            "submissionType"     -> "CIS300MR",
            "status"             -> "SUBMITTED",
            "taxOfficeNumber"    -> "123",
            "taxOfficeReference" -> "456789",
            "taxYear"            -> "2025-26",
            "taxMonth"           -> "06",
            "instanceId"         -> "instance-monthly-return-001",
            "agentId"            -> "A123456"
          )
        )
      )

    val emptyResponse: JsValue =
      Json.obj(
        "verificationSubmissions"  -> Json.arr(),
        "monthlyReturnSubmissions" -> Json.arr()
      )
  }
}
