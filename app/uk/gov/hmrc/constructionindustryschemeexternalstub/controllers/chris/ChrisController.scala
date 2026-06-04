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
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig
import uk.gov.hmrc.constructionindustryschemeexternalstub.services.ChrisService
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.inject.{Inject, Singleton}
import scala.xml.{Node, NodeSeq}

@Singleton()
class ChrisController @Inject() (
  service: ChrisService,
  config: AppConfig,
  resourceHelper: ResourceHelper,
  cc: ControllerComponents
) extends BackendController(cc) {
  private val logger      = Logger(classOf[ChrisController])
  private val irMarkStore = new ConcurrentHashMap[String, String]()

  private val monthlyNilReturnResponsePath                        = "/resources/monthlyNilReturns"
  private val submitCISMessage_acknowledgement_ResponsePath       =
    s"$monthlyNilReturnResponsePath/submitCISMessage-acknowledgement-response.xml"
  private val submitCISMessage_success_ResponsePath               =
    s"$monthlyNilReturnResponsePath/submitCISMessage-success-response.xml"
  private val submitCISMessage_fatalError_ResponsePath            =
    s"$monthlyNilReturnResponsePath/submitCISMessage-fatalError-response.xml"
  private val submitCISMessage_businessError_ResponsePath         =
    s"$monthlyNilReturnResponsePath/submitCISMessage-businessError-response.xml"
  private val submitCISMessage_irMarkMismatchError_ResponsePath   =
    s"$monthlyNilReturnResponsePath/submitCISMessage-irMarkMismatchError-response.xml"
  private val submitCISMessage_delete_ResponsePath                =
    s"$monthlyNilReturnResponsePath/submitCISMessage-delete-response.xml"
  private val submitCISMessage_recoverableError_3000_ResponsePath =
    s"$monthlyNilReturnResponsePath/submitCISMessage-recoverableError-3000-response.xml"
  private val submitCISMessage_recoverableError_2005_ResponsePath =
    s"$monthlyNilReturnResponsePath/submitCISMessage-recoverableError-2005-response.xml"
  private val submitCISMessage_recoverableError_1000_ResponsePath =
    s"$monthlyNilReturnResponsePath/submitCISMessage-recoverableError-1000-response.xml"

  private val verificationResponsePath                                = "/resources/verification"
  private val submitCISVerifyMessage_acknowledgement_ResponsePath     =
    s"$verificationResponsePath/submitCISVerifyMessage-acknowledgement-response.xml"
  private val submitCISVerifyMessage_fatalError_ResponsePath          =
    s"$verificationResponsePath/submitCISVerifyMessage-fatalError-response.xml"
  private val submitCISVerifyMessage_irMarkMismatchError_ResponsePath =
    s"$verificationResponsePath/submitCISVerifyMessage-irMarkMismatchError-response.xml"
  private val submitCISVerifyMessage_businessError_ResponsePath       =
    s"$verificationResponsePath/submitCISVerifyMessage-businessError-response.xml"
  private val submitCISVerifyMessage_success_ResponsePath             =
    s"$verificationResponsePath/submitCISVerifyMessage-success-response.xml"
  private val submitCISVerifyMessage_delete_ResponsePath              =
    s"$verificationResponsePath/submitCISVerifyMessage-delete-response.xml"

  def submitCISMessage(): Action[AnyContent] = Action { request =>
    submitCIS(
      request = request,
      regime = "IR-CIS-CIS300MR",
      acknowledgementResponsePath = submitCISMessage_acknowledgement_ResponsePath,
      fatalErrorResponsePath = submitCISMessage_fatalError_ResponsePath
    )
  }

  def submitCISVerifyMessage(): Action[AnyContent] = Action { request =>
    submitCIS(
      request = request,
      regime = "IR-CIS-VERIFY",
      acknowledgementResponsePath = submitCISVerifyMessage_acknowledgement_ResponsePath,
      fatalErrorResponsePath = submitCISVerifyMessage_fatalError_ResponsePath
    )
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
    getCisPollResponse(
      count = count,
      request = request,
      deleteResponsePath = submitCISMessage_delete_ResponsePath,
      finalStatusResponsePaths = Map(
        "ACKNOWLEDGE"            -> submitCISMessage_acknowledgement_ResponsePath,
        "SUBMITTED_NO_RECEIPT"   -> submitCISMessage_irMarkMismatchError_ResponsePath,
        "FATAL_ERROR"            -> submitCISMessage_fatalError_ResponsePath,
        "DEPARTMENTAL_ERROR"     -> submitCISMessage_businessError_ResponsePath,
        "RECOVERABLE_ERROR_3000" -> submitCISMessage_recoverableError_3000_ResponsePath,
        "RECOVERABLE_ERROR_2005" -> submitCISMessage_recoverableError_2005_ResponsePath,
        "RECOVERABLE_ERROR_1000" -> submitCISMessage_recoverableError_1000_ResponsePath
      ),
      defaultResponsePath = submitCISMessage_success_ResponsePath
    )
  }

  def getCISVerifyResponse(count: Int): Action[AnyContent] = Action { request =>
    getCisPollResponse(
      count = count,
      request = request,
      deleteResponsePath = submitCISVerifyMessage_delete_ResponsePath,
      finalStatusResponsePaths = Map(
        "ACKNOWLEDGE"          -> submitCISVerifyMessage_acknowledgement_ResponsePath,
        "SUBMITTED_NO_RECEIPT" -> submitCISVerifyMessage_irMarkMismatchError_ResponsePath,
        "FATAL_ERROR"          -> submitCISVerifyMessage_fatalError_ResponsePath,
        "DEPARTMENTAL_ERROR"   -> submitCISVerifyMessage_businessError_ResponsePath
      ),
      defaultResponsePath = submitCISVerifyMessage_success_ResponsePath
    )
  }

  private def replaceCorrelationId(response: String, correlationId: String, pollingUrlHost: String): String =
    response.replace("[correlationId]", correlationId).replace("[pollingUrlHost]", pollingUrlHost)

  private def submitCIS(
    request: Request[AnyContent],
    regime: String,
    acknowledgementResponsePath: String,
    fatalErrorResponsePath: String
  ): Result = {
    val message          = request.body.asXml.get
    val correlationId    = (message \ "Header" \ "MessageDetails" \ "CorrelationID").text
    val pollingUrlHost   = config.callback
    val gatewayTimestamp = LocalDateTime.now().toString
    val keys             = message \ "GovTalkDetails" \ "Keys" \ "Key"

    def typeIs(value: String)(node: Node) = node \@ "Type" == value

    val taxOfficeNumber    = (keys filter typeIs("TaxOfficeNumber")).text match {
      case text if text.nonEmpty => text
      case _                     => "123"
    }
    val taxOfficeReference = (keys filter typeIs("TaxOfficeReference")).text

    val irMark = (message \\ "IRmark").text.trim
    if (irMark.nonEmpty) {
      irMarkStore.put(correlationId, irMark)
      logger.info(s"[ChrisStub] Stored IRmark for correlationId=$correlationId")
    }

    service.initialCisStatus(taxOfficeNumber, taxOfficeReference) match {
      case FATAL_ERROR =>
        Ok(
          replaceCorrelationId(
            resourceHelper.resourceAsString(fatalErrorResponsePath),
            correlationId,
            pollingUrlHost
          )
        )

      case _ =>
        val basePollUrl = config.pollUrl(regime)

        val pollUrlWith0 =
          if (service.isForeverPending(taxOfficeNumber)) {
            s"$basePollUrl/0"
          } else {
            val finalStatus = service.terminalStatusFor(taxOfficeNumber)
            s"$basePollUrl/0?final=$finalStatus"
          }

        val xml =
          replaceCorrelationId(
            resourceHelper.resourceAsString(acknowledgementResponsePath),
            correlationId,
            pollingUrlHost
          )
            .replace("[pollUrl]", pollUrlWith0)
            .replace("[gatewayTimestamp]", gatewayTimestamp)

        Ok(xml)
    }
  }

  private def getCisPollResponse(
    count: Int,
    request: Request[AnyContent],
    deleteResponsePath: String,
    finalStatusResponsePaths: Map[String, String],
    defaultResponsePath: String
  ): Result = {
    val message          = request.body.asXml.get
    val correlationId    = (message \ "Header" \ "MessageDetails" \ "CorrelationID").text
    val pollingUrlHost   = config.callback
    val gatewayTimestamp = LocalDateTime.now().toString
    val function         = (message \ "Header" \ "MessageDetails" \ "Function").text

    logger.info(s"[ChrisStub] function=$function, correlationId=$correlationId, count=$count")

    if (function == "delete") {
      val rawXml = resourceHelper.resourceAsString(deleteResponsePath)
      val xml    = replaceCorrelationId(rawXml, correlationId, pollingUrlHost)
      Ok(xml)
    } else {
      val finalStatusParam =
        request.getQueryString("final").getOrElse("SUBMITTED")

      val resourcePath =
        finalStatusResponsePaths.getOrElse(finalStatusParam, defaultResponsePath)

      val rawXml       = resourceHelper.resourceAsString(resourcePath)
      val nextPollUrl  = ""
      val storedIrMark = Option(irMarkStore.get(correlationId)).getOrElse("NO_IRMARK_FOUND")

      val xml =
        replaceCorrelationId(rawXml, correlationId, pollingUrlHost)
          .replace("[pollUrl]", nextPollUrl)
          .replace("[gatewayTimestamp]", gatewayTimestamp)
          .replace("[digestValue]", storedIrMark)

      Ok(xml)
    }
  }

}
