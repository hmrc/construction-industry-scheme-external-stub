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

package uk.gov.hmrc.constructionindustryschemeexternalstub.services

import com.typesafe.config.ConfigFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{ACKNOWLEDGE, FATAL_ERROR}

class ChrisServiceSpec extends AnyWordSpec with Matchers with OneInstancePerTest {

  private val submitCISMRSuccessFilingXML =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>IR-CIS-CIS300MR</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000001</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTest>1</GatewayTest>
          <GatewayTimestamp>2017-04-06T08:46:08.081</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails>
          <IDAuthentication>
            <SenderID>ISV147</SenderID>
            <Authentication>
              <Method>clear</Method>
              <Role>principal</Role>
              <Value>xxxxx</Value>
            </Authentication>
          </IDAuthentication>
        </SenderDetails>
      </Header>
      <GovTalkDetails>
        <Keys>
          <Key Type='TaxOfficeNumber'>123</Key>
          <Key Type='TaxOfficeReference'>GL02</Key>
        </Keys>
        <TargetDetails>
          <Organisation>IR</Organisation>
        </TargetDetails>
        <ChannelRouting>
          <Channel>
            <Name>0147</Name>
            <Product>sdsteam</Product>
            <Version>1</Version>
          </Channel>
        </ChannelRouting>
      </GovTalkDetails>
      <Body>
        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/CISreturn">
          <IRheader>
            <TestMessage>0</TestMessage>
            <Keys>
              <Key Type='TaxOfficeNumber'>123</Key>
              <Key Type='TaxOfficeReference'>GL02</Key>
            </Keys>
            <PeriodEnd>2007-05-05</PeriodEnd>
            <DefaultCurrency>GBP</DefaultCurrency>
            <IRmark Type='generic'>mF6S2IDEh8MWsWHx9S7ECcPQrB0=</IRmark>
            <Sender>Individual</Sender>
          </IRheader>
          <CISreturn>
            <Contractor>
              <UTR>2325648152</UTR>
              <AOref>123PP87654321</AOref>
            </Contractor>
            <NilReturn>yes</NilReturn>
            <Declarations>
              <InformationCorrect>yes</InformationCorrect>
            </Declarations>
          </CISreturn>
        </IRenvelope>
      </Body>
    </GovTalkMessage>

  private val submitCISMRAcknowledgeFilingXML =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>IR-CIS-CIS300MR</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000001</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTest>1</GatewayTest>
          <GatewayTimestamp>2017-04-06T08:46:08.081</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails>
          <IDAuthentication>
            <SenderID>ISV147</SenderID>
            <Authentication>
              <Method>clear</Method>
              <Role>principal</Role>
              <Value>xxxxx</Value>
            </Authentication>
          </IDAuthentication>
        </SenderDetails>
      </Header>
      <GovTalkDetails>
        <Keys>
          <Key Type='TaxOfficeNumber'>123</Key>
          <Key Type='TaxOfficeReference'>GL01</Key>
        </Keys>
        <TargetDetails>
          <Organisation>IR</Organisation>
        </TargetDetails>
        <ChannelRouting>
          <Channel>
            <Name>0147</Name>
            <Product>sdsteam</Product>
            <Version>1</Version>
          </Channel>
        </ChannelRouting>
      </GovTalkDetails>
      <Body>
        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/CISreturn">
          <IRheader>
            <TestMessage>0</TestMessage>
            <Keys>
              <Key Type='TaxOfficeNumber'>123</Key>
              <Key Type='TaxOfficeReference'>GL01</Key>
            </Keys>
            <PeriodEnd>2007-05-05</PeriodEnd>
            <DefaultCurrency>GBP</DefaultCurrency>
            <IRmark Type='generic'>mF6S2IDEh8MWsWHx9S7ECcPQrB0=</IRmark>
            <Sender>Individual</Sender>
          </IRheader>
          <CISreturn>
            <Contractor>
              <UTR>2325648152</UTR>
              <AOref>123PP87654321</AOref>
            </Contractor>
            <NilReturn>yes</NilReturn>
            <Declarations>
              <InformationCorrect>yes</InformationCorrect>
            </Declarations>
          </CISreturn>
        </IRenvelope>
      </Body>
    </GovTalkMessage>

  private val submitUnknownCISMessage =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>UNKNOWN Message</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000001</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTest>1</GatewayTest>
          <GatewayTimestamp>2017-04-06T08:46:08.081</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails>
          <IDAuthentication>
            <SenderID>ISV147</SenderID>
            <Authentication>
              <Method>clear</Method>
              <Role>principal</Role>
              <Value>xxxxx</Value>
            </Authentication>
          </IDAuthentication>
        </SenderDetails>
      </Header>
      <GovTalkDetails/>
    </GovTalkMessage>

