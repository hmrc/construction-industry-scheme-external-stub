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

import play.api.{Configuration, Logging}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class BatchPollController @Inject() (
  resourceHelper: ResourceHelper,
  cc: ControllerComponents,
  configuration: Configuration
) extends BackendController(cc)
    with Logging {

  private val batchPollResponsePath = "/resources/batchPoll"

  private val getBatchPollSubmissions200ResponsePath =
    s"$batchPollResponsePath/getBatchPollSubmissions-200-response.json"

  private val getBatchPollSubmissions200EmptyResponsePath =
    s"$batchPollResponsePath/getBatchPollSubmissions-200-empty-response.json"

  private val getBatchPollSubmissions200MissingProceedResponsePath =
    s"$batchPollResponsePath/getBatchPollSubmissions-200-missingProceed-response.json"

  // The backend calls this endpoint without query params, so a scenario can also be set via config,
  // e.g. `sbt run -Dstub.batchPoll.defaultScenario=missingProceed`
  private val defaultScenario: Option[String] =
    configuration.getOptional[String]("stub.batchPoll.defaultScenario")

  def getBatchPollSubmissions: Action[AnyContent] =
    Action { request =>
      request.getQueryString("scenario").orElse(defaultScenario) match {
        case Some("500") =>
          logger.warn("[BatchPollController][getBatchPollSubmissions] Scenario 500 - returning Unexpected error")
          InternalServerError(Json.obj("message" -> "Unexpected error"))
        case Some("502") =>
          logger.warn("[BatchPollController][getBatchPollSubmissions] Scenario 502 - returning formp failed")
          BadGateway(Json.obj("message" -> "formp failed"))
        case Some("000") =>
          logger.info("[BatchPollController][getBatchPollSubmissions] Scenario 000 - returning empty submissions")
          Ok(Json.parse(resourceHelper.resourceAsString(getBatchPollSubmissions200EmptyResponsePath)))

        case Some("missingProceed") =>
          logger.info(
            "[BatchPollController][getBatchPollSubmissions] Scenario missingProceed - returning verification submission with batch ref 6"
          )
          Ok(Json.parse(resourceHelper.resourceAsString(getBatchPollSubmissions200MissingProceedResponsePath)))

        case _ =>
          logger.info("[BatchPollController][getBatchPollSubmissions] Returning batch poll submissions")
          Ok(Json.parse(resourceHelper.resourceAsString(getBatchPollSubmissions200ResponsePath)))
      }
    }
}
