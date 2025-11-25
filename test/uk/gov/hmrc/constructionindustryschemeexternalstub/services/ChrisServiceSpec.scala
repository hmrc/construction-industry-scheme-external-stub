/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.constructionindustryschemeexternalstub.services

import com.typesafe.config.ConfigFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import uk.gov.hmrc.constructionindustryschemeexternalstub.config.AppConfig

class ChrisServiceSpec extends AnyWordSpec with Matchers with OneInstancePerTest {
//  private val submitFATCAFilingSuccessXML = <xml></xml>

  private def submitCorrelationTestSuccessXML(clazz :String, corrId: String) =
    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>{clazz}</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>{corrId}</CorrelationID>
          <GatewayTimestamp>2014-04-06T21:58:38.935</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails />
      </Header>
      <GovTalkDetails>
      </GovTalkDetails>
      <Body>
      </Body>
    </GovTalkMessage>

//  private val submitSAFilingSuccessXML =
//      <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//          <EnvelopeVersion>2.0</EnvelopeVersion>
//          <Header>
//              <MessageDetails>
//                  <Class>HMRC-SA-SA100</Class>
//                  <Qualifier>request</Qualifier>
//                  <Function>submit</Function>
//                  <TransactionID>141500000000000000000000000174</TransactionID>
//                  <CorrelationID>01234567890123456789012345678912</CorrelationID>
//                  <Transformation>XML</Transformation>
//                  <GatewayTest>1</GatewayTest>
//                  <GatewayTimestamp>2016-03-08T11:28:53</GatewayTimestamp>
//              </MessageDetails>
//              <SenderDetails/>
//          </Header>
//          <GovTalkDetails>
//              <Keys>
//                  <Key Type="UTR">1121766916</Key>
//              </Keys>
//              <ChannelRouting>
//                  <Channel>
//                      <URI>0046</URI>
//                      <Product>EzGov HMRC-SA-SA100</Product>
//                      <Version>3.4</Version>
//                  </Channel>
//              </ChannelRouting>
//          </GovTalkDetails>
//
//          <Body>
//              <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/SA/SA100/14-15/1">
//                  <IRheader>
//                      <Keys>
//                          <Key Type="UTR">1121766916</Key>
//                      </Keys>
//                      <PeriodEnd>2015-04-05</PeriodEnd>
//                      <Principal>
//                          <Contact>
//                              <Email>test@3jl.com</Email>
//                          </Contact>
//                      </Principal>
//                      <IRmark Type="generic">jAsSIEVkibd99Sx3p3xw0Fc6VRw=</IRmark>
//                      <Sender>Individual</Sender>
//                  </IRheader>
//                  <MTR>
//                      <SA100>
//                          <YourPersonalDetails>
//                              <DateOfBirth>1977-12-01</DateOfBirth>
//                              <NewAddress>
//                                  <AddressLine1>1 gWUR street</AddressLine1>
//                                  <AddressLine2>London</AddressLine2>
//                                  <Postcode>SE1 9NN</Postcode>
//                              </NewAddress>
//                              <NationalInsuranceNumber>QQ123456A</NationalInsuranceNumber>
//                          </YourPersonalDetails>
//                          <YourTaxReturn>
//                              <EmploymentSchedule>yes</EmploymentSchedule>
//                              <NumberOfEmploymentSchedules>1</NumberOfEmploymentSchedules>
//                          </YourTaxReturn>
//                          <StudentLoanRepayments/>
//                          <Income>
//                              <OtherUKIncome/>
//                          </Income>
//                          <TaxReliefs>
//                              <Pensions>
//                                  <RetirementAnnuityContractPayments>1200.00</RetirementAnnuityContractPayments>
//                              </Pensions>
//                              <BlindPersonsAllowance/>
//                          </TaxReliefs>
//                          <FinishingYourTaxReturn>
//                              <NotPaidEnough>
//                                  <TaxOwedNotToBeCodedOut>yes</TaxOwedNotToBeCodedOut>
//                                  <NonPAYEIncomeNotToBeCodedOut>yes</NonPAYEIncomeNotToBeCodedOut>
//                              </NotPaidEnough>
//                              <PaidTooMuch>
//                                  <PaymentDetails/>
//                                  <NoBankOrBuildingSocietyAccount>yes</NoBankOrBuildingSocietyAccount>
//                              </PaidTooMuch>
//                              <SigningYourForm/>
//                          </FinishingYourTaxReturn>
//                      </SA100>
//                      <SA102>
//                          <Employment>
//                              <PayFromEmployment>33254.00</PayFromEmployment>
//                              <TaxTakenOffPay>2000.00</TaxTakenOffPay>
//                              <EmployerPAYEReference>326/429</EmployerPAYEReference>
//                              <EmployersName>SomeEmployer</EmployersName>
//                              <CompanyDirector>no</CompanyDirector>
//                          </Employment>
//                      </SA102>
//                      <SA110>
//                          <SelfAssessment>
//                              <TotalTaxEtcDue>4410.80</TotalTaxEtcDue>
//                              <Class4NICsDue>0.00</Class4NICsDue>
//                          </SelfAssessment>
//                          <UnderpaidTax>
//                              <UnderpaidTaxForEarlierYearsIncludedInCode>0.00</UnderpaidTaxForEarlierYearsIncludedInCode>
//                              <UnderpaidTaxForYearIncludedInFutureCode>0.00</UnderpaidTaxForYearIncludedInFutureCode>
//                              <OutstandingDebtCodedOutAmount>2800.00</OutstandingDebtCodedOutAmount>
//                          </UnderpaidTax>
//                          <PaymentsOnAccount/>
//                          <AdjustmentsToTaxDue/>
//                      </SA110>
//                      <TaxpayerName>zOLs8bJAQl DdqGVOtbPG</TaxpayerName>
//                      <Declaration>
//                          <IndividualDeclaration>yes</IndividualDeclaration>
//                      </Declaration>
//                  </MTR>
//              </IRenvelope>
//          </Body>
//      </GovTalkMessage>

//  private val submitSAFilingPendingXML =
//    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//      <EnvelopeVersion>2.0</EnvelopeVersion>
//      <Header>
//        <MessageDetails>
//          <Class>HMRC-SA-SA100</Class>
//          <Qualifier>request</Qualifier>
//          <Function>submit</Function>
//          <TransactionID>141500000000000000000000000174</TransactionID>
//          <CorrelationID/>
//          <Transformation>XML</Transformation>
//          <GatewayTest>1</GatewayTest>
//          <GatewayTimestamp>2016-03-08T11:28:53</GatewayTimestamp>
//        </MessageDetails>
//        <SenderDetails/>
//      </Header>
//      <GovTalkDetails>
//        <Keys>
//          <Key Type="UTR">2321766916</Key>
//        </Keys>
//        <ChannelRouting>
//          <Channel>
//            <URI>0046</URI>
//            <Product>EzGov HMRC-SA-SA100</Product>
//            <Version>3.4</Version>
//          </Channel>
//        </ChannelRouting>
//      </GovTalkDetails>
//
//      <Body>
//        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/SA/SA100/14-15/1">
//          <IRheader>
//            <Keys>
//              <Key Type="UTR">1121766916</Key>
//            </Keys>
//            <PeriodEnd>2015-04-05</PeriodEnd>
//            <Principal>
//              <Contact>
//                <Email>test@3jl.com</Email>
//              </Contact>
//            </Principal>
//            <IRmark Type="generic">jAsSIEVkibd99Sx3p3xw0Fc6VRw=</IRmark>
//            <Sender>Individual</Sender>
//          </IRheader>
//          <MTR>
//            <SA100>
//              <YourPersonalDetails>
//                <DateOfBirth>1977-12-01</DateOfBirth>
//                <NewAddress>
//                  <AddressLine1>1 gWUR street</AddressLine1>
//                  <AddressLine2>London</AddressLine2>
//                  <Postcode>SE1 9NN</Postcode>
//                </NewAddress>
//                <NationalInsuranceNumber>QQ123456A</NationalInsuranceNumber>
//              </YourPersonalDetails>
//              <YourTaxReturn>
//                <EmploymentSchedule>yes</EmploymentSchedule>
//                <NumberOfEmploymentSchedules>1</NumberOfEmploymentSchedules>
//              </YourTaxReturn>
//              <StudentLoanRepayments/>
//              <Income>
//                <OtherUKIncome/>
//              </Income>
//              <TaxReliefs>
//                <Pensions>
//                  <RetirementAnnuityContractPayments>1200.00</RetirementAnnuityContractPayments>
//                </Pensions>
//                <BlindPersonsAllowance/>
//              </TaxReliefs>
//              <FinishingYourTaxReturn>
//                <NotPaidEnough>
//                  <TaxOwedNotToBeCodedOut>yes</TaxOwedNotToBeCodedOut>
//                  <NonPAYEIncomeNotToBeCodedOut>yes</NonPAYEIncomeNotToBeCodedOut>
//                </NotPaidEnough>
//                <PaidTooMuch>
//                  <PaymentDetails/>
//                  <NoBankOrBuildingSocietyAccount>yes</NoBankOrBuildingSocietyAccount>
//                </PaidTooMuch>
//                <SigningYourForm>
//                  <OtherInformationSpace>DELAYEDSUBMISSION</OtherInformationSpace>
//                </SigningYourForm>
//              </FinishingYourTaxReturn>
//            </SA100>
//            <SA102>
//              <Employment>
//                <PayFromEmployment>33254.00</PayFromEmployment>
//                <TaxTakenOffPay>2000.00</TaxTakenOffPay>
//                <EmployerPAYEReference>326/429</EmployerPAYEReference>
//                <EmployersName>SomeEmployer</EmployersName>
//                <CompanyDirector>no</CompanyDirector>
//              </Employment>
//            </SA102>
//            <SA110>
//              <SelfAssessment>
//                <TotalTaxEtcDue>4410.80</TotalTaxEtcDue>
//                <Class4NICsDue>0.00</Class4NICsDue>
//              </SelfAssessment>
//              <UnderpaidTax>
//                <UnderpaidTaxForEarlierYearsIncludedInCode>0.00</UnderpaidTaxForEarlierYearsIncludedInCode>
//                <UnderpaidTaxForYearIncludedInFutureCode>0.00</UnderpaidTaxForYearIncludedInFutureCode>
//                <OutstandingDebtCodedOutAmount>2800.00</OutstandingDebtCodedOutAmount>
//              </UnderpaidTax>
//              <PaymentsOnAccount/>
//              <AdjustmentsToTaxDue/>
//            </SA110>
//            <TaxpayerName>zOLs8bJAQl DdqGVOtbPG</TaxpayerName>
//            <Declaration>
//              <IndividualDeclaration>yes</IndividualDeclaration>
//            </Declaration>
//          </MTR>
//        </IRenvelope>
//      </Body>
//    </GovTalkMessage>


//  private val vatSubmitXml = (vatDueAmount: String) => <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//    <EnvelopeVersion>2.0</EnvelopeVersion>
//    <Header>
//      <MessageDetails>
//        <Class>HMRC-VAT-DEC</Class>
//        <Qualifier>request</Qualifier>
//        <Function>submit</Function>
//        <TransactionID>141500000000000000000000000174</TransactionID>
//        <CorrelationID/>
//        <Transformation>XML</Transformation>
//        <GatewayTimestamp>2016-04-22T11:37:50</GatewayTimestamp>
//      </MessageDetails>
//      <SenderDetails/>
//    </Header>
//    <GovTalkDetails>
//      <Keys>
//        <Key Type="VATRegNo">999902541</Key>
//      </Keys>
//      <ChannelRouting>
//        <Channel>
//          <URI>1352</URI>
//          <Product>ASPIRE HMRC-VAT100-DEC</Product>
//          <Version>1.0</Version>
//        </Channel>
//      </ChannelRouting>
//    </GovTalkDetails>
//    <Body>
//      <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/vat/vatdeclaration/2">
//        <IRheader>
//          <Keys>
//            <Key Type="VATRegNo">999902541</Key>
//          </Keys>
//          <PeriodID>2009-04</PeriodID>
//          <PeriodStart>2009-02-01</PeriodStart>
//          <PeriodEnd>2009-04-30</PeriodEnd>
//          <Principal>
//            <Contact>
//              <Email>prestigeworldwide2@hotmail.com</Email>
//            </Contact>
//          </Principal>
//          <IRmark Type="generic">OwPHdTDDFfgJSu5Ww++qZ+vswtU=</IRmark>
//          <Sender>Individual</Sender>
//        </IRheader>
//        <VATDeclarationRequest>
//          <VATDueOnOutputs>{vatDueAmount}</VATDueOnOutputs>
//          <VATDueOnECAcquisitions>100.00</VATDueOnECAcquisitions>
//          <TotalVAT>200.00</TotalVAT>
//          <VATReclaimedOnInputs>10.00</VATReclaimedOnInputs>
//          <NetVAT>190.00</NetVAT>
//          <NetSalesAndOutputs>1000</NetSalesAndOutputs>
//          <NetPurchasesAndInputs>1000</NetPurchasesAndInputs>
//          <NetECSupplies>1000</NetECSupplies>
//          <NetECAcquisitions>1000</NetECAcquisitions>
//        </VATDeclarationRequest>
//      </IRenvelope>
//    </Body>
//  </GovTalkMessage>
  
//   private val vatSubmitXmlWithID = (vatDueAmount: String, irMark: String, correlationId: String) => <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//    <EnvelopeVersion>2.0</EnvelopeVersion>
//    <Header>
//      <MessageDetails>
//        <Class>HMRC-VAT-DEC</Class>
//        <Qualifier>request</Qualifier>
//        <Function>submit</Function>
//        <TransactionID>141500000000000000000000000177</TransactionID>
//        <CorrelationID>{correlationId}</CorrelationID>
//        <Transformation>XML</Transformation>
//        <GatewayTimestamp>2016-04-22T11:37:50</GatewayTimestamp>
//      </MessageDetails>
//      <SenderDetails/>
//    </Header>
//    <GovTalkDetails>
//      <Keys>
//        <Key Type="VATRegNo">999902541</Key>
//      </Keys>
//      <ChannelRouting>
//        <Channel>
//          <URI>1352</URI>
//          <Product>ASPIRE HMRC-VAT100-DEC</Product>
//          <Version>1.0</Version>
//        </Channel>
//      </ChannelRouting>
//    </GovTalkDetails>
//    <Body>
//      <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/vat/vatdeclaration/2">
//        <IRheader>
//          <Keys>
//            <Key Type="VATRegNo">999902541</Key>
//          </Keys>
//          <PeriodID>2009-04</PeriodID>
//          <PeriodStart>2009-02-01</PeriodStart>
//          <PeriodEnd>2009-04-30</PeriodEnd>
//          <Principal>
//            <Contact>
//              <Email>prestigeworldwide2@hotmail.com</Email>
//            </Contact>
//          </Principal>
//          <IRmark Type="generic">{irMark}</IRmark>
//          <Sender>Individual</Sender>
//        </IRheader>
//        <VATDeclarationRequest>
//          <VATDueOnOutputs>{vatDueAmount}</VATDueOnOutputs>
//          <VATDueOnECAcquisitions>100.00</VATDueOnECAcquisitions>
//          <TotalVAT>200.00</TotalVAT>
//          <VATReclaimedOnInputs>10.00</VATReclaimedOnInputs>
//          <NetVAT>190.00</NetVAT>
//          <NetSalesAndOutputs>1000</NetSalesAndOutputs>
//          <NetPurchasesAndInputs>1000</NetPurchasesAndInputs>
//          <NetECSupplies>1000</NetECSupplies>
//          <NetECAcquisitions>1000</NetECAcquisitions>
//        </VATDeclarationRequest>
//      </IRenvelope>
//    </Body>
//  </GovTalkMessage>

//  private def payeSubmitXml(p11DCount: String, p46Count: String,
//                       testValue: String, correlationId: String,
//                       amountDueDescription: String = "") = <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//
//    <EnvelopeVersion>2.0</EnvelopeVersion>
//    <Header>
//      <MessageDetails>
//        <Class>IR-PAYE-EXB</Class>
//        <Qualifier>request</Qualifier>
//        <Function>submit</Function>
//        <TransactionID>141500000000000000000000000174</TransactionID>
//        <CorrelationID>{correlationId}</CorrelationID>
//        <Transformation>XML</Transformation>
//        <GatewayTimestamp>2016-04-22T11:37:50</GatewayTimestamp>
//      </MessageDetails>
//      <SenderDetails/>
//    </Header>
//    <GovTalkDetails/>
//    <Body>
//      <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/paye">
//        <IRheader>
//          <Keys>
//            <Key Type="VATRegNo">999902541</Key>
//          </Keys>
//          <PeriodID>2009-04</PeriodID>
//          <PeriodStart>2009-02-01</PeriodStart>
//          <PeriodEnd>2009-04-30</PeriodEnd>
//          <Principal>
//            <Contact>
//              <Email>prestigeworldwide2@hotmail.com</Email>
//            </Contact>
//          </Principal>
//          <IRmark Type="generic">OwPHdTDDFfgJSu5Ww++qZ+vswtU=</IRmark>
//          <Sender>Organisation</Sender>
//        </IRheader>
//        <ExpensesAndBenefits>
//          <P11DrecordCount>{p11DCount}</P11DrecordCount>
//          <P46CarRecordCount>{p46Count}</P46CarRecordCount>
//          <P11Db>
//            <Class1AcontributionsDue>
//              <Adjustments>
//                <AmountDue>
//                  <Description>{amountDueDescription}</Description>
//                  <Adjustment>1.00</Adjustment>
//                </AmountDue>
//              </Adjustments>
//            </Class1AcontributionsDue>
//          </P11Db>
//          <P9D><TotalExpenses>{testValue}</TotalExpenses></P9D>
//          <P11D><Vans><CashEquiv>{testValue}</CashEquiv></Vans></P11D>
//          <P46Car><CarDetails><MakeAndModel>{testValue}</MakeAndModel></CarDetails></P46Car>
//        </ExpensesAndBenefits>
//      </IRenvelope>
//    </Body>
//  </GovTalkMessage>

