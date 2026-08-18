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
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.EnqueueMessageHeaderRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper

import scala.concurrent.{ExecutionContext, Future}

class UdasQueueControllerSpec extends AnyFreeSpec with Matchers with ScalaFutures with MockitoSugar {
  ".enqueueMessageHeader" - {

    "returns 200 with status when service succeeds for an unknown agentReference" in new Setup {
      val request: EnqueueMessageHeaderRequest = EnqueueMessageHeaderRequest(
        sender = "Portal",
        queueName = "AGTAUTH",
        replyQueue = "",
        correlationId = "",
        filter = "RemoveClient"
      )
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("200"))

      val req = FakeRequest(POST, "/rds-datacache-proxy/cis/enqueue-message-header").withBody(request)

      val res: Future[Result] = controller.enqueueMessageHeader()(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.obj("messageId" -> "10")

    }

    "propagates UpstreamErrorResponse for agentReference = 400" in new Setup {
      val request: EnqueueMessageHeaderRequest = EnqueueMessageHeaderRequest(
        sender = "Portal",
        queueName = "AGTAUTH",
        replyQueue = "",
        correlationId = "",
        filter = "RemoveClient"
      )

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("400"))

      val req: FakeRequest[EnqueueMessageHeaderRequest] =
        FakeRequest(POST, "/rds-datacache-proxy/cis/enqueue-message-header").withBody(request)

      val res: Future[Result] = controller.enqueueMessageHeader()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "error").as[String] must include("credentialId and serviceName must be provided")

    }

    "returns 500 with generic message for agentReference = 500" in new Setup {
      val request: EnqueueMessageHeaderRequest = EnqueueMessageHeaderRequest(
        sender = "Portal",
        queueName = "AGTAUTH",
        replyQueue = "",
        correlationId = "",
        filter = "RemoveClient"
      )

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any()))
        .thenReturn(Some("500"))

      val req: FakeRequest[EnqueueMessageHeaderRequest] =
        FakeRequest(POST, "/formp-proxy/cis/enqueue-message-header").withBody(request)
      val res: Future[Result]                           = controller.enqueueMessageHeader()(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "error").as[String] mustBe "could enqueue message header"
    }

  }

  private trait Setup {
    implicit val ec: ExecutionContext    = scala.concurrent.ExecutionContext.global
    private val cc: ControllerComponents = stubControllerComponents()
    private val parsers: PlayBodyParsers = cc.parsers

    private def fakeAuth: AuthAction = new FakeAuthAction(parsers)

    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val controller = new UdasQueueController(fakeAuth, mockEnrolmentsHelper, cc)
  }
}
