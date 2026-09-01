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

import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.Logger
import play.api.http.HttpEntity
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request, ResponseHeader, Result}
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
  private val submitCISMessage_success_no_receipt_ResponsePath    =
    s"$monthlyNilReturnResponsePath/submitCISMessage-success-no-receipt-response.xml"
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

  private val verificationResponsePath                                   = "/resources/verification"
  private val submitCISVerifyMessage_acknowledgement_ResponsePath        =
    s"$verificationResponsePath/submitCISVerifyMessage-acknowledgement-response.xml"
  private val submitCISVerifyMessage_fatalError_ResponsePath             =
    s"$verificationResponsePath/submitCISVerifyMessage-fatalError-response.xml"
  private val submitCISVerifyMessage_irMarkMismatchError_ResponsePath    =
    s"$verificationResponsePath/submitCISVerifyMessage-irMarkMismatchError-response.xml"
  private val submitCISVerifyMessage_businessError_ResponsePath          =
    s"$verificationResponsePath/submitCISVerifyMessage-businessError-response.xml"
  private val submitCISVerifyMessage_departmentalError_3000_ResponsePath =
    s"$verificationResponsePath/submitCISVerifyMessage-departmentalError-3000-response.xml"
  private val submitCISVerifyMessage_success_ResponsePath                =
    s"$verificationResponsePath/submitCISVerifyMessage-success-response.xml"
  private val submitCISVerifyMessage_success_no_receipt_ResponsePath     =
    s"$verificationResponsePath/submitCISVerifyMessage-success-no-receipt-response.xml"
  private val submitCISVerifyMessage_delete_ResponsePath                 =
    s"$verificationResponsePath/submitCISVerifyMessage-delete-response.xml"

  private val ServerErrorTriggerTaxOfficeNumbers: Set[String] = (500 to 505).map(_.toString).toSet
  private val ServerErrorPollFinalStatuses: Set[String]       = (500 to 505).map(s => s"SERVER_ERROR_$s").toSet

  // Response-entity-failure scenarios: instead of returning a complete HTTP response, emit a 200 header followed by a
  // partial chunked body that fails mid-stream, so the caller sees a premature entity close (an incomplete-read /
  // stream-failure exception) rather than a completed 5xx response. This is not a TCP connection refused/reset - a
  // genuine connection-refused requires an unreachable listener, which a controller returning a response cannot
  // produce. The consuming service is expected to map this premature close onto its connection-failure branch
  // (validated in that service's own tests). Submit is keyed by TaxOfficeNumber; the poll handler never sees the TON,
  // so it is keyed by a ?final= token + count>=2, mirroring the SERVER_ERROR_* lever.
  private val ResponseEntityFailureTaxOfficeNumbers: Set[String] = Set("781")
  private val ResponseEntityFailurePollFinalStatus: String       = "CONNECTION_ABORT"

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
        "SUBMITTED_NO_RECEIPT"   -> submitCISMessage_success_no_receipt_ResponsePath,
        "FATAL_ERROR"            -> submitCISMessage_fatalError_ResponsePath,
        "DEPARTMENTAL_ERROR"     -> submitCISMessage_businessError_ResponsePath,
        "RECOVERABLE_ERROR_3000" -> submitCISMessage_recoverableError_3000_ResponsePath,
        "RECOVERABLE_ERROR_2005" -> submitCISMessage_recoverableError_2005_ResponsePath,
        "RECOVERABLE_ERROR_1000" -> submitCISMessage_recoverableError_1000_ResponsePath,
        "IRMARK_MISMATCH_ERROR"  -> submitCISMessage_irMarkMismatchError_ResponsePath
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
        "ACKNOWLEDGE"             -> submitCISVerifyMessage_acknowledgement_ResponsePath,
        "SUBMITTED_NO_RECEIPT"    -> submitCISVerifyMessage_success_no_receipt_ResponsePath,
        "FATAL_ERROR"             -> submitCISVerifyMessage_fatalError_ResponsePath,
        "DEPARTMENTAL_ERROR"      -> submitCISVerifyMessage_businessError_ResponsePath,
        "DEPARTMENTAL_ERROR_3000" -> submitCISVerifyMessage_departmentalError_3000_ResponsePath,
        "IRMARK_MISMATCH_ERROR"   -> submitCISVerifyMessage_irMarkMismatchError_ResponsePath
      ),
      defaultResponsePath = submitCISVerifyMessage_businessError_ResponsePath
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

    if (ResponseEntityFailureTaxOfficeNumbers.contains(taxOfficeNumber)) {
      terminateResponseEarly(correlationId)
    } else if (ServerErrorTriggerTaxOfficeNumbers.contains(taxOfficeNumber)) {
      val statusCode = taxOfficeNumber.toInt
      logger.info(s"[ChrisStub] Simulating $statusCode for taxOfficeNumber=$taxOfficeNumber corrId=$correlationId")
      Status(statusCode)("<error>Simulated ChRIS server error</error>").as("application/xml")
    } else {
      service.initialCisStatus(regime, taxOfficeNumber, taxOfficeReference) match {
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
            if (service.isForeverPending(regime, taxOfficeNumber)) {
              s"$basePollUrl/0"
            } else {
              val finalStatus = service.terminalStatusFor(regime, taxOfficeNumber)
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
    } else if (request.getQueryString("final").exists(ServerErrorPollFinalStatuses.contains) && count >= 2) {
      val finalStatus = request.getQueryString("final").get
      val statusCode  = finalStatus.stripPrefix("SERVER_ERROR_").toInt

      logger.info(s"[ChrisStub] Simulating $statusCode on poll corrId=$correlationId count=$count")
      Status(statusCode)("<error>Simulated ChRIS server error</error>").as("application/xml")
    } else if (request.getQueryString("final").contains(ResponseEntityFailurePollFinalStatus) && count >= 2) {
      terminateResponseEarly(correlationId)
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

  /** Emits a `200` header followed by a partial chunked body that fails mid-stream, so the calling HTTP client sees a
    * premature entity close rather than a completed HTTP response.
    */
  private def terminateResponseEarly(correlationId: String): Result = {
    logger.info(s"[ChrisStub] Failing response entity mid-stream corrId=$correlationId")
    val body = Source
      .single(ByteString("<partial"))
      .concat(Source.failed[ByteString](new RuntimeException("simulated response entity failure")))
    Result(
      header = ResponseHeader(OK),
      body = HttpEntity.Streamed(body, None, Some("application/xml"))
    )
  }

}
