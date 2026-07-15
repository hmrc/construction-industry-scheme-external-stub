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

import org.mockito.Mockito.*
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper

import java.time.LocalDateTime
import scala.concurrent.Future

class GovTalkControllerSpec extends SpecBase {

  ".getGovTalkStatus" - {

    val getGovTalkStatusUrl = "/cis/govtalkstatus/get"
    val responsePath        = "/resources/govTalk/getGovTalkStatus-200-response.json"

    def validRequestBody: JsValue =
      Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

    def okResponse: JsObject =
      Json.obj(
        "govtalk_status" -> Json.arr(
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

    "returns 200 with valid data when stage=polling" in new Setup {
      when(mockResourceHelper.resourceAsString(responsePath))
        .thenReturn(okResponse.toString)

      val request: FakeRequest[JsValue] =
        makeJsonRequest(validRequestBody, s"$getGovTalkStatusUrl?stage=polling")

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe okResponse
      verify(mockResourceHelper).resourceAsString(responsePath)
    }

    "returns 404 when stage=initial" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(validRequestBody, s"$getGovTalkStatusUrl?stage=initial")

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe NOT_FOUND
      verifyNoInteractions(mockResourceHelper)
    }

    "returns 200 with valid data when stage=initial and batchPoll=true" in new Setup {
      when(mockResourceHelper.resourceAsString(responsePath))
        .thenReturn(okResponse.toString)

      val request: FakeRequest[JsValue] =
        makeJsonRequest(validRequestBody, s"$getGovTalkStatusUrl?stage=initial&batchPoll=true")

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe okResponse
      verify(mockResourceHelper).resourceAsString(responsePath)
    }

    "returns 404 when stage is missing" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(validRequestBody, getGovTalkStatusUrl)

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe NOT_FOUND
      verifyNoInteractions(mockResourceHelper)
    }

    "returns 404 for scenario 404" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(
          validRequestBody,
          s"$getGovTalkStatusUrl?stage=polling&scenario=404"
        )

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe NOT_FOUND
      verifyNoInteractions(mockResourceHelper)
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {
      val invalidBody: JsObject = Json.obj("nope" -> "nope")

      val request: FakeRequest[JsValue] =
        makeJsonRequest(invalidBody, s"$getGovTalkStatusUrl?stage=polling")

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
      verifyNoInteractions(mockResourceHelper)
    }

    "returns 502 for scenario 502" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(
          validRequestBody,
          s"$getGovTalkStatusUrl?stage=polling&scenario=502"
        )

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe BAD_GATEWAY
      (contentAsJson(result) \ "message").as[String] must include("formp failed")
      verifyNoInteractions(mockResourceHelper)
    }

    "returns 500 for scenario 500" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(
          validRequestBody,
          s"$getGovTalkStatusUrl?stage=polling&scenario=500"
        )

