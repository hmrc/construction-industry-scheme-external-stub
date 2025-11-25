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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.ClientListDownloadStatus.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ClientController @Inject() (
  authorise: AuthAction,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)(using ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getClientListDownloadStatus(
    credentialId: String,
    serviceName: String,
    gracePeriod: Int = 14400
  ): Action[AnyContent] = authorise { implicit request =>
    if (serviceName.trim().isEmpty || credentialId.trim().isEmpty) {
      BadRequest(Json.obj("error" -> "credentialId and serviceName must be provided"))
    } else {
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(agentReference) =>
          agentReference match {
            case "400"    => BadRequest(Json.obj("error" -> "credentialId and serviceName must be provided"))
            case "500"    => InternalServerError(Json.obj("error" -> "Could not map client list download status"))
            case "InDown" => Ok(Json.obj("status" -> InitiateDownload.toString))
            case "InProg" => Ok(Json.obj("status" -> InProgress.toString))
            case "Succes" => Ok(Json.obj("status" -> Succeeded.toString))
            case "Failed" => Ok(Json.obj("status" -> Failed.toString))
          }
        case None                 => InternalServerError
      }
    }
  }
}
