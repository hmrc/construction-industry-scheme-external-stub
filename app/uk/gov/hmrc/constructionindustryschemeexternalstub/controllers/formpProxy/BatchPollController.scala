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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class BatchPollController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val batchPollResponsePath = "/resources/batchPoll"

  private val getBatchPollSubmissions200ResponsePath =
    s"$batchPollResponsePath/getBatchPollSubmissions-200-response.json"

  private val getBatchPollSubmissions200EmptyResponsePath =
    s"$batchPollResponsePath/getBatchPollSubmissions-200-empty-response.json"

  def getBatchPollSubmissions: Action[AnyContent] = authorise { implicit request =>
    enrolmentHelper.contractorEnrolmentsOpt(request) match {
      case Some(enrolmentReference) =>
        (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
          case ("500", _) =>
            logger.warn("[BatchPollController][getBatchPollSubmissions] Contractor 500 - returning Unexpected error")
            InternalServerError(Json.obj("message" -> "Unexpected error"))
          case ("502", _) =>
            logger.warn("[BatchPollController][getBatchPollSubmissions] Contractor 502 - returning formp failed")
            BadGateway(Json.obj("message" -> "formp failed"))
          case ("000", _) =>
            logger.info("[BatchPollController][getBatchPollSubmissions] Contractor 000 - returning empty submissions")
            Ok(resourceHelper.resourceAsString(getBatchPollSubmissions200EmptyResponsePath))
          case _          =>
            logger.info("[BatchPollController][getBatchPollSubmissions] Contractor - returning batch poll submissions")
            Ok(resourceHelper.resourceAsString(getBatchPollSubmissions200ResponsePath))
        }

      case None =>
        enrolmentHelper.agentEnrolmentsOpt(request) match {
          case Some("AGT500") =>
            logger.warn("[BatchPollController][getBatchPollSubmissions] Agent AGT500 - returning Unexpected error")
            InternalServerError(Json.obj("message" -> "Unexpected error"))
          case Some("AGT502") =>
            logger.warn("[BatchPollController][getBatchPollSubmissions] Agent AGT502 - returning formp failed")
            BadGateway(Json.obj("message" -> "formp failed"))
          case Some("AGT000") =>
            logger.info("[BatchPollController][getBatchPollSubmissions] Agent AGT000 - returning empty submissions")
            Ok(resourceHelper.resourceAsString(getBatchPollSubmissions200EmptyResponsePath))
          case Some(_)        =>
            logger.info("[BatchPollController][getBatchPollSubmissions] Agent - returning batch poll submissions")
            Ok(resourceHelper.resourceAsString(getBatchPollSubmissions200ResponsePath))
          case None           =>
            logger.warn("[BatchPollController][getBatchPollSubmissions] No valid enrolment found")
            InternalServerError(Json.obj("message" -> "Missing enrolment"))
        }
    }
  }
}
