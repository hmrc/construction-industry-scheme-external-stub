package uk.gov.hmrc.constructionindustryschemeexternalstub.models

import play.api.libs.json.{Json, OFormat}

case class ContractorSchemeLastVerification(
  name: Option[String] = None,
  utr: Option[String] = None,
  accountsOfficeReference: Option[String] = None,
  emailAddress: Option[String] = None
)

object ContractorSchemeLastVerification {
  given OFormat[ContractorSchemeLastVerification] = Json.format[ContractorSchemeLastVerification]
}
