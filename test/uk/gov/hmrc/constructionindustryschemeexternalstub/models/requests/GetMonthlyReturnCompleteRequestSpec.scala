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

package uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class GetMonthlyReturnCompleteRequestSpec extends AnyWordSpec with Matchers {

  "GetMonthlyReturnCompleteRequest JSON format" should {

    "serialize to JSON" in {
      val model = GetMonthlyReturnCompleteRequest(
        instanceId = "instance-456",
        taxYear = 2025,
        taxMonth = 6,
        amendment = "N"
      )

      val json = Json.toJson(model)

      json mustBe Json.obj(
        "instanceId" -> "instance-456",
        "taxYear"    -> 2025,
        "taxMonth"   -> 6,
        "amendment"  -> "N"
      )
    }

    "deserialize from JSON" in {
      val json = Json.obj(
        "instanceId" -> "instance-456",
        "taxYear"    -> 2025,
        "taxMonth"   -> 6,
        "amendment"  -> "N"
      )

      json.as[GetMonthlyReturnCompleteRequest] mustBe
        GetMonthlyReturnCompleteRequest(
          instanceId = "instance-456",
          taxYear = 2025,
          taxMonth = 6,
          amendment = "N"
        )
    }
  }
}
