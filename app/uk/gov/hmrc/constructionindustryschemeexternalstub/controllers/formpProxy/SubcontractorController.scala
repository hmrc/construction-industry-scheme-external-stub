/*
 * Copyright 2026 HM Revenue & Customs
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
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.{CreateSubmissionRequest, SubcontractorCreateRequest}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class SubcontractorController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val subcontractorResponsePath            = "/resources/subcontractor"
  private val createSubcontractor_201_ResponsePath = s"$subcontractorResponsePath/createSubcontractor-201-response.json"

  def createSubcontractor(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[SubcontractorCreateRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          body =>
            val enrolments = enrolmentHelper.contractorEnrolmentsOpt(request)
            enrolments match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
                  case _          => Created(resourceHelper.resourceAsString(createSubcontractor_201_ResponsePath))
                }
              case None                     => InternalServerError
            }
        )

    }
}
