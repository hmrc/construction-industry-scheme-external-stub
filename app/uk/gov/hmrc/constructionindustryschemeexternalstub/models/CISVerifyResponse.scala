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

case class CISVerifyResponse(transactionId: String,
                             correlationId: String,
                             timestamp: String,
                             service: String,
                             body: NodeSeq,
                             utr: Option[String],
                             matched: String,
                             taxTreatment: String,
                             verificationNumber: String,
                             tradingName: Option[String],
                             url: String,
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
                <dsig:DigestValue>RPu0RnkpsfKnCZaGbpn46VoTvvM=</dsig:DigestValue>
              </dsig:Reference>
            </dsig:SignedInfo>
            <dsig:SignatureValue>bH9O8SFl1jM5rmsSMGPII/jsRXgBdLncTT7P+hn/CID/UiipsMgS8iP4ARAXsIXwv5uouAViJW7NCglnKalcHwJEk+gM/K9HjunD8CJeGrJhqBvQaDO1kEg2PfT6T32wU1pnl/WF1S24fT3GCBUcn0LGbLqqlv9v6F119ycUgk8=</dsig:SignatureValue>
            <dsig:KeyInfo>
              <dsig:X509Data>
                <dsig:X509Certificate>MIID0zCCAzygAwIBAgIBADANBgkqhkiG9w0BAQQFADCBqDELMAkGA1UEBhMCbmwxFjAUBgNVBAgTDU5vb3JkLUhvbGxhbmQxFzAVBgNVBAoTDk1vYmlsZWZpc2guY29tMRAwDgYDVQQHEwdaYWFuZGFtMRIwEAYDVQQLEwlNYXJrZXRpbmcxGzAZBgNVBAMTEnd3dy5tb2JpbGVmaXNoLmNvbTElMCMGCSqGSIb3DQEJARYWY29udGFjdEBtb2JpbGVmaXNoLmNvbTAeFw0xMTEwMTMxMDI2NTZaFw0xMjEwMTIxMDI2NTZaMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3o83CcmMMOC/fnjVv2puirJTs36+al6RDBe2tbFLKKODd29DZbmH9/6R77VPZACvXxBdRzMls//YRVHoJyJVudy+B4siUfHP80pssg2ZXCmCtUZGS71ohmlHcGQGTVLj8wmicf/DfmMAgq19OFZJP5LUn3md/MQBOUYrFXt21dQIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFAIuWYA/BMx8Gn/YOILevnJthkIZMIHVBgNVHSMEgc0wgcqAFAIuWYA/BMx8Gn/YOILevnJthkIZoYGupIGrMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEEBQADgYEABCb+f82DKWIWBczTeKGc6Ka5U7oys/itCY7XOYMIvXYPj+tb+5PBrmTO3jZNoZso9cYYFcDGXySbk6wSZiEPlbMqkoYE62E6dVXAmbza3ZNNIX/yEpkE3ZeBBtYzJMPQme9jrMgwgMIhgVzQNL2KPkbWOtQfoYgnThHQKLBry6Y=</dsig:X509Certificate>
              </dsig:X509Data>
            </dsig:KeyInfo>
          </dsig:Signature>
          <Message code="1">HMRC has received the IR-CIS-VERIFY document ref: 123/GL01 at 08.46 on 06/04/2017. The associated IRmark was: IT53IRTZFGY7FJYJS2DG5GPY5FNBHPXT. We advise you to keep this receipt in both electronic and hardcopy versions for your records. You may wish to use them to identify your submission in the future.</Message>
        </IRmarkReceipt>
        <Message code="9004">The Subcontractor Verification has been processed and passed full validation</Message>
        <AcceptedTime>2017-04-06T08:46:08.081</AcceptedTime>
        <ResponseData>
          <CISresponse xmlns="http://www.govtalk.gov.uk/taxation/CISresponse">
            <Contractor>
              <UTR>2413348328</UTR>
              <AOref>754PZ00002322</AOref>
            </Contractor>
            <Subcontractor>
              <Name>
                <Fore>PAUL</Fore>
                <Sur>PECAN</Sur>
              </Name>
              <TradingName>{tradingName getOrElse "Paul Pecan"}</TradingName>
              <UTR>{utr getOrElse "9325690695"}</UTR>
              <NINO>MT000245A</NINO>
              <Matched>{matched}</Matched>
              <TaxTreatment>{taxTreatment}</TaxTreatment>
              <VerificationNumber>{verificationNumber}</VerificationNumber>
            </Subcontractor>
          </CISresponse>
        </ResponseData>
      </SuccessResponse>
    </Body>
  </GovTalkMessage>

  override def errorResponseXmlBusiness(): Elem =
    CommonChrisResponse(transactionId, correlationId, timestamp, service, body, url, "2").toErrorResponseXml(
      <Error>
        <RaisedBy>ChRIS</RaisedBy>
        <Number>5005</Number>
        <Type>business</Type>
        <Text>Keys in the GovTalkDetails do not match those in the IRheader.</Text>
        <Location>/hd:GovTalkMessage[1]/hd:Body[1]/r68:IRenvelope[1]/r68:IRheader[1]/r68:Keys[1]/r68:Key[1]</Location>
        <ErrorData>AA00001</ErrorData>
      </Error>
    )

  override def errorResponseXmlFatal(): NodeSeq = ???
  override def acknowledgeResponseXml(): NodeSeq = ???
}