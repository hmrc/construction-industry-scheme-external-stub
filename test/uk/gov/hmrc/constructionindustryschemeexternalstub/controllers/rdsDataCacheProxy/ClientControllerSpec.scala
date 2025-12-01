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
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{CisClientSearchResult, CisTaxpayerSearchResult}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class ClientControllerSpec extends SpecBase with MockitoSugar {

  ".getClientListDownloadStatus" - {

    "returns 200 with status 'InitiateDownload' when agentReference = InDown" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("InDown"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&serviceName=service-xyz&gracePeriod=14400")
      val res: Future[Result]                      = controller.getClientListDownloadStatus("cred-123", "service-xyz")(req)

      status(res) mustBe OK
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "status").as[String] mustBe "InitiateDownload"
    }

    "returns 200 with status 'InProgress' when agentReference = InProg" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("InProg"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&serviceName=service-xyz&gracePeriod=14400")
      val res: Future[Result]                      = controller.getClientListDownloadStatus("cred-123", "service-xyz")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "status").as[String] mustBe "InProgress"
    }

    "returns 200 with status 'Succeeded' when agentReference = Succes" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("Succes"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&serviceName=service-xyz&gracePeriod=14400")
      val res: Future[Result]                      = controller.getClientListDownloadStatus("cred-123", "service-xyz")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "status").as[String] mustBe "Succeeded"
    }

    "returns 200 with status 'Failed' when agentReference = Failed" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("Failed"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&serviceName=service-xyz&gracePeriod=14400")
      val res: Future[Result]                      = controller.getClientListDownloadStatus("cred-123", "service-xyz")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "status").as[String] mustBe "Failed"
    }

    "returns 500 with error message when agentReference = 500" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("500"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&serviceName=service-xyz&gracePeriod=14400")
      val res: Future[Result]                      = controller.getClientListDownloadStatus("cred-123", "service-xyz")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Could not map client list download status"
    }

    "returns 400 when credentialId is empty" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("400"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=&serviceName=service-xyz&gracePeriod=14400")
      val res: Future[Result]                      = controller.getClientListDownloadStatus("", "service-xyz")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and serviceName must be provided"
    }

    "returns 400 when serviceName is empty" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("400"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&serviceName=&gracePeriod=14400")
      val res: Future[Result]                      = controller.getClientListDownloadStatus("cred-123", "")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and serviceName must be provided"
    }

    "progresses PollSu from InitiateDownload to InProgress to Succeeded and then resets" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("PollSu"))

      val statuses = (1 to 8).map(_ => callOnce())

      statuses mustBe Seq(
        "InitiateDownload",
        "InProgress",
        "InProgress",
        "InProgress",
        "InProgress",
        "InProgress",
        "Succeeded",
        "InitiateDownload"
      )
    }

    "progresses PollFa from InitiateDownload to InProgress to Failed" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("PollFa"))

      val statuses = (1 to 7).map(_ => callOnce())

      statuses mustBe Seq(
        "InitiateDownload",
        "InProgress",
        "InProgress",
        "InProgress",
        "InProgress",
        "InProgress",
        "Failed"
      )
    }

    "progresses PollIn with InitiateDownload on first and terminal InitiateDownload after limit" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("PollIn"))

      val statuses = (1 to 7).map(_ => callOnce())

      statuses mustBe Seq(
        "InitiateDownload",
        "InProgress",
        "InProgress",
        "InProgress",
        "InProgress",
        "InProgress",
        "InitiateDownload"
      )
    }
  }

  ".getClientList" - {

    "returns 200 with client list when agentReference is unknown" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("200"))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(clientList).toString)

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?irAgentId=IR123456&credentialId=CRED-ABC-123")
      val res: Future[Result]                      = controller.getClientList(irAgentId = "IR123456", credentialId = "CRED-ABC-123")(req)

      status(res) mustBe OK
//      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe Json.toJson(clientList)
    }

    "returns 500 with error message when agentReference = 500" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("500"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?irAgentId=IR123456&credentialId=CRED-ABC-123")
      val res: Future[Result]                      = controller.getClientList(irAgentId = "IR123456", credentialId = "CRED-ABC-123")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Could not get client list"
    }

    "returns 400 when irAgentId is missing" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?irAgentId=&credentialId=CRED-ABC-123")
      val res: Future[Result]                      = controller.getClientList(irAgentId = "", credentialId = "CRED-ABC-123")(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and irAgentId must be provided"
    }

    "returns 400 when credentialId is missing" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?irAgentId=IR123456&credentialId=")
      val res: Future[Result]                      = controller.getClientList(irAgentId = "IR123456", credentialId = "")(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and irAgentId must be provided"
    }

  }

  private trait Setup {
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]
    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val controller                             = new ClientController(fakeAuthAction, mockResourceHelper, mockEnrolmentsHelper, cc)(using ec)

    def callOnce(): String = {
      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/cis/client-list-status?credentialId=cred-123&serviceName=service-xyz&gracePeriod=14400")
      val res: Future[Result]                      =
        controller.getClientListDownloadStatus("cred-123", "service-xyz")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "status").as[String]
    }

    val clientList: CisClientSearchResult =
      CisClientSearchResult(
        clients = List(
          CisTaxpayerSearchResult(
            uniqueId = "1",
            taxOfficeNumber = "123",
            taxOfficeRef = "AB001",
            aoDistrict = None,
            aoPayType = None,
            aoCheckCode = None,
            aoReference = None,
            validBusinessAddr = None,
            correlation = None,
            ggAgentId = None,
            employerName1 = Some("ABC Ltd"),
            employerName2 = None,
            agentOwnRef = None,
            schemeName = Some("ABC")
          ),
          CisTaxpayerSearchResult(
            uniqueId = "2",
            taxOfficeNumber = "456",
            taxOfficeRef = "CD002",
            aoDistrict = None,
            aoPayType = None,
            aoCheckCode = None,
            aoReference = None,
            validBusinessAddr = None,
            correlation = None,
            ggAgentId = None,
            employerName1 = Some("XYZ Builders"),
            employerName2 = None,
            agentOwnRef = None,
            schemeName = Some("XYZ")
          )
        ),
        totalCount = 2,
        clientNameStartingCharacters = List("A", "X")
      )
  }
}
