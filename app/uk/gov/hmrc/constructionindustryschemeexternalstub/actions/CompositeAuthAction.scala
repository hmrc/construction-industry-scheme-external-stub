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

package uk.gov.hmrc.constructionindustryschemeexternalstub.actions

import play.api.Configuration
import play.api.mvc.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.AuthenticatedRequest

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/** Routes requests to InternalAuthAction when the Authorization header matches the configured internal-auth token;
  * otherwise falls through to the standard GG session DefaultAuthAction. This lets formpProxy endpoints accept
  * service-to-service calls (which CIS makes with an internal-auth token) while rds-datacache-proxy endpoints continue
  * to use user session auth.
  */
class CompositeAuthAction @Inject() (
  defaultAuth: DefaultAuthAction,
  internalAuth: InternalAuthAction,
  config: Configuration,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends AuthAction {

  private val expectedToken = config.get[String]("internal-auth.token")

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    request.headers.get("Authorization") match {
      case Some(token) if token == expectedToken => internalAuth.invokeBlock(request, block)
      case _                                     => defaultAuth.invokeBlock(request, block)
    }
}
