
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

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: 123456


- Request body: N/A


- Response status: `200`
- Response body:
```json
{
  "status": "Succeeded"
}
```

**Endpoint**: `GET /cis/client-list?irAgentId=$irAgentId&credentialId=$credentialId`

**Description**: Returns clients for the agent.

#### Happy Path

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: 123456


- Request body: N/A


- Response status: `200`
- Response body:
```json
{
  "clients":
  [
    {
      "uniqueId": "1",
      "taxOfficeNumber": "123",
      "taxOfficeRef": "AB001",
      "employerName1": "ABC Ltd",
      "schemeName": "ABC"
    },
    {
      "uniqueId": "2",
      "taxOfficeNumber": "456",
      "taxOfficeRef": "CD002",
      "employerName1": "XYZ Builders",
      "schemeName": "XYZ"
    }
  ],
  "totalCount": 2,
  "clientNameStartingCharacters": ["A","X"]
}
```

**Endpoint**: `POST /cis/prepop-contractor`

**Description**: Returns contractor known facts for prepop.

#### Happy Path (contractor exists)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Valid IRAgentReference except for AGT204

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid Tax Office no. and ref. combination except for 204/EZ00100
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid Tax Office no. and ref. combination except for 204/EZ00100


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "taxOfficeNumber": "205",
  "taxOfficeReference": "EZ00100",
  "accountOfficeReference": "123PA12345678"
}
```

- Response status: `200`
- Response body:
```json
{
  "knownfacts": {
    "taxOfficeNumber": "205",
    "taxOfficeReference": "EZ00100",
    "accountOfficeReference": "123PA12345678"
  },
  "prePopContractor": {
    "schemeName": "PAL-355 Scheme",
    "utr": "1123456789",
    "response": 0
  }
}
```

#### Unhappy Path (no contractor exists)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT204

or 

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00100


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "taxOfficeNumber": "204",
  "taxOfficeReference": "EZ00100",
  "accountOfficeReference": "123PA12345678"
}
```

- Response status: `404`
- Response body:
```json
{
  "message": "No CIS scheme pre-pop data found for TON=204, TOR=EZ00100, AO=123PA12345678"
}
```

**Endpoint**: `POST /cis/prepop-subcontractor `

**Description**: Returns subcontractor(s) known facts for prepop.

#### Happy Path (contractor exists and 1 subcontractor exists)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT206

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00200


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "taxOfficeNumber": "204",
  "taxOfficeReference": "EZ00200",
  "accountOfficeReference": "123PA12345678"
}
```

- Response status: `200`
- Response body:
```json
{
  "knownfacts": {
    "taxOfficeNumber": "204",
    "taxOfficeReference": "EZ00200",
    "accountOfficeReference": "123PA12345678"
  },
  "prePopSubcontractors": {
    "response": 0,
    "subcontractors": [
      {
        "subcontractorType": "C",
        "utr": "1123456789",
        "verificationNumber": "12345678901",
        "verificationSuffix": "AB",
        "title": "Mr",
        "firstName": "First",
        "secondName": "",
        "surname": "Surname"
      }
    ]
  }
}
```

#### Unhappy Path (contractor exists and no subcontractor exists)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT207

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00201


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "taxOfficeNumber": "204",
  "taxOfficeReference": "EZ00201",
  "accountOfficeReference": "123PA12345678"
}
```

- Response status: `404`
- Response body:
```json
{
  "message": "No CIS subcontractor pre-pop data found for TON=204, TOR=EZ00201, AO=123PA12345678"
}
```

