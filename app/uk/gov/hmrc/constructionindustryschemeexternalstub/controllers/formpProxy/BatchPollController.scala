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
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class BatchPollController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val batchPollResponsePath = "/resources/batchPoll"

  private val getBatchPollSubmissions200ResponsePath =
    s"$batchPollResponsePath/getBatchPollSubmissions-200-response.json"

  private val getBatchPollSubmissionsEmpty200ResponsePath =
    s"$batchPollResponsePath/getBatchPollSubmissions-empty-200-response.json"

  def getBatchPollSubmissions: Action[JsValue] = authorise(parse.json) { implicit request =>
    enrolmentHelper.contractorEnrolmentsOpt(request) match {
      case Some(enrolmentReference) =>
        (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
          case ("500", _) => InternalServerError("""{"message":"Unexpected error"}""")
          case ("502", _) => BadGateway("""{"message":"formp failed"}""")
          case ("000", _) => Ok(resourceHelper.resourceAsString(getBatchPollSubmissionsEmpty200ResponsePath))
          case _          => Ok(resourceHelper.resourceAsString(getBatchPollSubmissions200ResponsePath))
        }

      case None =>
        enrolmentHelper.agentEnrolmentsOpt(request) match {
          case Some("AGT500") => InternalServerError("""{"message":"Unexpected error"}""")
          case Some("AGT502") => BadGateway("""{"message":"formp failed"}""")
          case Some("AGT000") => Ok(resourceHelper.resourceAsString(getBatchPollSubmissionsEmpty200ResponsePath))
          case Some(_)        => Ok(resourceHelper.resourceAsString(getBatchPollSubmissions200ResponsePath))
          case None           => InternalServerError("""{"message":"Missing enrolment"}""")
        }
    }
  }
}
