package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.GetMonthlyReturnForEditRequest

class GetMonthlyReturnForEditRequestSpec extends AnyWordSpec with Matchers {

  "GetMonthlyReturnForEditRequest JSON format" should {

    "deserialize valid JSON" in {
      val json = Json.parse(
        """
          |{
          |  "instanceId": "900001",
          |  "taxYear": 2025,
          |  "taxMonth": 1
          |}
          |""".stripMargin
      )

      json.validate[GetMonthlyReturnForEditRequest] shouldBe
        JsSuccess(GetMonthlyReturnForEditRequest("900001", 2025, 1))
    }

    "serialize to JSON" in {
      val model = GetMonthlyReturnForEditRequest(
        instanceId = "900001",
        taxYear = 2025,
        taxMonth = 1
      )

      Json.toJson(model) shouldBe Json.obj(
        "instanceId" -> "900001",
        "taxYear"    -> 2025,
        "taxMonth"   -> 1
      )
    }
  }
}
