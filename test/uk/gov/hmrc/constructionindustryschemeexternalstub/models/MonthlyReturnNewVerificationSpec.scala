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

package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

final class MonthlyReturnNewVerificationSpec extends AnyWordSpec with Matchers {

  "MonthlyReturnNewVerification JSON format" should {

    "read JSON into model with missing optional field using default" in {
      val json = Json.parse(
        """
          |{
          |  "monthlyReturnId": 777
          |}
          |""".stripMargin
      )

      val out = json.as[MonthlyReturnNewVerification]
      out.monthlyReturnId mustBe 777L
      out.decNoMoreSubPayments mustBe None
    }

    "write model to JSON" in {
      val model = MonthlyReturnNewVerification(
        monthlyReturnId = 777L,
        decNoMoreSubPayments = Some("Y")
      )

      val json = Json.toJson(model)
      (json \ "monthlyReturnId").as[Long] mustBe 777L
      (json \ "decNoMoreSubPayments").as[String] mustBe "Y"
    }

    "round-trip (model -> json -> model) without losing data" in {
      val model = MonthlyReturnNewVerification(777L, Some("N"))
      val json  = Json.toJson(model)
      json.validate[MonthlyReturnNewVerification] mustBe JsSuccess(model)
    }
  }
}