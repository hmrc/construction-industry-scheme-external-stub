package uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests

import play.api.libs.json.Json
import uk.gov.hmrc.constructionindustryschemeexternalstub.base.SpecBase

class GetSubmissionWithVerificationBatchRequestSpec extends SpecBase {

  "GetSubmissionWithVerificationBatchRequest" - {

    "serialise and deserialise successfully" in {
      val model =
        GetSubmissionWithVerificationBatchRequest(
          instanceId = "abc-123",
          verificationBatchResourceRef = 77L
        )

      val json = Json.toJson(model)

      json mustBe Json.obj(
        "instanceId"                   -> "abc-123",
        "verificationBatchResourceRef" -> 77L
      )

      json.as[GetSubmissionWithVerificationBatchRequest] mustBe model
    }
  }
}
