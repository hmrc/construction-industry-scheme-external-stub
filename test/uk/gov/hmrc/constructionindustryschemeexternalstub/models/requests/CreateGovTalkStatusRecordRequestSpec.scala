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

package uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class CreateGovTalkStatusRecordRequestSpec extends AnyWordSpec with Matchers {

  "CreateGovTalkStatusRecordRequest (JSON)" should {

    "read and write with mandatory fields" in {
      val json = Json.parse("""
          |{
          |  "userIdentifier": "1",
          |  "formResultID": "12890",
          |  "correlationID": "128903445",
          |  "gatewayURL": "http://vat.chris.hmrc.gov.uk:9102/ChRIS/UKVAT/Filing/action/VATDEC"
          |}
        """.stripMargin)

      val model = json.as[CreateGovTalkStatusRecordRequest]
      model.userIdentifier mustBe "1"
      model.formResultID mustBe "12890"
      model.correlationID mustBe "128903445"
      model.gatewayURL mustBe "http://vat.chris.hmrc.gov.uk:9102/ChRIS/UKVAT/Filing/action/VATDEC"
      Json.toJson(model) mustBe json
    }

    "fail to read missing userIdentifier" in {
      val json = Json.parse("""
                              |{
                              |  "formResultID": "12890",
                              |  "correlationID": "128903445",
                              |  "gatewayURL": "http://vat.chris.hmrc.gov.uk:9102/ChRIS/UKVAT/Filing/action/VATDEC"
                              |}
        """.stripMargin)

      val result = json.validate[CreateGovTalkStatusRecordRequest]
      result.isError mustBe true
    }

    "fail to read missing formResultID" in {
      val json = Json.parse("""
                              |{
                              |  "userIdentifier": "1",
                              |  "correlationID": "128903445",
                              |  "gatewayURL": "http://vat.chris.hmrc.gov.uk:9102/ChRIS/UKVAT/Filing/action/VATDEC"
                              |}
        """.stripMargin)

      val result = json.validate[CreateGovTalkStatusRecordRequest]
      result.isError mustBe true
    }

    "fail to read missing correlationID" in {
      val json = Json.parse("""
                              |{
                              |  "userIdentifier": "1",
                              |  "formResultID": "12890",
                              |  "gatewayURL": "http://vat.chris.hmrc.gov.uk:9102/ChRIS/UKVAT/Filing/action/VATDEC"
                              |}
        """.stripMargin)

      val result = json.validate[CreateGovTalkStatusRecordRequest]
      result.isError mustBe true
    }

    "fail to read missing gatewayURL" in {
      val json = Json.parse("""
                              |{
                              |  "userIdentifier": "1",
                              |  "formResultID": "12890",
                              |  "correlationID": "128903445"
                              |}
        """.stripMargin)

      val result = json.validate[CreateGovTalkStatusRecordRequest]
      result.isError mustBe true
    }
  }
}
