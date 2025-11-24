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

package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import scala.xml.{Elem, NodeSeq}

case class CISMRFilingResponse(transactionId: String,
                               correlationId: String,
                               timestamp: String,
                               service: String,
                               body: NodeSeq,
                               url: String,
                               pollingUrl: String,
                               irMarkValue: Option[String]) extends ChrisResponse {

  override def successResponseXml(): Elem = <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
      <MessageDetails>
        <Class>{service}</Class>
        <Qualifier>response</Qualifier>
        <Function>submit</Function>
        <CorrelationID>{correlationId}</CorrelationID>
        <ResponseEndPoint/>
        <GatewayTimestamp>{timestamp}</GatewayTimestamp>
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
                <dsig:DigestValue>{irMarkValue.get}</dsig:DigestValue>
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

  override def acknowledgeResponseXml(): Elem = <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
      <MessageDetails>
        <Class>CISR</Class>
        <Qualifier>acknowledgement</Qualifier>
        <Function>submit</Function>
        <CorrelationID>{correlationId}</CorrelationID>
        <ResponseEndPoint PollInterval="5">{pollingUrl}</ResponseEndPoint>
        <GatewayTimestamp/>
        <Transformation>XML</Transformation>
      </MessageDetails>
    </Header>
    <GovTalkDetails>
      <Keys/>
      <GovTalkErrors>
        <Error>
          <RaisedBy>ChRIS</RaisedBy>
          <Number>5999</Number>
          <Type>business</Type>
          <Text>Unknown CorrelationId</Text>
        </Error>
      </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
  </GovTalkMessage>

  override def errorResponseXmlFatal(): Elem =
    CommonChrisResponse(transactionId, correlationId, timestamp, service, body, url, "2").toErrorResponseXml(
      errorResponse =
        <Error>
          <RaisedBy>Gateway</RaisedBy>
          <Number>1020</Number>
          <Type>fatal</Type>
          <Text>Forced fatal error (stub)</Text>
          <Location></Location>
        </Error>
    )

  override def errorResponseXmlBusiness(): Elem =
    CommonChrisResponse(transactionId, correlationId, timestamp, service, body, url, "2").toErrorResponseXml(
      errorResponse =
        <Error>
          <RaisedBy>Department</RaisedBy>
          <Number>3001</Number>
          <Type>business</Type>
          <Text>The submission of this document has failed due to departmental specific business logic in the Body tag.</Text>
          <Location></Location>
        </Error>
    )
}