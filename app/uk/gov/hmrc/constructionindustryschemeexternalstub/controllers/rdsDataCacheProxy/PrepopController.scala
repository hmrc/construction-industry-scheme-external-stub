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
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.PrepopKnownFacts
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import play.api.Logging
import scala.collection.Seq

class PrepopController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val resourcesBasePath                            = "/resources"
  private val getSchemePrepopByKnownFacts_200_ResponsePath =
    s"$resourcesBasePath/getSchemePrepopByKnownFacts-200-response.json"
  private val getSubconPrepopByKnownFacts_200_ResponsePath =
    s"$resourcesBasePath/getSubcontractorPrepopByKnownFacts-200-response.json"

  private def context(knownFactsSummary: PrepopKnownFacts): String =
    s"TON=${knownFactsSummary.taxOfficeNumber}, TOR=${knownFactsSummary.taxOfficeReference}, AO=${knownFactsSummary.agentOwnReference}"

  private def badJson(errs: Seq[(JsPath, Seq[JsonValidationError])]): Result =
    BadRequest(
      Json.obj(
        "message" -> "Invalid JSON body",
        "errors"  -> JsError.toJson(errs)
      )
    )

  private def knownfactsJson(knownFactsSummary: PrepopKnownFacts): JsObject =
    Json.obj(
      "knownfacts" -> Json.toJson(knownFactsSummary)
    )

  def getSchemePrepopByKnownFacts: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[PrepopKnownFacts]
        .fold(
          errs => badJson(errs),
          knownFacts => {
            val ctx = context(knownFacts)

            val enrolments = enrolmentHelper
              .contractorEnrolmentsOpt(request)
              .orElse(enrolmentHelper.agentEnrolmentsOpt(request))

            enrolments match {
              case Some(_) =>
                (knownFacts.taxOfficeNumber, knownFacts.taxOfficeReference, knownFacts.agentOwnReference) match {
                  case ("404", _, _) =>
                    NotFound(
                      Json.obj(
                        "message" -> s"No CIS scheme pre-pop data found for $ctx"
                      )
                    )

                  case ("500", _, _) =>
                    InternalServerError(Json.obj("message" -> "Unexpected error"))

                  case _ =>
                    val json =
                      Json
                        .parse(
                          resourceHelper.resourceAsString(getSchemePrepopByKnownFacts_200_ResponsePath)
                        )
                        .as[JsObject]

                    val updated = json.deepMerge(knownfactsJson(knownFacts))

                    Ok(updated)
                }

              case None =>
                InternalServerError
            }
          }
        )
    }

  def getSubcontractorPrepopByKnownFacts: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[PrepopKnownFacts]
        .fold(
          errs => badJson(errs),
          knownFacts => {
            val ctx = context(knownFacts)

            val enrolments = enrolmentHelper
              .contractorEnrolmentsOpt(request)
              .orElse(enrolmentHelper.agentEnrolmentsOpt(request))

            enrolments match {
              case Some(_) =>
                (knownFacts.taxOfficeNumber, knownFacts.taxOfficeReference, knownFacts.agentOwnReference) match {
                  case ("404", _, _) =>
                    NotFound(
                      Json.obj(
                        "message" -> s"No CIS subcontractor pre-pop data found for $ctx"
                      )
                    )

                  case ("500", _, _) =>
                    InternalServerError(Json.obj("message" -> "Unexpected error"))

                  case _ =>
                    val json =
                      Json
                        .parse(
                          resourceHelper.resourceAsString(getSubconPrepopByKnownFacts_200_ResponsePath)
                        )
                        .as[JsObject]

                    val updated = json.deepMerge(knownfactsJson(knownFacts))

                    Ok(updated)
                }

              case None =>
                InternalServerError
            }
          }
        )
    }
}