  private val pollXML =
    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>HMRC-SA-SA100</Class>
          <Qualifier>poll</Qualifier>
          <Function>submit</Function>
          <TransactionID>141500000000000000000000000174</TransactionID>
          <CorrelationID>D2C723C1F1BE7756619F751FE47D3308</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTimestamp>2016-03-08T11:28:53</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails/>
      </Header>
      <GovTalkDetails>
        <Keys/>
        <ChannelRouting/>
      </GovTalkDetails>
      <Body/>
    </GovTalkMessage>
    
//  private val vatPollXML = (correlationId: String) =>
//    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//      <EnvelopeVersion>2.0</EnvelopeVersion>
//      <Header>
//        <MessageDetails>
//          <Class>HMRC-VAT-DEC</Class>
//          <Qualifier>poll</Qualifier>
//          <Function>submit</Function>
//          <TransactionID>141500000000000000000000000177</TransactionID>
//          <CorrelationID>{correlationId}</CorrelationID>
//          <Transformation>XML</Transformation>
//          <GatewayTimestamp>2016-03-08T11:28:53</GatewayTimestamp>
//        </MessageDetails>
//        <SenderDetails/>
//      </Header>
//      <GovTalkDetails>
//        <Keys/>
//        <ChannelRouting/>
//      </GovTalkDetails>
//      <Body/>
//    </GovTalkMessage>

//  private val submitCHARITIESSuccessFilingXML =
//    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//      <EnvelopeVersion>2.0</EnvelopeVersion>
//      <Header>
//        <MessageDetails>
//          <Class>HMRC-CHAR-CLM</Class>
//          <Qualifier>response</Qualifier>
//          <Function>submit</Function>
//          <CorrelationID>D2C723C1F1BE7756619F751FE47D3308</CorrelationID>
//          <GatewayTimestamp>2014-04-06T21:58:38.935</GatewayTimestamp>
//        </MessageDetails>
//        <SenderDetails />
//      </Header>
//      <GovTalkDetails>
//        <Keys>
//          <Key Type="SessionID">dit2ganode1_SMS::91cbafa2dd7a4df4efea799647c60a5f</Key>
//          <Key Type="CredentialID">0000000712998339</Key>
//          <Key Type="CHARID">GP12351</Key>
//        </Keys>
//      </GovTalkDetails>
//      <Body>
//        <forms>
//          <form formId="65542499" userName="Sanjay Charity Bogaram" isAgent="false">
//            <formdata uri="/about-the-charity">
//              <map>
//                <areYouClaimingGiftAid>true</areYouClaimingGiftAid>
//                <areYouClaimingTaxDeducted>false</areYouClaimingTaxDeducted>
//                <areYouClaimingUnderGasds>false</areYouClaimingUnderGasds>
//                <connectedToAnyOtherCharities></connectedToAnyOtherCharities>
//                <donationsCollectedInCommunityBuildings></donationsCollectedInCommunityBuildings>
//                <donationsReceivedByOrganisation></donationsReceivedByOrganisation>
//                <makingAnAdjustmentToPrevious></makingAnAdjustmentToPrevious>
//              </map>
//            </formdata>
//            <formdata uri="/about-the-organisation">
//              <map>
//                <areYouACorporateTrustee>false</areYouACorporateTrustee>
//                <asYouSelectedOtherProvideDetails></asYouSelectedOtherProvideDetails>
//                <charityRegistrationNumber>1234</charityRegistrationNumber>
//                <corporateTrusteePostcode></corporateTrusteePostcode>
//                <daytimeTelephoneNumber>12345</daytimeTelephoneNumber>
//                <firstName>Brian</firstName>
//                <lastName>Cunningham</lastName>
//                <nameOfCharityRegulator>EnglandAndWales</nameOfCharityRegulator>
//                <nameOfCorporateTrustee></nameOfCorporateTrustee>
//                <notCorporateTrusteePostcode>AB12 3YZ</notCorporateTrusteePostcode>
//                <title>Mr</title>
//              </map>
//            </formdata>
//            <formdata uri="/repayment-claim-summary">
//              <map>
//                <gas-schedule>complete</gas-schedule>
//              </map>
//            </formdata>
//            <formdata uri="/submission-declaration">
//              <map>
//                <includedAnyAdjustmentsInClaimPrompt>Adjustments Text</includedAnyAdjustmentsInClaimPrompt>
//                <understandFalseStatements>on</understandFalseStatements>
//              </map>
//            </formdata>
//          </form>
//        </forms>
//      </Body>
//    </GovTalkMessage>

