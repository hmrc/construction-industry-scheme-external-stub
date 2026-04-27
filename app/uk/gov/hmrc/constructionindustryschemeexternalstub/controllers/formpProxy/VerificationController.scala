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
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.CreateVerificationBatchAndVerificationsRequest

import javax.inject.Inject

class VerificationController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val verificationResponsePath                                                     = "/resources/verification"
  private val getNewestVerificationBatch_200_ResponsePath                                  =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response.json"
  private val getCurrentVerificationBatch_200_verificationBatchStatus_none_ResponsePath    =
    s"$verificationResponsePath/getCurrentVerificationBatch-200-verificationBatchStatus-none-response.json"
  private val getCurrentVerificationBatch_200_verificationBatchStatus_started_ResponsePath =
    s"$verificationResponsePath/getCurrentVerificationBatch-200-verificationBatchStatus-started-response.json"
  private val createVerificationBatchAndVerifications_201_ResponsePath                     =
    s"$verificationResponsePath/createVerificationBatchAndVerifications-201-response.json"

  def getNewestVerificationBatch(instanceId: String): Action[AnyContent] =
    authorise { implicit request =>
      enrolmentHelper.contractorEnrolmentsOpt(request) match {
        case Some(enrolmentReference) =>
          (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
            case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
            case _          =>
              Ok(Json.parse(resourceHelper.resourceAsString(getNewestVerificationBatch_200_ResponsePath)))
          }
        case None                     =>
          enrolmentHelper.agentEnrolmentsOpt(request) match {
            case Some(_) => Ok(Json.parse(resourceHelper.resourceAsString(getNewestVerificationBatch_200_ResponsePath)))
            case None    => InternalServerError
          }
      }
    }

  def getCurrentVerificationBatch(instanceId: String): Action[AnyContent] =
    authorise { implicit request =>
      enrolmentHelper.contractorEnrolmentsOpt(request) match {
        case Some(enrolmentReference) =>
          (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
            case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
            case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
            case _          =>
              if (instanceId == "1") {
                Ok(
                  Json.parse(
                    resourceHelper.resourceAsString(
                      getCurrentVerificationBatch_200_verificationBatchStatus_none_ResponsePath
                    )
                  )
                )
              } else {
                Ok(
                  Json.parse(
                    resourceHelper.resourceAsString(
                      getCurrentVerificationBatch_200_verificationBatchStatus_started_ResponsePath
                    )
                  )
                )
              }
          }
        case None                     =>
          enrolmentHelper.agentEnrolmentsOpt(request) match {
            case Some(_) =>
              if (instanceId == "1") {
                Ok(
                  Json.parse(
                    resourceHelper.resourceAsString(
                      getCurrentVerificationBatch_200_verificationBatchStatus_none_ResponsePath
                    )
                  )
                )
              } else {
                Ok(
                  Json.parse(
                    resourceHelper.resourceAsString(
                      getCurrentVerificationBatch_200_verificationBatchStatus_started_ResponsePath
                    )
                  )
                )
              }
            case None    => InternalServerError
          }
      }
    }

  def createVerificationBatchAndVerifications(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[CreateVerificationBatchAndVerificationsRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ =>
            enrolmentHelper.contractorEnrolmentsOpt(request) match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
                  case ("500", _) => InternalServerError(Json.obj("message" -> "Unexpected error"))
                  case ("502", _) => BadGateway(Json.obj("message" -> "formp failed"))
                  case _          =>
                    Created(
                      Json.parse(
                        resourceHelper.resourceAsString(createVerificationBatchAndVerifications_201_ResponsePath)
                      )
                    )
                }
              case None                     =>
                enrolmentHelper.agentEnrolmentsOpt(request) match {
                  case Some(_) =>
                    Created(
                      Json.parse(
                        resourceHelper.resourceAsString(createVerificationBatchAndVerifications_201_ResponsePath)
                      )
                    )
                  case None    =>
                    InternalServerError
                }
            }
        )
    }
}
