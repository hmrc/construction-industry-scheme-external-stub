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
import play.api.mvc.Results.{InternalServerError, NotFound}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import javax.inject.Inject

class CisTaxpayerController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val monthlyNilReturnResponsePath = "/resources"
  private val getCisTaxpayerByTaxReference_200_ResponsePath = s"$monthlyNilReturnResponsePath/getCisTaxpayerByTaxReference-200-response.json"

  def getCisTaxpayerByTaxReference: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[EmployerReference]
        .fold(
          errs =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid JSON body",
                "errors"  -> JsError.toJson(errs)
              )
            ),
          er => {
            val enrolments = enrolmentHelper.contractorEnrolmentsOpt(request)
            enrolments match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
                  case ("404", _) => NotFound(Json.obj("message" -> s"CIS taxpayer not found for TON=${er.taxOfficeNumber}, TOR=${er.taxOfficeReference}"))
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case _ => Ok(resourceHelper.resourceAsString(getCisTaxpayerByTaxReference_200_ResponsePath))
                }
              case None => InternalServerError
            }
          }
        )
    }

}
