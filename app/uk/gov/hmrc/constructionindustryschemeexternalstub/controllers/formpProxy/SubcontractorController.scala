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
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests._
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.response._
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.Future

class SubcontractorController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val subcontractorResponsePath                              = "/resources/subcontractor"
  private val getSubcontractorList_200_ResponsePath                  =
    s"$subcontractorResponsePath/getSubcontractorList-200-response.json"
  private val getSubcontractorList_noSubcontractor_200_ResponsePath  =
    s"$subcontractorResponsePath/getSubcontractorList-200-noSubcontractor-response.json"
  private val getSubcontractorIndividual_200_ResponsePath            =
    s"$subcontractorResponsePath/getSubcontractorIndividual-200-verifiedResponse.json"
  private val getSubcontractorTrust_200_ResponsePath                 =
    s"$subcontractorResponsePath/getSubcontractorTrust-200-verifiedResponse.json"
  private val getSubcontractorCompany_200_ResponsePath               =
    s"$subcontractorResponsePath/getSubcontractorCompany-200-verifiedResponse.json"
  private val getSubcontractorPartnership_200_ResponsePath           =
    s"$subcontractorResponsePath/getSubcontractorPartnership-200-verifiedResponse.json"
  private val getSubcontractorIndividual_200_UnverifiedResponsePath  =
    s"$subcontractorResponsePath/getSubcontractorIndividual-200-unverifiedResponse.json"
  private val getSubcontractorTrust_200_UnverifiedResponsePath       =
    s"$subcontractorResponsePath/getSubcontractorTrust-200-unverifiedResponse.json"
  private val getSubcontractorCompany_200_UnverifiedResponsePath     =
    s"$subcontractorResponsePath/getSubcontractorCompany-200-unverifiedResponse.json"
  private val getSubcontractorPartnership_200_UnverifiedResponsePath =
    s"$subcontractorResponsePath/getSubcontractorPartnership-200-unverifiedResponse.json"

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
            case ("500", _)     => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case ("502", _)     => BadGateway(Json.obj("message" -> "formp failed"))
            case (_, "EZ00225") =>
              Ok(resourceHelper.resourceAsString(getSubcontractorList_noSubcontractor_200_ResponsePath))
            case _              => Ok(resourceHelper.resourceAsString(getSubcontractorList_200_ResponsePath))
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
        subbieResourceRef != 7L

      val subcontractorName =
        if (subbieResourceRef == 7L) {
          "Delta Trust"
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

      val contractorRefOpt = enrolmentHelper.contractorEnrolmentsOpt(request)
      val agentRefOpt      = enrolmentHelper.agentEnrolmentsOpt(request)

      val responsePath = subbieResourceRef match {
        case 1 => getSubcontractorIndividual_200_ResponsePath
        case 2 => getSubcontractorIndividual_200_UnverifiedResponsePath
        case 3 => getSubcontractorCompany_200_ResponsePath
        case 4 => getSubcontractorCompany_200_UnverifiedResponsePath
        case 5 => getSubcontractorPartnership_200_ResponsePath
        case 6 => getSubcontractorPartnership_200_UnverifiedResponsePath
        case 7 => getSubcontractorTrust_200_ResponsePath
        case 8 => getSubcontractorTrust_200_UnverifiedResponsePath
        case _ => getSubcontractorIndividual_200_ResponsePath
      }

      (contractorRefOpt, agentRefOpt) match {
        case (Some(employerRef), _) =>
          employerRef.taxOfficeNumber match {
            case "500" =>
              InternalServerError(Json.obj("message" -> "Unexpected error"))

            case "502" =>
              BadGateway(Json.obj("message" -> "formp failed"))

            case _ =>
              Ok(Json.parse(resourceHelper.resourceAsString(responsePath)))
          }

        case (None, Some(_)) =>
          Ok(Json.parse(resourceHelper.resourceAsString(responsePath)))

        case _ =>
          logger.warn("[SubcontractorController][getSubcontractor] Missing contractor and agent enrolments")
          InternalServerError(Json.obj("message" -> "Missing enrolments"))
      }
    }

  def deleteSubcontractor: Action[DeleteSubcontractorRequest] =
    authorise.async(parse.json[DeleteSubcontractorRequest]) { _ =>
      Future.successful(NoContent)
    }

  def updateSubcontractor: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[UpdateSubcontractorRequest]
        .fold(
          errs =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errs)
              )
            ),
          body => {
            val contractorRefOpt: Option[EmployerReference] =
              enrolmentHelper.contractorEnrolmentsOpt(request)

            val agentRefOpt: Option[String] =
              enrolmentHelper.agentEnrolmentsOpt(request)

            (contractorRefOpt, agentRefOpt) match {

              case (Some(employerRef), _) =>
                employerRef.taxOfficeNumber match {
                  case "500" =>
                    InternalServerError(Json.obj("message" -> "Unexpected error"))

                  case "502" =>
                    BadGateway(Json.obj("message" -> "formp failed"))

                  case _ =>
                    Ok(
                      Json.toJson(
                        UpdateSubcontractorResponse(
                          version = body.subcontractor.version.getOrElse(0) + 1
                        )
                      )
                    )
                }

              case (None, Some(_)) =>
                Ok(
                  Json.toJson(
                    UpdateSubcontractorResponse(
                      version = body.subcontractor.version.getOrElse(0) + 1
                    )
                  )
                )

              case (None, None) =>
                logger.warn("[SubcontractorController][updateSubcontractor] Missing contractor and agent enrolments")
                InternalServerError(Json.obj("message" -> "Missing enrolments"))
            }
          }
        )
    }

  def updateSubcontractorForEdit: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[UpdateSubcontractorRequest]
        .fold(
          errs =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errs)
              )
            ),
          body => {

            val contractorRefOpt: Option[EmployerReference] =
              enrolmentHelper.contractorEnrolmentsOpt(request)

            val agentRefOpt: Option[String] =
              enrolmentHelper.agentEnrolmentsOpt(request)

            (contractorRefOpt, agentRefOpt) match {

              case (Some(employerRef), _) =>
                employerRef.taxOfficeNumber match {

                  case "500" =>
                    InternalServerError(
                      Json.obj(
                        "message" -> "Unexpected error"
                      )
                    )

                  case "502" =>
                    BadGateway(
                      Json.obj(
                        "message" -> "formp failed"
                      )
                    )

                  case _ =>
                    Ok(
                      Json.toJson(
                        UpdateSubcontractorResponse(
                          version = body.subcontractor.version.getOrElse(0) + 1
                        )
                      )
                    )
                }

              case (None, Some(_)) =>
                Ok(
                  Json.toJson(
                    UpdateSubcontractorResponse(
                      version = body.subcontractor.version.getOrElse(0) + 1
                    )
                  )
                )

              case (None, None) =>
                logger.warn(
                  "[SubcontractorController][updateSubcontractorForEdit] Missing contractor and agent enrolments"
                )

                InternalServerError(
                  Json.obj(
                    "message" -> "Missing enrolments"
                  )
                )
            }
          }
        )
    }
}
