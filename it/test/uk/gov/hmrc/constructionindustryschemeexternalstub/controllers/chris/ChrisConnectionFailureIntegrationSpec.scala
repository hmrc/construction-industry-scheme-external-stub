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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.chris

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.writeableOf_String
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import scala.concurrent.ExecutionContext.Implicits.global

class ChrisConnectionFailureIntegrationSpec
  extends AnyWordSpec
     with Matchers
     with ScalaFutures
     with IntegrationPatience
     with GuiceOneServerPerSuite:

  private given HeaderCarrier = HeaderCarrier()

  private val httpClient = app.injector.instanceOf[HttpClientV2]
  private val baseUrl    = s"http://localhost:$port"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder().build()

  private def submitMessage(taxOfficeNumber: String): String =
    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
      <Header>
        <MessageDetails>
          <Class>IR-CIS-CIS300MR</Class>
          <CorrelationID>CORR-IT-ENTITY-FAIL</CorrelationID>
        </MessageDetails>
      </Header>
      <GovTalkDetails>
        <Keys>
          <Key Type="TaxOfficeNumber">{taxOfficeNumber}</Key>
          <Key Type="TaxOfficeReference">EZ00125</Key>
        </Keys>
      </GovTalkDetails>
      <Body/>
    </GovTalkMessage>.toString

  private val pollMessage: String =
    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
      <Header>
        <MessageDetails>
          <CorrelationID>CORR-IT-ENTITY-FAIL</CorrelationID>
        </MessageDetails>
      </Header>
      <Body/>
    </GovTalkMessage>.toString

  "ChRIS response-entity-failure triggers" should:

    "fail the response entity stream for submit when TaxOfficeNumber is 781" in:
      val result =
        httpClient
          .post(url"$baseUrl/submission/ChRIS/CISR/Filing/sync/CIS300MR")
          .setHeader("Content-Type" -> "application/xml")
          .withBody(submitMessage("781"))
          .execute[HttpResponse]
          .failed
          .futureValue

      result shouldBe a[Throwable]

    "fail the response entity stream on terminal poll (count >= 2) when final=CONNECTION_ABORT" in:
      val result =
        httpClient
          .post(url"$baseUrl/submission/ChRIS/poll/IR-CIS-CIS300MR/2?final=CONNECTION_ABORT")
          .setHeader("Content-Type" -> "application/xml")
          .withBody(pollMessage)
          .execute[HttpResponse]
          .failed
          .futureValue

      result shouldBe a[Throwable]

    "return a normal 200 poll response before the terminal count even when final=CONNECTION_ABORT" in:
      val response =
        httpClient
          .post(url"$baseUrl/submission/ChRIS/poll/IR-CIS-CIS300MR/1?final=CONNECTION_ABORT")
          .setHeader("Content-Type" -> "application/xml")
          .withBody(pollMessage)
          .execute[HttpResponse]
          .futureValue

      response.status shouldBe 200
      response.body should not be empty