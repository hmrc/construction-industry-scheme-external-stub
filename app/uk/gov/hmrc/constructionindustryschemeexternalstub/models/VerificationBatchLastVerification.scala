package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import play.api.libs.json.{Json, OFormat}

case class VerificationBatchLastVerification(
  verificationBatchId: Option[Long],
  verifBatchResourceRef: Option[Long]
)

object VerificationBatchLastVerification {
  given format: OFormat[VerificationBatchLastVerification] = Json.format[VerificationBatchLastVerification]
}