  private val submitCISMRSuccessFilingXML =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>IR-CIS-CIS300MR</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000001</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTest>1</GatewayTest>
          <GatewayTimestamp>2017-04-06T08:46:08.081</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails>
          <IDAuthentication>
            <SenderID>ISV147</SenderID>
            <Authentication>
              <Method>clear</Method>
              <Role>principal</Role>
              <Value>xxxxx</Value>
            </Authentication>
          </IDAuthentication>
        </SenderDetails>
      </Header>
      <GovTalkDetails>
        <Keys>
          <Key Type='TaxOfficeNumber'>123</Key>
          <Key Type='TaxOfficeReference'>GL02</Key>
        </Keys>
        <TargetDetails>
          <Organisation>IR</Organisation>
        </TargetDetails>
        <ChannelRouting>
          <Channel>
            <Name>0147</Name>
            <Product>sdsteam</Product>
            <Version>1</Version>
          </Channel>
        </ChannelRouting>
      </GovTalkDetails>
      <Body>
        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/CISreturn">
          <IRheader>
            <TestMessage>0</TestMessage>
            <Keys>
              <Key Type='TaxOfficeNumber'>123</Key>
              <Key Type='TaxOfficeReference'>GL02</Key>
            </Keys>
            <PeriodEnd>2007-05-05</PeriodEnd>
            <DefaultCurrency>GBP</DefaultCurrency>
            <IRmark Type='generic'>mF6S2IDEh8MWsWHx9S7ECcPQrB0=</IRmark>
            <Sender>Individual</Sender>
          </IRheader>
          <CISreturn>
            <Contractor>
              <UTR>2325648152</UTR>
              <AOref>123PP87654321</AOref>
            </Contractor>
            <NilReturn>yes</NilReturn>
            <Declarations>
              <InformationCorrect>yes</InformationCorrect>
            </Declarations>
          </CISreturn>
        </IRenvelope>
      </Body>
    </GovTalkMessage>

