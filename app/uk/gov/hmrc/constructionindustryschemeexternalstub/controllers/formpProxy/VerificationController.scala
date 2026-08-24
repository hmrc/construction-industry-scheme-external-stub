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
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{CreateVerifications, DeleteVerifications}

import javax.inject.Inject

class VerificationController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  private val verificationResponsePath                                                                = "/resources/verification"
  private val getNewestVerificationBatch_200_ResponsePath                                             =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response.json"
  private val getNewestVerificationBatch_200_Inactive_ResponsePath                                    =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-inactive.json"
  private val getNewestVerificationBatch_200_ReverifyOnly_ResponsePath                                =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-no-newly-added.json"
  private val getNewestVerificationBatch_200_VerifyOnly_ResponsePath                                  =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-no-reverify.json"
  private val getNewestVerificationBatch_200_Pending_ResponsePath                                     =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-verification-in-progress.json"
  private val getNewestVerificationBatch_200_no_Subcontractor_ResponsePath                            =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-no-subcontractor.json"
  private val getNewestVerificationBatch_200_Unmatched_ResponsePath                                   =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-unmatched.json"
  private val getNewestVerificationBatch_200_Insufficient_ResponsePath                                =
    s"$verificationResponsePath/getNewestVerificationBatch-200-response-insufficient.json"
  private val getCurrentVerificationBatch_200_verificationBatchStatus_none_ResponsePath               =
    s"$verificationResponsePath/getCurrentVerificationBatch-200-verificationBatchStatus-none-response.json"
  private val getCurrentVerificationBatch_200_verificationBatchStatus_started_ResponsePath            =
    s"$verificationResponsePath/getCurrentVerificationBatch-200-verificationBatchStatus-started-response.json"
  private val getCurrentVerificationBatch_200_verificationBatchStatus_chris_ResponsePath              =
    s"$verificationResponsePath/getCurrentVerificationBatch-200-verificationBatchStatus-chris-response.json"
  private val getCurrentVerificationBatch_200_verificationBatchStatus_Insufficient_chris_ResponsePath =
    s"$verificationResponsePath/getCurrentVerificationBatch-200-verificationBatchStatus-chris-response-insufficient.json"
  private val getLastSubmittedVerificationBatch_200_ResponsePath                                      =
    s"$verificationResponsePath/getLastSubmittedVerificationBatch-200-response.json"
  private val getLastSubmittedVerificationBatch_200_verificationBatchStatus_accepted_ResponsePath     =
    s"$verificationResponsePath/getLastSubmittedVerificationBatch-200-verificationBatchStatus-accepted-response.json"
  private val getLastSubmittedVerificationBatch_200_verificationBatchStatus_pending_ResponsePath      =
    s"$verificationResponsePath/getLastSubmittedVerificationBatch-200-verificationBatchStatus-pending-response.json"
  private val getLastSubmittedVerificationBatch_200_verificationBatchStatus_none_ResponsePath         =
    s"$verificationResponsePath/getLastSubmittedVerificationBatch-200-verificationBatchStatus-none-response.json"
  private val createVerificationBatchAndVerifications_201_ResponsePath                                =
    s"$verificationResponsePath/createVerificationBatchAndVerifications-201-response.json"
  private val createSubmissionForVerification_201_ResponsePath                                        =
    s"$verificationResponsePath/createSubmissionForVerification-201-response.json"
  private val getSubmissionWithVerificationBatch_200_ResponsePath                                     =
    s"$verificationResponsePath/getSubmissionWithVerificationBatch-200-response.json"
  private val getSubmittedVerifications_200_ResponsePath                                              =
    s"$verificationResponsePath/getSubmittedVerifications-200-response.json"
  private val getSubmittedVerifications_200_multiYear_ResponsePath                                    =
    s"$verificationResponsePath/getSubmittedVerifications-200-multiTaxYears-response.json"
  private val getSubmittedVerifications_200_noHistory_ResponsePath                                    =
    s"$verificationResponsePath/getSubmittedVerifications-200-noHistory-response.json"

  private def withEnrolmentDispatch(onSuccess: => Result)(implicit request: AuthenticatedRequest[_]): Result =
    enrolmentHelper.contractorEnrolmentsOpt(request) match {
      case Some(enrolmentReference) =>
        enrolmentReference.taxOfficeNumber match {
          case "500" => InternalServerError(Json.obj("message" -> "Unexpected error"))
          case "502" => BadGateway(Json.obj("message" -> "formp failed"))
          case _     => onSuccess
        }
      case None                     =>
        enrolmentHelper.agentEnrolmentsOpt(request) match {
          case Some(_) => onSuccess
          case None    => InternalServerError
        }
    }

  def getNewestVerificationBatch(instanceId: String): Action[AnyContent] =
    authorise { implicit request =>
      withEnrolmentDispatch {
        val responsePath =
          instanceId match {
            case "1"   => getNewestVerificationBatch_200_ResponsePath
            case "800" => getNewestVerificationBatch_200_ResponsePath
            case "125" => getNewestVerificationBatch_200_VerifyOnly_ResponsePath
            case "150" => getNewestVerificationBatch_200_ReverifyOnly_ResponsePath
            case "175" => getNewestVerificationBatch_200_Inactive_ResponsePath
            case "200" => getNewestVerificationBatch_200_Pending_ResponsePath
            case "225" => getNewestVerificationBatch_200_no_Subcontractor_ResponsePath
            case "250" => getNewestVerificationBatch_200_Unmatched_ResponsePath
            case "275" => getNewestVerificationBatch_200_Insufficient_ResponsePath
            case _     => getNewestVerificationBatch_200_ResponsePath
          }

        Ok(Json.parse(resourceHelper.resourceAsString(responsePath)))
      }
    }

  def getLastSubmittedVerificationBatch(instanceId: String): Action[AnyContent] =
    authorise { implicit request =>
      withEnrolmentDispatch {
        val responsePath =
          instanceId match {
            case "150" => getLastSubmittedVerificationBatch_200_verificationBatchStatus_pending_ResponsePath
            case "175" => getLastSubmittedVerificationBatch_200_verificationBatchStatus_accepted_ResponsePath
            case "225" => getLastSubmittedVerificationBatch_200_verificationBatchStatus_none_ResponsePath
            case _     => getLastSubmittedVerificationBatch_200_ResponsePath
          }

        Ok(Json.parse(resourceHelper.resourceAsString(responsePath)))
      }
    }

  def getCurrentVerificationBatch(instanceId: String): Action[AnyContent] =
    authorise { implicit request =>
      withEnrolmentDispatch {
        val responsePath =
          instanceId match {
            case "1"   => getCurrentVerificationBatch_200_verificationBatchStatus_started_ResponsePath
            case "800" => getCurrentVerificationBatch_200_verificationBatchStatus_chris_ResponsePath
            case "125" => getCurrentVerificationBatch_200_verificationBatchStatus_chris_ResponsePath
            case "150" => getCurrentVerificationBatch_200_verificationBatchStatus_chris_ResponsePath
            case "175" => getCurrentVerificationBatch_200_verificationBatchStatus_chris_ResponsePath
            case "275" => getCurrentVerificationBatch_200_verificationBatchStatus_Insufficient_chris_ResponsePath
            case _     => getCurrentVerificationBatch_200_verificationBatchStatus_none_ResponsePath
          }

        Ok(Json.parse(resourceHelper.resourceAsString(responsePath)))
      }
    }

  def createVerificationBatchAndVerifications(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[CreateVerificationBatchAndVerificationsRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ =>
            withEnrolmentDispatch(
              Created(
                Json.parse(resourceHelper.resourceAsString(createVerificationBatchAndVerifications_201_ResponsePath))
              )
            )
        )
    }

  def modifyVerifications(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[ModifyVerificationsRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          req => withEnrolmentDispatch(modifyVerificationsResult(req))
        )
    }

  private def modifyVerificationsResult(request: ModifyVerificationsRequest): Result =
    validateModifyVerificationsRequest(request) match {
      case Left(errorMessage) => InternalServerError(Json.obj("message" -> "Unexpected error"))

      case Right(validRequest) =>
        if (hasNoSubcontractorToModify(validRequest)) {
          InternalServerError(Json.obj("message" -> "Unexpected error"))
        } else {
          NoContent
        }
    }

  private def validateModifyVerificationsRequest(
    request: ModifyVerificationsRequest
  ): Either[String, ModifyVerificationsRequest] =
    request match {
      case ModifyVerificationsRequest(_, Some(DeleteVerifications(verificationResourceReferences)), _)
          if verificationResourceReferences.isEmpty =>
        Left("verificationResourceReferences must not be empty when deleteVerifications is provided")

      case ModifyVerificationsRequest(_, _, Some(CreateVerifications(_, verificationResourceReferences, _)))
          if verificationResourceReferences.isEmpty =>
        Left("verificationResourceReferences must not be empty when createVerifications is provided")

      case _ =>
        Right(request)
    }

  private def hasNoSubcontractorToModify(request: ModifyVerificationsRequest): Boolean =
    request.deleteVerifications.isEmpty && request.createVerifications.isEmpty

  def createSubmissionAndUpdateVerifications(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[CreateSubmissionAndUpdateVerificationsRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ =>
            withEnrolmentDispatch(
              Created(Json.parse(resourceHelper.resourceAsString(createSubmissionForVerification_201_ResponsePath)))
            )
        )
    }

  def updateVerificationSubmission(): Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body
        .validate[UpdateVerificationSubmissionRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ => NoContent
        )
    }

  def processVerificationResponseFromChris(): Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body
        .validate[ProcessVerificationResponseFromChrisRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ => NoContent
        )
    }

  def getSubmissionWithVerificationBatchByRefs(
    instanceId: String,
    verificationBatchResourceRef: Long
  ): Action[AnyContent] =
    Action {
      logger.info(
        s"[VerificationController][getSubmissionWithVerificationBatchByRefs] Returning stubbed response for instanceId=$instanceId, verificationBatchResourceRef=$verificationBatchResourceRef"
      )

      Ok(
        Json.parse(
          resourceHelper.resourceAsString(
            getSubmissionWithVerificationBatch_200_ResponsePath
          )
        )
      )
    }

  def getSubmittedVerifications: Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[GetSubmittedVerificationsRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ =>
            enrolmentHelper.contractorEnrolmentsOpt(request) match {
              case Some(enrolmentReference) =>
                (enrolmentReference.taxOfficeNumber, enrolmentReference.taxOfficeReference) match {
                  case ("500", _) =>
                    InternalServerError(Json.obj("message" -> "Unexpected error"))

                  case ("502", _) =>
                    BadGateway(Json.obj("message" -> "formp failed"))

                  case (_, "EZ00150") =>
                    Ok(
                      Json.parse(
                        resourceHelper.resourceAsString(getSubmittedVerifications_200_multiYear_ResponsePath)
                      )
                    )

                  case (_, "EZ00225") =>
                    Ok(
                      Json.parse(
                        resourceHelper.resourceAsString(getSubmittedVerifications_200_noHistory_ResponsePath)
                      )
                    )

                  case _ =>
                    Ok(
                      Json.parse(
                        resourceHelper.resourceAsString(getSubmittedVerifications_200_ResponsePath)
                      )
                    )
                }

              case None =>
                enrolmentHelper.agentEnrolmentsOpt(request) match {
                  case Some(_) =>
                    Ok(
                      Json.parse(
                        resourceHelper.resourceAsString(getSubmittedVerifications_200_ResponsePath)
                      )
                    )

                  case None =>
                    InternalServerError
                }
            }
        )
    }

  def getSubmissionWithVerificationBatch: Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body
        .validate[GetSubmissionWithVerificationBatchRequest]
        .fold(
          errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))),
          _ =>
            Ok(
              Json.parse(
                resourceHelper.resourceAsString(getSubmissionWithVerificationBatch_200_ResponsePath)
              )
            )
        )
    }

  def deleteVerification(): Action[JsValue] =
    authorise(parse.json) { implicit request =>
      request.body
        .validate[DeleteVerificationRequest]
        .fold(errs => BadRequest(Json.obj("message" -> "Invalid payload", "errors" -> JsError.toJson(errs))), _ => Ok)
    }
}
