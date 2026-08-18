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
import play.api.libs.json.{JsError, JsObject, JsValue, Json}
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

  private val monthlyNilReturnResponsePath                             = "/resources"
  private val verificationResponsePath                                 = "/resources/verification"
  private val getCisTaxpayerByTaxReference_200_ResponsePath            =
    s"$monthlyNilReturnResponsePath/getCisTaxpayerByTaxReference-200-response.json"
  private val getNewestVerificationBatch_200_VerifyOnly_ResponsePath   =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-no-reverify.json"
  private val getNewestVerificationBatch_200_ReverifyOnly_ResponsePath =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-no-newly-added.json"
  private val getNewestVerificationBatch_200_Inactive_ResponsePath     =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-inactive.json"

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
            val enrolments = enrolmentHelper
              .contractorEnrolmentsOpt(request)
              .orElse(enrolmentHelper.agentEnrolmentsOpt(request))
            enrolments match {
              case Some(enrolmentReference) =>
                (er.taxOfficeNumber, er.taxOfficeReference) match {
                  case ("404", _)     =>
                    NotFound(
                      Json.obj(
                        "message" -> s"CIS taxpayer not found for TON=${er.taxOfficeNumber}, TOR=${er.taxOfficeReference}"
                      )
                    )
                  case (_, "EZ10800") =>
                    val json    =
                      Json.parse(resourceHelper.resourceAsString(getCisTaxpayerByTaxReference_200_ResponsePath))
                    val updated = json
                      .as[JsObject]
                      .deepMerge(
                        Json.obj(
                          "uniqueId"        -> "800",
                          "taxOfficeNumber" -> er.taxOfficeNumber,
                          "taxOfficeRef"    -> er.taxOfficeReference
                        )
                      )
                    Ok(updated)
                  case (_, "EZ00125") =>
                    val json    =
                      Json
                        .parse(resourceHelper.resourceAsString(getNewestVerificationBatch_200_VerifyOnly_ResponsePath))
                    val updated = json
                      .as[JsObject]
                      .deepMerge(
                        Json.obj(
                          "uniqueId"        -> "125",
                          "taxOfficeNumber" -> er.taxOfficeNumber,
                          "taxOfficeRef"    -> er.taxOfficeReference
                        )
                      )
                    Ok(updated)
                  case (_, "EZ00150") =>
                    val json    =
                      Json.parse(
                        resourceHelper.resourceAsString(getNewestVerificationBatch_200_ReverifyOnly_ResponsePath)
                      )
                    val updated = json
                      .as[JsObject]
                      .deepMerge(
                        Json.obj(
                          "uniqueId"        -> "150",
                          "taxOfficeNumber" -> er.taxOfficeNumber,
                          "taxOfficeRef"    -> er.taxOfficeReference
                        )
                      )
                    Ok(updated)
                  case (_, "EZ00175") =>
                    val json    =
                      Json.parse(resourceHelper.resourceAsString(getNewestVerificationBatch_200_Inactive_ResponsePath))
                    val updated = json
                      .as[JsObject]
                      .deepMerge(
                        Json.obj(
                          "uniqueId"        -> "175",
                          "taxOfficeNumber" -> er.taxOfficeNumber,
                          "taxOfficeRef"    -> er.taxOfficeReference
                        )
                      )
                    Ok(updated)
                  case (ton, tor)     =>
                    val json    =
                      Json.parse(resourceHelper.resourceAsString(getCisTaxpayerByTaxReference_200_ResponsePath))
                    val updated = json
                      .as[JsObject]
                      .deepMerge(
                        Json.obj(
                          "taxOfficeNumber" -> ton,
                          "taxOfficeRef"    -> tor
                        )
                      )
                    Ok(updated)
                }
              case None                     => InternalServerError
            }
          }
        )
    }
}
