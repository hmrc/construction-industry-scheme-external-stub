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
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.FakeAuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{CreateVerifications, DeleteVerifications, EmployerReference}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.*
import scala.concurrent.Future
import java.time.LocalDateTime

class VerificationControllerSpec extends AnyFreeSpec with SpecBase {

  private val instanceId = "123"
  private val url        = s"/cis/verification-batch/newest/$instanceId"
  private val postUrl    = "/cis/verification-batch/create"

  private val validJson: JsValue =
    Json.toJson(
      CreateVerificationBatchAndVerificationsRequest(
        instanceId = instanceId,
        verificationResourceReferences = Seq(1L, 2L),
        actionIndicator = Some("A")
      )
    )

  ".getNewestVerificationBatch" - {

    "returns 200 OK with JSON body on success (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val responseJson: JsValue = Json.parse(
        """
          |{
          |  "subcontractors": [
          |    {
          |      "subcontractorId": 1,
          |      "subbieResourceRef": 10
          |    }
          |  ],
          |  "verificationBatch": {
          |    "verificationBatchId": 99,
          |    "status": "SUBMITTED"
          |  },
          |  "verifications": [
          |    {
          |      "verificationId": 1001,
          |      "matched": "Y",
          |      "verificationNumber": "V0000000001",
          |      "actionIndicator": "VERIFY",
          |      "proceed": "Y",
          |      "verificationResourceRef": 10
          |    }
          |  ],
          |  "submission": {
          |    "submissionId": 555
          |  },
          |  "monthlyReturn": {
          |    "monthlyReturnId": 777
          |  }
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
      val subcontractor = (body \ "subcontractors")(0)

      (subcontractor \ "subcontractorId").as[Long] mustBe 1L
      (subcontractor \ "subbieResourceRef").as[Long] mustBe 10L

      (body \ "verificationBatch").\("verificationBatchId").as[Long] mustBe 99L
      (body \ "verifications")(0).\("verificationId").as[Long] mustBe 1001L

      (body \ "submission").\("submissionId").as[Long] mustBe 555L
      (body \ "monthlyReturn").\("monthlyReturnId").as[Long] mustBe 777L
      (body \ "verificationBatch" \ "status").as[String] mustBe "SUBMITTED"

      val verification = (body \ "verifications")(0)

      (verification \ "verificationId").as[Long] mustBe 1001L
      (verification \ "matched").as[String] mustBe "Y"
      (verification \ "verificationNumber").as[String] mustBe "V0000000001"
      (verification \ "actionIndicator").as[String] mustBe "VERIFY"
      (verification \ "proceed").as[String] mustBe "Y"
      (verification \ "verificationResourceRef").as[Long] mustBe 10L
    }

    "returns 200 OK with JSON body on success (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val responseJson: JsValue = Json.parse(
        s"""
           |{
           |  "subcontractors": [
           |    { "subcontractorId": 1 }
           |  ],
           |  "verificationBatch": { "verificationBatchId": 99 },
           |  "verifications": [
           |    { "verificationId": 1001 }
           |  ],
           |  "submission": { "submissionId": 555 },
           |  "monthlyReturn": { "monthlyReturnId": 777 }
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

    ".getCurrentVerificationBatch" - {

      val instanceId = "123"
      val url        = s"/cis/verification-batch/current/$instanceId"

      "returns 200 OK with JSON body (instanceId not 1) on success (contractor enrolment)" in new Setup {
        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("200", "")))
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(None)

        val responseJson: JsValue = Json.parse(
          s"""
             |{
             |  "subcontractors": [
             |    { "subcontractorId": 1 }
             |  ],
             |  "verificationBatch": { "verificationBatchId": 99 },
             |  "verifications": [
             |    { "verificationId": 1001 }
             |  ]
             |}
             |""".stripMargin
        )

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(responseJson.toString())

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe OK
        contentType(res) mustBe Some(JSON)

        val body = contentAsJson(res)

        body mustBe responseJson

        (body \ "subcontractors")(0).\("subcontractorId").as[Long] mustBe 1L

        (body \ "verificationBatch").\("verificationBatchId").as[Long] mustBe 99L
        (body \ "verifications")(0).\("verificationId").as[Long] mustBe 1001L

      }

      "returns 200 OK with JSON body (instanceId equal 1) on success (contractor enrolment)" in new Setup {
        val instanceId = "1"

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("200", "")))
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(None)

