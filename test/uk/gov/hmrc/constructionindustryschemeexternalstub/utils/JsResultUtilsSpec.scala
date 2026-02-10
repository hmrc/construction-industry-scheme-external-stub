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

package uk.gov.hmrc.constructionindustryschemeexternalstub.utils

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.*
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.test.Helpers.*
import JsResultUtils.*

import scala.concurrent.Future

class JsResultUtilsSpec extends AnyFreeSpec with Matchers with ScalaFutures {

  "JsResultUtils.foldErrorsIntoBadRequest" - {

    "returns the mapped Result when JsSuccess" in {
      val jsResult: JsResult[Int] = JsSuccess(123)

      val resF: Future[Result] =
        jsResult.foldErrorsIntoBadRequest { value =>
          Future.successful(Results.Ok(Json.obj("value" -> value)))
        }

      status(resF) mustBe OK
      contentAsJson(resF) mustBe Json.obj("value" -> 123)
    }

    "returns 400 with error payload when JsError" in {
      val jsResult: JsResult[Int] =
        JsError(JsPath \ "field", JsonValidationError("error.expected.int"))

      val resF: Future[Result] =
        jsResult.foldErrorsIntoBadRequest(_ => Future.successful(Results.Ok))

      status(resF) mustBe BAD_REQUEST
      (contentAsJson(resF) \ "message").as[String] mustBe "Invalid JSON body"
      (contentAsJson(resF) \ "errors").toOption.isDefined mustBe true
    }
  }
}