#### Unhappy Path (no contractor exists and no subcontractor exists)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT204

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00100


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "taxOfficeNumber": "204",
  "taxOfficeReference": "EZ00100",
  "accountOfficeReference": "123PA12345678"
}
```

- Response status: `404`
- Response body:
```json
{
  "message": "No CIS subcontractor pre-pop data found for TON=204, TOR=EZ00201, AO=123PA12345678"
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


**Endpoint**: `POST /scheme/:instanceId `

**Description**: Returns Scheme data.

#### Happy Path (no utr but there is a name, prepopSuccessful = "N", subcontractorCounter = 1)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT202

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 202
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2020,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "202",
  "taxOfficeReference": "Tax office Ref. in the request enrollment",
  "utr": null,
  "name": "ABC Construction Ltd",
  "emailAddress": "contact@example.com",
  "displayWelcomePage": null,
  "prePopCount": 1,
  "prePopSuccessful": "N",
  "subcontractorCounter": 1,
  "verificationBatchCounter": 0,
  "lastUpdate": "2025-01-01T12:00:00Z",
  "version": 1
}
```

#### Happy Path (no name but there is a utr, prepopSuccessful = "N", subcontractorCounter = 1)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT203

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 203
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2020,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "203",
  "taxOfficeReference": "Tax office Ref. in the request enrollment",
  "utr": null,
  "name": "ABC Construction Ltd",
  "emailAddress": "contact@example.com",
  "displayWelcomePage": null,
  "prePopCount": 1,
  "prePopSuccessful": "N",
  "subcontractorCounter": 1,
  "verificationBatchCounter": 0,
  "lastUpdate": "2025-01-01T12:00:00Z",
  "version": 1
}
```


#### Happy Path (no utr, no name, prepopSuccessful = "N", subcontractorCounter = 0)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT204

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00100


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2040,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "204",
  "taxOfficeReference": "EZ00100",
  "utr": null,
  "name": null,
  "emailAddress": null,
  "displayWelcomePage": null,
  "prePopCount": 0,
  "prePopSuccessful": "N",
  "subcontractorCounter": 0,
  "verificationBatchCounter": 0,
  "lastUpdate": null,
  "version": 0
}
```


#### Happy Path (successful prepoped scheme table, subcontractorCounter = 1)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT206

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00200


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2010,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "201",
  "taxOfficeReference": "AB1234",
  "utr": "1234567890",
  "name": "ABC Construction Ltd",
  "emailAddress": "contact@example.com",
  "displayWelcomePage": null,
  "prePopCount": 1,
  "prePopSuccessful": "Y",
  "subcontractorCounter": 1,
  "verificationBatchCounter": 0,
  "lastUpdate": "2025-01-01T12:00:00Z",
  "version": 1
}
```

#### Happy Path (successful prepoped scheme table, subcontractorCounter = 0)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT207

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00201


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 1000,
  "instanceId": "CIS-123",
  "accountsOfficeReference": "123MXY1234567XY",
  "taxOfficeNumber": "123",
  "taxOfficeReference": "AB1234",
  "utr": "1234567890",
  "name": "ABC Construction Ltd",
  "emailAddress": "test@test.com",
  "displayWelcomePage": null,
  "prePopCount": 1,
  "prePopSuccessful": "Y",
  "subcontractorCounter": 0,
  "verificationBatchCounter": 0,
  "lastUpdate": "2025-01-01T12:00:00Z",
  "version": 1
}
```

#### Happy Path (name & utr exists, prepopSuccessful = 'Y', subcontractorCounter = 1)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT207

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 204
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ00201


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 1000,
  "instanceId": "CIS-123",
  "accountsOfficeReference": "123MXY1234567XY",
  "taxOfficeNumber": "123",
  "taxOfficeReference": "AB1234",
  "utr": "1234567890",
  "name": "ABC Construction Ltd",
  "emailAddress": "test@test.com",
  "displayWelcomePage": null,
  "prePopCount": 1,
  "prePopSuccessful": "Y",
  "subcontractorCounter": 0,
  "verificationBatchCounter": 0,
  "lastUpdate": "2025-01-01T12:00:00Z",
  "version": 1
}
```

**Endpoint**: `POST /scheme`

**Description**: Create a new Scheme.

#### Happy Path 

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid value
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "123",
  "taxOfficeNumber": "204",
  "taxOfficeReference": "EZ00100",
  "accountsOfficeReference": "123PA12345678"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `201`
- Response body:
```json
{
  "schemeId": 12345
}
```


**Endpoint**: `POST /scheme/update`

**Description**: Update an existing scheme.

