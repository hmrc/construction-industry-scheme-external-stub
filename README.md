
# construction-industry-scheme-external-stub

Th construction-industry-scheme-external-stub provides stubs for downstream services to mock the responses.

## Running the service

Service Manager: `sm2 --start CIS_ALL`

To start the server locally: `sbt run`

## Testing
Run unit tests with:
```shell
sbt test
```

Run integration tests with:
```shell
sbt it/test
```

Check code coverage with:
```shell
sbt clean coverage test it/test coverageReport
```

---

## Endpoints

### RDS DataCache Proxy

**Endpoint**: `POST /cis-taxpayer`

**Description**: Returns the taxpayer (contractor) details for the given employer reference (tax office number and tax office reference).

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "taxOfficeNumber": "200",
  "taxOfficeReference": "AB123456"
}
```
- Response status: `200`
- Response body:
```json
{
  "uniqueId" : "1",
  "taxOfficeNumber" : "123",
  "taxOfficeRef" : "AB123456",
  "aoDistrict" : "123",
  "aoPayType" : "M",
  "aoCheckCode" : "XY",
  "aoReference" : "1234567XY",
  "validBusinessAddr" : "Y",
  "correlation" : "corr-abc",
  "ggAgentId" : "AGENT-001",
  "employerName1" : "TEST LTD",
  "agentOwnRef" : "AG-REF-001",
  "schemeName" : "CIS Scheme",
  "utr" : "1234567890",
  "enrolledSig" : "Y"
}
```

**Endpoint**: `GET /cis/client-list-status?credentialId=$credentialId&serviceName=$serviceName&gracePeriod=$gracePeriodSeconds`

**Description**: Returns the status of the client list download process.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456


- Request body: N/A


- Response status: `200`
- Response body:
```json
{
  "status": "Succeeded"
}
```

### FormP Proxy

**Endpoint**: `POST /monthly-returns `

**Description**: Returns the full list of Monthly Returns for the given scheme.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "1"
}
```
- Response status: `200`
- Response body:
```json
{
  "monthlyReturnList": [
    {
      "monthlyReturnId": 1000001,
      "taxYear": 2025,
      "taxMonth": 1,
      "nilReturnIndicator": "N",
      "decEmpStatusConsidered": "Y",
      "decAllSubsVerified": "N",
      "decInformationCorrect": "N",
      "decNoMoreSubPayments": "Y",
      "decNilReturnNoPayments": "N",
      "status": "SUBMITTED",
      "lastUpdate": "2025-07-23T00:00:00",
      "amendment": "N"
    },
    {
      "monthlyReturnId": 1000002,
      "taxYear": 2025,
      "taxMonth": 2,
      "nilReturnIndicator": "N",
      "decEmpStatusConsidered": "Y",
      "decAllSubsVerified": "N",
      "decInformationCorrect": "N",
      "decNoMoreSubPayments": "Y",
      "decNilReturnNoPayments": "Y",
      "status": "SUBMITTED",
      "lastUpdate": "2025-07-19T00:00:00",
      "amendment": "N"
    },
    {
      "monthlyReturnId": 1000003,
      "taxYear": 2025,
      "taxMonth": 3,
      "nilReturnIndicator": "Y",
      "decEmpStatusConsidered": "N",
      "decAllSubsVerified": "Y",
      "decInformationCorrect": "Y",
      "decNoMoreSubPayments": "Y",
      "decNilReturnNoPayments": "N",
      "status": "STARTED",
      "lastUpdate": "2025-08-03T00:00:00",
      "amendment": "Y"
    }
  ]
}
```

**Endpoint**: `POST /monthly-return/nil/create`

**Description**: Creates a monthly return record in the monthly return table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11,
  "decInformationCorrect": "true",
  "decNilReturnNoPayments": "true"
}
```
- Response status: `200`
- Response body:
```json
{
  "status": ??? 
}
```

**Endpoint**: `POST /scheme/email`

**Description**: Return a scheme email from the scheme table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body:

```json
{
  "instanceId": "1"
}
```
- Response status: `200`
- Response body:
```json
{
  "email": "test@test.com" 
}
```

**Endpoint**: `POST /submissions/create`

**Description**: Creates a new submission record in the submission table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11,
  "emailRecipient": "test@test.com"
}
```
- Response status: `200`
- Response body:
```json
{
  "submissionId": "90001" 
}
```

**Endpoint**: `POST /submissions/update`

