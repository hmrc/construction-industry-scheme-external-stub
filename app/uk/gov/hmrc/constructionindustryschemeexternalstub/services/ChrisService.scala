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

import java.util.{Date, TimeZone}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{ACKNOWLEDGE, BUSINESS_ERROR, CISMRFilingResponse, CISVerifyResponse, ChRISResponseType, ChrisResponse, CommonChrisResponse, FATAL_ERROR, SUCCESS}

import scala.xml.{Node, NodeSeq}

@Singleton
class ChrisService @Inject() (config: AppConfig) {

  private val logger = Logger(classOf[ChrisService])

  private def makeSimpleDataFormatter() = {
    val simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
    simpleDateFormat
  }

  def responseCISMessage(message: NodeSeq): Option[NodeSeq] = {
    val messageClass = (message \ "Header" \ "MessageDetails" \ "Class").text
    val keys         = message \ "GovTalkDetails" \ "Keys" \ "Key"

    def typeIs(value: String)(node: Node) = node \@ "Type" == value

    val taxOfficeNumber    = (keys filter typeIs("TaxOfficeNumber")).text match {
      case text if text.nonEmpty => text
      case _                     => "123"
    }
    val taxOfficeReference = (keys filter typeIs("TaxOfficeReference")).text

    val acknowledge = config.acknowledgeFilter.contains(taxOfficeNumber + "/" + taxOfficeReference)
    val fatalError  = config.fatalErrorFilter.contains(taxOfficeNumber + "/" + taxOfficeReference)

    val pollTerminalStatus = config.pollingStatus(taxOfficeNumber)

    val responseType: ChRISResponseType =
      calculateResponseType(acknowledge = acknowledge, fatalError = fatalError)

    if (messageClass == "IR-CIS-CIS300MR") {
      Some(sendCISMonthlyReturnMessage(message, messageClass, responseType, pollTerminalStatus))
    } else {
      None
    }
  }

  def responseCISVerifyMessage(message: NodeSeq): Option[NodeSeq] = {
    val messageClass = (message \ "Header" \ "MessageDetails" \ "Class").text
    if (messageClass == "IR-CIS-VERIFY") {
      Some(sendCISVerifyMessage(message, SUCCESS))
    } else {
      None
    }
  }

  def responseActionMonthlyReturnCISMessage(message: NodeSeq): Option[NodeSeq] = {
    val messageClass = (message \ "Header" \ "MessageDetails" \ "Class").text
    if (messageClass == "IR-CIS-CIS300MR") {
      Some(sendCISActionMonthlyReturnMessage(message, "IR-CIS-CIS300MR"))
    } else {
      None
    }
  }

  private def sendCISMonthlyReturnMessage(
    message: NodeSeq,
    service: String,
    responseType: ChRISResponseType,
    pollTerminalStatus: String
  ): NodeSeq =
    responseMessageCISMRFiling(message, service, responseType, pollTerminalStatus)

  private def sendCISActionMonthlyReturnMessage(message: NodeSeq, service: String): NodeSeq =
    responseMessageCISMRFiling(message, service)

  private def sendCISVerifyMessage(message: NodeSeq, responseType: ChRISResponseType): NodeSeq =
    responseMessageCISVerify(message, responseType)

  private def isError(rt: ChRISResponseType): Boolean =
    rt match {
      case BUSINESS_ERROR | FATAL_ERROR => true
      case _                            => false
    }

  def pollMessage(message: NodeSeq, service: String, count: Int, error: Boolean): NodeSeq = {
    logger.info(s"Message received to send to Chris: \n${message.toString}")
    val body           = message \ "Body"
    val messageDetails = message \ "Header" \ "MessageDetails"
    val transactionId  = (messageDetails \ "TransactionID").text
    val correlationId  = (messageDetails \ "CorrelationID").text
    val timestamp      = makeSimpleDataFormatter().format(new Date())
    val url            = if (count < 3) {
      "%s/%s/%s".format(config.pollUrl(service), count + 1, error)
    } else {
      "%s/%s".format(config.responseUrl(service), error)
    }
    val response       = CommonChrisResponse(transactionId, correlationId, timestamp, service, body, url, config.pollInterval)
    logger.info(s"Response is : ${response.toPollXml.toString()}")
    response.toPollXml
  }

