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
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.FakeAuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.GetGovTalkStatusRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class GovTalkControllerSpec extends SpecBase {

  ".getGovTalkStatus" - {

    val getGovTalkStatusUrl = "/cis/govtalkstatus/get"

    "returns 200 with valid data on valid payload for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      val response: JsObject =
        Json.obj(
          "govtallk_status" -> Json.arr(
            Json.obj(
              "userIdentifier"  -> "1",
              "formResultID"    -> "12890",
              "correlationID"   -> "C742D5DEE7EB4D15B4F7EFD50B890525",
              "formLock"        -> "false",
              "createDate"      -> "2026-02-03T00:00:00",
              "endStateDate"    -> JsNull,
              "lastMessageDate" -> "2026-02-03T00:00:00",
              "numPolls"        -> 0,
              "pollInterval"    -> 0,
              "protocolStatus"  -> "dataRequest",
              "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
            )
          )
        )

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(response.toString)

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe response
    }

    "returns 200 with empty array on valid payload for for taxOfficeNumber = 404" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("404", "")))

      val response: JsObject =
        Json.obj(
          "govtallk_status" -> Json.arr()
        )

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(response.toString)

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe response
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject             = Json.obj("nope" -> "nope")
      val req: FakeRequest[JsValue] = makeJsonRequest(bad, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }

    "propagates UpstreamErrorResponse for taxOfficeNumber = 502" in new Setup {

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 500 with generic message for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller      = new GovTalkController(auth, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, "/submissions")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