  private val submitCISMRAcknowledgeFilingXML =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>IR-CIS-CIS300MR</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000001</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTest>1</GatewayTest>
          <GatewayTimestamp>2017-04-06T08:46:08.081</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails>
          <IDAuthentication>
            <SenderID>ISV147</SenderID>
            <Authentication>
              <Method>clear</Method>
              <Role>principal</Role>
              <Value>xxxxx</Value>
            </Authentication>
          </IDAuthentication>
        </SenderDetails>
      </Header>
      <GovTalkDetails>
        <Keys>
          <Key Type='TaxOfficeNumber'>123</Key>
          <Key Type='TaxOfficeReference'>GL01</Key>
        </Keys>
        <TargetDetails>
          <Organisation>IR</Organisation>
        </TargetDetails>
        <ChannelRouting>
          <Channel>
            <Name>0147</Name>
            <Product>sdsteam</Product>
            <Version>1</Version>
          </Channel>
        </ChannelRouting>
      </GovTalkDetails>
      <Body>
        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/CISreturn">
          <IRheader>
            <TestMessage>0</TestMessage>
            <Keys>
              <Key Type='TaxOfficeNumber'>123</Key>
              <Key Type='TaxOfficeReference'>GL01</Key>
            </Keys>
            <PeriodEnd>2007-05-05</PeriodEnd>
            <DefaultCurrency>GBP</DefaultCurrency>
            <IRmark Type='generic'>mF6S2IDEh8MWsWHx9S7ECcPQrB0=</IRmark>
            <Sender>Individual</Sender>
          </IRheader>
          <CISreturn>
            <Contractor>
              <UTR>2325648152</UTR>
              <AOref>123PP87654321</AOref>
            </Contractor>
            <NilReturn>yes</NilReturn>
            <Declarations>
              <InformationCorrect>yes</InformationCorrect>
            </Declarations>
          </CISreturn>
        </IRenvelope>
      </Body>
    </GovTalkMessage>

