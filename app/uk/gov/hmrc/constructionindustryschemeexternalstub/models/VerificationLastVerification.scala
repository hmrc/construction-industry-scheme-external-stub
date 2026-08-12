package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import play.api.libs.json.{Json, OFormat}

case class VerificationLastVerification(
  verificationId: Long,
  verificationBatchId: Option[Long],
  verificationResourcesRef: Option[Long],
  matched: Option[String],
  verificationNumber: Option[String],
  taxTreatment: Option[String],
  subcontractorName: Option[String]
)

object VerificationLastVerification {
  given format: OFormat[VerificationLastVerification] = Json.format[VerificationLastVerification]
}
