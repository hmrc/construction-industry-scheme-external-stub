package uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class GetGovTalkStatusRequestSpec extends AnyWordSpec with Matchers {

  "GetGovTalkStatusRequest (JSON)" should {

    "read and write with SoleTrader type" in {
      val json = Json.parse("""
                              |{
                              |  "userIdentifier": "1",
                              |  "formResultID": "12890"
                              |}
        """.stripMargin)

      val model = json.as[GetGovTalkStatusRequest]
      model.userIdentifier mustBe "1"
      model.formResultID mustBe "12890"

      Json.toJson(model) mustBe json
    }

    "fail to read missing userIdentifier" in {
      val json = Json.parse("""
                              |{
                              |  "formResultID": "12890"
                              |}
        """.stripMargin)

      val result = json.validate[GetGovTalkStatusRequest]
      result.isError mustBe true
    }

    "fail to read missing formResultID" in {
      val json = Json.parse("""
                              |{
                              |  "userIdentifier": "1"
                              |}
        """.stripMargin)

      val result = json.validate[GetGovTalkStatusRequest]
      result.isError mustBe true
    }
  }
}