**Description**: Updates the submission record in the submission table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11,
  "hmrcMarkGenerated": "Dj5TVJDyRYCn9zta5EdySeY4fyA=",
  "submittableStatus": "ACCEPTED"
}
```
- Response status: `200`
- Response body: N/A


### ChRIS

**Endpoint**: `POST /submission/ChRIS/CISR/Filing/sync/CIS300MR`

**Description**: A soap message constructed and submitted to the ChRIS service.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body:
```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-CIS300MR</Class>
            <Qualifier>response</Qualifier>
            <Function>submit</Function>
            <CorrelationID>49654E0E5535489F97B6F504E0ACE7C7</CorrelationID>
            <ResponseEndPoint/>
            <GatewayTimestamp>2025-10-16T13:25:28.720</GatewayTimestamp>
            <Transformation>XML</Transformation>
        </MessageDetails>
    </Header>
    <GovTalkDetails>
        <Keys/>
    </GovTalkDetails>
    <Body>
        <SuccessResponse xmlns="http://www.inlandrevenue.gov.uk/SuccessResponse">
            <IRmarkReceipt>
                <dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">
                    <dsig:SignedInfo>
                        <dsig:CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
                        <dsig:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
                        <dsig:Reference>
                            <dsig:Transforms>
                                <dsig:Transform Algorithm="http://www.w3.org/TR/1999/REC-xpath-19991116">
                                    <dsig:XPath>(count(ancestor-or-self::node()|/gti:GovTalkMessage/gti:Body)=count(ancestor-or-self::node())) and (count(ancestor-or-self::node()|/gti:GovTalkMessage/gti:Body/*[name()='IRenvelope']/*[name()='IRheader']/*[name()='IRmark'])!=count(ancestor-or-self::node()))</dsig:XPath>
                                </dsig:Transform>
                                <dsig:Transform Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"/>
                            </dsig:Transforms>
                            <dsig:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
                            <dsig:DigestValue>mMnIokxfPI0/v44JEqDDIr1iQvU=</dsig:DigestValue>
                        </dsig:Reference>
                    </dsig:SignedInfo>
                    <dsig:SignatureValue>xjd0lzhAQrnHZsE5inNCOVsmwcQ9HTu+CFUoyqEcOhVvxj2jvYGcjkhu7sZkZJ9RBjBcEP/eQTbesMTrnUgofuMqaROt8ZyD/RJKFIwh5TtNzYzDM55Pa3GDd2ZXcmfR38mS9KPwqc5Ty+Eqv69FxqivCQk46H20F8fnWnx85H4=</dsig:SignatureValue>
                    <dsig:KeyInfo>
                        <dsig:X509Data>
                            <dsig:X509Certificate>MIID0zCCAzygAwIBAgIBADANBgkqhkiG9w0BAQQFADCBqDELMAkGA1UEBhMCbmwxFjAUBgNVBAgTDU5vb3JkLUhvbGxhbmQxFzAVBgNVBAoTDk1vYmlsZWZpc2guY29tMRAwDgYDVQQHEwdaYWFuZGFtMRIwEAYDVQQLEwlNYXJrZXRpbmcxGzAZBgNVBAMTEnd3dy5tb2JpbGVmaXNoLmNvbTElMCMGCSqGSIb3DQEJARYWY29udGFjdEBtb2JpbGVmaXNoLmNvbTAeFw0xMTEwMTMxMDI2NTZaFw0xMjEwMTIxMDI2NTZaMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3o83CcmMMOC/fnjVv2puirJTs36+al6RDBe2tbFLKKODd29DZbmH9/6R77VPZACvXxBdRzMls//YRVHoJyJVudy+B4siUfHP80pssg2ZXCmCtUZGS71ohmlHcGQGTVLj8wmicf/DfmMAgq19OFZJP5LUn3md/MQBOUYrFXt21dQIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFAIuWYA/BMx8Gn/YOILevnJthkIZMIHVBgNVHSMEgc0wgcqAFAIuWYA/BMx8Gn/YOILevnJthkIZoYGupIGrMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEEBQADgYEABCb+f82DKWIWBczTeKGc6Ka5U7oys/itCY7XOYMIvXYPj+tb+5PBrmTO3jZNoZso9cYYFcDGXySbk6wSZiEPlbMqkoYE62E6dVXAmbza3ZNNIX/yEpkE3ZeBBtYzJMPQme9jrMgwgMIhgVzQNL2KPkbWOtQfoYgnThHQKLBry6Y=</dsig:X509Certificate>\n
                        </dsig:X509Data>
                    </dsig:KeyInfo>
                </dsig:Signature>
                <Message code="1\">HMRC has received the IR-CIS-CIS300MR document ref: 123/GL01 at 08.46 on 06/04/2017. The associated IRmark was: TBPJFWEAYSD4GFVRMHY7KLWEBHB5BLA5. We advise you to keep this receipt in both electronic and hardcopy versions for your records. You may wish to use them to identify your submission in the future.</Message>
            </IRmarkReceipt>
            <Message code="9004">The Monthly Return has been processed and passed full validation</Message>
            <AcceptedTime>2017-04-06T08:46:08.081</AcceptedTime>
        </SuccessResponse>
    </Body>
</GovTalkMessage>
```


### Iass 


### Client Exchange Proxy

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").