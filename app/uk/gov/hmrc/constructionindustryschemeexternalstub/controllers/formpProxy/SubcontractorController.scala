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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.CreateAndUpdateSubcontractorRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.response.GetSubcontractorForDeleteResponse
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.response.GetSubcontractorResponse

import javax.inject.Inject

class SubcontractorController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val subcontractorResponsePath             = "/resources/subcontractor"
  private val getSubcontractorList_200_ResponsePath =
    s"$subcontractorResponsePath/getSubcontractorList-200-response.json"
  private val getSubcontractor_200_ResponsePath     =
    s"$subcontractorResponsePath/getSubcontractor-200-response.json"

  def createAndUpdateSubcontractor(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[CreateAndUpdateSubcontractorRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          body => NoContent
        )
    }

  def getSubcontractorList(cisId: String): Action[AnyContent] =
    authorise { implicit request =>
      val contractorRefOpt: Option[EmployerReference] = enrolmentHelper.contractorEnrolmentsOpt(request)
      val agentRefOpt: Option[String]                 = enrolmentHelper.agentEnrolmentsOpt(request)

      (contractorRefOpt, agentRefOpt) match {

        case (Some(employerRef), _) =>
          (employerRef.taxOfficeNumber, employerRef.taxOfficeReference) match {
            case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
            case _          => Ok(resourceHelper.resourceAsString(getSubcontractorList_200_ResponsePath))
          }

        case (None, Some(agentRef)) =>
          Ok(resourceHelper.resourceAsString(getSubcontractorList_200_ResponsePath))

        case (None, None) =>
          logger.warn("[SubcontractorController][getSubcontractorList] Missing contractor and agent enrolments")
          InternalServerError(Json.obj("message" -> "Missing enrolments"))
      }

    }

  def getSubcontractorForDelete(
    cisId: String,
    subbieResourceRef: Long
  ): Action[AnyContent] =
    authorise { implicit request =>

      val canDelete =
        subbieResourceRef != 27L

      val subcontractorName =
        if (subbieResourceRef == 27L) {
          "Gamma Builders"
        } else {
          "Test Subcontractor"
        }

      Ok(
        Json.toJson(
          GetSubcontractorForDeleteResponse(
            subcontractorName = subcontractorName,
            subcontractorCanBeDeleted = canDelete
          )
        )
      )
    }

  def getSubcontractor(
    cisId: String,
    subbieResourceRef: Long
  ): Action[AnyContent] =
    authorise { implicit request =>
      val contractorRefOpt: Option[EmployerReference] =
        enrolmentHelper.contractorEnrolmentsOpt(request)

      val agentRefOpt: Option[String] =
        enrolmentHelper.agentEnrolmentsOpt(request)

      (contractorRefOpt, agentRefOpt) match {

        case (Some(employerRef), _) =>
          (employerRef.taxOfficeNumber, employerRef.taxOfficeReference) match {
            case ("500", _) =>
              InternalServerError(Json.obj("message" -> "Unexpected error"))

            case ("502", _) =>
              BadGateway(Json.obj("message" -> "formp failed"))

            case _ =>
              Ok(
                Json.parse(
                  resourceHelper.resourceAsString(getSubcontractor_200_ResponsePath)
                )
              )
          }

        case (None, Some(_)) =>
          Ok(
            Json.parse(
              resourceHelper.resourceAsString(getSubcontractor_200_ResponsePath)
            )
          )

        case (None, None) =>
          logger.warn("[SubcontractorController][getSubcontractor] Missing contractor and agent enrolments")
          InternalServerError(Json.obj("message" -> "Missing enrolments"))
      }
    }

}
