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

package uk.gov.hmrc.constructionindustryschemeexternalstub.controllers.formpProxy

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.ResourceHelper

class BatchPollingFixtureConsistencySpec extends SpecBase {

  private lazy val resourceHelper =
    app.injector.instanceOf[ResourceHelper]

  private def resourceJson(path: String): JsValue =
    Json.parse(resourceHelper.resourceAsString(path))

  "Batch polling fixtures" - {

    "align the verification F1 submission with the F7 snapshot" in {
      val batchPollResponse =
        resourceJson(
          "/resources/batchPoll/getBatchPollSubmissions-200-response.json"
        )

      val verificationSnapshot =
        resourceJson(
          "/resources/verification/getSubmissionWithVerificationBatch-200-response.json"
        )

      val verificationSubmission =
        (batchPollResponse \ "verificationSubmissions")
          .as[Seq[JsValue]]
          .head

      val verifications =
        (verificationSnapshot \ "verifications")
          .as[Seq[JsValue]]

      val subcontractors =
        (verificationSnapshot \ "subcontractors")
          .as[Seq[JsValue]]

      (verificationSubmission \ "submissionId")
        .as[Long] mustBe
        (verificationSnapshot \ "submission" \ "submissionId")
          .as[Long]

      (verificationSubmission \ "instanceId")
        .as[String] mustBe
        (verificationSnapshot \ "scheme" \ "instanceId")
          .as[String]

      (verificationSubmission \ "verificationBatchResourceRef")
        .as[Long] mustBe
        (verificationSnapshot \ "verificationBatch" \ "verificationBatchResourceRef")
          .as[Long]

      (verificationSnapshot \ "verificationBatch" \ "verificationsCounter")
        .as[Int] mustBe verifications.size

      verifications.map { verification =>
        (verification \ "subcontractorId").as[Long]
      } mustBe subcontractors.map { subcontractor =>
        (subcontractor \ "subcontractorId").as[Long]
      }

      verifications.map { verification =>
        (verification \ "verificationResourceRef").as[Long]
      } mustBe subcontractors.map { subcontractor =>
        (subcontractor \ "subbieResourceRef").as[Long]
      }
    }

    "align the monthly-return F1 submission with the batch-polling edit fixture" in {
      val batchPollResponse =
        resourceJson(
          "/resources/batchPoll/getBatchPollSubmissions-200-response.json"
        )

      val monthlyReturnSnapshot =
        resourceJson(
          "/resources/getMonthlyReturnForEdit-batch-polling-200-response.json"
        )

      val monthlySubmission =
        (batchPollResponse \ "monthlyReturnSubmissions")
          .as[Seq[JsValue]]
          .head

      val scheme =
        (monthlyReturnSnapshot \ "scheme")
          .as[Seq[JsValue]]
          .head

      val monthlyReturn =
        (monthlyReturnSnapshot \ "monthlyReturn")
          .as[Seq[JsValue]]
          .head

      val submission =
        (monthlyReturnSnapshot \ "submission")
          .as[Seq[JsValue]]
          .head

      (monthlySubmission \ "instanceId")
        .as[String] mustBe
        (scheme \ "instanceId").as[String]

      (monthlySubmission \ "submissionId")
        .as[Long] mustBe
        (submission \ "submissionId").as[Long]

      (monthlySubmission \ "taxYear")
        .as[Int] mustBe
        (monthlyReturn \ "taxYear").as[Int]

      (monthlySubmission \ "taxMonth")
        .as[Int] mustBe
        (monthlyReturn \ "taxMonth").as[Int]

      (monthlyReturn \ "status")
        .as[String] mustBe
        (submission \ "status").as[String]

      (monthlyReturn \ "status")
        .as[String] mustBe "SUBMITTED"
    }

    "keep the normal monthly-return edit fixture editable" in {
      val normalMonthlyReturnSnapshot =
        resourceJson(
          "/resources/getMonthlyReturnForEdit-200-response.json"
        )

      val monthlyReturn =
        (normalMonthlyReturnSnapshot \ "monthlyReturn")
          .as[Seq[JsValue]]
          .head

      (monthlyReturn \ "status")
        .as[String] mustBe "STARTED"
    }

    "align the GovTalk fixtures with the F1 submission IDs" in {
      val batchPollResponse =
        resourceJson(
          "/resources/batchPoll/getBatchPollSubmissions-200-response.json"
        )

      val verificationGovTalkResponse =
        resourceJson(
          "/resources/govTalk/getGovTalkStatus-verification-200-response.json"
        )

      val monthlyReturnGovTalkResponse =
        resourceJson(
          "/resources/govTalk/getGovTalkStatus-monthlyReturn-200-response.json"
        )

      val verificationSubmission =
        (batchPollResponse \ "verificationSubmissions")
          .as[Seq[JsValue]]
          .head

      val monthlySubmission =
        (batchPollResponse \ "monthlyReturnSubmissions")
          .as[Seq[JsValue]]
          .head

      val verificationGovTalkStatus =
        (verificationGovTalkResponse \ "govtalk_status")
          .as[Seq[JsValue]]
          .head

      val monthlyReturnGovTalkStatus =
        (monthlyReturnGovTalkResponse \ "govtalk_status")
          .as[Seq[JsValue]]
          .head

      (verificationGovTalkStatus \ "formResultID")
        .as[String] mustBe
        (verificationSubmission \ "submissionId")
          .as[Long]
          .toString

      (monthlyReturnGovTalkStatus \ "formResultID")
        .as[String] mustBe
        (monthlySubmission \ "submissionId")
          .as[Long]
          .toString

      (verificationGovTalkStatus \ "gatewayURL")
        .as[String] mustBe
        "[pollingUrlHost]submission/ChRIS/poll/IR-CIS-VERIFY/0?final=SUBMITTED"

      (monthlyReturnGovTalkStatus \ "gatewayURL")
        .as[String] mustBe
        "[pollingUrlHost]submission/ChRIS/poll/IR-CIS-CIS300MR/0?final=SUBMITTED"
    }
  }
}