#### Happy Path 

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid value
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "schemeId": "12345",
  "instanceId": "123",
  "accountsOfficeReference": "123PA12345678",
  "taxOfficeNumber": "204",
  "taxOfficeReference": "EZ00100"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `201`
- Response body:
```json
{
  "schemeId": 12345
}
```


**Endpoint**: `POST /scheme/version-update`

**Description**: Update Scheme table by incrementing version by 1.

#### Happy Path 

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid value
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "123",
  "version": "1"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body (version is incremented by 1 upon the request body version value):
```json
{
  "version": 2 
}
```


**Endpoint**: `POST /scheme/prepopulate`

**Description**: Apply contractor and subcontractor prepopulation.

#### Happy Path

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid value
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value


To trigger the happy path, ensure you provide a valid request body:
```json
{
  "schemeId": 1000,
  "instanceId": "123",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "123",
  "taxOfficeReference": "AB1234",
  "utr": "1234567890",
  "name": "Test Ltd",
  "emailAddress": "test@test.com",
  "displayWelcomePage": "N",
  "prePopCount": 1,
  "prePopSuccessful": "N",
  "version": 1,
  "subcontractorTypes": ["soletrader"]
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body (version is incremented by 1 upon the request body version value):
```json
{
  "version": 2 
}
```



### ChRIS

**Endpoint**: `POST /submission/ChRIS/CISR/Filing/sync/CIS300MR`

**Description**: A soap message constructed and submitted to the ChRIS service.

#### Happy Path
###### HTTP 200 - acknowledgement (will trigger polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**

To trigger the happy path, ensure you provide a valid request body:
```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-CIS300MR</Class>
            <Qualifier>request</Qualifier>
            <Function>submit</Function>
            <CorrelationID>FB2E47B242134FF289328EF8A39C3BDC</CorrelationID>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>2025-11-25T11:41:09.413</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails/>
    </Header>
    <GovTalkDetails>
        <Keys>
            <Key Type="TaxOfficeNumber">754</Key>
            <Key Type="TaxOfficeReference">EZ00100</Key>
        </Keys>
        <TargetDetails>
            <Organisation>IR</Organisation>
        </TargetDetails>
        <ChannelRouting>
            <Channel>
                <URI>0126</URI>
                <Product>EzGov IR-CIS-CIS300MR</Product>
                <Version>3.4</Version>
            </Channel>
        </ChannelRouting>
    </GovTalkDetails>
    <Body>
        <IRenvelope xmlns="http://www.govtalk.gov.uk/taxation/CISreturn">
            <IRheader>
                <Keys>
                    <Key Type="TaxOfficeNumber">754</Key>
                    <Key Type="TaxOfficeReference">EZ00100</Key>
                </Keys>
                <PeriodEnd>2025-05-05</PeriodEnd>
                <DefaultCurrency>GBP</DefaultCurrency>
                <Manifest>
                    <Contains>
                        <Reference>
                            <Namespace>http://www.govtalk.gov.uk/taxation/CISreturn</Namespace>
                            <SchemaVersion>2005-v1.1</SchemaVersion>
                            <TopElementName>CISreturn</TopElementName>
                        </Reference>
                    </Contains>
                </Manifest>
                <IRmark Type="generic">Fv1KhWmy3UvlCGU/skHcT01qiiI=</IRmark>
                <Sender>Company</Sender>
            </IRheader>
            <CISreturn>
                <Contractor>
                    <UTR>1234567890</UTR>
                    <AOref>1234567XY</AOref>
                </Contractor>
                <NilReturn>yes</NilReturn>
                <Declarations>
                    <InformationCorrect>yes</InformationCorrect>
                </Declarations>
            </CISreturn>
        </IRenvelope>
    </Body>