  private val submitUnknownCISMessage =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>UNKNOWN Message</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000001</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTest>1</GatewayTest>
          <GatewayTimestamp>2017-04-06T08:46:08.081</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails>
          <IDAuthentication>
            <SenderID>ISV147</SenderID>
            <Authentication>
              <Method>clear</Method>
              <Role>principal</Role>
              <Value>xxxxx</Value>
            </Authentication>
          </IDAuthentication>
        </SenderDetails>
      </Header>
      <GovTalkDetails/>
    </GovTalkMessage>

  private val submitCISVerifyXML =
    <GovTalkMessage xmlns='http://www.govtalk.gov.uk/CM/envelope'>
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>IR-CIS-VERIFY</Class>
          <Qualifier>request</Qualifier>
          <Function>submit</Function>
          <CorrelationID>0000000007</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTest>1</GatewayTest>
          <GatewayTimestamp>2017-04-06T08:46:08.081</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails>
          <IDAuthentication>
            <SenderID>ISV147</SenderID>
            <Authentication>
              <Method>clear</Method>
              <Role>principal</Role>
              <Value>xxxxx</Value>
            </Authentication>
          </IDAuthentication>
        </SenderDetails>
      </Header>
      <GovTalkDetails>
        <Keys>
          <Key Type='TaxOfficeNumber'>123</Key>
          <Key Type='TaxOfficeReference'>GL01</Key>
        </Keys>
        <TargetDetails>
          <Organisation>IR</Organisation>
        </TargetDetails>
        <ChannelRouting>
          <Channel>
            <Name>0147</Name>
            <Product>sdsteam</Product>
            <Version>1</Version>
          </Channel>
        </ChannelRouting>
      </GovTalkDetails>
      <Body>
        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/CISrequest">
          <IRheader>
            <TestMessage>0</TestMessage>
            <Keys>
              <Key Type='TaxOfficeNumber'>123</Key>
              <Key Type='TaxOfficeReference'>GL01</Key>
            </Keys>
            <PeriodEnd>2007-05-05</PeriodEnd>
            <DefaultCurrency>GBP</DefaultCurrency>
            <IRmark Type='generic'>RPu0RnkpsfKnCZaGbpn46VoTvvM=</IRmark>
            <Sender>Individual</Sender>
          </IRheader>
          <CISrequest>
            <Contractor>
              <UTR>2325648152</UTR>
              <AOref>123PP87654321</AOref>
            </Contractor>
            <Subcontractor>
              <Action>match</Action>
              <Type>soletrader</Type>
              <Name>
                <Fore>John</Fore>
                <Sur>Smith</Sur>
              </Name>
              <UTR>2325648152</UTR>
              <NINO>YW000003A</NINO>
            </Subcontractor>
            <Subcontractor>
              <Action>verify</Action>
              <Type>soletrader</Type>
              <Name>
                <Fore>Fred</Fore>
                <Fore>George</Fore>
                <Sur>Bingham</Sur>
              </Name>
              <UTR>2345678901</UTR>
              <Address>
                <Line>15 High Street</Line>
                <PostCode>RH11 8BG</PostCode>
                <Country>England</Country>
              </Address>
            </Subcontractor>
            <Declaration>yes</Declaration>
          </CISrequest>
        </IRenvelope>
      </Body>
    </GovTalkMessage>

//  private val submitCHARITIEFailureFilingXML =
//    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
//      <EnvelopeVersion>2.0</EnvelopeVersion>
//      <Header>
//        <MessageDetails>
//          <Class>HMRC-CHAR-CLM</Class>
//          <Qualifier>error</Qualifier>
//          <Function>submit</Function>
//          <CorrelationID>0</CorrelationID>
//          <GatewayTimestamp>2014-04-06T21:58:38.935</GatewayTimestamp>
//        </MessageDetails>
//        <SenderDetails />
//      </Header>
//      <GovTalkDetails>
//        <Keys/>
//        <GovTalkErrors>
//          <Error>
//            <RaisedBy>ChRIS</RaisedBy>
//            <Number>3001</Number>
//            <Type>business</Type>
//            <Text>Your submission failed due to business validation errors. Please see below for details.</Text>
//          </Error>
//        </GovTalkErrors>
//      </GovTalkDetails>
//      <Body>
//        <forms>
//          <form formId="65542400" userName="Test charity" isAgent="false">
//            <formdata uri="/about-the-organisation">
//              <map>
//                <areYouACorporateTrustee>false</areYouACorporateTrustee>
//                <asYouSelectedOtherProvideDetails></asYouSelectedOtherProvideDetails>
//                <charityRegistrationNumber>1234</charityRegistrationNumber>
//                <corporateTrusteePostcode></corporateTrusteePostcode>
//                <daytimeTelephoneNumber>12345</daytimeTelephoneNumber>
//                <firstName>Brian</firstName>
//                <lastName>Cunningham</lastName>
//                <nameOfCharityRegulator>EnglandAndWales</nameOfCharityRegulator>
//                <nameOfCorporateTrustee></nameOfCorporateTrustee>
//                <notCorporateTrusteePostcode>AB12 3YZ</notCorporateTrusteePostcode>
//                <title>Mr</title>
//              </map>
//            </formdata>
//            <formdata uri="/repayment-claim-summary">
//              <map>
//                <gas-schedule>error</gas-schedule>
//              </map>
//            </formdata>
//          </form>
//        </forms>
//      </Body>
//    </GovTalkMessage>

