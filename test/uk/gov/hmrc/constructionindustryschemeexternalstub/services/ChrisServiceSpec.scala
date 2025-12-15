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

  private val submitCISVerifyXML =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>IR-CIS-VERIFY</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000007</CorrelationID>
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
        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/CISrequest">
          <IRheader>
            <TestMessage>0</TestMessage>
            <Keys>
              <Key Type='TaxOfficeNumber'>123</Key>
              <Key Type='TaxOfficeReference'>GL01</Key>
            </Keys>
            <PeriodEnd>2007-05-05</PeriodEnd>
            <DefaultCurrency>GBP</DefaultCurrency>
            <IRmark Type='generic'>RPu0RnkpsfKnCZaGbpn46VoTvvM=</IRmark>
            <Sender>Individual</Sender>
          </IRheader>
          <CISrequest>
            <Contractor>
              <UTR>2325648152</UTR>
              <AOref>123PP87654321</AOref>
            </Contractor>
            <Subcontractor>
              <Action>match</Action>
              <Type>soletrader</Type>
              <Name>
                <Fore>John</Fore>
                <Sur>Smith</Sur>
              </Name>
              <UTR>2325648152</UTR>
              <NINO>YW000003A</NINO>
            </Subcontractor>
            <Subcontractor>
              <Action>verify</Action>
              <Type>soletrader</Type>
              <Name>
                <Fore>Fred</Fore>
                <Fore>George</Fore>
                <Sur>Bingham</Sur>
              </Name>
              <UTR>2345678901</UTR>
              <Address>
                <Line>15 High Street</Line>
                <PostCode>RH11 8BG</PostCode>
                <Country>England</Country>
              </Address>
            </Subcontractor>
            <Declaration>yes</Declaration>
          </CISrequest>
        </IRenvelope>
      </Body>
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

    "responseCISVERIFYMessage should return successful business response for a valid CIS VERIFY submit request" in {
      val response = testInstance.responseCISVerifyMessage(submitCISVerifyXML).get

      val clazz           = (response \ "Header" \ "MessageDetails" \ "Class").text
      val qualifier       = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
      val successResponse = (response \ "Body" \ "SuccessResponse" \ "Message").text
      qualifier mustBe "response"
      successResponse mustBe "The Subcontractor Verification has been processed and passed full validation"
      clazz mustBe "IR-CIS-VERIFY"
    }

    "responseCISMessage should return an empty response for request with unknown message class" in {
      val response = testInstance.responseCISMessage(submitUnknownCISMessage)
      response mustBe empty
    }

    "responseCISVERIFYMessage should return an empty response for request with unknown message class" in {
      val response = testInstance.responseCISVerifyMessage(submitUnknownCISMessage)
      response mustBe empty
    }

  }
}
