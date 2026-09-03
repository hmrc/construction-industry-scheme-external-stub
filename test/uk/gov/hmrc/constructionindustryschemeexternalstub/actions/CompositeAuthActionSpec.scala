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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompositeAuthActionSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

  private lazy val app    = GuiceApplicationBuilder().build()
  private lazy val parser = app.injector.instanceOf[BodyParsers.Default]

  override def afterAll(): Unit = app.stop()

  private val token  = "test-internal-auth-token"
  private val config = Configuration("internal-auth.token" -> token)

  private def makeComposite: (CompositeAuthAction, DefaultAuthAction, InternalAuthAction) = {
    val mockDefault  = mock[DefaultAuthAction]
    val mockInternal = mock[InternalAuthAction]
    val composite    = new CompositeAuthAction(mockDefault, mockInternal, config, parser)
    (composite, mockDefault, mockInternal)
  }

  "CompositeAuthAction" should {

    "delegate to InternalAuthAction when Authorization header matches the configured token" in {
      val (composite, mockDefault, mockInternal) = makeComposite

      when(mockInternal.invokeBlock(any(), any()))
        .thenReturn(Future.successful(Results.Ok("from-internal")))

      val result = composite.invokeBlock(
        FakeRequest().withHeaders("Authorization" -> token),
        _ => Future.successful(Results.Ok("block"))
      )

      status(result) mustBe OK
      verify(mockInternal).invokeBlock(any(), any())
      verifyNoInteractions(mockDefault)
    }

    "delegate to DefaultAuthAction when Authorization header does not match the configured token" in {
      val (composite, mockDefault, mockInternal) = makeComposite

      when(mockDefault.invokeBlock(any(), any()))
        .thenReturn(Future.successful(Results.Unauthorized))

      val result = composite.invokeBlock(
        FakeRequest().withHeaders("Authorization" -> "wrong-token"),
        _ => Future.successful(Results.Ok("block"))
      )

      status(result) mustBe UNAUTHORIZED
      verify(mockDefault).invokeBlock(any(), any())
      verifyNoInteractions(mockInternal)
    }

    "delegate to DefaultAuthAction when Authorization header is absent" in {
      val (composite, mockDefault, mockInternal) = makeComposite

      when(mockDefault.invokeBlock(any(), any()))
        .thenReturn(Future.successful(Results.Unauthorized))

      val result = composite.invokeBlock(
        FakeRequest(),
        _ => Future.successful(Results.Ok("block"))
      )

      status(result) mustBe UNAUTHORIZED
      verify(mockDefault).invokeBlock(any(), any())
      verifyNoInteractions(mockInternal)
    }
  }
}
