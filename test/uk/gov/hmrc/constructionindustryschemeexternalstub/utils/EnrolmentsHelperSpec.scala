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

package uk.gov.hmrc.constructionindustryschemeexternalstub.utils

import play.api.mvc.AnyContent
import uk.gov.hmrc.auth.core.{EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.AuthenticatedRequest
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.EmployerReference

class EnrolmentsHelperSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val helper = new EnrolmentsHelper

  "contractorEnrolmentsOpt" should {
    "return EmployerReference when enrolment and identifiers exist" in {
      val request      = mock[AuthenticatedRequest[AnyContent]]
      val enrolments   = mock[Enrolments]
      val cisEnrol     = mock[Enrolment]
      val taxOfficeNr  = EnrolmentIdentifier("TaxOfficeNumber", "123")
      val taxOfficeRef = EnrolmentIdentifier("TaxOfficeReference", "AB456")
      when(request.enrolments).thenReturn(enrolments)
      when(enrolments.getEnrolment("HMRC-CIS-ORG")).thenReturn(Some(cisEnrol))
      when(cisEnrol.getIdentifier("TaxOfficeNumber")).thenReturn(Some(taxOfficeNr))
      when(cisEnrol.getIdentifier("TaxOfficeReference")).thenReturn(Some(taxOfficeRef))
      helper.contractorEnrolmentsOpt(request) mustBe Some(EmployerReference("123", "AB456"))
    }
    "return None when the enrolment does not exist" in {
      val request    = mock[AuthenticatedRequest[AnyContent]]
      val enrolments = mock[Enrolments]
      when(request.enrolments).thenReturn(enrolments)
      when(enrolments.getEnrolment("HMRC-CIS-ORG")).thenReturn(None)
      helper.contractorEnrolmentsOpt(request) mustBe None
    }
    "return None when identifiers are missing" in {
      val request    = mock[AuthenticatedRequest[AnyContent]]
      val enrolments = mock[Enrolments]
      val cisEnrol   = mock[Enrolment]
      when(request.enrolments).thenReturn(enrolments)
      when(enrolments.getEnrolment("HMRC-CIS-ORG")).thenReturn(Some(cisEnrol))
      when(cisEnrol.getIdentifier("TaxOfficeNumber")).thenReturn(None)
      helper.contractorEnrolmentsOpt(request) mustBe None
    }
  }

  "agentEnrolmentsOpt" should {
    "return agent reference when enrolment and identifier exist" in {
      val request    = mock[AuthenticatedRequest[AnyContent]]
      val enrolments = mock[Enrolments]
      val agentEnrol = mock[Enrolment]
      val agentId    = EnrolmentIdentifier("IRAgentReference", "AGENT123")
      when(request.enrolments).thenReturn(enrolments)
      when(enrolments.getEnrolment("IR-PAYE-AGENT")).thenReturn(Some(agentEnrol))
      when(agentEnrol.getIdentifier("IRAgentReference")).thenReturn(Some(agentId))
      helper.agentEnrolmentsOpt(request) mustBe Some("AGENT123")
    }
    "return None when enrolment is missing" in {
      val request    = mock[AuthenticatedRequest[AnyContent]]
      val enrolments = mock[Enrolments]
      when(request.enrolments).thenReturn(enrolments)
      when(enrolments.getEnrolment("IR-PAYE-AGENT")).thenReturn(None)
      helper.agentEnrolmentsOpt(request) mustBe None
    }
    "return None when identifier is missing" in {
      val request    = mock[AuthenticatedRequest[AnyContent]]
      val enrolments = mock[Enrolments]
      val agentEnrol = mock[Enrolment]
      when(request.enrolments).thenReturn(enrolments)
      when(enrolments.getEnrolment("IR-PAYE-AGENT")).thenReturn(Some(agentEnrol))
      when(agentEnrol.getIdentifier("IRAgentReference")).thenReturn(None)
      helper.agentEnrolmentsOpt(request) mustBe None
    }
  }
}
