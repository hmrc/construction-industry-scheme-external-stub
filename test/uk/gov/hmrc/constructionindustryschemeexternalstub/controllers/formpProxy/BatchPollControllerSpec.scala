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

import org.mockito.Mockito.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper

import scala.concurrent.Future

class BatchPollControllerSpec extends AnyFreeSpec with Matchers with MockitoSugar {

  ".getBatchPollSubmissions" - {

    "returns 200 with batch poll submissions by default" in new Setup {
      when(mockResourceHelper.resourceAsString(nonEmptyPath))
        .thenReturn(nonEmptyResponse.toString)

      val request: FakeRequest[AnyContentAsEmpty.type] =
        makeGetRequest()

      val result: Future[Result] =
        controller.getBatchPollSubmissions()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe nonEmptyResponse

      verify(mockResourceHelper).resourceAsString(nonEmptyPath)
    }

    "returns 200 with batch poll submissions for scenario 200" in new Setup {
      when(mockResourceHelper.resourceAsString(nonEmptyPath))
        .thenReturn(nonEmptyResponse.toString)

      val request: FakeRequest[AnyContentAsEmpty.type] =
        makeGetRequest(Some("200"))

      val result: Future[Result] =
        controller.getBatchPollSubmissions()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe nonEmptyResponse

      verify(mockResourceHelper).resourceAsString(nonEmptyPath)
    }

    "returns 200 with empty submissions for scenario 000" in new Setup {
      when(mockResourceHelper.resourceAsString(emptyPath))
        .thenReturn(emptyResponse.toString)

      val request: FakeRequest[AnyContentAsEmpty.type] =
        makeGetRequest(Some("000"))

      val result: Future[Result] =
        controller.getBatchPollSubmissions()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe emptyResponse

      verify(mockResourceHelper).resourceAsString(emptyPath)
    }

    "returns 502 for scenario 502" in new Setup {
      val request: FakeRequest[AnyContentAsEmpty.type] =
        makeGetRequest(Some("502"))

      val result: Future[Result] =
        controller.getBatchPollSubmissions()(request)

      status(result) mustBe BAD_GATEWAY
      (contentAsJson(result) \ "message").as[String] must include("formp failed")

      verifyNoInteractions(mockResourceHelper)
    }

    "returns 500 for scenario 500" in new Setup {
      val request: FakeRequest[AnyContentAsEmpty.type] =
        makeGetRequest(Some("500"))

      val result: Future[Result] =
        controller.getBatchPollSubmissions()(request)

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "message").as[String] mustBe "Unexpected error"

      verifyNoInteractions(mockResourceHelper)
    }
  }

  private trait Setup {

    private val cc: ControllerComponents =
      stubControllerComponents()

    val mockResourceHelper: ResourceHelper =
      mock[ResourceHelper]

    val nonEmptyPath: String =
      "/resources/batchPoll/getBatchPollSubmissions-200-response.json"

    val emptyPath: String =
      "/resources/batchPoll/getBatchPollSubmissions-200-empty-response.json"

    val controller =
      new BatchPollController(
        resourceHelper = mockResourceHelper,
        cc = cc
      )

    def makeGetRequest(
      scenario: Option[String] = None
    ): FakeRequest[AnyContentAsEmpty.type] = {

      val requestUrl: String =
        scenario.fold("/cis/batchpoll-submissions") { value =>
          s"/cis/batchpoll-submissions?scenario=$value"
        }

      FakeRequest(GET, requestUrl)
        .withHeaders(ACCEPT -> JSON)
    }

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
            "taxYear"            -> 2025,
            "taxMonth"           -> 6,
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
