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
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.FakeAuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import java.time.LocalDateTime
import scala.concurrent.Future

class GovTalkControllerSpec extends SpecBase {

  ".getGovTalkStatus" - {

    val getGovTalkStatusUrl = "/cis/govtalkstatus/get"

//    "returns 200 with valid data on valid payload for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {
//
//      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
//        .thenReturn(Some(EmployerReference("200", "")))
//
//      val response: JsObject =
//        Json.obj(
//          "govtalk_status" -> Json.arr(
//            Json.obj(
//              "userIdentifier"  -> "1",
//              "formResultID"    -> "12890",
//              "correlationID"   -> "C742D5DEE7EB4D15B4F7EFD50B890525",
//              "formLock"        -> "false",
//              "createDate"      -> "2026-02-03T00:00:00",
//              "endStateDate"    -> JsNull,
//              "lastMessageDate" -> "2026-02-03T00:00:00",
//              "numPolls"        -> 0,
//              "pollInterval"    -> 0,
//              "protocolStatus"  -> "dataRequest",
//              "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
//            )
//          )
//        )
//
//      val json: JsValue = Json.toJson(
//        GetGovTalkStatusRequest(
//          userIdentifier = "123",
//          formResultID = "YE2025"
//        )
//      )
//
//      when(mockResourceHelper.resourceAsString(any()))
//        .thenReturn(response.toString)
//
//      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
//      val res: Future[Result]       = controller.getGovTalkStatus()(req)
//
//      status(res) mustBe OK
//      contentAsJson(res) mustBe response
//    }
//
//    "returns 200 with valid data on valid payload for an unknown agentReference" in new Setup {
//
//      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
//        .thenReturn(None)
//
//      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
//        .thenReturn(Some("agentRef"))
//
//      val response: JsObject =
//        Json.obj(
//          "govtalk_status" -> Json.arr(
//            Json.obj(
//              "userIdentifier"  -> "1",
//              "formResultID"    -> "12890",
//              "correlationID"   -> "C742D5DEE7EB4D15B4F7EFD50B890525",
//              "formLock"        -> "false",
//              "createDate"      -> "2026-02-03T00:00:00",
//              "endStateDate"    -> JsNull,
//              "lastMessageDate" -> "2026-02-03T00:00:00",
//              "numPolls"        -> 0,
//              "pollInterval"    -> 0,
//              "protocolStatus"  -> "dataRequest",
//              "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
//            )
//          )
//        )
//
//      val json: JsValue = Json.toJson(
//        GetGovTalkStatusRequest(
//          userIdentifier = "123",
//          formResultID = "YE2025"
//        )
//      )
//
//      when(mockResourceHelper.resourceAsString(any()))
//        .thenReturn(response.toString)
//
//      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
//      val res: Future[Result]       = controller.getGovTalkStatus()(req)
//
//      status(res) mustBe OK
//      contentAsJson(res) mustBe response
//    }

    "returns 404 on valid payload for taxOfficeNumber = 404" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("404", "")))

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe NOT_FOUND
    }

    "returns 404 on valid payload for agentReference = AGT404" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("AGT404"))

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe NOT_FOUND
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

    "returns 500 with generic message for invalid enrollment" in new Setup {

      val json: JsValue = Json.toJson(
        GetGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025"
        )
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(None)
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(None)

      val req: FakeRequest[JsValue] = makeJsonRequest(json, getGovTalkStatusUrl)
      val res: Future[Result]       = controller.getGovTalkStatus()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
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

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusCorrelationId(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON payload" in new Setup {
      val bad: JsValue = Json.obj("nope" -> "nope")

      val req: FakeRequest[JsValue] = makeJsonRequest(bad, updateUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusCorrelationId(req)

      status(res) mustBe BAD_REQUEST
    }
  }

  ".resetGovTalkStatus" - {

    val resetGovTalkStatusUrl = "/cis/govtalkstatus/reset"

    "returns 204 on valid payload for an unknown taxOfficeNumber / taxOfficeReference / agent" in new Setup {

      val json: JsValue = Json.toJson(
        ResetGovTalkStatusRequest(
          userIdentifier = "1",
          formResultID = "12890",
          oldProtocolStatus = "dataRequest",
          gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, resetGovTalkStatusUrl)
      val res: Future[Result]       = controller.resetGovTalkStatus()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject             = Json.obj("nope" -> "nope")
      val req: FakeRequest[JsValue] = makeJsonRequest(bad, resetGovTalkStatusUrl)
      val res: Future[Result]       = controller.resetGovTalkStatus()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  ".updateGovTalkStatus" - {

    val updateGovTalkStatusUrl = "/cis/govtalkstatus/update-status"

    "returns 204 on valid payload for an unknown taxOfficeNumber / taxOfficeReference / agent enrollments" in new Setup {

      val response: JsObject =
        Json.obj(
          "govtalk_status" -> Json.arr(
            Json.obj(
              "userIdentifier" -> "1",
              "formResultID"   -> "12890",
              "endStateDate"   -> "2026-02-03T00:00:00",
              "protocolStatus" -> "dataRequest"
            )
          )
        )

      val json: JsValue = Json.toJson(
        UpdateGovTalkStatusRequest(
          userIdentifier = "123",
          formResultID = "YE2025",
          endStateDate = Some(LocalDateTime.parse("2026-02-03T00:00:00")),
          protocolStatus = "dataRequest"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateGovTalkStatusUrl)
      val res: Future[Result]       = controller.updateGovTalkStatus()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject             = Json.obj("nope" -> "nope")
      val req: FakeRequest[JsValue] = makeJsonRequest(bad, updateGovTalkStatusUrl)
      val res: Future[Result]       = controller.updateGovTalkStatus()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  ".updateGovTalkStatusStatistics" - {

    val updateGovTalkStatusStatisticsUrl = "/cis/govtalkstatus/update-statistics"

    "returns 204 NoContent on valid payload for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {

      val json: JsValue = Json.toJson(
        UpdateGovTalkStatusStatisticsRequest(
          userIdentifier = "123456789",
          formResultID = "SUB123456",
          lastMessageDate = java.time.LocalDateTime.parse("2026-02-16T10:30:00"),
          numPolls = 3,
          pollInterval = 300,
          gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusStatistics()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 204 NoContent with zero polls" in new Setup {

      val json: JsValue = Json.toJson(
        UpdateGovTalkStatusStatisticsRequest(
          userIdentifier = "123456789",
          formResultID = "SUB123456",
          lastMessageDate = java.time.LocalDateTime.parse("2026-02-16T10:30:00"),
          numPolls = 0,
          pollInterval = 0,
          gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusStatistics()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 204 NoContent with high poll numbers" in new Setup {

      val json: JsValue = Json.toJson(
        UpdateGovTalkStatusStatisticsRequest(
          userIdentifier = "123456789",
          formResultID = "SUB123456",
          lastMessageDate = java.time.LocalDateTime.parse("2026-02-16T10:30:00"),
          numPolls = 100,
          pollInterval = 3600,
          gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusStatistics()(req)

      status(res) mustBe NO_CONTENT
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject             = Json.obj("nope" -> "nope")
      val req: FakeRequest[JsValue] = makeJsonRequest(bad, updateGovTalkStatusStatisticsUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusStatistics()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }

    "returns 400 BadRequest for missing userIdentifier" in new Setup {

      val json: JsObject = Json.obj(
        "formResultID"    -> "SUB123456",
        "lastMessageDate" -> "2026-02-16T10:30:00",
        "numPolls"        -> 3,
        "pollInterval"    -> 300,
        "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusStatistics()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }

    "returns 400 BadRequest for missing formResultID" in new Setup {

      val json: JsObject = Json.obj(
        "userIdentifier"  -> "123456789",
        "lastMessageDate" -> "2026-02-16T10:30:00",
        "numPolls"        -> 3,
        "pollInterval"    -> 300,
        "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusStatistics()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }

    "returns 400 BadRequest for invalid date format" in new Setup {

      val json: JsObject = Json.obj(
        "userIdentifier"  -> "123456789",
        "formResultID"    -> "SUB123456",
        "lastMessageDate" -> "invalid-date",
        "numPolls"        -> 3,
        "pollInterval"    -> 300,
        "gatewayURL"      -> "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, updateGovTalkStatusStatisticsUrl)
      val res: Future[Result]       = controller.updateGovTalkStatusStatistics()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  ".createGovTalkStatusRecord" - {

    val createGovTalkStatusUrl = "/cis/govtalkstatus/create"

    "returns 201 on valid payload for an unknown taxOfficeNumber / taxOfficeReference / agent" in new Setup {

      val json: JsValue = Json.toJson(
        CreateGovTalkStatusRecordRequest(
          userIdentifier = "1",
          formResultID = "12890",
          correlationID = "C742D5DEE7EB4D15B4F7EFD50B890525",
          gatewayURL = "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, createGovTalkStatusUrl)
      val res: Future[Result]       = controller.createGovTalkStatusRecord()(req)

      status(res) mustBe CREATED
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject             = Json.obj("nope" -> "nope")
      val req: FakeRequest[JsValue] = makeJsonRequest(bad, createGovTalkStatusUrl)
      val res: Future[Result]       = controller.createGovTalkStatusRecord()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  private trait Setup {
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    private val auth: FakeAuthAction = new FakeAuthAction(cc.parsers)
    lazy val controller              = new GovTalkController(auth, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, url)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