  private val configuration = new Configuration(ConfigFactory.load("test-application.conf"))

  private val appConfig                  = new AppConfig(configuration)
  private val testInstance: ChrisService = new ChrisService(appConfig)

  "ChrisService.responseMessage" should {

    "responseCISMessage should return successful business response for a valid CIS MR-FILING submit request" in {

      val response = testInstance.responseCISMessage(submitCISMRSuccessFilingXML).get

      val clazz           = (response \ "Header" \ "MessageDetails" \ "Class").text
      val qualifier       = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
      val successResponse = (response \ "Body" \ "SuccessResponse" \ "Message").text
      qualifier mustBe "response"
      successResponse mustBe "The Monthly Return has been processed and passed full validation"
      clazz mustBe "IR-CIS-CIS300MR"
    }

    "responseCISMessage should return acknowledged business response for orgs enabled for acknowledged submissions" in {

      val response = testInstance.responseCISMessage(submitCISMRAcknowledgeFilingXML).get

      val clazz            = (response \ "Header" \ "MessageDetails" \ "Class").text
      val qualifier        = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
      val govTalkErrorText = (response \ "GovTalkDetails" \ "GovTalkErrors" \ "Error" \ "Text").text

      clazz mustBe "CISR"
      qualifier mustBe "acknowledgement"
      govTalkErrorText mustBe "Unknown CorrelationId"
    }

    "responseCISMessage should set the polling url to be the stub url if stubbing is true" in {

      val response = testInstance.responseCISMessage(submitCISMRAcknowledgeFilingXML).get

      val pollingUrl = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
      pollingUrl mustBe "http://localhost/submission/ChRIS/poll/IR-CIS-CIS300MR/0/false"
    }

    "responseCISMessage should set the polling url to be a real polling url if stubbing is false" in {
      val response = testInstance.responseCISMessage(submitCISMRSuccessFilingXML).get

      val pollingUrl = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
      pollingUrl mustBe ""
    }

    "responseCISMessage should throw for request with unknown message class" in {
      val exception = intercept[RuntimeException] {
        testInstance.responseCISMessage(submitUnknownCISMessage)
      }

      exception.getMessage mustBe "Unknown regime: UNKNOWN Message"
    }

    "ChrisService.initialCisStatus" should {

      "return FATAL_ERROR when tax office / reference is in fatalErrorFilter" in {
        val fatalKey                             = appConfig.fatalErrorFilter.headOption.getOrElse("754/EZ00125")
        val Array(taxOfficeNumber, taxOfficeRef) = fatalKey.split("/")

        val result = testInstance.initialCisStatus("IR-CIS-CIS300MR", taxOfficeNumber, taxOfficeRef)

        result mustBe FATAL_ERROR
      }

      "return ACKNOWLEDGE when tax office / reference is not in fatalErrorFilter" in {
        val ackKey                               = appConfig.acknowledgeFilter.headOption.getOrElse("754/EZ00100")
        val Array(taxOfficeNumber, taxOfficeRef) = ackKey.split("/")

        val result = testInstance.initialCisStatus("IR-CIS-CIS300MR", taxOfficeNumber, taxOfficeRef)

        result mustBe ACKNOWLEDGE
      }
    }

    "ChrisService.terminalStatusFor" should {

      "return configured terminal status for a known tax office number" in {
        val taxOfficeNumber = "754"
        val expected        = appConfig.pollingStatus("IR-CIS-CIS300MR", taxOfficeNumber)

        testInstance.terminalStatusFor("IR-CIS-CIS300MR", taxOfficeNumber) mustBe expected
      }

      "return SUBMITTED when tax office number is not configured" in {
        val unknownTaxOfficeNumber = "99999"

        appConfig.pollingStatus("IR-CIS-CIS300MR", unknownTaxOfficeNumber) mustBe "SUBMITTED"
        testInstance.terminalStatusFor("IR-CIS-CIS300MR", unknownTaxOfficeNumber) mustBe "SUBMITTED"
      }
    }

    "ChrisService.isForeverPending" should {

      "return true when polling status is ACKNOWLEDGE" in {
        val taxOfficeNumber = "758"
        testInstance.terminalStatusFor("IR-CIS-CIS300MR", taxOfficeNumber) mustBe "ACKNOWLEDGE"
        testInstance.isForeverPending("IR-CIS-CIS300MR", taxOfficeNumber) mustBe true
      }

      "return false when polling status is not ACKNOWLEDGE" in {
        val taxOfficeNumber = "754"
        testInstance.isForeverPending("IR-CIS-CIS300MR", taxOfficeNumber) mustBe false
      }
    }
  }
}
