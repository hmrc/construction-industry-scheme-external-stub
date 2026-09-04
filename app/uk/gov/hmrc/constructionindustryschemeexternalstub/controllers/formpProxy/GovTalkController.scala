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
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.JsResultUtils.foldErrorsIntoBadRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.Future

class GovTalkController @Inject() (
  appConfig: AppConfig,
  resourceHelper: ResourceHelper,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val govTalkReturnResponsePath = "/resources/govTalk"

  private val getGovTalkStatus200ResponsePath                =
    s"$govTalkReturnResponsePath/getGovTalkStatus-200-response.json"
  private val getGovTalkStatus200_monthlyReturn_ResponsePath =
    s"$govTalkReturnResponsePath/getGovTalkStatus-monthlyReturn-200-response.json"
  private val getGovTalkStatus200_verification_ResponsePath  =
    s"$govTalkReturnResponsePath/getGovTalkStatus-verification-200-response.json"

  def getGovTalkStatus: Action[JsValue] =
    Action(parse.json) { request =>
      val stage    = request.getQueryString("stage")
      val scenario = request.getQueryString("scenario")

      request.body
        .validate[GetGovTalkStatusRequest]
        .fold(
          errors =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errors)
              )
            ),
          govTalkRequest =>
            scenarioResult(
              scenario,
              govTalkStatusResult(
                stage,
                govTalkRequest.formResultID
              )
            )
        )
    }

  private def govTalkStatusResult(
    stage: Option[String],
    submissionId: String
  ): Result =
    stage match {
      case Some("initial") =>
        NotFound

      case Some("polling") =>
        submissionId match {
          case "90001" =>
            Ok(govTalkStatusResponse(getGovTalkStatus200_verification_ResponsePath))

          case "90002" =>
            Ok(govTalkStatusResponse(getGovTalkStatus200_monthlyReturn_ResponsePath))
          case _       =>
            Ok(Json.parse(resourceHelper.resourceAsString(getGovTalkStatus200ResponsePath)))
        }
      case _               =>
        NotFound
    }

  private def scenarioResult(
    scenario: Option[String],
    defaultResult: => Result
  ): Result =
    scenario match {
      case Some("500") =>
        InternalServerError(Json.obj("message" -> "Unexpected error"))
      case Some("502") =>
        BadGateway(Json.obj("message" -> "formp failed"))
      case Some("404") =>
        NotFound
      case _           =>
        defaultResult
    }

  private def govTalkStatusResponse(responsePath: String): JsValue =
    Json.parse(
      resourceHelper
        .resourceAsString(responsePath)
        .replace(
          "[pollingUrlHost]",
          appConfig.callback
        )
    )

  def updateGovTalkStatusCorrelationId: Action[JsValue] =
    Action.async(parse.json) { request =>
      request.body
        .validate[UpdateGovTalkStatusCorrelationIdRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(NoContent)
        }
    }

  def resetGovTalkStatus: Action[JsValue] =
    Action(parse.json) { request =>
      val scenario = request.getQueryString("scenario")

      request.body
        .validate[ResetGovTalkStatusRequest]
        .fold(
          errors =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errors)
              )
            ),
          _ => scenarioResult(scenario, NoContent)
        )
    }

  def updateGovTalkStatus: Action[JsValue] =
    Action(parse.json) { request =>
      request.body
        .validate[UpdateGovTalkStatusRequest]
        .fold(
          errors =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errors)
              )
            ),
          _ => NoContent
        )
    }

  def updateGovTalkStatusStatistics(): Action[JsValue] =
    Action(parse.json) { request =>
      request.body
        .validate[UpdateGovTalkStatusStatisticsRequest]
        .fold(
          errors =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errors)
              )
            ),
          _ => NoContent
        )
    }

  def createGovTalkStatusRecord: Action[JsValue] =
    Action(parse.json) { request =>
      request.body
        .validate[CreateGovTalkStatusRecordRequest]
        .fold(
          errors =>
            BadRequest(
              Json.obj(
                "message" -> "Invalid payload",
                "errors"  -> JsError.toJson(errors)
              )
            ),
          _ => Created
        )
    }
}
