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

import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase

class AmendMonthlyReturnControllerSpec extends SpecBase {

  private val controller =
    new AmendMonthlyReturnController(
      authorise = fakeAuthAction,
      cc = cc
    )

  "AmendMonthlyReturnController" - {

    "createAmendedMonthlyReturn should return 201 Created for a valid request" in {
      val json = Json.obj(
        "instanceId" -> "1",
        "taxYear"    -> 2024,
        "taxMonth"   -> 6,
        "version"    -> 0
      )

      val result = controller.createAmendedMonthlyReturn()(
        fakeRequestWithJsonBody(json)
      )

      status(result) mustBe CREATED
    }

    "createAmendedMonthlyReturn should return 400 Bad Request for an invalid request" in {
      val json = Json.obj(
        "instanceId" -> "1",
        "taxYear"    -> 2024
      )

      val result = controller.createAmendedMonthlyReturn()(
        fakeRequestWithJsonBody(json)
      )

      status(result) mustBe BAD_REQUEST
    }
  }
}
