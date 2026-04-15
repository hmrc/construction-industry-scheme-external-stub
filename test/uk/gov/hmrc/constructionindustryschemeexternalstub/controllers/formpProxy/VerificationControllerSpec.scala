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
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class VerificationControllerSpec extends AnyFreeSpec with SpecBase {

  private val instanceId = "123"
  private val url        = s"/cis/verification-batch/newest/$instanceId"

  ".getNewestVerificationBatch" - {

    "returns 200 OK with JSON body on success (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val responseJson: JsValue = Json.parse(
        s"""
           |{
           |  "scheme": [
           |    { "schemeId": 123, "instanceId": "$instanceId" }
           |  ],
           |  "subcontractors": [
           |    { "subcontractorId": 1, "utr": "1111111111" }
           |  ],
           |  "verificationBatch": [
           |    { "verificationBatchId": 99 }
           |  ],
           |  "verifications": [
           |    { "verificationId": 1001 }
           |  ],
           |  "submission": [
           |    { "submissionId": 555 }
           |  ],
           |  "monthlyReturn": [
           |    { "monthlyReturnId": 777 }
           |  ],
           |  "monthlyReturnSubmission": [
           |    { "submissionId": 556 }
           |  ]
           |}
           |""".stripMargin
      )

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
      val res: Future[Result]                      = controller.getNewestVerificationBatch(instanceId)(req)

      status(res) mustBe OK
      contentType(res) mustBe Some(JSON)

      val body = contentAsJson(res)

      body mustBe responseJson

      (body \ "scheme")(0).\("schemeId").as[Int] mustBe 123
      (body \ "scheme")(0).\("instanceId").as[String] mustBe instanceId

      (body \ "subcontractors")(0).\("subcontractorId").as[Long] mustBe 1L
      (body \ "subcontractors")(0).\("utr").as[String] mustBe "1111111111"

      (body \ "verificationBatch")(0).\("verificationBatchId").as[Long] mustBe 99L
      (body \ "verifications")(0).\("verificationId").as[Long] mustBe 1001L

      (body \ "submission")(0).\("submissionId").as[Long] mustBe 555L
      (body \ "monthlyReturn")(0).\("monthlyReturnId").as[Long] mustBe 777L
      (body \ "monthlyReturnSubmission")(0).\("submissionId").as[Long] mustBe 556L
    }

    "returns 200 OK with JSON body on success (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val responseJson: JsValue = Json.parse(
        s"""
           |{
           |  "scheme": [
           |    { "schemeId": 123, "instanceId": "$instanceId" }
           |  ],
           |  "subcontractors": [],
           |  "verificationBatch": [],
           |  "verifications": [],
           |  "submission": [],
           |  "monthlyReturn": [],
           |  "monthlyReturnSubmission": []
           |}
           |""".stripMargin
      )

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
      val res: Future[Result]                      = controller.getNewestVerificationBatch(instanceId)(req)

      status(res) mustBe OK
      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe responseJson
    }

    "returns 502 BadGateway for taxOfficeNumber = 502 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
      val res: Future[Result]                      = controller.getNewestVerificationBatch(instanceId)(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 InternalServerError for taxOfficeNumber = 500 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
      val res: Future[Result]                      = controller.getNewestVerificationBatch(instanceId)(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when no contractor enrolment and no agent enrolment found" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
      val res: Future[Result]                      = controller.getNewestVerificationBatch(instanceId)(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new VerificationController(auth, mockResourceHelper, mockEnrolmentsHelper, cc)
  }
}