</GovTalkMessage>
```
- Response status: `200`
- Response body:

**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
      <MessageDetails>
        <Class>CISR</Class>
        <Qualifier>acknowledgement</Qualifier>
        <Function>submit</Function>
        <CorrelationID>FB2E47B242134FF289328EF8A39C3BDC</CorrelationID>
        <ResponseEndPoint PollInterval="5">{environmentUrl}submission/ChRIS/poll/IR-CIS-CIS300MR/0/false</ResponseEndPoint>
        <GatewayTimestamp/>
        <Transformation>XML</Transformation>
      </MessageDetails>
    </Header>
    <GovTalkDetails>
      <Keys/>
      <GovTalkErrors>
        <Error>
          <RaisedBy>ChRIS</RaisedBy>
          <Number>5999</Number>
          <Type>business</Type>
          <Text>Unknown CorrelationId</Text>
        </Error>
      </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
  </GovTalkMessage>
```

#### Unhappy Path

To trigger the unhappy paths, ensure you provide the following auth login stub values:

###### HTTP 200 - Fatal Error (will not trigger polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00125**


- Request body: **See valid request body above.**


- Response status: `200`
- Response body:

**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-CIS300MR</Class>
            <Qualifier>error</Qualifier>
            <Function>submit</Function>
            <TransactionID></TransactionID>
            <CorrelationID>C0A341CF946B46A18BF01C270D15B1E6</CorrelationID>
            <ResponseEndPoint PollInterval="2">{environmentUrl}submission/ChRIS/IR-CIS-CIS300MR/Filing/data/true</ResponseEndPoint>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>2025-11-25T12:03:25.242</GatewayTimestamp>
        </MessageDetails>
    </Header>
    <GovTalkDetails>
        <Keys/>
        <GovTalkErrors>
            <Error>
                <RaisedBy>Gateway</RaisedBy>
                <Number>1020</Number>
                <Type>fatal</Type>
                <Text>Forced fatal error (stub)</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```


###### HTTP 200 - Department Error (will not trigger Polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00150**


- Request body: **See valid request body above.**


- Response status: `200`
- Response body:

**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/


```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-CIS300MR</Class>
            <Qualifier>error</Qualifier>
            <Function>submit</Function>
            <TransactionID></TransactionID>
            <CorrelationID>0F3C0E6EB3D8400C89DED4C9AC8FAB95</CorrelationID>
            <ResponseEndPoint PollInterval="2">{environmentUrl}submission/ChRIS/IR-CIS-CIS300MR/Filing/data/true</ResponseEndPoint>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>2025-11-25T12:08:20.586</GatewayTimestamp>
        </MessageDetails>
    </Header>
    <GovTalkDetails>
        <Keys/>
        <GovTalkErrors>
            <Error>
                <RaisedBy>Department</RaisedBy>
                <Number>3001</Number>
                <Type>business</Type>
                <Text>The submission of this document has failed due to departmental specific business logic in the Body tag.</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body>
        <ErrorResponse SchemaVersion="2.0">
            <Application>
                <MessageCount>1</MessageCount>
            </Application>
            <Error>
                <RaisedBy>System</RaisedBy>
                <Number>5005</Number>
                <Type>business</Type>
                <Text>Keys in the GovTalkDetails do not match those in the IRheader.</Text>
                <Location>/hd:GovTalkMessage[1]/hd:Body[1]/MTR:IRenvelope[1]/MTR:IRheader[1]/MTR:Keys[1]/MTR:Key[1]</Location>
            </Error>
        </ErrorResponse>
    </Body>
</GovTalkMessage>
```

###### HTTP 200 - irMark Miss Match Error (will not trigger Polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00200**


- Request body: **See valid request body above.**


- Response status: `200`
- Response body:

**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-CIS300MR</Class>
            <Qualifier>error</Qualifier>
            <Function>submit</Function>
            <TransactionID></TransactionID>
            <CorrelationID>6483DA820AA844D8A62B1AE43FFC6014</CorrelationID>
            <ResponseEndPoint PollInterval="2">{environmentUrl}submission/ChRIS/IR-CIS-CIS300MR/Filing/data/true</ResponseEndPoint>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>2025-12-01T10:51:31.225</GatewayTimestamp>
        </MessageDetails>
    </Header>
    <GovTalkDetails>
        <Keys/>
        <GovTalkErrors>
            <Error>
                <RaisedBy>ChRIS</RaisedBy>
                <Number>3001</Number>
                <Type>business</Type>
                <Text>Your submission failed due to business validation errors. Please see below for details.</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body>
        <ErrorResponse SchemaVersion="2.0">
            <Application>
                <MessageCount>1</MessageCount>
            </Application>
            <Error>
                <RaisedBy>ChRIS</RaisedBy>
                <Number>2021</Number>
                <Type>business</Type>
                <Text>The supplied IRmark is incorrect.</Text>
                <Location>IRmark</Location>
            </Error>
        </ErrorResponse>
    </Body>
</GovTalkMessage>
```

