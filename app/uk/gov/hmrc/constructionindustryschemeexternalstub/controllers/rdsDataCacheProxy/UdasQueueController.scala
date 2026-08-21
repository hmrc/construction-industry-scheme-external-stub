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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.rdsDataCacheProxy

import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.{EnqueueClobRequest, EnqueueMessageHeaderRequest}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UdasQueueController @Inject() (
  authorise: AuthAction,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)(using ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def enqueueMessageHeader(): Action[JsValue] = authorise(parse.json) { implicit request =>
    request.body
      .validate[EnqueueMessageHeaderRequest]
      .fold(
        errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
        _ =>
          enrolmentHelper.agentEnrolmentsOpt(request) match {
            case Some(agentReference) =>
              agentReference match {
                case "400" => BadRequest(Json.obj("error" -> "credentialId and serviceName must be provided"))
                case "500" => InternalServerError(Json.obj("error" -> "could not enqueue message header"))
                case _     => Ok(Json.obj("messageId" -> 1))
              }
            case None                 => InternalServerError
          }
      )
  }

  def enqueueClob(): Action[JsValue] = authorise(parse.json) { implicit request =>
    request.body
      .validate[EnqueueClobRequest]
      .fold(
        errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
        _ =>
          enrolmentHelper.agentEnrolmentsOpt(request) match {
            case Some(agentReference) =>
              agentReference match {
                case "400" => BadRequest(Json.obj("error" -> "credentialId and serviceName must be provided"))
                case "500" => InternalServerError(Json.obj("error" -> "could not enqueue clob"))
                case _     => Ok(Json.obj("messageIDOut" -> 1))
              }
            case None                 => InternalServerError
          }
      )
  }
}
