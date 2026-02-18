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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.JsResultUtils.foldErrorsIntoBadRequest

import javax.inject.Inject
import scala.concurrent.Future

class MonthlyReturnController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val monthlyNilReturnResponsePath                       = "/resources/monthlyNilReturns"
  private val retrieveMonthlyReturns_200_ResponsePath            =
    s"$monthlyNilReturnResponsePath/retrieveMonthlyReturns-200-response.json"
  private val retrieveMonthlyReturns_empty_200_ResponsePath      =
    s"$monthlyNilReturnResponsePath/retrieveMonthlyReturns-empty-200-response.json"
  private val createNilMonthlyReturn_200_ResponsePath            =
    s"$monthlyNilReturnResponsePath/createNilMonthlyReturn-200-response.json"
  private val getSchemeEmail_200_ResponsePath                    = s"$monthlyNilReturnResponsePath/getSchemeEmail-200-response.json"
  private val getSchemeEmail_null_200_ResponsePath               =
    s"$monthlyNilReturnResponsePath/getSchemeEmail-null-200-response.json"
  private val retrieveUnsubmittedMonthlyReturns_200_ResponsePath =
    "/resources/retrieveUnsubmittedMonthlyReturns-200-response.json"
  private val getMonthlyReturnForEdit_200_ResponsePath           =
    "/resources/getMonthlyReturnForEdit-200-response.json"

  def retrieveMonthlyReturns: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[InstanceIdRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful {
            enrolmentHelper.contractorEnrolmentsOpt(request) match {
              // Org journey
              case Some(orgEmployerRef) =>
                (orgEmployerRef.taxOfficeNumber, orgEmployerRef.taxOfficeReference) match {
                  //                  case ("404", _) => NotFound(Json.obj("message" -> "CIS taxpayer not found"))
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
                  case ("000", _) => Ok(resourceHelper.resourceAsString(retrieveMonthlyReturns_empty_200_ResponsePath))
                  case _          => Ok(resourceHelper.resourceAsString(retrieveMonthlyReturns_200_ResponsePath))
                }

              // Agent journey
              case None                 =>
                enrolmentHelper.agentEnrolmentsOpt(request) match {
                  case Some(agentRef) =>
                    agentRef match {
                      case "AGT500" => InternalServerError(Json.obj("message" -> "Unexpected error"))
                      case "AGT502" => BadGateway(Json.obj("message" -> "formp failed"))
                      case "AGT000" =>
                        Ok(resourceHelper.resourceAsString(retrieveMonthlyReturns_empty_200_ResponsePath))
                      case _        => Ok(resourceHelper.resourceAsString(retrieveMonthlyReturns_200_ResponsePath))
                    }

                  case None => InternalServerError(Json.obj("message" -> "Missing enrolment"))
                }
            }
          }
        }
    }

  def createNilMonthlyReturn: Action[CreateNilMonthlyReturnRequest] =
    authorise(parse.json[CreateNilMonthlyReturnRequest]) { implicit request =>
      val enrolments = enrolmentHelper.contractorEnrolmentsOpt(request)
      enrolments match {
        case Some(enrolmentReference) =>
          (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
//            case ("400", _) => BadRequest(Json.obj("message" -> "Missing CIS enrolment identifiers"))
//            case ("404", _) => NotFound(Json.obj("message" -> "CIS taxpayer not found"))
            case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
            case _          => Created(resourceHelper.resourceAsString(createNilMonthlyReturn_200_ResponsePath))
          }
        case None                     => InternalServerError
      }
    }

  def updateMonthlyReturnItem(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[UpdateMonthlyReturnItemRequest]
        .fold(
          errs =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errs)
              )
            ),
          _ => NoContent
        )
    }

  def createMonthlyReturn: Action[CreateMonthlyReturnRequest] =
    authorise.async(parse.json[CreateMonthlyReturnRequest]) { _ =>
      Future.successful(Created)
    }

  def updateNilMonthlyReturn: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[UpdateNilMonthlyReturnRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(NoContent)
        }
    }

  def getSchemeEmail: Action[InstanceIdRequest] =
    authorise(parse.json[InstanceIdRequest]) { implicit request =>
      val enrolments = enrolmentHelper.contractorEnrolmentsOpt(request)
      enrolments match {
        case Some(enrolmentReference) =>
          (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
//            case ("400", _) => BadRequest(Json.obj("message" -> "Missing CIS enrolment identifiers"))
//            case ("404", _) => NotFound(Json.obj("message" -> "CIS taxpayer not found"))
            case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
            case ("000", _) => Ok(resourceHelper.resourceAsString(getSchemeEmail_null_200_ResponsePath))
            case _          => Ok(resourceHelper.resourceAsString(getSchemeEmail_200_ResponsePath))
          }
        case None                     => InternalServerError
      }
    }

  def retrieveUnsubmittedMonthlyReturns: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[InstanceIdRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(Ok(resourceHelper.resourceAsString(retrieveUnsubmittedMonthlyReturns_200_ResponsePath)))
        }
    }

  def getMonthlyReturnForEdit: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[GetMonthlyReturnForEditRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(Ok(resourceHelper.resourceAsString(getMonthlyReturnForEdit_200_ResponsePath)))
        }
    }

  def syncMonthlyReturnItems: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[SyncMonthlyReturnItemsRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(NoContent)
        }
    }
}
