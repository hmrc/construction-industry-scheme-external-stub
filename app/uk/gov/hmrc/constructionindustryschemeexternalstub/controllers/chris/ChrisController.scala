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
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{FATAL_ERROR, SUCCESS}

import javax.inject.{Inject, Singleton}
import scala.xml.NodeSeq

@Singleton()
class ChrisController @Inject() (service: ChrisService, config: AppConfig, cc: ControllerComponents)
    extends BackendController(cc) {
  private val logger = Logger(classOf[ChrisController])

  def submitCISMessage(): Action[AnyContent] = Action { (request: Request[AnyContent]) =>
    val rootTextOpt = service.responseCISMessage(request.body.asXml.get)
    if (rootTextOpt.isDefined) {
      Ok(rootTextOpt.get)
    } else {
      NotFound
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

  def getCISResponse(error: Boolean): Action[AnyContent] = Action { (request: Request[AnyContent]) =>

    val rootTextOpt = request.body.asXml.map(
      service.responseMessageCISMRFiling(
        _,
        "IR-CIS-CIS300MR",
        if (error) {
          FATAL_ERROR
        } else {
          SUCCESS
        }
      )
    )
    Ok(rootTextOpt.get)
  }

}