  def responseMessageCISMRFiling(
    message: NodeSeq,
    service: String,
    responseType: ChRISResponseType = SUCCESS,
    pollTerminalStatus: String = "SUBMITTED"
  ): NodeSeq = {
    val body                = message \ "Body"
    val messageDetails      = message \ "Header" \ "MessageDetails"
    val transactionId       = (messageDetails \ "TransactionID").text
    val correlationId       = (messageDetails \ "CorrelationID").text
    val timestamp           = makeSimpleDataFormatter().format(new Date())
    val acknowledgementPoll = pollTerminalStatus match {
      case "SUBMITTED"            => config.pollUrl(service) + s"/0/false"
      case "SUBMITTED_NO_RECEIPT" => config.pollUrl(service) + s"/0/false"
      case "FATAL_ERROR"          => config.pollUrl(service) + s"/0/true"
      case "DEPARTMENTAL_ERROR"   => config.pollUrl(service) + s"/0/true"
      case "ACKNOWLEDGE"          => config.pollUrl(service) + "/-100/true"
      case _                      => config.pollUrl(service) + s"/0/false"
    }

    val url        = "%s/%s".format(config.responseUrl(service), isError(responseType))
    val pollingUrl =
      if (responseType == ACKNOWLEDGE) acknowledgementPoll
      else ""

    val irMarkValue         = Option((message \ "Body" \ "IRenvelope" \ "IRheader" \ "IRmark").text)
    val cisMRFilingResponse =
      CISMRFilingResponse(transactionId, correlationId, timestamp, service, body, url, pollingUrl, irMarkValue)

    logger.info(s"Message received to send to Chris: \n${message.toString}")
    responseMessage(cisMRFilingResponse, responseType)
  }

  private def responseMessageCISVerify(message: NodeSeq, responseType: ChRISResponseType): NodeSeq = {
    val service        = "IR-CIS-VERIFY"
    val body           = message \ "Body"
    val messageDetails = message \ "Header" \ "MessageDetails"
    val transactionId  = (messageDetails \ "TransactionID").text
    val correlationId  = (messageDetails \ "CorrelationID").text
    val timestamp      = makeSimpleDataFormatter().format(new Date())
    val url            = "%s/%s".format(config.responseUrl(service), isError(responseType))
    val irMarkValue    = Option((message \ "Body" \ "IRenvelope" \ "IRheader" \ "IRmark").text)

    val utr                                         = Option(message \ "Body" \ "IRenvelope" \ "CISrequest" \ "Subcontractor" \ "UTR").map(_.text)
    val tradingName                                 =
      Option(message \ "Body" \ "IRenvelope" \ "CISrequest" \ "Subcontractor" \ "TradingName").map(_.text)
    val worksRef                                    = Option(message \ "Body" \ "IRenvelope" \ "CISrequest" \ "Subcontractor" \ "WorksRef").map(_.text)
    val (matched, taxTreatment, verificationNumber) = defineSubcontractorResponseDetails(worksRef.getOrElse(""))

    val cisVerifyResponse = CISVerifyResponse(
      transactionId,
      correlationId,
      timestamp,
      service,
      body,
      utr,
      matched,
      taxTreatment,
      verificationNumber,
      tradingName,
      url,
      irMarkValue
    )

    logger.info(s"Message received to send to Chris: \n${message.toString}")
    responseMessage(cisVerifyResponse, responseType)
  }

  private def defineSubcontractorResponseDetails(worksRef: String) =
    if (worksRef.contains("unmatched"))
      ("unmatched", "", "")
    else if (worksRef.contains("net"))
      ("matched", "net", "V1000000009")
    else
      ("matched", "gross", "V1000000009")

  private def responseMessage(response: ChrisResponse, responseType: ChRISResponseType): NodeSeq =
    responseType match {
      case FATAL_ERROR    =>
        logger.info(s"Error response is : ${response.errorResponseXmlFatal().toString()}")
        response.errorResponseXmlFatal()
      case BUSINESS_ERROR =>
        logger.info(s"Error response is : ${response.errorResponseXmlBusiness().toString()}")
        response.errorResponseXmlBusiness()
      case ACKNOWLEDGE    =>
        logger.info(s"Pending response is : ${response.acknowledgeResponseXml().toString()}")
        response.acknowledgeResponseXml()
      case SUCCESS        =>
        logger.info(s"Response is : ${response.successResponseXml().toString()}")
        response.successResponseXml()
    }

  private def calculateResponseType(
    acknowledge: Boolean = false,
    fatalError: Boolean = false,
    businessError: Boolean = false
  ): ChRISResponseType =
    if (fatalError) FATAL_ERROR
    else if (acknowledge) ACKNOWLEDGE
    else if (businessError) BUSINESS_ERROR
    else SUCCESS

  def initialCisStatus(taxOfficeNumber: String, taxOfficeReference: String): ChRISResponseType = {
    val key = s"$taxOfficeNumber/$taxOfficeReference"

    if (config.fatalErrorFilter.contains(key)) FATAL_ERROR
    else ACKNOWLEDGE
  }

  def terminalStatusFor(taxOfficeNumber: String): String =
    config.pollingStatus(taxOfficeNumber)

  def isForeverPending(taxOfficeNumber: String): Boolean =
    terminalStatusFor(taxOfficeNumber) == "ACKNOWLEDGE"
}

object CorrelationIDGenerator {
  private val HEX_CHARS = ('A' to 'F') ++ ('0' to '9')

  def randomHexString(length: Int): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      val randomNum = util.Random.nextInt(HEX_CHARS.length)
      sb.append(HEX_CHARS(randomNum))
    }
    sb.toString
  }
}
