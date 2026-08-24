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
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ClientController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)(using ExecutionContext)
    extends BackendController(cc)
    with Logging {

  private val responsePath                            = "/resources"
  private val getClientList_200_ResponsePath          = s"$responsePath/getClientList-200-response.json"
  private val getClientListByEmployerRef_ResponsePath = s"$responsePath/getClientListByEmployerRef-1-response.json"

  private val getClientList_200_Alt_ResponsePath =
    s"$responsePath/getClientList-200-alt-response.json"

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
            case "Failed" => Ok(Json.obj("status" -> Failed.toString))
            case _        => Ok(Json.obj("status" -> Succeeded.toString))
          }
        case None                 => InternalServerError
      }
    }
  }

  def getClientList(
    irAgentId: String,
    credentialId: String,
    start: Int = 0,
    count: Int = -1,
    sort: Int = 0,
    ascending: Boolean = true
  ): Action[AnyContent] = authorise { implicit request =>
    if (irAgentId.trim().isEmpty || credentialId.trim().isEmpty) {
      BadRequest(Json.obj("error" -> "credentialId and irAgentId must be provided"))
    } else {
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(_) =>
          irAgentId match {
//            case "400" => BadRequest(Json.obj("error" -> "credentialId and irAgentId must be provided"))
            case "500"    => InternalServerError(Json.obj("error" -> "Could not get client list"))
            case "000123" => Ok(resourceHelper.resourceAsString(getClientList_200_Alt_ResponsePath))
            case _        =>
              Ok(resourceHelper.resourceAsString(getClientList_200_ResponsePath))
          }
        case None    => InternalServerError
      }
    }
  }

  def hasClient(
    irAgentId: String,
    credentialId: String,
    taxOfficeNumber: String,
    taxOfficeReference: String
  ): Action[AnyContent] = authorise { implicit request =>
    if (irAgentId.trim().isEmpty || credentialId.trim().isEmpty) {
      BadRequest(Json.obj("error" -> "credentialId and irAgentId must be provided"))
    } else {
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(_) =>
          irAgentId match {
            case "400"                                                                             => BadRequest(Json.obj("error" -> "credentialId and irAgentId must be provided"))
            case "500"                                                                             => InternalServerError(Json.obj("error" -> "Could not check hasClient"))
            case "000123" if List("789", "900").contains(taxOfficeNumber)                          => Ok(Json.obj("hasClient" -> true))
            case _ if irAgentId != "000123" && List("123", "456", "754").contains(taxOfficeNumber) =>
              Ok(Json.obj("hasClient" -> true))
            case _                                                                                 => Ok(Json.obj("hasClient" -> false))
          }
        case None    => InternalServerError
      }
    }

  }

  def getClientsByEmployersReference(
    irAgentId: String,
    credentialId: String,
    employerRef: String
  ): Action[AnyContent] = authorise { implicit request =>
    if (irAgentId.trim().isEmpty || credentialId.trim().isEmpty || employerRef.trim().isEmpty) {
      BadRequest(Json.obj("error" -> "credentialId and irAgentId and employerRef must be provided"))
    } else {
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(_) =>
          irAgentId match {
            case "400" => BadRequest(Json.obj("error" -> "credentialId and irAgentId must be provided"))
            case "500" => InternalServerError(Json.obj("error" -> "Could not get clients by employersRef"))
            case _     =>
              Ok(resourceHelper.resourceAsString(getClientListByEmployerRef_ResponsePath))
          }
        case None    => InternalServerError
      }
    }
  }
}
