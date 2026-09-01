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
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.{ApplyPrepopulationRequest, UpdateSchemeVersionRequest}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{CreateContractorSchemeParams, EmployerReference, UpdateContractorSchemeParams}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.JsResultUtils.foldErrorsIntoBadRequest

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import scala.concurrent.Future
import scala.collection.concurrent.TrieMap

class ContractorSchemeController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val basePath                              = "/resources/contractorSchemes"
  private val getScheme_200_no_sub_ResponsePath     = s"$basePath/getScheme-200-no-sub-response.json"
  private val getScheme_sub1_ResponsePath           = s"$basePath/getScheme-200-sub1-response.json"
  private val getScheme_sub1_rest_no_ResponsePath   = s"$basePath/getScheme-200-sub1-rest-no-response.json"
  private val getScheme_nameOnly_ResponsePath       = s"$basePath/getScheme-200-name-only-response.json"
  private val getScheme_utrOnly_ResponsePath        = s"$basePath/getScheme-200-utr-only-response.json"
  private val getScheme_prepop_no_Only_ResponsePath = s"$basePath/getScheme-200-flag-no-only-response.json"
  private val getScheme_firstTime_ResponsePath      = s"$basePath/getScheme-200-first-time-response.json"
  private val createScheme_201_ResponsePath         = s"$basePath/createScheme-201-response.json"
  private val updateScheme_200_ResponsePath         = s"$basePath/updateScheme-200-response.json"

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
          Ok(schemeJson(getScheme_sub1_ResponsePath))
      }
    }

  def createScheme: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[CreateContractorSchemeParams]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(
            Created(resourceHelper.resourceAsString(createScheme_201_ResponsePath))
          )
        }
    }

  def updateScheme: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[UpdateContractorSchemeParams]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(
            Ok(resourceHelper.resourceAsString(updateScheme_200_ResponsePath))
          )
        }
    }

  def updateSchemeVersion: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[UpdateSchemeVersionRequest]
        .foldErrorsIntoBadRequest { payload =>
          Future.successful(
            Ok(Json.obj("version" -> (payload.version + 1)))
          )
        }
    }

  def applyPrepopulation: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[ApplyPrepopulationRequest]
        .foldErrorsIntoBadRequest { payload =>
          Future.successful(
            Ok(Json.obj("version" -> (payload.version + 1)))
          )
        }
    }

  private def contractorSchemeResult(taxOfficeNumber: String, taxOfficeReference: String): Result = {

    val key = s"$taxOfficeNumber|$taxOfficeReference"

    taxOfficeReference match {
      // multiple call scenarios, only for local & Jenkins pipelines runs, DO NOT USE IN STAGING

      // cis-ui-tests PrepopulationSpec Scenario 1
      case "EZ10350"             =>
        val callNumber = nextCallAndResetAfterFour(key)
        logger.info(s"[getScheme] ref=$taxOfficeReference callNumber=$callNumber")
        if (callNumber <= 4) {
          Ok(schemeJson(getScheme_firstTime_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))
        } else {
          Ok(schemeJson(getScheme_200_no_sub_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))
        }

      // cis-ui-tests PrepopulationSpec Scenario 2 & 9
      case "EZ10400" | "EZ10750" =>
        val callNumber = nextCallAndResetAfterFour(key)
        logger.info(s"[getScheme] ref=$taxOfficeReference callNumber=$callNumber")
        if (callNumber <= 4) {
          Ok(schemeJson(getScheme_firstTime_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))
        } else {
          Ok(schemeJson(getScheme_sub1_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))
        }

      // 1 call scenarios

      // cis-ui-tests PrepopulationSpec Scenario 3
      case "EZ10450"             =>
        Ok(schemeJson(getScheme_firstTime_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // cis-ui-tests PrepopulationSpec Scenario 4
      case "EZ10500"             =>
        Ok(schemeJson(getScheme_sub1_rest_no_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // cis-ui-tests PrepopulationSpec Scenario 5
      case "EZ10550"             =>
        Ok(schemeJson(getScheme_utrOnly_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // cis-ui-tests PrepopulationSpec Scenario 6
      case "EZ10600"             =>
        Ok(schemeJson(getScheme_prepop_no_Only_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // cis-ui-tests PrepopulationSpec Scenario 7
      case "EZ10650"             =>
        Ok(schemeJson(getScheme_nameOnly_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // cis-ui-tests PrepopulationSpec Scenario 8
      case "EZ10700"             =>
        Ok(schemeJson(getScheme_sub1_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))

      // default happy path with successfully prepoped scheme and subcontractorCounter = 1
      case _                     =>
        Ok(schemeJson(getScheme_sub1_ResponsePath, Some(taxOfficeNumber), Some(taxOfficeReference)))
    }
  }

  private def agentSchemeResult(agentRef: String): Result =
    agentRef match {
      // 0) no utr, no name, prepopSuccessful = "N", subcontractorCounter = 1
      case "AGT201"   =>
        Ok(schemeJson(getScheme_sub1_rest_no_ResponsePath))

      // 1) no utr but there is a name, prepopSuccessful = "N"
      case "AGT202"   =>
        Ok(schemeJson(getScheme_nameOnly_ResponsePath))

      // 2) there is an utr but no name, prepopSuccessful = "N"
      case "AGT203"   =>
        Ok(schemeJson(getScheme_utrOnly_ResponsePath))

      // 3) no utr, no name, prepopSuccessful = "N", subcontractorCounter = 0
      case "AGT204"   =>
        Ok(schemeJson(getScheme_firstTime_ResponsePath))

      // 4) Not found scheme, expected to call createScheme endpoint
      case "AGT205"   =>
        NotFound(Json.obj("message" -> "Scheme not found"))

      // 5) successful prepoped scheme table, subcontractorCounter = 1
      case "AGT206"   =>
        Ok(schemeJson(getScheme_sub1_ResponsePath))

      // 6) successful prepoped scheme table, subcontractorCounter = 0
      case "AGT207"   =>
        Ok(schemeJson(getScheme_200_no_sub_ResponsePath))

      // 7) name & utr exists, prepopSuccessful = "N", subcontractorCounter = 1
      case ("AGT208") =>
        Ok(schemeJson(getScheme_prepop_no_Only_ResponsePath))

      // default happy path with successfully prepoped scheme and subcontractorCounter = 1
      case _          =>
        Ok(schemeJson(getScheme_sub1_ResponsePath))
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

  private val schemeCounters = TrieMap.empty[String, AtomicInteger]

  private def nextCallAndResetAfterFour(key: String): Int = {
    val counter    = schemeCounters.getOrElseUpdate(key, new AtomicInteger(0))
    val callNumber = counter.incrementAndGet()
    if (callNumber >= 6) {
      schemeCounters.remove(key)
    }
    callNumber
  }
}
