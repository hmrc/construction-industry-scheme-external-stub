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

class SendEmailRequestSpec extends AnyWordSpec with Matchers {

  "SendEmailRequest (JSON)" should {

    "read and write with mandatory fields" in {
      val json = Json.parse("""
                              |{
                              |  "to": ["email1@test.com", "email2@test.com"],
                              |  "templateId": "emailTemplateId",
                              |  "parameters": {
                              |    "year": "2026",
                              |    "month": "March"
                              |  }
                              |}
                            """.stripMargin)

      val model = json.as[SendEmailRequest]
      model.to mustBe List("email1@test.com", "email2@test.com")
      model.templateId mustBe "emailTemplateId"
      model.parameters mustBe Map(
        "year"  -> "2026",
        "month" -> "March"
      )
      Json.toJson(model) mustBe json
    }

    "fail to read missing to" in {
      val json = Json.parse("""
                              |{
                              |  "templateId": "emailTemplateId",
                              |  "parameters": {
                              |    "year": "2026",
                              |    "month": "March"
                              |  }
                              |}
                            """.stripMargin)

      val result = json.validate[SendEmailRequest]
      result.isError mustBe true
    }

    "fail to read missing templateId" in {
      val json = Json.parse("""
                              |{
                              |  "to": ["email1@test.com", "email2@test.com"],
                              |  "parameters": {
                              |    "year": "2026",
                              |    "month": "March"
                              |  }
                              |}
                            """.stripMargin)

      val result = json.validate[SendEmailRequest]
      result.isError mustBe true
    }

    "fail to read missing parameters" in {
      val json = Json.parse("""
                              |{
                              |  "to": ["email1@test.com", "email2@test.com"],
                              |  "templateId": "emailTemplateId"
                              |}
                            """.stripMargin)

      val result = json.validate[SendEmailRequest]
      result.isError mustBe true
    }
  }
}