###### HTTP 200 - Submitted (will not trigger Polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **Any value excluding: EZ00100, EZ00125, EZ00150** e.g. EZ00300


- Request body: **See valid request body above.**


- Response status: `200`
- Response body:

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-CIS300MR</Class>
            <Qualifier>response</Qualifier>
            <Function>submit</Function>
            <CorrelationID>330782F075BE473C8F8BE85B61DC73EF</CorrelationID>
            <ResponseEndPoint/>
            <GatewayTimestamp>2025-11-25T12:10:30.504</GatewayTimestamp>
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
                            <dsig:DigestValue>WHQKQx1cQqIR6xzjwnDnYa9+Its=</dsig:DigestValue>
                        </dsig:Reference>
                    </dsig:SignedInfo>
                    <dsig:SignatureValue>xjd0lzhAQrnHZsE5inNCOVsmwcQ9HTu+CFUoyqEcOhVvxj2jvYGcjkhu7sZkZJ9RBjBcEP/eQTbesMTrnUgofuMqaROt8ZyD/RJKFIwh5TtNzYzDM55Pa3GDd2ZXcmfR38mS9KPwqc5Ty+Eqv69FxqivCQk46H20F8fnWnx85H4=</dsig:SignatureValue> <dsig:KeyInfo>
                    <dsig:X509Data>
                        <dsig:X509Certificate>MIID0zCCAzygAwIBAgIBADANBgkqhkiG9w0BAQQFADCBqDELMAkGA1UEBhMCbmwxFjAUBgNVBAgTDU5vb3JkLUhvbGxhbmQxFzAVBgNVBAoTDk1vYmlsZWZpc2guY29tMRAwDgYDVQQHEwdaYWFuZGFtMRIwEAYDVQQLEwlNYXJrZXRpbmcxGzAZBgNVBAMTEnd3dy5tb2JpbGVmaXNoLmNvbTElMCMGCSqGSIb3DQEJARYWY29udGFjdEBtb2JpbGVmaXNoLmNvbTAeFw0xMTEwMTMxMDI2NTZaFw0xMjEwMTIxMDI2NTZaMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3o83CcmMMOC/fnjVv2puirJTs36+al6RDBe2tbFLKKODd29DZbmH9/6R77VPZACvXxBdRzMls//YRVHoJyJVudy+B4siUfHP80pssg2ZXCmCtUZGS71ohmlHcGQGTVLj8wmicf/DfmMAgq19OFZJP5LUn3md/MQBOUYrFXt21dQIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFAIuWYA/BMx8Gn/YOILevnJthkIZMIHVBgNVHSMEgc0wgcqAFAIuWYA/BMx8Gn/YOILevnJthkIZoYGupIGrMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEEBQADgYEABCb+f82DKWIWBczTeKGc6Ka5U7oys/itCY7XOYMIvXYPj+tb+5PBrmTO3jZNoZso9cYYFcDGXySbk6wSZiEPlbMqkoYE62E6dVXAmbza3ZNNIX/yEpkE3ZeBBtYzJMPQme9jrMgwgMIhgVzQNL2KPkbWOtQfoYgnThHQKLBry6Y=</dsig:X509Certificate>
                    </dsig:X509Data>
                </dsig:KeyInfo>
                </dsig:Signature>
                <Message code="1">HMRC has received the IR-CIS-CIS300MR document ref: 123/GL01 at 08.46 on 06/04/2017. The associated IRmark was: TBPJFWEAYSD4GFVRMHY7KLWEBHB5BLA5. We advise you to keep this receipt in both electronic and hardcopy versions for your records. You may wish to use them to identify your submission in the future.</Message>
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