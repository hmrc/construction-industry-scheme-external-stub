/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.chris

import com.typesafe.config.ConfigFactory
import org.mockito.Mockito.*
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.mvc.*
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig
import uk.gov.hmrc.constructionindustryschemeexternalstub.services.ChrisService

import scala.xml.NodeSeq


class ChrisControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  private val configuration = new Configuration(ConfigFactory.load("test-application.conf"))

  private val appConfig     = new AppConfig(configuration)

  private val service = mock[ChrisService]
  private val testInstance = new ChrisController(service, appConfig, Helpers.stubControllerComponents())

  private val message = <ChRISEnvelope xmns="http://www.hmrc.gov.uk/ChRIS/Envelope/2">
    <Header>
      <MessageClass>HMRC_PEV_SA</MessageClass>
    </Header>
  </ChRISEnvelope>

  private val postRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/submission/ChRIS")

  "ChrisController " should {


    "handle pollMessage with an acknowledgement" in {
      val expected: NodeSeq = <poll></poll>
      when(service.pollMessage(message, "HMRC-SA-SA100", 0, false)).thenReturn(expected)

      val response = testInstance.pollMessage("HMRC-SA-SA100", 0, false).apply(postRequest.withXmlBody(message))
      status(response) mustBe OK
      contentType(response).get mustBe "application/xml"
      contentAsString(response) mustBe expected.toString()
    }

    "handle async submissions with an acknowledgement" in {
      val response = testInstance.submitAsyncMessage("service", "serviceId").apply(postRequest.withXmlBody(message))
      status(response) mustBe OK
    }

    "handle submitCIS300MRMessage with a response" in {
      val cisMessage = <ChRISEnvelope xmns="http://www.hmrc.gov.uk/ChRIS/Envelope/2">
        <Header>
          <MessageDetails>
            <MessageClass>IR-CIS-CIS300MR</MessageClass>
          </MessageDetails>
        </Header>
      </ChRISEnvelope>

      val expected: NodeSeq = <poll></poll>
      val request = postRequest.withXmlBody(cisMessage)
      when(service.responseCISMessage(request.body.xml)).thenReturn(Some(expected))

      val response = testInstance.submitCISMessage().apply(request)
      status(response) mustBe OK
      contentType(response).get mustBe "application/xml"
      contentAsString(response) mustBe expected.toString()
    }

    "handle actionCISRMessage with a response" in {
      val actionCisMessage = <ChRISEnvelope xmns="http://www.hmrc.gov.uk/ChRIS/Envelope/2">
        <Header>
          <MessageDetails>
            <MessageClass>IR-CIS-CIS300MR</MessageClass>
          </MessageDetails>
        </Header>
      </ChRISEnvelope>

      val request = postRequest.withXmlBody(message)
      when(service.responseActionMonthlyReturnCISMessage(request.body.xml)).thenReturn(Some(actionCisMessage))

      val response = testInstance.actionCISMessage().apply(request)
      status(response) mustBe OK
      contentType(response).get mustBe "application/xml"
      contentAsString(response) mustBe actionCisMessage.toString()
    }

    "handle submitUnknownCISMessage with a response" in {
      val cisMessage = <ChRISEnvelope xmns="http://www.hmrc.gov.uk/ChRIS/Envelope/2">
        <Header>
          <MessageDetails>
            <MessageClass>Unknown</MessageClass>
          </MessageDetails>
        </Header>
      </ChRISEnvelope>

      val request = postRequest.withXmlBody(cisMessage)
      when(service.responseCISMessage(request.body.xml)).thenReturn(None)

      val response = testInstance.submitCISMessage().apply(request)
      status(response) mustBe NOT_FOUND
    }

    "handle submitCISVERIFYMessage with a response" in {
      val cisMessage = <ChRISEnvelope xmns="http://www.hmrc.gov.uk/ChRIS/Envelope/2">
        <Header>
          <MessageDetails>
            <MessageClass>IR-CIS-VERIFY</MessageClass>
          </MessageDetails>
        </Header>
      </ChRISEnvelope>

      val expected: NodeSeq = <poll></poll>
      val request = postRequest.withXmlBody(cisMessage)
      when(service.responseCISVerifyMessage(request.body.xml)).thenReturn(Some(expected))

      val response = testInstance.submitCISVerifyMessage().apply(request)
      status(response) mustBe OK
      contentType(response).get mustBe "application/xml"
      contentAsString(response) mustBe expected.toString()
    }
  }
}
