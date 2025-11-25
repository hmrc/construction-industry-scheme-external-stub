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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.iass

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, *}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}

@Singleton
class StubController @Inject() (
  val authConnector: AuthConnector,
  authorise: AuthAction,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with AuthorisedFunctions {

  def clientlist(service: String, credential: String, ignored: String): Action[JsValue] = authorise(parse.json) {
    implicit request =>
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(agentReference) =>
          agentReference match {
            case "400" => BadRequest(Json.obj("message" -> "Missing CIS enrolment identifiers"))
            case "404" => NotFound(Json.obj("message" -> "CIS taxpayer not found"))
            case "500" => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case _     => Ok("test")
          }
        case None                 => InternalServerError
      }
  }

}
