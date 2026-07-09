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

package uk.gov.hmrc.constructionindustryschemeexternalstub.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class AppConfig @Inject() (config: Configuration):

  lazy val callback: String = config.get[String]("stub.polling.callback")

  def responseUrl(service: String): String                     = callback + config.get[String](s"stub.$service.response")
  def pollUrl(service: String): String                         = callback + config.get[String](s"stub.$service.poll")
  def pollingStatus(regime: String, taxNumber: String): String = {
    val path =
      regime match {
        case "IR-CIS-VERIFY"   =>
          s"stub.endpoint.submission.cis.verify.polling.statusMap.$taxNumber"
        case "IR-CIS-CIS300MR" =>
          s"stub.endpoint.submission.cis.polling.statusMap.$taxNumber"
        case _                 =>
          throw new RuntimeException(s"Unknown regime: $regime")
      }

    config.getOptional[String](path).getOrElse("SUBMITTED")
  }

  val appName: String = config.get[String]("appName")

  lazy val acknowledgeFilter: Seq[String] =
    config.get[Seq[String]]("stub.endpoint.submission.cis.filing.acknowledgeFilter")
  lazy val fatalErrorFilter: Seq[String]  =
    config.get[Seq[String]]("stub.endpoint.submission.cis.filing.fatalErrorFilter")

  lazy val verifyAcknowledgeFilter: Seq[String] =
    config.get[Seq[String]]("stub.endpoint.submission.cis.verify.acknowledgeFilter")
  lazy val verifyFatalErrorFilter: Seq[String]  =
    config.get[Seq[String]]("stub.endpoint.submission.cis.verify.fatalErrorFilter")
  lazy val perfMode: Boolean                    = config.get[Boolean]("perfMode")
  lazy val pollInterval: String                 = if (perfMode) "0" else "2"
