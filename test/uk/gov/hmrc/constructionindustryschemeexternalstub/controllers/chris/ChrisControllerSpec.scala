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
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.mvc.*
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{ACKNOWLEDGE, FATAL_ERROR}
import uk.gov.hmrc.constructionindustryschemeexternalstub.services.ChrisService
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.NodeSeq

class ChrisControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  private implicit val system: ActorSystem = ActorSystem("ChrisControllerSpec")
  private implicit val mat: Materializer   = Materializer(system)

  private val configuration = new Configuration(ConfigFactory.load("test-application.conf"))

  private val appConfig = new AppConfig(configuration)

  private val service                            = mock[ChrisService]
  private val mockResourceHelper: ResourceHelper = mock[ResourceHelper]
  private val testInstance                       =
    new ChrisController(service, appConfig, mockResourceHelper, Helpers.stubControllerComponents())

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

      val successResponse = <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
        <EnvelopeVersion>2.0</EnvelopeVersion>
        <Header>
          <MessageDetails>
            <Class>IR-CIS-CIS300MR</Class>
            <Qualifier>response</Qualifier>
            <Function>submit</Function>
            <CorrelationID>77E82433B5764086B7523DDFDE4082BE</CorrelationID>
            <ResponseEndPoint/>
            <GatewayTimestamp>2025-12-01T11:41:05.431</GatewayTimestamp>
            <Transformation>XML</Transformation>
          </MessageDetails>
        </Header>
        <GovTalkDetails>
          <Keys/>
        </GovTalkDetails>
        <Body>
          <SuccessResponse xmlns="http://www.inlandrevenue.gov.uk/SuccessResponse">
            <IRmarkReceipt>
              <dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">
                <dsig:SignedInfo>
                  <dsig:CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
                  <dsig:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
                  <dsig:Reference>
                    <dsig:Transforms>
                      <dsig:Transform Algorithm="http://www.w3.org/TR/1999/REC-xpath-19991116">
                        <dsig:XPath>(count(ancestor-or-self::node()|/gti:GovTalkMessage/gti:Body)=count(ancestor-or-self::node())) and (count(ancestor-or-self::node()|/gti:GovTalkMessage/gti:Body/*[name()='IRenvelope']/*[name()='IRheader']/*[name()='IRmark'])!=count(ancestor-or-self::node()))</dsig:XPath>
                      </dsig:Transform>
                      <dsig:Transform Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"/>
                    </dsig:Transforms>
                    <dsig:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
                    <dsig:DigestValue>[digestValue]</dsig:DigestValue>
                  </dsig:Reference>
                </dsig:SignedInfo>
                <dsig:SignatureValue>xjd0lzhAQrnHZsE5inNCOVsmwcQ9HTu+CFUoyqEcOhVvxj2jvYGcjkhu7sZkZJ9RBjBcEP/eQTbesMTrnUgofuMqaROt8ZyD/RJKFIwh5TtNzYzDM55Pa3GDd2ZXcmfR38mS9KPwqc5Ty+Eqv69FxqivCQk46H20F8fnWnx85H4=</dsig:SignatureValue> <dsig:KeyInfo>
                <dsig:X509Data>
                  <dsig:X509Certificate>MIID0zCCAzygAwIBAgIBADANBgkqhkiG9w0BAQQFADCBqDELMAkGA1UEBhMCbmwxFjAUBgNVBAgTDU5vb3JkLUhvbGxhbmQxFzAVBgNVBAoTDk1vYmlsZWZpc2guY29tMRAwDgYDVQQHEwdaYWFuZGFtMRIwEAYDVQQLEwlNYXJrZXRpbmcxGzAZBgNVBAMTEnd3dy5tb2JpbGVmaXNoLmNvbTElMCMGCSqGSIb3DQEJARYWY29udGFjdEBtb2JpbGVmaXNoLmNvbTAeFw0xMTEwMTMxMDI2NTZaFw0xMjEwMTIxMDI2NTZaMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3o83CcmMMOC/fnjVv2puirJTs36+al6RDBe2tbFLKKODd29DZbmH9/6R77VPZACvXxBdRzMls//YRVHoJyJVudy+B4siUfHP80pssg2ZXCmCtUZGS71ohmlHcGQGTVLj8wmicf/DfmMAgq19OFZJP5LUn3md/MQBOUYrFXt21dQIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFAIuWYA/BMx8Gn/YOILevnJthkIZMIHVBgNVHSMEgc0wgcqAFAIuWYA/BMx8Gn/YOILevnJthkIZoYGupIGrMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEEBQADgYEABCb+f82DKWIWBczTeKGc6Ka5U7oys/itCY7XOYMIvXYPj+tb+5PBrmTO3jZNoZso9cYYFcDGXySbk6wSZiEPlbMqkoYE62E6dVXAmbza3ZNNIX/yEpkE3ZeBBtYzJMPQme9jrMgwgMIhgVzQNL2KPkbWOtQfoYgnThHQKLBry6Y=</dsig:X509Certificate>
                </dsig:X509Data>
              </dsig:KeyInfo>
              </dsig:Signature>
              <Message code="1">HMRC has received the IR-CIS-CIS300MR document ref: 123/GL01 at 08.46 on 06/04/2017. The associated IRmark was: TBPJFWEAYSD4GFVRMHY7KLWEBHB5BLA5. We advise you to keep this receipt in both electronic and hardcopy versions for your records. You may wish to use them to identify your submission in the future.</Message>
            </IRmarkReceipt>
            <Message code="9004">The Monthly Return has been processed and passed full validation</Message>
            <AcceptedTime>2017-04-06T08:46:08.081</AcceptedTime>
          </SuccessResponse>
        </Body>
      </GovTalkMessage>

      val request = postRequest.withXmlBody(cisMessage)
      when(service.responseCISMessage(request.body.xml)).thenReturn(Some(successResponse))
      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(successResponse.toString)

      val response = testInstance.submitCISMessage().apply(request)
      status(response) mustBe OK
      contentAsString(response) mustBe successResponse.toString()
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

    "handle submitCISVERIFYMessage with a response" in {
      val correlationId = "CORR-VERIFY-123"

      val cisMessage =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <Class>IR-CIS-VERIFY</Class>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <GovTalkDetails>
            <Keys>
              <Key Type="TaxOfficeNumber">123</Key>
              <Key Type="TaxOfficeReference">AB00001</Key>
            </Keys>
          </GovTalkDetails>
          <Body/>
        </GovTalkMessage>

      when(service.initialCisStatus(eqTo("IR-CIS-VERIFY"), eqTo("123"), eqTo("AB00001")))
        .thenReturn(ACKNOWLEDGE)
      when(service.isForeverPending(eqTo("IR-CIS-VERIFY"), eqTo("123")))
        .thenReturn(false)
      when(service.terminalStatusFor(eqTo("IR-CIS-VERIFY"), eqTo("123")))
        .thenReturn("SUBMITTED")

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[pollUrl]-[gatewayTimestamp]")

      val request  = postRequest.withXmlBody(cisMessage)
      val response = testInstance.submitCISVerifyMessage().apply(request)

      status(response) mustBe OK
      contentAsString(response) must include(correlationId)
    }

    "return a 5xx response from submitCISMessage when taxOfficeNumber is a 500..505 server-error trigger" in {
      val correlationId = "CORR-5XX"

      (500 to 505).foreach { code =>
        val cisMessage =
          <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
            <Header>
              <MessageDetails>
                <Class>IR-CIS-CIS300MR</Class>
                <CorrelationID>{correlationId}</CorrelationID>
              </MessageDetails>
            </Header>
            <GovTalkDetails>
              <Keys>
                <Key Type="TaxOfficeNumber">{code.toString}</Key>
                <Key Type="TaxOfficeReference">EZ00125</Key>
              </Keys>
            </GovTalkDetails>
            <Body/>
          </GovTalkMessage>

        val request  = postRequest.withXmlBody(cisMessage)
        val response = testInstance.submitCISMessage().apply(request)

        status(response) mustBe code
        contentType(response).get mustBe "application/xml"
      }
    }

    "return a 5xx response from getCISResponse when final=SERVER_ERROR_5xx and pollCount has reached the terminal" in {
      val correlationId = "CORR-POLL-5XX"
      val pollCount     = 2

      val pollRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      (500 to 505).foreach { code =>
        val request = FakeRequest(
          "POST",
          s"/dummy-path?final=SERVER_ERROR_$code"
        ).withXmlBody(pollRequestXml)

        val response = testInstance.getCISResponse(pollCount).apply(request)

        status(response) mustBe code
        contentType(response).get mustBe "application/xml"
      }
    }

    "fail the response entity stream from submitCISMessage when taxOfficeNumber is 781" in {
      val cisMessage =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <Class>IR-CIS-CIS300MR</Class>
              <CorrelationID>CORR-ENTITY-FAIL</CorrelationID>
            </MessageDetails>
          </Header>
          <GovTalkDetails>
            <Keys>
              <Key Type="TaxOfficeNumber">781</Key>
              <Key Type="TaxOfficeReference">EZ00125</Key>
            </Keys>
          </GovTalkDetails>
          <Body/>
        </GovTalkMessage>

      val request  = postRequest.withXmlBody(cisMessage)
      val response = testInstance.submitCISMessage().apply(request)

      // A 200 header is emitted, but consuming the streamed entity fails mid-stream (premature close).
      status(response) mustBe OK
      response.flatMap(_.body.consumeData).failed.futureValue mustBe a[Throwable]
    }

    "fail the response entity stream from getCISResponse when final=CONNECTION_ABORT and pollCount has reached the terminal" in {
      val pollRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>CORR-POLL-ENTITY-FAIL</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      val request  = FakeRequest("POST", "/dummy-path?final=CONNECTION_ABORT").withXmlBody(pollRequestXml)
      val response = testInstance.getCISResponse(2).apply(request)

      status(response) mustBe OK
      response.flatMap(_.body.consumeData).failed.futureValue mustBe a[Throwable]
    }

    "return a normal poll response from getCISResponse when final=CONNECTION_ABORT but pollCount is before the terminal" in {
      val correlationId = "CORR-POLL-PRE-ABORT"

      val pollRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[pollUrl]-[digestValue]")

      val request  = FakeRequest("POST", "/dummy-path?final=CONNECTION_ABORT").withXmlBody(pollRequestXml)
      val response = testInstance.getCISResponse(1).apply(request)

      status(response) mustBe OK
      contentAsString(response) mustBe s"$correlationId--NO_IRMARK_FOUND"
    }

    "return fatal error response for submitCISMessage when initial status is FATAL_ERROR" in {
      val correlationId = "CORR-123"
      val cisMessage    =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <Class>IR-CIS-CIS300MR</Class>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <GovTalkDetails>
            <Keys>
              <Key Type="TaxOfficeNumber">754</Key>
              <Key Type="TaxOfficeReference">EZ00125</Key>
            </Keys>
          </GovTalkDetails>
          <Body/>
        </GovTalkMessage>

      when(service.initialCisStatus(eqTo("IR-CIS-CIS300MR"), eqTo("754"), eqTo("EZ00125")))
        .thenReturn(FATAL_ERROR)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("FATAL-[correlationId]")

      val request  = postRequest.withXmlBody(cisMessage)
      val response = testInstance.submitCISMessage().apply(request)

      status(response) mustBe OK
      contentAsString(response) mustBe "FATAL-" + correlationId
    }

    "return SUBMITTED polling response on first poll" in {
      val correlationId = "CORR-ACK-1"
      val pollCount     = 0

      val pollRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[pollUrl]-[digestValue]")

      val request  = postRequest.withXmlBody(pollRequestXml)
      val response = testInstance.getCISResponse(pollCount).apply(request)

      status(response) mustBe OK

      contentAsString(response) mustBe
        s"$correlationId--NO_IRMARK_FOUND"
    }

    "return correct polling response template for all terminal statuses" in {
      val correlationId = "CORR-LOOP"
      val pollCount     = 2

      val pollRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      val statuses = Seq(
        "ACKNOWLEDGE",
        "SUBMITTED_NO_RECEIPT",
        "FATAL_ERROR",
        "DEPARTMENTAL_ERROR",
        "RECOVERABLE_ERROR_3000",
        "RECOVERABLE_ERROR_2005",
        "RECOVERABLE_ERROR_1000",
        "SUBMITTED"
      )

      statuses.foreach { statusValue =>
        reset(service, mockResourceHelper)

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn("[correlationId]-[pollUrl]-[digestValue]-" + statusValue)

        val request = FakeRequest(
          "POST",
          s"/dummy-path?final=$statusValue"
        ).withXmlBody(pollRequestXml)

        val response = testInstance.getCISResponse(pollCount).apply(request)

        status(response) mustBe OK

        contentAsString(response) mustBe
          s"$correlationId--NO_IRMARK_FOUND-$statusValue"
      }
    }

    "store IRmark from submitCISMessage and use it as digestValue in getCISResponse" in {
      val correlationId = "CORR-IRMARK"
      val irMarkValue   = "TBPJFWEAYSD4GFVRMHY7KLWEBHB5BLA5"

      val submitMessage =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <Class>IR-CIS-CIS300MR</Class>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <GovTalkDetails>
            <Keys>
              <Key Type="TaxOfficeNumber">123</Key>
              <Key Type="TaxOfficeReference">AB00001</Key>
            </Keys>
          </GovTalkDetails>
          <Body>
            <IRenvelope>
              <IRheader>
                <IRmark>{irMarkValue}</IRmark>
              </IRheader>
            </IRenvelope>
          </Body>
        </GovTalkMessage>

      reset(service, mockResourceHelper)
      when(service.initialCisStatus(eqTo("IR-CIS-CIS300MR"), eqTo("123"), eqTo("AB00001")))
        .thenReturn(ACKNOWLEDGE)
      when(service.isForeverPending(eqTo("IR-CIS-CIS300MR"), eqTo("123")))
        .thenReturn(false)
      when(service.terminalStatusFor(eqTo("IR-CIS-CIS300MR"), eqTo("123")))
        .thenReturn("SUBMITTED")
      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[pollUrl]-[gatewayTimestamp]")

      val submitRequest  = postRequest.withXmlBody(submitMessage)
      val submitResponse = testInstance.submitCISMessage().apply(submitRequest)
      status(submitResponse) mustBe OK

      reset(mockResourceHelper)
      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[digestValue]")

      val pollMessage =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      val pollRequest  = postRequest.withXmlBody(pollMessage)
      val pollResponse = testInstance.getCISResponse(0).apply(pollRequest)

      status(pollResponse) mustBe OK
      contentAsString(pollResponse) mustBe s"$correlationId-$irMarkValue"
    }

    "return delete response when function is delete" in {
      val correlationId = "CORR-DELETE"

      val deleteRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <Function>delete</Function>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
        </GovTalkMessage>

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("DELETE-[correlationId]")

      val request  = postRequest.withXmlBody(deleteRequestXml)
      val response = testInstance.getCISResponse(0).apply(request)

      status(response) mustBe OK
      contentAsString(response) mustBe s"DELETE-$correlationId"
    }

    "return verification SUBMITTED polling response on first poll" in {
      val correlationId = "CORR-VERIFY-ACK-1"
      val pollCount     = 0

      val pollRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[pollUrl]-[digestValue]")

      val request  = postRequest.withXmlBody(pollRequestXml)
      val response = testInstance.getCISVerifyResponse(pollCount).apply(request)

      status(response) mustBe OK

      contentAsString(response) mustBe
        s"$correlationId--NO_IRMARK_FOUND"
    }

    "return correct verification polling response template for all terminal statuses" in {
      val correlationId = "CORR-VERIFY-LOOP"
      val pollCount     = 2

      val pollRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      val statuses = Seq(
        "ACKNOWLEDGE",
        "SUBMITTED_NO_RECEIPT",
        "FATAL_ERROR",
        "DEPARTMENTAL_ERROR",
        "DEPARTMENTAL_ERROR_3000",
        "SUBMITTED"
      )

      statuses.foreach { statusValue =>
        reset(service, mockResourceHelper)

        when(mockResourceHelper.resourceAsString(any()))
          .thenReturn("[correlationId]-[pollUrl]-[digestValue]-" + statusValue)

        val request = FakeRequest(
          "POST",
          s"/dummy-path?final=$statusValue"
        ).withXmlBody(pollRequestXml)

        val response = testInstance.getCISVerifyResponse(pollCount).apply(request)

        status(response) mustBe OK

        contentAsString(response) mustBe
          s"$correlationId--NO_IRMARK_FOUND-$statusValue"
      }
    }

    "store IRmark from submitCISVerifyMessage and use it as digestValue in getCISVerifyResponse" in {
      val correlationId = "CORR-VERIFY-IRMARK"
      val irMarkValue   = "VERIFYIRMARK123456789"

      val submitMessage =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <Class>IR-CIS-VERIFY</Class>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <GovTalkDetails>
            <Keys>
              <Key Type="TaxOfficeNumber">123</Key>
              <Key Type="TaxOfficeReference">AB00001</Key>
            </Keys>
          </GovTalkDetails>
          <Body>
            <IRenvelope>
              <IRheader>
                <IRmark>{irMarkValue}</IRmark>
              </IRheader>
            </IRenvelope>
          </Body>
        </GovTalkMessage>

      reset(service, mockResourceHelper)

      when(service.initialCisStatus(eqTo("IR-CIS-VERIFY"), eqTo("123"), eqTo("AB00001")))
        .thenReturn(ACKNOWLEDGE)
      when(service.isForeverPending(eqTo("IR-CIS-VERIFY"), eqTo("123")))
        .thenReturn(false)
      when(service.terminalStatusFor(eqTo("IR-CIS-VERIFY"), eqTo("123")))
        .thenReturn("SUBMITTED")
      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[pollUrl]-[gatewayTimestamp]")

      val submitRequest  = postRequest.withXmlBody(submitMessage)
      val submitResponse = testInstance.submitCISVerifyMessage().apply(submitRequest)

      status(submitResponse) mustBe OK

      reset(mockResourceHelper)

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("[correlationId]-[digestValue]")

      val pollMessage =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
          <Body/>
        </GovTalkMessage>

      val pollRequest  = postRequest.withXmlBody(pollMessage)
      val pollResponse = testInstance.getCISVerifyResponse(0).apply(pollRequest)

      status(pollResponse) mustBe OK
      contentAsString(pollResponse) mustBe s"$correlationId-$irMarkValue"
    }

    "return verification delete response when function is delete" in {
      val correlationId = "CORR-VERIFY-DELETE"

      val deleteRequestXml =
        <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
          <Header>
            <MessageDetails>
              <Function>delete</Function>
              <CorrelationID>{correlationId}</CorrelationID>
            </MessageDetails>
          </Header>
        </GovTalkMessage>

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("VERIFY-DELETE-[correlationId]")

      val request  = postRequest.withXmlBody(deleteRequestXml)
      val response = testInstance.getCISVerifyResponse(0).apply(request)

      status(response) mustBe OK
      contentAsString(response) mustBe s"VERIFY-DELETE-$correlationId"
    }
  }
}
