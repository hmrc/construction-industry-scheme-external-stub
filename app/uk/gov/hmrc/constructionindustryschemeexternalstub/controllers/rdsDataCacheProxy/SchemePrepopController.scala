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

import javax.inject.Inject
import play.api.libs.json.*
import play.api.mvc.Results.{InternalServerError, NotFound}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.SchemePrepopKnownFactsRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import play.api.Logging

class SchemePrepopController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val resourcesBasePath                            = "/resources"
  private val getSchemePrepopByKnownFacts_200_ResponsePath =
    s"$resourcesBasePath/getSchemePrepopByKnownFacts-200-response.json"

  def getSchemePrepopByKnownFacts: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[SchemePrepopKnownFactsRequest]
        .fold(
          errs =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid JSON body",
                "errors"  -> JsError.toJson(errs)
              )
            ),
          knownFacts => {
            val context =
              s"TON=${knownFacts.taxOfficeNumber}, " +
                s"TOR=${knownFacts.taxOfficeReference}, " +
                s"AO=${knownFacts.aoRef}"

            val enrolments = enrolmentHelper
              .contractorEnrolmentsOpt(request)
              .orElse(enrolmentHelper.agentEnrolmentsOpt(request))

            enrolments match {
              case Some(_) =>
                (knownFacts.taxOfficeNumber, knownFacts.taxOfficeReference, knownFacts.aoRef) match {
                  case ("404", _, _) =>
                    NotFound(
                      Json.obj(
                        "message" -> s"No CIS scheme pre-pop data found for $context"
                      )
                    )

                  case ("500", _, _) =>
                    InternalServerError(Json.obj("message" -> "Unexpected error"))

                  case (taxOfficeNumber, taxOfficeReference, aoRef) =>
                    val json =
                      Json.parse(resourceHelper.resourceAsString(getSchemePrepopByKnownFacts_200_ResponsePath))

                    val updated = json
                      .as[JsObject]
                      .deepMerge(
                        Json.obj(
                          "taxOfficeNumber"    -> taxOfficeNumber,
                          "taxOfficeReference" -> taxOfficeReference,
                          "aoRef"              -> aoRef
                        )
                      )

                    Ok(updated)
                }

              case None =>
                InternalServerError
            }
          }
        )
    }
}
