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

import javax.inject.Inject

class SubmissionController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val monthlyNilReturnResponsePath      = "/resources/monthlyNilReturns"
  private val createSubmission_200_ResponsePath = s"$monthlyNilReturnResponsePath/createSubmission-200-response.json"
  private val getGovTalkStatus_200_ResponsePath = s"$monthlyNilReturnResponsePath/getGovTalkStatus-200-response.json"

  def createSubmission(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[CreateSubmissionRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          body =>
            val enrolments = enrolmentHelper.contractorEnrolmentsOpt(request)
            enrolments match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
//                    case ("400", _) => BadRequest(Json.obj("message" -> "Missing CIS enrolment identifiers"))
//                    case ("404", _) => NotFound(Json.obj("message" -> "CIS taxpayer not found"))
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case _          => Created(resourceHelper.resourceAsString(createSubmission_200_ResponsePath))
                }
              case None                     => InternalServerError
            }
        )
    }

  def updateSubmission(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[UpdateSubmissionRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          body =>
            val enrolments = enrolmentHelper.contractorEnrolmentsOpt(request)
            enrolments match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
//                  case ("400", _) => BadRequest(Json.obj("message" -> "Missing CIS enrolment identifiers"))
//                  case ("404", _) => NotFound(Json.obj("message" -> "CIS taxpayer not found"))
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case _          => NoContent
                }
              case None                     => InternalServerError
            }
        )
    }

  def getGovTalkStatus: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[GetGovTalkStatusRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          body =>
            val enrolments = enrolmentHelper.contractorEnrolmentsOpt(request)
            enrolments match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case _          => Ok(resourceHelper.resourceAsString(getGovTalkStatus_200_ResponsePath))
                }
              case None                     => InternalServerError
            }
        )
    }
}