      val result: Future[Result] = controller.getGovTalkStatus()(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "message").as[String] mustBe "Unexpected error"
      verifyNoInteractions(mockResourceHelper)
    }
  }

  ".updateGovTalkStatusCorrelationId" - {

    val updateUrl = "/cis/govtalkstatus/update-correlationID"

    "returns 204 NoContent for valid JSON payload" in new Setup {
      val json: JsValue = Json.obj(
        "userIdentifier" -> "1",
        "formResultID"   -> "12890",
        "correlationID"  -> "C742D5DEE7EB4D15B4F7EFD50B890525",
        "pollInterval"   -> 1,
        "gatewayURL"     -> "http://example.com"
      )

      val request: FakeRequest[JsValue] = makeJsonRequest(json, updateUrl)
      val result: Future[Result]        = controller.updateGovTalkStatusCorrelationId(request)

      status(result) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON payload" in new Setup {
      val invalidBody: JsValue = Json.obj("nope" -> "nope")

      val request: FakeRequest[JsValue] = makeJsonRequest(invalidBody, updateUrl)
      val result: Future[Result]        = controller.updateGovTalkStatusCorrelationId(request)

      status(result) mustBe BAD_REQUEST
    }
  }

  ".resetGovTalkStatus" - {

    val resetGovTalkStatusUrl = "/cis/govtalkstatus/reset"

    def validResetBody: JsValue =
      Json.toJson(
        ResetGovTalkStatusRequest(
          userIdentifier = "1",
          formResultID = "12890",
          oldProtocolStatus = "dataRequest",
          gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )
      )

    "returns 204 with a valid payload" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(validResetBody, resetGovTalkStatusUrl)

      val result: Future[Result] = controller.resetGovTalkStatus()(request)

      status(result) mustBe NO_CONTENT
    }

    "returns 500 for scenario 500" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(validResetBody, s"$resetGovTalkStatusUrl?scenario=500")

      val result: Future[Result] = controller.resetGovTalkStatus()(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "message").as[String] mustBe "Unexpected error"
    }

    "returns 502 for scenario 502" in new Setup {
      val request: FakeRequest[JsValue] =
        makeJsonRequest(validResetBody, s"$resetGovTalkStatusUrl?scenario=502")

      val result: Future[Result] = controller.resetGovTalkStatus()(request)

      status(result) mustBe BAD_GATEWAY
      (contentAsJson(result) \ "message").as[String] must include("formp failed")
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {
      val invalidBody: JsObject = Json.obj("nope" -> "nope")

      val request: FakeRequest[JsValue] =
        makeJsonRequest(invalidBody, resetGovTalkStatusUrl)

      val result: Future[Result] = controller.resetGovTalkStatus()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  ".updateGovTalkStatus" - {

    val updateGovTalkStatusUrl = "/cis/govtalkstatus/update-status"

    "returns 204 for a valid payload" in new Setup {
      val json: JsValue =
        Json.toJson(
          UpdateGovTalkStatusRequest(
            userIdentifier = "123",
            formResultID = "YE2025",
            endStateDate = Some(LocalDateTime.parse("2026-02-03T00:00:00")),
            protocolStatus = "dataRequest"
          )
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, updateGovTalkStatusUrl)

      val result: Future[Result] = controller.updateGovTalkStatus()(request)

      status(result) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {
      val invalidBody: JsObject = Json.obj("nope" -> "nope")

      val request: FakeRequest[JsValue] =
        makeJsonRequest(invalidBody, updateGovTalkStatusUrl)

      val result: Future[Result] = controller.updateGovTalkStatus()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  ".updateGovTalkStatusStatistics" - {

    val updateGovTalkStatusStatisticsUrl = "/cis/govtalkstatus/update-statistics"

    "returns 204 NoContent on valid payload" in new Setup {
      val json: JsValue =
        Json.toJson(
          UpdateGovTalkStatusStatisticsRequest(
            userIdentifier = "123456789",
            formResultID = "SUB123456",
            lastMessageDate = LocalDateTime.parse("2026-02-16T10:30:00"),
            numPolls = 3,
            pollInterval = 300,
            gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
          )
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)

      val result: Future[Result] =
        controller.updateGovTalkStatusStatistics()(request)

      status(result) mustBe NO_CONTENT
    }

    "returns 204 NoContent with zero polls" in new Setup {
      val json: JsValue =
        Json.toJson(
          UpdateGovTalkStatusStatisticsRequest(
            userIdentifier = "123456789",
            formResultID = "SUB123456",
            lastMessageDate = LocalDateTime.parse("2026-02-16T10:30:00"),
            numPolls = 0,
            pollInterval = 0,
            gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
          )
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)

      val result: Future[Result] =
        controller.updateGovTalkStatusStatistics()(request)

      status(result) mustBe NO_CONTENT
    }

    "returns 204 NoContent with high poll numbers" in new Setup {
      val json: JsValue =
        Json.toJson(
          UpdateGovTalkStatusStatisticsRequest(
            userIdentifier = "123456789",
            formResultID = "SUB123456",
            lastMessageDate = LocalDateTime.parse("2026-02-16T10:30:00"),
            numPolls = 100,
            pollInterval = 3600,
            gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
          )
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)

      val result: Future[Result] =
        controller.updateGovTalkStatusStatistics()(request)

      status(result) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {
      val invalidBody: JsObject = Json.obj("nope" -> "nope")

      val request: FakeRequest[JsValue] =
        makeJsonRequest(invalidBody, updateGovTalkStatusStatisticsUrl)

      val result: Future[Result] =
        controller.updateGovTalkStatusStatistics()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
    }

    "returns 400 BadRequest for missing userIdentifier" in new Setup {
      val json: JsObject =
        Json.obj(
          "formResultID"    -> "SUB123456",
          "lastMessageDate" -> "2026-02-16T10:30:00",
          "numPolls"        -> 3,
          "pollInterval"    -> 300,
          "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)

      val result: Future[Result] =
        controller.updateGovTalkStatusStatistics()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
    }

    "returns 400 BadRequest for missing formResultID" in new Setup {
      val json: JsObject =
        Json.obj(
          "userIdentifier"  -> "123456789",
          "lastMessageDate" -> "2026-02-16T10:30:00",
          "numPolls"        -> 3,
          "pollInterval"    -> 300,
          "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)

      val result: Future[Result] =
        controller.updateGovTalkStatusStatistics()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
    }

    "returns 400 BadRequest for invalid date format" in new Setup {
      val json: JsObject =
        Json.obj(
          "userIdentifier"  -> "123456789",
          "formResultID"    -> "SUB123456",
          "lastMessageDate" -> "invalid-date",
          "numPolls"        -> 3,
          "pollInterval"    -> 300,
          "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)

      val result: Future[Result] =
        controller.updateGovTalkStatusStatistics()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  ".createGovTalkStatusRecord" - {

    val createGovTalkStatusUrl = "/cis/govtalkstatus/create"

    "returns 201 for a valid payload" in new Setup {
      val json: JsValue =
        Json.toJson(
          CreateGovTalkStatusRecordRequest(
            userIdentifier = "1",
            formResultID = "12890",
            correlationID = "C742D5DEE7EB4D15B4F7EFD50B890525",
            gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
          )
        )

      val request: FakeRequest[JsValue] =
        makeJsonRequest(json, createGovTalkStatusUrl)

      val result: Future[Result] =
        controller.createGovTalkStatusRecord()(request)

      status(result) mustBe CREATED
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {
      val invalidBody: JsObject = Json.obj("nope" -> "nope")

      val request: FakeRequest[JsValue] =
        makeJsonRequest(invalidBody, createGovTalkStatusUrl)

      val result: Future[Result] =
        controller.createGovTalkStatusRecord()(request)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper = mock[ResourceHelper]

    lazy val controller =
      new GovTalkController(
        resourceHelper = mockResourceHelper,
        cc = cc
      )

    def makeJsonRequest(
      body: JsValue,
      url: String
    ): FakeRequest[JsValue] =
      FakeRequest(POST, url)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
