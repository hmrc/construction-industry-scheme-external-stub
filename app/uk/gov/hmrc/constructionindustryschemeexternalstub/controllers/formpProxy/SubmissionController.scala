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
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.{CreateSubmissionRequest, UpdateSubmissionRequest}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

class SubmissionController @Inject() (
  authorise: AuthAction,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  def createSubmission(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[CreateSubmissionRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          body =>
            val orgEnrolments   = enrolmentHelper.contractorEnrolmentsOpt(request)
            val agentEnrolments = enrolmentHelper.agentEnrolmentsOpt(request)
            (orgEnrolments, agentEnrolments) match {
              case (Some(enrolmentReference), _) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
//                    case ("400", _) => BadRequest(Json.obj("message" -> "Missing CIS enrolment identifiers"))
//                    case ("404", _) => NotFound(Json.obj("message" -> "CIS taxpayer not found"))
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case _          => createSubmissionSuccessResponse
                }
              case (_, Some(_))                  => createSubmissionSuccessResponse
              case (None, None)                  => InternalServerError
            }
        )
    }

  def updateSubmission(): Action[JsValue] =
    Action(parse.json) { implicit request =>
      val scenario = request.getQueryString("scenario")
      request.body
        .validate[UpdateSubmissionRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ =>
            scenario match {
              case Some("500") => InternalServerError(Json.obj("message" -> "Unexpected error"))
              case _           => NoContent
            }
        )
    }

  private val submissionIdCounter = new AtomicLong(90000L)

  private def createSubmissionSuccessResponse: Result = {
    val submissionId = submissionIdCounter.incrementAndGet().toString
    logger.info(s"[SubmissionController] createSubmission returning submissionId=$submissionId")

    Created(Json.obj("submissionId" -> submissionId))
  }
}
