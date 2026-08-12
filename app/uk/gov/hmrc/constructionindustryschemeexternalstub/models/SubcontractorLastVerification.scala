package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import play.api.libs.json.{Json, OFormat}

case class SubcontractorLastVerification(
  subcontractorId: Long,
  subbieResourceRef: Option[Long],
  subcontractorType: Option[String],
  utr: Option[String]
)

object SubcontractorLastVerification {
  given format: OFormat[SubcontractorLastVerification] = Json.format[SubcontractorLastVerification]
}
