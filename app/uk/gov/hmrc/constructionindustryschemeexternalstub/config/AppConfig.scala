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

  def responseUrl(service: String): String     = callback + config.get[String](s"stub.$service.response")
  def pollUrl(service: String): String         = callback + config.get[String](s"stub.$service.poll")
  def pollingStatus(taxNumber: String): String = config
    .getOptional[String](s"stub.endpoint.submission.cis.polling.statusMap.$taxNumber")
    .getOrElse("SUBMITTED")

  val appName: String = config.get[String]("appName")

  lazy val acknowledgeFilter: Seq[String] =
    config.get[Seq[String]]("stub.endpoint.submission.cis.filing.acknowledgeFilter")
  lazy val fatalErrorFilter: Seq[String]  =
    config.get[Seq[String]]("stub.endpoint.submission.cis.filing.fatalErrorFilter")
  lazy val perfMode: Boolean              = config.get[Boolean]("perfMode")
  lazy val pollInterval: String           = if (perfMode) "0" else "2"