  private def pollCorrelationAnswer(corrId: String) =
    <GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
      <EnvelopeVersion>2.0</EnvelopeVersion>
      <Header>
        <MessageDetails>
          <Class>HMRC-SA-SA100</Class>
          <Qualifier>poll</Qualifier>
          <Function>submit</Function>
          <TransactionID>141500000000000000000000000174</TransactionID>
          <CorrelationID>{corrId}</CorrelationID>
          <Transformation>XML</Transformation>
          <GatewayTimestamp>2016-03-08T11:28:53</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails/>
      </Header>
      <GovTalkDetails>
        <Keys/>
        <ChannelRouting/>
      </GovTalkDetails>
      <Body/>
    </GovTalkMessage>

  private val configuration = new Configuration(ConfigFactory.load("test-application.conf"))

  private val appConfig     = new AppConfig(configuration)
  private val testInstance: ChrisService = new ChrisService(appConfig)
  

  "ChrisService.pollMessage" should {

//    "send poll response for a poll request with count = -1 and error = false" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", -1, false)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/0/false"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = -1 and error = true" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", -1, true)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/0/true"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 0 and error = false" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 0, false)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/1/false"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 0 and error = true" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 0, true)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/1/true"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 1 and error = false" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 1, false)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/2/false"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 1 and error = true" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 1, true)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/2/true"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 2 and error = false" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 2, false)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/3/false"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 2 and error = true" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 2, true)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/poll/HMRC-SA-SA100/3/true"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 3 and error = false" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 3, false)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/HMRC-SA-SA100/Filing/data/false"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 3 and error = true" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 3, true)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/HMRC-SA-SA100/Filing/data/true"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 4 and error = false" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 4, false)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/HMRC-SA-SA100/Filing/data/false"
//      pollInterval mustBe "2"
//    }

//    "send poll response for a poll request with count = 4 and error = true" in {
//      val response = testInstance.pollMessage(pollXML, "HMRC-SA-SA100", 4, true)
//      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
//      val transactionId = (response \ "Header" \ "MessageDetails" \ "TransactionID").text
//      val correlationId = (response \ "Header" \ "MessageDetails" \ "CorrelationID").text
//      val responseEndpoint = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
//      val pollInterval = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint" \ "@PollInterval").text
//      qualifier mustBe "acknowledgement"
//      transactionId mustBe "141500000000000000000000000174"
//      correlationId mustBe "D2C723C1F1BE7756619F751FE47D3308"
//      responseEndpoint mustBe "http://localhost/submission/ChRIS/HMRC-SA-SA100/Filing/data/true"
//      pollInterval mustBe "2"
//    }
  }

