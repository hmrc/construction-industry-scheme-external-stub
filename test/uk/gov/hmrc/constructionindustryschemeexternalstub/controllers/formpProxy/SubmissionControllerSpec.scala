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
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{EmployerReference, GovTalkErrorStatus}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.{CreateSubmissionRequest, UpdateSubmissionRequest}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class SubmissionControllerSpec extends SpecBase {

  ".createSubmission" - {

    val createSubmissionUrl = "/submissions"

    "returns 201 Created with submissionId on valid payload for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val json: JsValue = Json.toJson(
        CreateSubmissionRequest(
          instanceId = "123",
          taxYear = 2024,
          taxMonth = 4,
          amendment = "N",
          hmrcMarkGenerated = Some("Dj5TVJDyRYCn9zta5EdySeY4fyA="),
          emailRecipient = Some("test@test.com")
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, createSubmissionUrl)
      val res: Future[Result]       = controller.createSubmission()(req)

      status(res) mustBe CREATED

      val responseJson = contentAsJson(res)
      val submissionId = (responseJson \ "submissionId").as[String]

      submissionId.toLong must be >= 90001L
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject             = Json.obj("nope" -> "nope")
      val req: FakeRequest[JsValue] = makeJsonRequest(bad, createSubmissionUrl)
      val res: Future[Result]       = controller.createSubmission()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val json: JsValue             = Json.toJson(CreateSubmissionRequest("123", 2024, 4, "N"))
      val req: FakeRequest[JsValue] = makeJsonRequest(json, createSubmissionUrl)
      val res: Future[Result]       = controller.createSubmission()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentAsJson(res) mustBe Json.obj("message" -> "Unexpected error")
    }
  }

  ".updateSubmission" - {

    val updateSubmissionUrl = "/submissions/update"

    "returns 204 NoContent on valid payload for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val json: JsValue = Json.toJson(
        UpdateSubmissionRequest(
          instanceId = "123",
          taxYear = 2024,
          taxMonth = 4,
          amendment = "N",
          hmrcMarkGenerated = "Dj5TVJDyRYCn9zta5EdySeY4fyA=",
          submittableStatus = "ACCEPTED"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubmissionUrl)
      val res: Future[Result]       = controller.updateSubmission()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject = Json.obj("bad" -> "json")

      val req: FakeRequest[JsValue] = makeJsonRequest(bad, updateSubmissionUrl)
      val res: Future[Result]       = controller.updateSubmission()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }

    "accepts a payload carrying a typed govTalkResponse and returns 204" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val json: JsValue = Json.toJson(
        UpdateSubmissionRequest(
          instanceId = "123",
          taxYear = 2024,
          taxMonth = 4,
          hmrcMarkGenerated = "Dj5TVJDyRYCn9zta5EdySeY4fyA=",
          submittableStatus = "FATAL_ERROR",
          amendment = "N",
          govTalkResponse = Some(GovTalkErrorStatus.ServerError(503))
        )
      )

      (json \ "govTalkResponse" \ "kind").as[String] mustBe "ServerError"
      (json \ "govTalkResponse" \ "httpStatus").as[Int] mustBe 503

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubmissionUrl)
      val res: Future[Result]       = controller.updateSubmission()(req)

      status(res) mustBe NO_CONTENT
    }

    "maps service failure to 500 (no body expected) for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val json: JsValue = Json.toJson(
        UpdateSubmissionRequest(
          instanceId = "123",
          taxYear = 2024,
          taxMonth = 4,
          amendment = "N",
          hmrcMarkGenerated = "Dj5TVJDyRYCn9zta5EdySeY4fyA=",
          submittableStatus = "ACCEPTED"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateSubmissionUrl)
      val res: Future[Result]       = controller.updateSubmission()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new SubmissionController(auth, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, "/submissions")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
