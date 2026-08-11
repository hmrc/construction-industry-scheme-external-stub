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

import play.api.Logging
import play.api.mvc.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisationException, AuthorisedFunctions}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.AuthenticatedRequest
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.EnrolmentsHelper
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefaultAuthAction @Inject() (
  override val authConnector: AuthConnector,
  val parser: BodyParsers.Default,
  enrolmentsHelper: EnrolmentsHelper
)(implicit val executionContext: ExecutionContext)
    extends AuthAction
    with AuthorisedFunctions
    with Logging {

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)
    logger.info(s"Request received: \n${request.method} ${request.uri}\n${request.body}")
    val sessionId                  = summon[HeaderCarrier].sessionId
      .getOrElse(throw new UnauthorizedException("Unable to retrieve session ID from headers"))

    authorised()
      .retrieve(Retrievals.internalId and Retrievals.credentials and Retrievals.allEnrolments) {
        case Some(internalId) ~ Some(credentials) ~ enrolments =>
          val authRequest = AuthenticatedRequest(request, internalId, credentials.providerId, sessionId, enrolments)

          if (hasAnyCisEnrolment(authRequest)) block(authRequest)
          else Future.successful(Results.Forbidden)

        case _ =>
          throw new UnauthorizedException("Unable to retrieve credential or internal Id")
      }
      .recover { case ae: AuthorisationException =>
        logger.warn(s"[Auth] Authorisation Exception ${ae.reason}")
        Results.Unauthorized
      }
  }

  private def hasAnyCisEnrolment(request: AuthenticatedRequest[_]): Boolean =
    enrolmentsHelper.contractorEnrolmentsOpt(request).isDefined || enrolmentsHelper
      .agentEnrolmentsOpt(request)
      .isDefined
}

trait AuthAction
    extends ActionBuilder[AuthenticatedRequest, AnyContent]
    with ActionFunction[Request, AuthenticatedRequest]