  "ChrisService.responseMessage" should {
    
    "responseCISMessage should return successful business response for a valid CIS MR-FILING submit request" in {

      val response = testInstance.responseCISMessage(submitCISMRSuccessFilingXML).get

      val clazz = (response \ "Header" \ "MessageDetails" \ "Class").text
      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
      val successResponse = (response \ "Body" \ "SuccessResponse" \ "Message").text
      qualifier mustBe "response"
      successResponse mustBe "The Monthly Return has been processed and passed full validation"
      clazz mustBe "IR-CIS-CIS300MR"
    }

    "responseCISMessage should return acknowledged business response for orgs enabled for acknowledged submissions" in {

      val response = testInstance.responseCISMessage(submitCISMRAcknowledgeFilingXML).get

      val clazz = (response \ "Header" \ "MessageDetails" \ "Class").text
      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
      val govTalkErrorText = (response \ "GovTalkDetails" \ "GovTalkErrors" \ "Error" \ "Text").text

      clazz mustBe "CISR"
      qualifier mustBe "acknowledgement"
      govTalkErrorText mustBe "Unknown CorrelationId"
    }

    "responseCISMessage should set the polling url to be the stub url if stubbing is true" in {

      val response = testInstance.responseCISMessage(submitCISMRAcknowledgeFilingXML).get

      val pollingUrl = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
      pollingUrl mustBe "http://localhost/submission/ChRIS/poll/IR-CIS-CIS300MR/0/false"
    }

    "responseCISMessage should set the polling url to be a real polling url if stubbing is false" in {
      val response = testInstance.responseCISMessage(submitCISMRSuccessFilingXML).get

      val pollingUrl = (response \ "Header" \ "MessageDetails" \ "ResponseEndPoint").text
      pollingUrl mustBe ""
    }

    "responseCISVERIFYMessage should return successful business response for a valid CIS VERIFY submit request" in {
      val response = testInstance.responseCISVerifyMessage(submitCISVerifyXML).get

      val clazz = (response \ "Header" \ "MessageDetails" \ "Class").text
      val qualifier = (response \ "Header" \ "MessageDetails" \ "Qualifier").text
      val successResponse = (response \ "Body" \ "SuccessResponse" \ "Message").text
      qualifier mustBe "response"
      successResponse mustBe "The Subcontractor Verification has been processed and passed full validation"
      clazz mustBe "IR-CIS-VERIFY"
    }

    "responseCISMessage should return an empty response for request with unknown message class" in {
      val response = testInstance.responseCISMessage(submitUnknownCISMessage)
      response mustBe empty
    }

    "responseCISVERIFYMessage should return an empty response for request with unknown message class" in {
      val response = testInstance.responseCISVerifyMessage(submitUnknownCISMessage)
      response mustBe empty
    }
    
  }
}
