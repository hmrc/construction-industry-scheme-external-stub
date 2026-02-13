/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.formpProxy

import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.JsResultUtils.foldErrorsIntoBadRequest

import javax.inject.Inject
import scala.concurrent.Future

class GovTalkController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val govTalkReturnResponsePath              = "/resources/govTalk"
  private val getGovTalkStatus_200_ResponsePath      = s"$govTalkReturnResponsePath/getGovTalkStatus-200-response.json"
  private val getGovTalkStatus_200_EmptyResponsePath =
    s"$govTalkReturnResponsePath/getGovTalkStatus-200-empty-response.json"

  def getGovTalkStatus: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[GetGovTalkStatusRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          body =>
            enrolmentHelper.contractorEnrolmentsOpt(request) match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
                  case ("404", _) => Ok(resourceHelper.resourceAsString(getGovTalkStatus_200_EmptyResponsePath))
                  case _          => Ok(resourceHelper.resourceAsString(getGovTalkStatus_200_ResponsePath))
                }
              case None                     =>
                enrolmentHelper.agentEnrolmentsOpt(request) match {
                  case Some("404") => Ok(resourceHelper.resourceAsString(getGovTalkStatus_200_EmptyResponsePath))
                  case Some(_)     => Ok(resourceHelper.resourceAsString(getGovTalkStatus_200_ResponsePath))
                  case None        => InternalServerError
                }
            }
        )
    }

  def updateGovTalkStatusCorrelationId: Action[JsValue] =
    authorise(parse.json).async { implicit request =>
      request.body
        .validate[UpdateGovTalkStatusCorrelationIdRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(NoContent)
        }
    }
}
