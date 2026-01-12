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
import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.AuthenticatedRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{CreateContractorSchemeParams, EmployerReference, UpdateContractorSchemeParams}
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
  private val getScheme_200_org_ResponsePath      = s"$basePath/getScheme-200-org-response.json"
  private val getScheme_200_agent_ResponsePath    = s"$basePath/getScheme-200-agent-response.json"
  private val getScheme_noNameNoUtr_sub1_Response = s"$basePath/getScheme-200-no-name-no-utr-response.json"
  private val getScheme_nameOnly_Response         = s"$basePath/getScheme-200-name-only-response.json"
  private val getScheme_utrOnly_Response          = s"$basePath/getScheme-200-utr-only-response.json"
  private val getScheme_firstTime_Response        = s"$basePath/getScheme-200-first-time-response.json"
  private val createScheme_201_ResponsePath       = s"$basePath/createScheme-201-response.json"
  private val updateScheme_200_ResponsePath       = s"$basePath/updateScheme-200-response.json"

  def getScheme(instanceId: String): Action[AnyContent] =
    authorise { implicit request =>
      val contractorRefOpt: Option[EmployerReference] = enrolmentHelper.contractorEnrolmentsOpt(request)
      val agentRefOpt: Option[String]                 = enrolmentHelper.agentEnrolmentsOpt(request)

      (contractorRefOpt, agentRefOpt) match {

        case (Some(employerRef), _) =>
          contractorSchemeResult(
            taxOfficeNumber = employerRef.taxOfficeNumber,
            taxOfficeReference = employerRef.taxOfficeReference
          )

        case (None, Some(agentRef)) =>
          agentSchemeResult(agentRef)

        case (None, None) =>
          logger.warn("[ContractorSchemeController][getScheme] Missing contractor and agent enrolments")
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
          _ =>
            if (hasAnyEnrolments) {
              Created(resourceHelper.resourceAsString(createScheme_201_ResponsePath))
            } else {
              logger.warn("[ContractorSchemeController][createScheme] Missing contractor enrolments")
              InternalServerError(Json.obj("message" -> "Missing enrolments"))
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
          _ =>
            if (hasAnyEnrolments) {
              Ok(resourceHelper.resourceAsString(updateScheme_200_ResponsePath))
            } else {
              logger.warn("[ContractorSchemeController][updateScheme] Missing contractor enrolments")
              InternalServerError(Json.obj("message" -> "Missing enrolments"))
            }
        )
    }

  private def hasAnyEnrolments(implicit request: AuthenticatedRequest[_]): Boolean =
    enrolmentHelper.contractorEnrolmentsOpt(request).isDefined ||
      enrolmentHelper.agentEnrolmentsOpt(request).isDefined

  private def contractorSchemeResult(
    taxOfficeNumber: String,
    taxOfficeReference: String
  ): Result =
    taxOfficeNumber match {
      // 1) no utr & no name, but subcontractorCounter = 1, prepopSuccessful = "N"
      case "201" =>
        Ok(schemeJson(getScheme_noNameNoUtr_sub1_Response, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // 2) no utr but there is a name, prepopSuccessful = "N"
      case "202" =>
        Ok(schemeJson(getScheme_nameOnly_Response, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // 3) there is an utr but no name, prepopSuccessful = "N"
      case "203" =>
        Ok(schemeJson(getScheme_utrOnly_Response, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // 4) no utr, no name, subcontractorCounter = 0 (first-time user), prepopSuccessful = "N"
      case "204" =>
        Ok(schemeJson(getScheme_firstTime_Response, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // 5) Not found scheme, expected to call createScheme
      case "205" =>
        NotFound(Json.obj("message" -> "Scheme not found"))

      // default happy path with prepopSuccessful = "Y", expected to pass F1 check without update / create scheme
      case _     =>
        Ok(schemeJson(getScheme_200_org_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))
    }

  private def agentSchemeResult(agentRef: String): Result =
    agentRef match {
      // 1) no utr & no name, but subcontractorCounter = 1, prepopSuccessful = "N"
      case "AGT201" =>
        Ok(schemeJson(getScheme_noNameNoUtr_sub1_Response))

      // 2) no utr but there is a name, prepopSuccessful = "N"
      case "AGT202" =>
        Ok(schemeJson(getScheme_nameOnly_Response))

      // 3) there is an utr but no name, prepopSuccessful = "N"
      case "AGT203" =>
        Ok(schemeJson(getScheme_utrOnly_Response))

      // 4) no utr, no name, subcontractorCounter = 0 (first-time user), prepopSuccessful = "N"
      case "AGT204" =>
        Ok(schemeJson(getScheme_firstTime_Response))

      // 5) Not found scheme, expected to call createScheme endpoint
      case "AGT205" =>
        NotFound(Json.obj("message" -> "Scheme not found"))

      // default happy path with prepopSuccessful = "Y", expected to pass F1 check without update / create scheme
      case _        =>
        Ok(schemeJson(getScheme_200_agent_ResponsePath))
    }

  private def schemeJson(
    path: String,
    taxOfficeNumberOpt: Option[String] = None,
    taxOfficeReferenceOpt: Option[String] = None
  ): JsValue = {
    val base: JsObject = Json.parse(resourceHelper.resourceAsString(path)).as[JsObject]

    (taxOfficeNumberOpt, taxOfficeReferenceOpt) match {
      case (Some(taxOfficeNumber), Some(taxOfficeReference)) =>
        base.deepMerge(
          Json.obj(
            "taxOfficeNumber"    -> taxOfficeNumber,
            "taxOfficeReference" -> taxOfficeReference
          )
        )
      case _                                                 =>
        base
    }
  }
}
