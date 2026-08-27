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

package uk.gov.hmrc.constructionindustryschemeexternalstub.actions

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InternalAuthActionSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll {

  private lazy val app    = GuiceApplicationBuilder().build()
  private lazy val parser = app.injector.instanceOf[BodyParsers.Default]

  override def afterAll(): Unit = app.stop()

  private val token = "test-internal-auth-token"

  private def action = new InternalAuthAction(parser, Configuration("internal-auth.token" -> token))

  "InternalAuthAction" should {

    "pass through and invoke the block when the Authorization header matches the configured token" in {
      var capturedInternalId = ""
      val request            = FakeRequest().withHeaders("Authorization" -> token)

      val result = action.invokeBlock(
        request,
        req => {
          capturedInternalId = req.internalId
          Future.successful(Results.Ok("ok"))
        }
      )

      status(result) mustBe OK
      capturedInternalId mustBe "internal-service"
    }

    "return 401 Unauthorized when the Authorization header is absent" in {
      val result = action.invokeBlock(FakeRequest(), _ => Future.successful(Results.Ok("unreachable")))
      status(result) mustBe UNAUTHORIZED
    }

    "return 401 Unauthorized when the Authorization header does not match the configured token" in {
      val request = FakeRequest().withHeaders("Authorization" -> "wrong-token")
      val result  = action.invokeBlock(request, _ => Future.successful(Results.Ok("unreachable")))
      status(result) mustBe UNAUTHORIZED
    }

    "populate AuthenticatedRequest with fixed internal-service identifiers on a valid token" in {
      var capturedCredentialId = ""
      val request              = FakeRequest().withHeaders("Authorization" -> token)

      val result = action.invokeBlock(
        request,
        req => {
          capturedCredentialId = req.credentialId
          Future.successful(Results.Ok("ok"))
        }
      )

      status(result) mustBe OK
      capturedCredentialId mustBe "internal-service"
    }
  }
}
