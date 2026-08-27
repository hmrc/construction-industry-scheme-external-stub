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
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{EnqueueMessage, EnqueueNumber, EnqueueTracking}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.EnqueueMessageRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper

import scala.concurrent.{ExecutionContext, Future}

class UdasQueueControllerSpec extends AnyFreeSpec with Matchers with ScalaFutures with MockitoSugar {

  "UdasQueueController" - {

    ".enqueue-message" - {

      val postUrl = "/cis/enqueue-message"

      val validJson: JsValue =
        Json.toJson(
          EnqueueMessageRequest(
            message = EnqueueMessage(
              sender = "Portal",
              queueName = "AGTAUTH",
              replyQueue = "",
              correlationID = "",
              filter = "RemoveClient",
              payload = Map(
                "IRAgentID"    -> "123456789",
                "Service"      -> "CIS",
                "TaxReference" -> "123/ABC123"
              )
            ),
            tracking = Some(
              EnqueueTracking(
                message = EnqueueMessage(
                  sender = "Portal",
                  queueName = "Tracking",
                  replyQueue = "",
                  correlationID = "",
                  filter = "AGENTAUTH",
                  payload = Map(
                    "GGIS_DTSTAMP"    -> "20260827 154512747",
                    "MESSAGE_TYPE"    -> "AGENT_AUTH_PORTAL",
                    "ADDITIONAL_INFO" -> "Request client removal",
                    "GW_AGENT_ID"     -> "AGENT123",
                    "IR_CLIENT_REF"   -> "123/ABC123",
                    "USER_ID"         -> "user123",
                    "Service"         -> "CIS"
                  )
                ),
                number = EnqueueNumber(
                  dataType = 1,
                  payload = Map(
                    "EVENT_TYPE" -> 1010L
                  )
                )
              )
            )
          )
        )

      "returns 200 with status when service succeeds for an unknown agentReference" in new Setup {

        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("200"))

        val req: FakeRequest[JsValue] =
          FakeRequest(POST, postUrl).withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON).withBody(validJson)

        val res: Future[Result] = controller.enqueueMessage()(req)

        status(res) mustBe OK
        contentAsJson(res) mustBe Json.obj("messageIDOut" -> 1)

      }

      "propagates UpstreamErrorResponse for agentReference = 400" in new Setup {

        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("400"))

        val req: FakeRequest[JsValue] =
          FakeRequest(POST, postUrl).withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON).withBody(validJson)

        val res: Future[Result] = controller.enqueueMessage()(req)

        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "error").as[String] must include("credentialId and serviceName must be provided")

      }

      "returns 500 with generic message for agentReference = 500" in new Setup {

        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("500"))

        val req: FakeRequest[JsValue] =
          FakeRequest(POST, postUrl).withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON).withBody(validJson)

        val res: Future[Result] = controller.enqueueMessage()(req)

        status(res) mustBe INTERNAL_SERVER_ERROR
        (contentAsJson(res) \ "error").as[String] mustBe "could not enqueue message"
      }

      "returns 400 BadRequest when JSON is invalid" in new Setup {
        val invalidJson: JsObject = Json.obj()

        val req: FakeRequest[JsValue] =
          FakeRequest(POST, postUrl).withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON).withBody(invalidJson)

        val res: Future[Result] = controller.enqueueMessage()(req)

        status(res) mustBe BAD_REQUEST
        (contentAsJson(res) \ "message").as[String] mustBe "Invalid Json"
        (contentAsJson(res) \ "errors").isDefined mustBe true
      }

      "returns 500 with generic message for missing agent" in new Setup {
        when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(None)

        val req: FakeRequest[JsValue] =
          FakeRequest(POST, postUrl).withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON).withBody(validJson)

        val res: Future[Result] = controller.enqueueMessage()(req)

        status(res) mustBe INTERNAL_SERVER_ERROR
      }
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
