/*
 * Copyright 2025 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{ControllerComponents, PlayBodyParsers, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.constructionindustryschemeexternalstub.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.requests.{CreateMonthlyReturnRequest, CreateNilMonthlyReturnRequest, InstanceIdRequest, UpdateMonthlyReturnItemRequest}
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.response.CreateNilMonthlyReturnResponse
import uk.gov.hmrc.constructionindustryschemeexternalstub.models.{EmployerReference, MonthlyReturn, UserMonthlyReturns, requests}
import uk.gov.hmrc.constructionindustryschemeexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class MonthlyReturnControllerSpec extends AnyFreeSpec with Matchers with ScalaFutures with MockitoSugar {

  ".retrieveMonthlyReturns" - {

    "returns 200 with MonthlyReturns for an unknown taxOfficeNumber /  taxOfficeReference" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(nonEmptyWrapper).toString)

      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("instanceId" -> "abc-123"))
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe OK
//      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe Json.toJson(nonEmptyWrapper)
    }

    "returns 200 with empty MonthlyReturns for taxOfficeNumber = 000" in new Setup {
      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("000", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(UserMonthlyReturns(Seq.empty)).toString)

      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("instanceId" -> "abc-123"))
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(UserMonthlyReturns(Seq.empty))
    }

    "returns 400 when JSON body is an empty object" in new Setup {
      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj())
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
    }

    "returns 400 when instanceId is missing" in new Setup {
      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("somethingElse" -> "oops"))
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
    }

    "propagates UpstreamErrorResponse (status & message) for taxOfficeNumber = 502" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("instanceId" -> "abc-123"))
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("instanceId" -> "abc-123"))
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }
  }

  ".createNilMonthlyReturn" - {

    "returns 201 with status when service succeeds for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {
      val request: CreateNilMonthlyReturnRequest   = CreateNilMonthlyReturnRequest(
        instanceId = "abc-123",
        taxYear = 2025,
        taxMonth = 2,
        decInformationCorrect = "Y",
        decNilReturnNoPayments = "Y"
      )
      val response: CreateNilMonthlyReturnResponse = CreateNilMonthlyReturnResponse(status = "STARTED")

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson(response).toString)

      val req: FakeRequest[CreateNilMonthlyReturnRequest] =
        FakeRequest(POST, "/formp-proxy/cis/monthly-return/nil/create").withBody(request)

      val res: Future[Result] = controller.createNilMonthlyReturn(req)

      status(res) mustBe CREATED
//      contentType(res) mustBe Some(JSON)
      contentAsJson(res) mustBe Json.obj("status" -> "STARTED")

    }

    "propagates UpstreamErrorResponse for taxOfficeNumber = 502" in new Setup {
      val request: CreateNilMonthlyReturnRequest = CreateNilMonthlyReturnRequest(
        instanceId = "abc-123",
        taxYear = 2025,
        taxMonth = 2,
        decInformationCorrect = "Y",
        decNilReturnNoPayments = "Y"
      )

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[CreateNilMonthlyReturnRequest] =
        FakeRequest(POST, "/formp-proxy/cis/monthly-return/nil/create").withBody(request)

      val res: Future[Result] = controller.createNilMonthlyReturn(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")

    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("instanceId" -> "abc-123"))
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

  }

  ".updateNilMonthlyReturn" - {

    "returns 204 when service succeeds for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {
      val body = Json.obj(
        "instanceId"             -> "abc-123",
        "taxYear"                -> 2025,
        "taxMonth"               -> 2,
        "amendment"              -> "N",
        "decInformationCorrect"  -> "Y",
        "decNilReturnNoPayments" -> "Y",
        "nilReturnIndicator"     -> "Y",
        "status"                 -> "STARTED"
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/formp-proxy/cis/monthly-return/update")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(body)

      val res: Future[Result] = controller.updateMonthlyReturn(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 400 when JSON body is invalid" in new Setup {
      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/formp-proxy/cis/monthly-return/update")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(Json.obj("instanceId" -> "abc-123"))

      val res: Future[Result] = controller.updateMonthlyReturn(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
    }
  }

  ".getSchemeEmail" - {

    "returns 200 with email when service succeeds for an unknown taxOfficeNumber / taxOfficeReference" in new Setup {

      val response: JsObject = Json.obj("email" -> "x@y.com")

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("200", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(response.toString())

      val req: FakeRequest[InstanceIdRequest] =
        FakeRequest(POST, "/formp-proxy/scheme/email").withBody(requests.InstanceIdRequest("abc-123"))
      val res: Future[Result]                 = controller.getSchemeEmail(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "email").asOpt[String] mustBe Some("x@y.com")
    }

    "returns 200 with null for taxOfficeNumber = 000" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("000", "")))

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(Json.toJson("email" -> "").toString)

      val req: FakeRequest[InstanceIdRequest] =
        FakeRequest(POST, "/formp-proxy/scheme/email").withBody(requests.InstanceIdRequest("abc-123"))
      val res: Future[Result]                 = controller.getSchemeEmail(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "email").toOption.flatMap(_.asOpt[String]) mustBe None
    }

    "propagates UpstreamErrorResponse" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("502", "")))

      val req: FakeRequest[InstanceIdRequest] =
        FakeRequest(POST, "/formp-proxy/scheme/email").withBody(requests.InstanceIdRequest("abc-123"))
      val res: Future[Result]                 = controller.getSchemeEmail(req)

      status(res) mustBe BAD_GATEWAY
      (contentAsJson(res) \ "message").as[String] must include("formp failed")
    }

    "returns 500 with generic message for taxOfficeNumber = 500" in new Setup {

      when(mockEnrolmentsHelper.contractorEnrolmentsOpt(any()))
        .thenReturn(Some(EmployerReference("500", "")))

      val req: FakeRequest[JsValue] = makeJsonRequest(Json.obj("instanceId" -> "abc-123"))
      val res: Future[Result]       = controller.retrieveMonthlyReturns(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(res) \ "message").as[String] mustBe "Unexpected error"
    }

  }

  ".createMonthlyReturn" - {

    "returns 201 Created for a valid request body" in new Setup {
      val requestBody = CreateMonthlyReturnRequest(
        instanceId = "abc-123",
        taxYear = 2025,
        taxMonth = 2
      )

      val request: FakeRequest[CreateMonthlyReturnRequest] =
        FakeRequest(POST, "/formp-proxy/monthly-return/create")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(requestBody)

      val result: Future[Result] = controller.createMonthlyReturn(request)

      status(result) mustBe CREATED
      contentAsString(result) mustBe ""
    }
  }

  ".retrieveUnsubmittedMonthlyReturns" - {

    "returns 200 when JSON body is valid" in new Setup {
      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(
          """{"scheme":{"schemeId":1,"instanceId":"123","accountsOfficeReference":"a","taxOfficeNumber":"1","taxOfficeReference":"b"},"monthlyReturn":[]}"""
        )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/monthly-return")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(Json.obj("instanceId" -> "abc-123"))

      val res = controller.retrieveUnsubmittedMonthlyReturns(req)

      status(res) mustBe OK
    }
  }

  ".updateMonthlyReturnItem" - {

    "returns 204 NO_CONTENT when payload is valid" in new Setup {

      val validRequest =
        UpdateMonthlyReturnItemRequest(
          instanceId = "abc-123",
          taxYear = 2025,
          taxMonth = 2,
          amendment = "N",
          itemResourceReference = 999L,
          totalPayments = "1000.00",
          costOfMaterials = "200.00",
          totalDeducted = "80.00",
          subcontractorName = "ABC Ltd",
          verificationNumber = "V123456"
        )

      val req = FakeRequest(POST, "/cis/monthly-return-item/update")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(Json.toJson(validRequest))

      val result = controller.updateMonthlyReturnItem()(req)

      status(result) mustBe NO_CONTENT
      contentAsString(result) mustBe ""
    }

    "returns 400 BAD_REQUEST when payload is invalid" in new Setup {

      val invalidBody = Json.obj(
        "instanceId" -> "abc-123"
      )

      val req = FakeRequest(POST, "/cis/monthly-return-item/update")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(invalidBody)

      val result = controller.updateMonthlyReturnItem()(req)

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] mustBe "Invalid payload"
      (contentAsJson(result) \ "errors").isDefined mustBe true
    }
  }

  ".getMonthlyReturnForEdit" - {

    "returns 200 when JSON body is valid" in new Setup {
      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn("""{"ok":true}""")

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/formp-proxy/cis/monthly-return-edit")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(
            Json.obj(
              "instanceId" -> "abc-123",
              "taxYear"    -> 2025,
              "taxMonth"   -> 1
            )
          )

      val res: Future[Result] = controller.getMonthlyReturnForEdit(req)

      status(res) mustBe OK
    }

    "returns 400 when JSON body is invalid" in new Setup {
      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/formp-proxy/cis/monthly-return-edit")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(
            Json.obj(
              "instanceId" -> "abc-123"
            )
          )

      val res: Future[Result] = controller.getMonthlyReturnForEdit(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
    }
  }

  "MonthlyReturnController syncMonthlyReturnItems" - {

    "returns 204 when JSON body is valid" in new Setup {

      val body = Json.obj(
        "instanceId"               -> "abc-123",
        "taxYear"                  -> 2025,
        "taxMonth"                 -> 1,
        "amendment"                -> "N",
        "createResourceReferences" -> Json.arr(5, 6),
        "deleteResourceReferences" -> Json.arr(1, 2)
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/cis/monthly-return-item/sync")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(body)

      val res: Future[Result] = controller.syncMonthlyReturnItems(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 400 when JSON body is invalid" in new Setup {

      val body = Json.obj(
        "instanceId" -> "abc-123"
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/cis/monthly-return-item/sync")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(body)

      val res: Future[Result] = controller.syncMonthlyReturnItems(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
    }
  }

  "MonthlyReturnController deleteMonthlyReturnItem" - {

    "returns 204 when JSON body is valid" in new Setup {

      val body = Json.obj(
        "instanceId"        -> "abc-123",
        "taxYear"           -> 2025,
        "taxMonth"          -> 1,
        "amendment"         -> "N",
        "resourceReference" -> 12345
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/cis/monthly-return-item/delete")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(body)

      val res: Future[Result] = controller.deleteMonthlyReturnItem(req)

      status(res) mustBe NO_CONTENT
      contentAsString(res) mustBe ""
    }

    "returns 400 when JSON body is invalid" in new Setup {

      val body = Json.obj(
        "instanceId" -> "abc-123"
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/cis/monthly-return-item/delete")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(body)

      val res: Future[Result] = controller.deleteMonthlyReturnItem(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
    }
  }

  ".retrieveSubmittedMonthlyReturns" - {

    "returns 200 when JSON body is valid" in new Setup {
      val body = Json.obj(
        "instanceId" -> "abc-123",
        "taxYear"    -> 2025,
        "taxMonth"   -> 1,
        "amendment"  -> "N"
      )

      when(mockResourceHelper.resourceAsString(any()))
        .thenReturn(
          """{"scheme":{"schemeId":1,"instanceId":"123","accountsOfficeReference":"a","taxOfficeNumber":"1","taxOfficeReference":"b"},"monthlyReturn":[]}"""
        )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/monthly-return")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(body)

      val res = controller.retrieveSubmittedMonthlyReturns(req)

      status(res) mustBe OK
    }

    "returns 400 when JSON body is invalid" in new Setup {

      val body = Json.obj(
        "instanceId" -> "abc-123"
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(POST, "/cis/monthly-return-item/delete")
          .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
          .withBody(body)

      val res: Future[Result] = controller.retrieveSubmittedMonthlyReturns(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid JSON body"
    }
  }

  private trait Setup {
    implicit val ec: ExecutionContext    = scala.concurrent.ExecutionContext.global
    private val cc: ControllerComponents = stubControllerComponents()
    private val parsers: PlayBodyParsers = cc.parsers
    private def fakeAuth: AuthAction     = new FakeAuthAction(parsers)

    val mockResourceHelper: ResourceHelper     = mock[ResourceHelper]
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]

    val controller = new MonthlyReturnController(fakeAuth, mockResourceHelper, mockEnrolmentsHelper, cc)

    def makeJsonRequest(body: JsValue): FakeRequest[JsValue] =
      FakeRequest(POST, "/formp-proxy/monthly-returns")
        .withHeaders(CONTENT_TYPE -> JSON, ACCEPT -> JSON)
        .withBody(body)

    private def mkReturn(id: Long, month: Int, year: Int = 2025): MonthlyReturn =
      MonthlyReturn(
        monthlyReturnId = id,
        taxYear = year,
        taxMonth = month,
        nilReturnIndicator = Some("N"),
        decEmpStatusConsidered = Some("Y"),
        decAllSubsVerified = Some("Y"),
        decInformationCorrect = Some("Y"),
        decNoMoreSubPayments = Some("N"),
        decNilReturnNoPayments = Some("N"),
        status = Some("SUBMITTED"),
        lastUpdate = Some(LocalDateTime.parse("2025-01-01T00:00:00")),
        amendment = Some("N"),
        supersededBy = None
      )

    val nonEmptyWrapper: UserMonthlyReturns =
      UserMonthlyReturns(Seq(mkReturn(66666L, 1), mkReturn(66667L, 7)))
  }

}
