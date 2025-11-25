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

case class CommonChrisResponse(
  transactionId: String,
  correlationId: String,
  timestamp: String,
  service: String,
  body: NodeSeq,
  url: String,
  pollInterval: String
) {

  def toPollXml: Elem = <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>1.0</EnvelopeVersion>
    <Header>
      <MessageDetails>
        <Class>{service}</Class>
        <Qualifier>acknowledgement</Qualifier>
        <Function>submit</Function>
        <TransactionID>{transactionId}</TransactionID>
        <CorrelationID>{correlationId}</CorrelationID>
        <ResponseEndPoint PollInterval={pollInterval}>{url}</ResponseEndPoint>
        <GatewayTest>1</GatewayTest>
        <GatewayTimestamp>{timestamp}</GatewayTimestamp>
      </MessageDetails>
      <SenderDetails/>
    </Header>
    <GovTalkDetails>
      <Keys/>
    </GovTalkDetails>
    {body}
  </GovTalkMessage>

  def toErrorResponseXml(errorResponse: Elem): Elem = {

    val errorTypeLower =
      (errorResponse \ "Type").text.trim.toLowerCase

    val isBusiness = errorTypeLower == "business"

    val bodyNode: scala.xml.NodeSeq =
      if (isBusiness)
        <Body>
          <ErrorResponse SchemaVersion="2.0">
            <Application>
              <MessageCount>1</MessageCount>
            </Application>
            <Error>
              <RaisedBy>System</RaisedBy>
              <Number>5005</Number>
              <Type>business</Type>
              <Text>Keys in the GovTalkDetails do not match those in the IRheader.</Text>
              <Location>/hd:GovTalkMessage[1]/hd:Body[1]/MTR:IRenvelope[1]/MTR:IRheader[1]/MTR:Keys[1]/MTR:Key[1]</Location>
            </Error>
          </ErrorResponse>
        </Body>
      else
        <Body/>

    val xml: Elem =
      <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
        <EnvelopeVersion>2.0</EnvelopeVersion>
        <Header>
          <MessageDetails>
            <Class>{service}</Class>
            <Qualifier>error</Qualifier>
            <Function>submit</Function>
            <TransactionID>{transactionId}</TransactionID>
            <CorrelationID>{correlationId}</CorrelationID>
            <ResponseEndPoint PollInterval={pollInterval}>{url}</ResponseEndPoint>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>{timestamp}</GatewayTimestamp>
          </MessageDetails>
        </Header>
        <GovTalkDetails>
          <Keys/>
          <GovTalkErrors>
            {errorResponse}
          </GovTalkErrors>
        </GovTalkDetails>
        {bodyNode}
      </GovTalkMessage>

    xml
  }
}
