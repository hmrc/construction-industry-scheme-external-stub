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

import play.api.Logger
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig
import uk.gov.hmrc.constructionindustryschemeexternalstub.services.ChrisService
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper

import javax.inject.{Inject, Singleton}
import scala.xml.{Node, NodeSeq}

@Singleton()
class ChrisController @Inject() (
  service: ChrisService,
  config: AppConfig,
  resourceHelper: ResourceHelper,
  cc: ControllerComponents
) extends BackendController(cc) {
  private val logger = Logger(classOf[ChrisController])

  private val monthlyNilReturnResponsePath                      = "/resources/monthlyNilReturns"
  private val submitCISMessage_acknowledgement_ResponsePath     =
    s"$monthlyNilReturnResponsePath/submitCISMessage-acknowledgement-response.xml"
  private val submitCISMessage_success_ResponsePath             =
    s"$monthlyNilReturnResponsePath/submitCISMessage-success-response.xml"
  private val submitCISMessage_fatalError_ResponsePath          =
    s"$monthlyNilReturnResponsePath/submitCISMessage-fatalError-response.xml"
  private val submitCISMessage_businessError_ResponsePath       =
    s"$monthlyNilReturnResponsePath/submitCISMessage-businessError-response.xml"
  private val submitCISMessage_irMarkMismatchError_ResponsePath =
    s"$monthlyNilReturnResponsePath/submitCISMessage-irMarkMismatchError-response.xml"

  def submitCISMessage(): Action[AnyContent] = Action { (request: Request[AnyContent]) =>
    val message                           = request.body.asXml.get
    val correlationId                     = (message \ "Header" \ "MessageDetails" \ "CorrelationID").text
    val keys                              = message \ "GovTalkDetails" \ "Keys" \ "Key"
    def typeIs(value: String)(node: Node) = node \@ "Type" == value
    val taxOfficeNumber                   = (keys filter typeIs("TaxOfficeNumber")).text match {
      case text if text.nonEmpty => text
      case _                     => "123"
    }
    val taxOfficeReference                = (keys filter typeIs("TaxOfficeReference")).text

    val terminalStatus = service.terminalStatusFor(taxOfficeNumber)

    service.initialCisStatus(taxOfficeNumber, taxOfficeReference) match {
      case FATAL_ERROR =>
        Ok(
          replaceCorrelationId(
            resourceHelper.resourceAsString(submitCISMessage_fatalError_ResponsePath),
            correlationId
          )
        )

      case _ =>
        val basePollUrl  = config.pollUrl("IR-CIS-CIS300MR")
        val pollUrlWith0 = s"$basePollUrl/0?final=$terminalStatus"
        val xml          =
          resourceHelper
            .resourceAsString(submitCISMessage_acknowledgement_ResponsePath)
            .replace("[correlationId]", correlationId)
            .replace("[pollUrl]", pollUrlWith0)

        Ok(xml)
    }

  }

  def submitCISVerifyMessage(): Action[AnyContent] = Action { (request: Request[AnyContent]) =>
    val rootTextOpt = service.responseCISVerifyMessage(request.body.asXml.get)
    if (rootTextOpt.isDefined) {
      Ok(rootTextOpt.get)
    } else {
      NotFound
    }
  }

  def actionCISMessage(): Action[AnyContent] = Action { (request: Request[AnyContent]) =>
    val rootTextOpt = service.responseActionMonthlyReturnCISMessage(request.body.asXml.get)
    if (rootTextOpt.isDefined) {
      Ok(rootTextOpt.get)
    } else {
      NotFound
    }
  }

  def submitAsyncMessage(service: String, serviceId: String): Action[AnyContent] = Action {
    (request: Request[AnyContent]) =>
      request.body.asXml.map { (message: NodeSeq) =>
        logger.info(s"Async submission received: \n${message.toString}")
      }
      Ok
  }

  def asyncSubmitToChRISReceiver(serviceName: String, serviceId: String): Action[AnyContent] = Action {
    (request: Request[AnyContent]) =>
      Ok("<ChRISReply><SuccessfulAcknowledgement>OK</SuccessfulAcknowledgement></ChRISReply>")
  }

  def pollMessage(serviceName: String, count: Int, error: Boolean): Action[AnyContent] = Action {
    (request: Request[AnyContent]) =>
      val rootTextOpt = request.body.asXml.map { (message: NodeSeq) =>
        if (config.perfMode) {
          serviceName match {
            case "HMRC-SA-SA100"     => ??? // service.responseMessageSAFiling(message, error)
            case "HMRC-VAT-DEC"      => ??? // service.responseMessageVATDec(message, error)
            case "IR-PAYE-EXB"       => ??? // service.responseMessagePAYEEXB(message, error)
            case "HMRC-FATCA-RETURN" => ??? // service.responseMessageFATCAFiling(message, error)
            case "SDLT-RETURN"       => ??? // service.responseMessageSDLTFiling(message, error)
          }
        } else {
          service.pollMessage(message, serviceName, count, error)
        }
      }
      Ok(rootTextOpt.get)
  }

  def getCISResponse(count: Int): Action[AnyContent] = Action { request =>
    val message       = request.body.asXml.get
    val correlationId = (message \ "Header" \ "MessageDetails" \ "CorrelationID").text
    val isFinalPoll   = count >= 2

    val finalStatusParam: String =
      request.getQueryString("final").getOrElse("SUBMITTED")

    val status =
      if (!isFinalPoll) "ACKNOWLEDGE"
      else finalStatusParam

    val resourcePath = status match {
      case "ACKNOWLEDGE"          => submitCISMessage_acknowledgement_ResponsePath
      case "SUBMITTED_NO_RECEIPT" => submitCISMessage_irMarkMismatchError_ResponsePath
      case "FATAL_ERROR"          => submitCISMessage_fatalError_ResponsePath
      case "DEPARTMENTAL_ERROR"   => submitCISMessage_businessError_ResponsePath
      case _                      => submitCISMessage_success_ResponsePath
    }

    val rawXml      = resourceHelper.resourceAsString(resourcePath)
    val basePollUrl = config.pollUrl("IR-CIS-CIS300MR")

    val nextPollUrl =
      if (isFinalPoll) ""
      else s"$basePollUrl/${count + 1}?final=$finalStatusParam"

    val xml = rawXml
      .replace("[correlationId]", correlationId)
      .replace("[pollUrl]", nextPollUrl)

    Ok(xml)
  }

  private def replaceCorrelationId(response: String, correlationId: String): String =
    response.replace("[correlationId]", correlationId)

}
