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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{CreateContractorSchemeParams, UpdateContractorSchemeParams}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class ContractorSchemeController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val basePath                            = "/resources/contractorSchemes"
  private val getScheme_200_ResponsePath          = s"$basePath/getScheme-200-response.json"
  private val getScheme_noNameNoUtr_sub1_Response = s"$basePath/getScheme-200-no-name-no-utr-response.json"
  private val getScheme_nameOnly_Response         = s"$basePath/getScheme-200-name-only-response.json"
  private val getScheme_utrOnly_Response          = s"$basePath/getScheme-200-utr-only-response.json"
  private val getScheme_firstTime_Response        = s"$basePath/getScheme-200-first-time-response.json"
  private val createScheme_201_ResponsePath       = s"$basePath/createScheme-201-response.json"
  private val updateScheme_200_ResponsePath       = s"$basePath/updateScheme-200-response.json"

  def getScheme(instanceId: String): Action[AnyContent] =
    authorise { implicit request =>
      val enrolmentsOpt = enrolmentHelper.contractorEnrolmentsOpt(request)

      enrolmentsOpt match {
        case Some(enrolRef) =>
          val ton = enrolRef.taxOfficeNumber
          val tor = enrolRef.taxOfficeReference

          (ton, tor) match {
            case ("400", _) => BadRequest(Json.obj("message" -> "Bad request"))
            case ("404", _) => NotFound(Json.obj("message" -> "Scheme not found"))
            case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))

            // 1) no utr & no name, but subcontractorCounter = 1, prepopSucessful = "N"
            case ("201", _) => Ok(resourceHelper.resourceAsString(getScheme_noNameNoUtr_sub1_Response))

            // 2) no utr but there is a name, prepopSucessful = "N"
            case ("202", _) => Ok(resourceHelper.resourceAsString(getScheme_nameOnly_Response))

            // 3) there is an utr but no name, prepopSucessful = "N"
            case ("203", _) => Ok(resourceHelper.resourceAsString(getScheme_utrOnly_Response))

            // 4) no utr, no name, subcontractorCounter = 0 (first-time user), prepopSucessful = "N"
            case ("204", _) => Ok(resourceHelper.resourceAsString(getScheme_firstTime_Response))

            // default happy path with prepopSucessful = "Y", name & utr existing, subcontractorCounter = 1
            case _ => Ok(resourceHelper.resourceAsString(getScheme_200_ResponsePath))
          }

        case None =>
          logger.warn("[ContractorSchemeController][getScheme] Missing contractor enrolments")
          InternalServerError(Json.obj("message" -> "Missing enrolments"))
      }
    }

  def createScheme: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[CreateContractorSchemeParams]
        .fold(
          errs =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid JSON body",
                "errors"  -> JsError.toJson(errs)
              )
            ),
          _ => {
            val enrolmentsOpt = enrolmentHelper.contractorEnrolmentsOpt(request)

            enrolmentsOpt match {
              case Some(enrolRef) =>
                (enrolRef.taxOfficeNumber, enrolRef.taxOfficeReference) match {
                  case ("400", _) => BadRequest(Json.obj("message" -> "Bad request"))
                  case ("404", _) => NotFound(Json.obj("message" -> "Scheme not found"))
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
                  case _          => Created(resourceHelper.resourceAsString(createScheme_201_ResponsePath))
                }

              case None =>
                logger.warn("[ContractorSchemeController][createScheme] Missing contractor enrolments")
                InternalServerError(Json.obj("message" -> "Missing enrolments"))
            }
          }
        )
    }

  def updateScheme: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[UpdateContractorSchemeParams]
        .fold(
          errs =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid JSON body",
                "errors"  -> JsError.toJson(errs)
              )
            ),
          _ => {
            val enrolmentsOpt = enrolmentHelper.contractorEnrolmentsOpt(request)

            enrolmentsOpt match {
              case Some(enrolRef) =>
                (enrolRef.taxOfficeNumber, enrolRef.taxOfficeReference) match {
                  case ("400", _) => BadRequest(Json.obj("message" -> "Bad request"))
                  case ("404", _) => NotFound(Json.obj("message" -> "Scheme not found"))
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
                  case _          => Ok(resourceHelper.resourceAsString(updateScheme_200_ResponsePath))
                }

              case None =>
                logger.warn("[ContractorSchemeController][updateScheme] Missing contractor enrolments")
                InternalServerError(Json.obj("message" -> "Missing enrolments"))
            }
          }
        )
    }
}
