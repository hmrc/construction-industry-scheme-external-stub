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
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.AuthAction
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.CreateAmendedMonthlyReturnRequest
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.JsResultUtils.foldErrorsIntoBadRequest

import javax.inject.Inject
import scala.concurrent.Future

class AmendMonthlyReturnController @Inject() (
  authorise: AuthAction,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  def createAmendedMonthlyReturn: Action[JsValue] =
    authorise.async(parse.json) { implicit request =>
      request.body
        .validate[CreateAmendedMonthlyReturnRequest]
        .foldErrorsIntoBadRequest { _ =>
          Future.successful(Created)
        }
    }

}
