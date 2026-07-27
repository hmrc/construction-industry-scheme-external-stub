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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.email

import play.api.http.Status.{ACCEPTED, BAD_REQUEST}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{ACCEPT, CONTENT_TYPE, JSON, POST, contentAsJson, status}
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.SendEmailRequest

import scala.concurrent.Future

class EmailControllerSpec extends SpecBase {
  ".sendEmail" - {
    val sendEmailUrl = "/hmrc/email"

    "returns 202 on valid payload for an unknown taxOfficeNumber / taxOfficeReference / agent" in new Setup {

      val json: JsValue = Json.toJson(
        SendEmailRequest(
          to = List("email1@test.com", "email2@test.com"),
          templateId = "emailTemplateId",
          parameters = Map(
            "year"  -> "2026",
            "month" -> "March"
          )
        )
      )

      val req: FakeRequest[JsValue] = makeJsonRequest(json, sendEmailUrl)
      val res: Future[Result]       = controller.sendEmail()(req)

      status(res) mustBe ACCEPTED
    }

    "returns 400 BadRequest for invalid JSON" in new Setup {

      val bad: JsObject             = Json.obj("nope" -> "nope")
      val req: FakeRequest[JsValue] = makeJsonRequest(bad, sendEmailUrl)
      val res: Future[Result]       = controller.sendEmail()(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid payload"
    }
  }

  private trait Setup {
    lazy val controller = new EmailController(cc)

    def makeJsonRequest(body: JsValue, url: String): FakeRequest[JsValue] =
      FakeRequest(POST, url)
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)
  }
}
