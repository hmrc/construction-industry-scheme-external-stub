package uk.gov.hmrc.constructionindustryschemeexternalstub.models.response

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.*

case class GetLastSubmittedVerificationBatchResponse(
  scheme: Option[ContractorSchemeLastVerification],
  subcontractors: Seq[SubcontractorLastVerification],
  verifications: Seq[VerificationLastVerification],
  verificationBatch: Option[VerificationBatchLastVerification],
  submission: Option[Submission]
)

object GetLastSubmittedVerificationBatchResponse {
  given format: OFormat[GetLastSubmittedVerificationBatchResponse] =
    Json.format[GetLastSubmittedVerificationBatchResponse]
}