        val responseJson: JsValue = Json.parse(
          s"""
             |{
             |  "subcontractors": [
             |    { "subcontractorId": 1 }
             |  ],
             |  "verificationBatch": { "verificationBatchId": 99 },
             |  "verifications": [
             |    { "verificationId": 1001 }
             |  ]
             |}
             |""".stripMargin
        )

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(responseJson.toString())

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe OK
        contentType(res) mustBe Some(JSON)

        val body = contentAsJson(res)

        body mustBe responseJson

        (body \ "subcontractors")(0).\("subcontractorId").as[Long] mustBe 1L

        (body \ "verificationBatch").\("verificationBatchId").as[Long] mustBe 99L
        (body \ "verifications")(0).\("verificationId").as[Long] mustBe 1001L

      }

      "returns 200 OK with JSON body (instanceId not 1) on success (agent enrolment)" in new Setup {
        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(None)
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(Some("IRAgentReference-123"))

        val responseJson: JsValue = Json.parse(
          s"""
             |{
             |  "subcontractors": [
             |    { "subcontractorId": 1 }
             |  ],
             |  "verificationBatch": { "verificationBatchId": 99 },
             |  "verifications": [
             |    { "verificationId": 1001 }
             |  ]
             |}
             |""".stripMargin
        )

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(responseJson.toString())

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe OK
        contentType(res) mustBe Some(JSON)
        contentAsJson(res) mustBe responseJson
      }

      "returns 200 OK with JSON body (instanceId equal 1) on success (agent enrolment)" in new Setup {
        val instanceId = "1"

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(None)
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(Some("IRAgentReference-123"))

        val responseJson: JsValue = Json.parse(
          s"""
             |{
             |  "subcontractors": [
             |    { "subcontractorId": 1 }
             |  ],
             |  "verificationBatch": { "verificationBatchId": 99 },
             |  "verifications": [
             |    { "verificationId": 1001 }
             |  ]
             |}
             |""".stripMargin
        )

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(responseJson.toString())

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe OK
        contentType(res) mustBe Some(JSON)
        contentAsJson(res) mustBe responseJson
      }

      "returns 200 OK with ChRIS JSON body when instanceId is 800 on success (contractor enrolment)" in new Setup {
        val instanceId = "800"
        val url        = s"/cis/verification-batch/current/$instanceId"

        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("200", "")))
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(None)

        val responseJson: JsValue = Json.parse(
          s"""
             |{
             |  "subcontractors": [
             |    { "subcontractorId": 1 }
             |  ],
             |  "verificationBatch": {
             |    "verificationBatchId": 800
             |  },
             |  "verifications": [
             |    { "verificationId": 1001 }
             |  ]
             |}
             |""".stripMargin
        )

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn(responseJson.toString())

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe OK
        contentType(res) mustBe Some(JSON)

        val body = contentAsJson(res)

        body mustBe responseJson
        (body \ "verificationBatch" \ "verificationBatchId").as[Long] mustBe 800L
      }

      "returns 502 BadGateway for taxOfficeNumber = 502 (contractor enrolment)" in new Setup {
        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("502", "")))
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(None)

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe BAD_GATEWAY
        (contentAsJson(res) \ "message").as[String] must include("formp failed")
      }

      "returns 500 InternalServerError for taxOfficeNumber = 500 (contractor enrolment)" in new Setup {
        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(Some(EmployerReference("500", "")))
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(None)

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe INTERNAL_SERVER_ERROR
        (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
      }

      "returns 500 InternalServerError when no contractor enrolment and no agent enrolment found" in new Setup {
        when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
          .thenReturn(None)
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
          .thenReturn(None)

        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, url)
        val res: Future[Result]                      = controller.getCurrentVerificationBatch(instanceId)(req)

        status(res) mustBe INTERNAL_SERVER_ERROR
      }
    }

  }

  ".createVerificationBatchAndVerifications" - {

    "returns 201 Created with JSON body on success (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val responseJson = Json.obj("verifBatchResourceRef" -> 10)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.createVerificationBatchAndVerifications()(req)

      status(res) mustBe CREATED
      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe responseJson
    }

    "returns 201 Created with JSON body on success (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val responseJson = Json.obj("verifBatchResourceRef" -> 10)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.createVerificationBatchAndVerifications()(req)

      status(res) mustBe CREATED
      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe responseJson
    }

    "returns 400 BadRequest when JSON is invalid" in new Setup {
      val invalidJson = Json.obj("instanceId" -> instanceId) // missing required fields

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidJson)

      val res: Future[Result] = controller.createVerificationBatchAndVerifications()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
      (contentAsJson(res) \ "errors").isDefined mustBe true
    }

    "returns 502 BadGateway for taxOfficeNumber = 502 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.createVerificationBatchAndVerifications()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 InternalServerError for taxOfficeNumber = 500 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.createVerificationBatchAndVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when no contractor enrolment and no agent enrolment found" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.createVerificationBatchAndVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }
  }

  ".modifyVerifications" - {

    val postUrl = "/cis/verification-batch/modify"

    val validJson: JsValue =
      Json.toJson(
        ModifyVerificationsRequest(
          instanceId = "abc-123",
          deleteVerifications = Some(
            DeleteVerifications(
              verificationResourceReferences = Seq(111L, 222L)
            )
          ),
          createVerifications = Some(
            CreateVerifications(
              verificationBatchResourceRef = 10L,
              verificationResourceReferences = Seq(333L, 444L),
              actionIndicator = Some("A")
            )
          )
        )
      )

    "returns 204 NO_CONTENT on success delete and create verifications (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success delete and create verifications (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success delete verifications (contractor enrolment)" in new Setup {
      val deleteVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = Some(
              DeleteVerifications(
                verificationResourceReferences = Seq(111L, 222L)
              )
            ),
            createVerifications = None
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(deleteVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success delete verifications (agent enrolment)" in new Setup {
      val deleteVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = Some(
              DeleteVerifications(
                verificationResourceReferences = Seq(111L, 222L)
              )
            ),
            createVerifications = None
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(deleteVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success create verifications with actionIndicator (contractor enrolment)" in new Setup {
      val createVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = None,
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq(333L, 444L),
                actionIndicator = Some("A")
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(createVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success create verifications with actionIndicator (agent enrolment)" in new Setup {
      val createVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = None,
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq(333L, 444L),
                actionIndicator = Some("A")
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(createVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success create verifications without actionIndicator (contractor enrolment)" in new Setup {
      val createVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = None,
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq(333L, 444L),
                actionIndicator = None
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(createVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success create verifications without actionIndicator (agent enrolment)" in new Setup {
      val createVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = None,
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq(333L, 444L),
                actionIndicator = None
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(createVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 400 BadRequest when JSON is invalid" in new Setup {
      val invalidJson = Json.obj() // missing required fields

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
      (contentAsJson(res) \ "errors").isDefined mustBe true
    }

    "returns 502 BadGateway for taxOfficeNumber = 502 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 InternalServerError for taxOfficeNumber = 500 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when no contractor enrolment and no agent enrolment found" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }

    "returns 500 InternalServerError when only instanceId is present (contractor enrolment)" in new Setup {
      val missingRequiredFieldsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = None,
            createVerifications = None
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(missingRequiredFieldsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when only instanceId is present (agent enrolment)" in new Setup {
      val missingRequiredFieldsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = None,
            createVerifications = None
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(missingRequiredFieldsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when deleteVerifications is provided but verificationResourceReferences is empty (contractor enrolment)" in new Setup {
      val invalidDeleteVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = Some(
              DeleteVerifications(
                verificationResourceReferences = Seq.empty
              )
            ),
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq(333L, 444L),
                actionIndicator = Some("A")
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidDeleteVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when deleteVerifications is provided but verificationResourceReferences is empty (agent enrolment)" in new Setup {
      val invalidDeleteVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = Some(
              DeleteVerifications(
                verificationResourceReferences = Seq.empty
              )
            ),
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq(333L, 444L),
                actionIndicator = Some("A")
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidDeleteVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when createVerifications is provided but verificationResourceReferences is empty (contractor enrolment)" in new Setup {
      val invalidCreateVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = Some(
              DeleteVerifications(
                verificationResourceReferences = Seq(111L, 222L)
              )
            ),
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq.empty,
                actionIndicator = Some("A")
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidCreateVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when when createVerifications is provided but verificationResourceReferences is empty (agent enrolment)" in new Setup {
      val invalidCreateVerificationsJson: JsValue =
        Json.toJson(
          ModifyVerificationsRequest(
            instanceId = "abc-123",
            deleteVerifications = Some(
              DeleteVerifications(
                verificationResourceReferences = Seq(111L, 222L)
              )
            ),
            createVerifications = Some(
              CreateVerifications(
                verificationBatchResourceRef = 10L,
                verificationResourceReferences = Seq.empty,
                actionIndicator = Some("A")
              )
            )
          )
        )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidCreateVerificationsJson)

      val res: Future[Result] = controller.modifyVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }
  }

  ".createSubmissionForVerification" - {

    val postUrl = "/cis/verification-batch/submission/create"

    val validSubmissionJson: JsValue =
      Json.toJson(
        CreateSubmissionAndUpdateVerificationsRequest(
          instanceId = instanceId,
          verificationBatchId = 99L,
          verificationBatchResourceRef = 10L,
          emailRecipient = Some("ops@example.com"),
          irMarkGenerated = Some("IR_MARK"),
          verifications = Seq(
            uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.VerificationToUpdate(
              subcontractorName = "ACME",
              verificationResourceRef = 111L,
              proceedVerification = "Y"
            ),
            uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.VerificationToUpdate(
              subcontractorName = "BETA",
              verificationResourceRef = 222L,
              proceedVerification = "N"
            )
          ),
          agentId = None
        )
      )

    "returns 201 Created with JSON body on success (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val responseJson = Json.obj("submissionId" -> 555)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validSubmissionJson)

      val res: Future[Result] = controller.createSubmissionAndUpdateVerifications()(req)

      status(res) mustBe CREATED
      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe responseJson
    }

    "returns 201 Created with JSON body on success (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val responseJson = Json.obj("submissionId" -> 555)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validSubmissionJson)

      val res: Future[Result] = controller.createSubmissionAndUpdateVerifications()(req)

      status(res) mustBe CREATED
      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe responseJson
    }

    "returns 400 BadRequest when JSON is invalid" in new Setup {
      val invalidJson = Json.obj(
        "instanceId" -> instanceId
      )

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidJson)

      val res: Future[Result] = controller.createSubmissionAndUpdateVerifications()(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)

      val body = contentAsJson(res)
      (body \ "message").as[String] mustBe "Invalid payload"
      (body \ "errors").isDefined mustBe true
    }

    "returns 502 BadGateway for taxOfficeNumber = 502 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validSubmissionJson)

      val res: Future[Result] = controller.createSubmissionAndUpdateVerifications()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 InternalServerError for taxOfficeNumber = 500 (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validSubmissionJson)

      val res: Future[Result] = controller.createSubmissionAndUpdateVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when no contractor enrolment and no agent enrolment found" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validSubmissionJson)

      val res: Future[Result] = controller.createSubmissionAndUpdateVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }
  }

  ".updateVerificationSubmission" - {

    val updateVerificationSubmissionUrl = "/cis/verification/submission/update"

    val validUpdateJson: JsValue = Json.toJson(
      UpdateVerificationSubmissionRequest(
        instanceId = instanceId,
        verificationBatchResourceRef = 77L,
        submissionRequestDate = Some(LocalDateTime.parse("2026-06-15T10:05:00")),
        hmrcMarkGenerated = Some("IR_MARK"),
        submittableStatus = "FATAL_ERROR",
        govtalkErrorCode = Some("500"),
        govtalkErrorType = Some("timeOut"),
        govtalkErrorMessage = Some("timeOut")
      )
    )

    "returns 204 NoContent on valid payload (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val req = FakeRequest(POST, updateVerificationSubmissionUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validUpdateJson)

      val res: Future[Result] = controller.updateVerificationSubmission()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 204 NoContent on valid payload (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, updateVerificationSubmissionUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validUpdateJson)

      val res: Future[Result] = controller.updateVerificationSubmission()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 400 BadRequest when JSON is invalid" in new Setup {
      val invalidJson = Json.obj("bad" -> "data")

      val req = FakeRequest(POST, updateVerificationSubmissionUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidJson)

      val res: Future[Result] = controller.updateVerificationSubmission()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  ".processVerificationResponseFromChris" - {

    val postUrl = "/cis/verification/response/process"

    val validProcessResponseJson: JsValue =
      Json.toJson(
        ProcessVerificationResponseFromChrisRequest(
          instanceId = instanceId,
          verificationBatchResourceRef = 10L,
          acceptedTime = "2026-06-15T10:05:00Z",
          submissionStatus = "ACCEPTED",
          irMarkReceived = Some("IR_MARK_GGIS"),
          verificationResults = Seq(
            VerificationResult(
              resourceRef = 111L,
              matched = Some("Y"),
              verified = Some("Y"),
              verificationNumber = Some("V123456"),
              taxTreatment = "NET",
              verifiedDate = Some(LocalDateTime.of(2026, 6, 15, 10, 5, 0))
            ),
            VerificationResult(
              resourceRef = 222L,
              matched = Some("N"),
              verified = Some("N"),
              verificationNumber = Some("V654321"),
              taxTreatment = "GROSS",
              verifiedDate = Some(LocalDateTime.of(2026, 6, 15, 10, 6, 0))
            )
          )
        )
      )

    "returns 204 NO_CONTENT on success (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validProcessResponseJson)

      val res: Future[Result] = controller.processVerificationResponseFromChris()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 204 NO_CONTENT on success (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validProcessResponseJson)

      val res: Future[Result] = controller.processVerificationResponseFromChris()(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 400 BadRequest when JSON is invalid" in new Setup {
      val invalidJson = Json.obj(
        "instanceId" -> instanceId
      )

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidJson)

      val res: Future[Result] = controller.processVerificationResponseFromChris()(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)

      val body = contentAsJson(res)
      (body \ "message").as[String] mustBe "Invalid payload"
      (body \ "errors").isDefined mustBe true
    }
  }

  ".getSubmissionWithVerificationBatchByRefs" - {

    val instanceId                   = "abc-123"
    val verificationBatchResourceRef = 77L
    val url                          =
      s"/cis/verification/submission-batch/$instanceId/$verificationBatchResourceRef"

    val responsePath =
      "/resources/verification/getSubmissionWithVerificationBatch-200-response.json"

    "returns 200 OK without performing an enrolment check" in new Setup {
      val responseJson: JsValue =
        Json.parse(
          s"""
             |{
             |  "scheme": {
             |    "schemeId": 1,
             |    "instanceId": "$instanceId"
             |  },
             |  "subcontractors": [
             |    {
             |      "subcontractorId": 1,
             |      "subbieResourceRef": 10
             |    }
             |  ],
             |  "verifications": [
             |    {
             |      "verificationId": 1001,
             |      "verificationResourceRef": 201
             |    }
             |  ],
             |  "verificationBatch": {
             |    "verificationBatchId": 99,
             |    "verificationBatchResourceRef": $verificationBatchResourceRef
             |  },
             |  "submission": {
             |    "submissionId": 555,
             |    "submissionType": "CIS_VERIFY"
             |  }
             |}
             |""".stripMargin
        )

      when(mockResourceHelper.resourceAsString(responsePath))
        .thenReturn(responseJson.toString())

      val request: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, url)
          .withHeaders(ACCEPT -> JSON)

      val result: Future[Result] =
        controller.getSubmissionWithVerificationBatchByRefs(
          instanceId,
          verificationBatchResourceRef
        )(request)

      status(result) mustBe OK
      contentType(result) mustBe Some(JSON)
      contentAsJson(result) mustBe responseJson

      val body =
        contentAsJson(result)

      (body \ "scheme" \ "schemeId").as[Long] mustBe 1L
      (body \ "scheme" \ "instanceId").as[String] mustBe instanceId
      (body \ "subcontractors")(0)
        .\("subcontractorId")
        .as[Long] mustBe 1L
      (body \ "verifications")(0)
        .\("verificationId")
        .as[Long] mustBe 1001L
      (body \ "verificationBatch" \ "verificationBatchResourceRef")
        .as[Long] mustBe verificationBatchResourceRef
      (body \ "submission" \ "submissionId")
        .as[Long] mustBe 555L

      verify(mockResourceHelper).resourceAsString(responsePath)
      verifyNoInteractions(mockEnrolmentsHelper)
    }

    "returns 200 OK through the application route without authentication or session headers" in {
      val request: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(
          GET,
          "/formp-proxy/cis/verification/submission-batch/10001/5"
        ).withHeaders(ACCEPT -> JSON)

      val result: Future[Result] =
        route(app, request).value

      status(result) mustBe OK
      contentType(result) mustBe Some(JSON)

      val body =
        contentAsJson(result)

      (body \ "scheme" \ "instanceId")
        .as[String] mustBe "10001"

      (body \ "verificationBatch" \ "verificationBatchResourceRef")
        .as[Long] mustBe 5L

      (body \ "submission" \ "submissionId")
        .as[Long] mustBe 90001L

      (body \ "verifications")
        .as[Seq[JsValue]]
        .size mustBe 1

      (body \ "subcontractors")
        .as[Seq[JsValue]]
        .size mustBe 1
    }
  }

  ".getSubmittedVerifications" - {

    val postUrl = "/cis/verification/submitted-verifications"

    val validJson: JsValue =
      Json.toJson(
        GetSubmittedVerificationsRequest(
          instanceId = "abc-123"
        )
      )

    val responseJson: JsValue =
      Json.obj(
        "scheme"              -> Json.arr(),
        "subcontractors"      -> Json.arr(),
        "verificationBatches" -> Json.arr(),
        "verifications"       -> Json.arr(),
        "submissions"         -> Json.arr()
      )

    "returns 200 OK with JSON body on success (contractor enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.getSubmittedVerifications()(req)

      status(res) mustBe OK
      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe responseJson
    }

    "returns 200 OK with JSON body on success (agent enrolment)" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("IRAgentReference-123"))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(responseJson.toString())

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.getSubmittedVerifications()(req)

      status(res) mustBe OK
      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe responseJson
    }

    "returns 400 BadRequest when JSON is invalid" in new Setup {
      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(Json.obj("invalid" -> "payload"))

      val res: Future[Result] = controller.getSubmittedVerifications()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
      (contentAsJson(res) \ "errors").isDefined mustBe true
    }

    "returns 502 BadGateway for taxOfficeNumber = 502" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.getSubmittedVerifications()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 InternalServerError for taxOfficeNumber = 500" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.getSubmittedVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 InternalServerError when neither contractor nor agent enrolment exists" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req = FakeRequest(POST, postUrl)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(validJson)

      val res: Future[Result] = controller.getSubmittedVerifications()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }
  }

  ".getSubmissionWithVerificationBatch" - {

    val postUrl =
      "/cis/verification/submission-batch"

    val responsePath =
      "/resources/verification/getSubmissionWithVerificationBatch-200-response.json"

    val validJson: JsValue =
      Json.toJson(
        GetSubmissionWithVerificationBatchRequest(
          instanceId = "10001",
          verificationBatchResourceRef = 5L
        )
      )

    val responseJson: JsValue =
      Json.obj(
        "scheme"            -> JsNull,
        "submission"        -> JsNull,
        "verificationBatch" -> JsNull,
        "verifications"     -> Json.arr(),
        "subcontractors"    -> Json.arr()
      )

    "returns 200 OK with JSON body for a valid payload" in new Setup {
      when(mockResourceHelper.resourceAsString(responsePath))
        .thenReturn(responseJson.toString())

      val request =
        FakeRequest(POST, postUrl)
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(validJson)

      val result: Future[Result] =
        controller.getSubmissionWithVerificationBatch()(request)

      status(result) mustBe OK
      contentType(result) mustBe Some(JSON)
      contentAsJson(result) mustBe responseJson

      verify(mockResourceHelper).resourceAsString(responsePath)
      verifyNoInteractions(mockEnrolmentsHelper)
    }

    "returns 400 BadRequest for an invalid payload" in new Setup {
      val request =
        FakeRequest(POST, postUrl)
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(Json.obj("invalid" -> "payload"))

      val result: Future[Result] =
        controller.getSubmissionWithVerificationBatch()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
      (contentAsJson(result) \ "errors").isDefined mustBe true

      verifyNoInteractions(mockResourceHelper)
      verifyNoInteractions(mockEnrolmentsHelper)
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new VerificationController(auth, mockResourceHelper, mockEnrolmentsHelper, cc)
  }
}
