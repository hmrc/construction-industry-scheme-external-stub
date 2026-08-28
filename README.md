
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
  "aoPayType" : "P",
  "aoCheckCode" : "A",
  "aoReference" : "12345678",
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

**Endpoint**: `POST /monthly-return `

**Description**: Returns the full list of Unsubmitted Monthly Returns for the given scheme.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: any valid Tax Office no.
- Identifier Name: TaxOfficeReference
- Identifier Value: any valid Tax Office ref.

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "any valid instanceId"
}
```
- Response status: `200`
- Response body:
```json
{
  "scheme": {
    "schemeId": 13343,
    "instanceId": "900001",
    "accountsOfficeReference": "125PA12345000",
    "taxOfficeNumber": "101",
    "taxOfficeReference": "AB0001",
    "utr": "1234657890",
    "name": "company",
    "prePopCount": 3,
    "prePopSuccessful": "Y",
    "subcontractorCounter": 1,
    "verificationBatchCounter": 0,
    "version": 3
  },
  "monthlyReturn": [
    {
      "monthlyReturnId": 30001,
      "taxYear": 2025,
      "taxMonth": 1,
      "nilReturnIndicator": "Y",
      "decInformationCorrect": "Y",
      "decNilReturnNoPayments": "N",
      "status": "STARTED",
      "lastUpdate": "2026-01-01T23:24:56",
      "amendment": "N"
    },
    {
      "monthlyReturnId": 30002,
      "taxYear": 2025,
      "taxMonth": 2,
      "nilReturnIndicator": "Y",
      "decInformationCorrect": "Y",
      "decNilReturnNoPayments": "N",
      "status": "VALIDATED",
      "lastUpdate": "2026-01-02T23:24:56",
      "amendment": "N"
    },
    {
      "monthlyReturnId": 30003,
      "taxYear": 2025,
      "taxMonth": 3,
      "nilReturnIndicator": "Y",
      "decInformationCorrect": "Y",
      "decNilReturnNoPayments": "N",
      "status": "DEPARTMENTAL_ERROR",
      "lastUpdate": "2026-01-03T23:24:56",
      "amendment": "N"
    }
  ]
}
```

**Endpoint**: `POST /cis/retrieve-submitted-monthly-returns`

**Description**: Returns the full list of Submitted Monthly Returns for the given scheme.

#### Happy Path

- Affinity Group: Organisation / Agent
- Enrolment Key: HMRC-CIS-ORG (for Organisation) or IR-PAYE-AGENT (for Agent)
- Identifier Name: TaxOfficeNumber (for Organisation) or IRAgentReference (for Agent)
- Identifier Value: any valid Tax Office no. or any valid IRAgentReference
- Identifier Name: TaxOfficeReference (for Organisation) or none (for Agent)
- Identifier Value: any valid Tax Office ref. (for Organisation)

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "any valid instanceId"
}
```
- Response status: `200`
- Response body:
```json
{
  "scheme": {
    "schemeId": 10877,
    "instanceId": "1",
    "accountsOfficeReference": "123PA12345678",
    "taxOfficeNumber": "123",
    "taxOfficeReference": "AB456",
    "utr": "1123456789",
    "name": "PAL-355 Scheme",
    "emailAddress": "test@test.com",
    "displayWelcomePage": "N",
    "prePopCount": 2,
    "prePopSuccessful": "N",
    "subcontractorCounter": 12,
    "verificationBatchCounter": 4,
    "lastUpdate": "2018-02-23T16:26:23Z",
    "version": 69
  },
  "monthlyReturns": [
    {
      "monthlyReturnId": 10000,
      "taxYear": 2022,
      "taxMonth": 1,
      "nilReturnIndicator": "N",
      "decEmpStatusConsidered": "Y",
      "decAllSubsVerified": "Y",
      "decInformationCorrect": "Y",
      "status": "SUBMITTED",
      "supersededBy": 20000,
      "amendmentStatus": "STARTED",
      "lastUpdate": "2018-02-23T16:25:47",
      "amendment": "N",
      "monthlyReturnItems": "N"
    }
  ],
  "submissions": [
    {
      "submissionId": 10900,
      "submissionType": "MONTHLY_RETURN",
      "activeObjectId": 10000,
      "status": "SUBMITTED",
      "hmrcMarkGenerated": "w5hsnI+Ziyr8rWZyJGWE+Nh/gRo=",
      "hmrcMarkGgis": null,
      "emailRecipient": "test@test.com",
      "acceptedTime": null,
      "createDate": "2018-02-23T16:22:45",
      "lastUpdate": "2018-02-23T16:22:52",
      "schemeId": 10877,
      "agentId": "-",
      "submissionRequestDate": "2018-02-23T16:22:49"
    }
  ]
}
```

**Endpoint**: `POST /cis/monthly-return/nil/create`

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

**Endpoint**: `POST /cis/monthly-return-edit `

**Description**: Returns the Monthly Returns for Edit

#### Happy Path

- Affinity Group: Organisation / Agent
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber (for Organisation) or IRAgentReference (for Agent)
- Identifier Value: any valid Tax Office no. or any valid IRAgentReference
- Identifier Name: TaxOfficeReference (for Organisation) or none (for Agent)
- Identifier Value: any valid Tax Office ref. (for Organisation)

To trigger the happy path, ensure you provide a valid request body similar to example below:
```json
{
  "instanceId": "900001",
  "taxYear": 2025,
  "taxMonth": 1 
}
```
- Response status: `200`
- Response body:
```json
{
  "scheme": [
    {
      "schemeId": 13343,
      "instanceId": "900001",
      "accountsOfficeReference": "125PA12345000",
      "taxOfficeNumber": "101",
      "taxOfficeReference": "AB0001",
      "utr": "1234657890",
      "name": "company",
      "emailAddress": "test@test",
      "displayWelcomePage": "N",
      "prePopCount": 3,
      "prePopSuccessful": "Y",
      "subcontractorCounter": 1,
      "verificationBatchCounter": 0,
      "lastUpdate": "2026-01-05T10:23:56Z",
      "version": 3
    }
  ],
  "monthlyReturn": [
    {
      "monthlyReturnId": 30001,
      "taxYear": 2025,
      "taxMonth": 1,
      "nilReturnIndicator": "N",
      "decEmpStatusConsidered": "Y",
      "decAllSubsVerified": "Y",
      "decInformationCorrect": "Y",
      "decNoMoreSubPayments": "Y",
      "decNilReturnNoPayments": "N",
      "status": "STARTED",
      "lastUpdate": "2026-01-01T23:24:56",
      "amendment": "N"
    }
  ],
  "subcontractors": [
    {
      "subcontractorId": 13377,
      "utr": "1234567890",
      "pageVisited": 0,
      "partnerUtr": "1234567891",
      "crn": "10000001",
      "firstName": "First",
      "nino": "AB623456C",
      "secondName": "Second",
      "surname": "Surname",
      "partnershipTradingName": "Test Ptr",
      "tradingName": "Test Tr",
      "subcontractorType": "company",
      "addressLine1": "one",
      "addressLine2": "two ",
      "addressLine3": "three",
      "addressLine4": "four",
      "country": "UK",
      "postCode": "B1 2CD",
      "emailAddress": "test@test.com",
      "phoneNumber": "07123456789",
      "mobilePhoneNumber": "7123456789",
      "worksReferenceNumber": "123",
      "createDate": "2026-01-05T10:23:56",
      "lastUpdate": "2026-01-05T10:23:56",
      "subbieResourceRef": 1,
      "matched": "Y",
      "autoVerified": "Y",
      "verified": "Y",
      "verificationNumber": "V1000000001",
      "taxTreatment": "net",
      "verificationDate": "2025-05-05T00:00:00",
      "version": 0,
      "updatedTaxTreatment": "TEST",
      "lastMonthlyReturnDate": "2025-05-05T00:00:00",
      "pendingVerifications": 0
    },
    {
      "subcontractorId": 13378,
      "utr": "1234567890",
      "pageVisited": 0,
      "partnerUtr": "1234567891",
      "crn": "10000001",
      "firstName": "First",
      "nino": "AB623456C",
      "secondName": "Second",
      "surname": "Surname",
      "partnershipTradingName": "Test Ptr 2",
      "tradingName": "Test Tr 2",
      "subcontractorType": "company",
      "addressLine1": "one",
      "addressLine2": "two ",
      "addressLine3": "three",
      "addressLine4": "four",
      "country": "UK",
      "postCode": "B1 2CD",
      "emailAddress": "test@test.com",
      "phoneNumber": "07123456789",
      "mobilePhoneNumber": "7123456789",
      "worksReferenceNumber": "123",
      "createDate": "2026-01-05T10:23:56",
      "lastUpdate": "2026-01-05T10:23:56",
      "subbieResourceRef": 2,
      "matched": "Y",
      "autoVerified": "Y",
      "verified": "Y",
      "verificationNumber": "V1000000001",
      "taxTreatment": "net",
      "verificationDate": "2025-05-05T00:00:00",
      "version": 0,
      "updatedTaxTreatment": "TEST",
      "lastMonthlyReturnDate": "2025-05-05T00:00:00",
      "pendingVerifications": 0
    },
    {
      "subcontractorId": 13379,
      "utr": "1234567890",
      "pageVisited": 0,
      "partnerUtr": "1234567891",
      "crn": "10000001",
      "firstName": "First",
      "nino": "AB623456C",
      "secondName": "Second",
      "surname": "Surname",
      "partnershipTradingName": "Test Ptr 3",
      "tradingName": "Test Tr 3",
      "subcontractorType": "company",
      "addressLine1": "one",
      "addressLine2": "two ",
      "addressLine3": "three",
      "addressLine4": "four",
      "country": "UK",
      "postCode": "B1 2CD",
      "emailAddress": "test@test.com",
      "phoneNumber": "07123456789",
      "mobilePhoneNumber": "7123456789",
      "worksReferenceNumber": "123",
      "createDate": "2026-01-05T10:23:56",
      "lastUpdate": "2026-01-05T10:23:56",
      "subbieResourceRef": 7,
      "matched": "Y",
      "autoVerified": "Y",
      "verified": "Y",
      "verificationNumber": "V1000000001",
      "taxTreatment": "net",
      "verificationDate": "2025-05-05T00:00:00",
      "version": 0,
      "updatedTaxTreatment": "TEST",
      "lastMonthlyReturnDate": "2025-05-05T00:00:00",
      "pendingVerifications": 0
    },
    {
      "subcontractorId": 13380,
      "utr": "1234567890",
      "pageVisited": 0,
      "partnerUtr": "1234567891",
      "crn": "10000001",
      "firstName": "First",
      "nino": "AB623456C",
      "secondName": "Second",
      "surname": "Surname",
      "partnershipTradingName": "Test Ptr 4",
      "tradingName": "Test Tr 4",
      "subcontractorType": "company",
      "addressLine1": "one",
      "addressLine2": "two ",
      "addressLine3": "three",
      "addressLine4": "four",
      "country": "UK",
      "postCode": "B1 2CD",
      "emailAddress": "test@test.com",
      "phoneNumber": "07123456789",
      "mobilePhoneNumber": "7123456789",
      "worksReferenceNumber": "123",
      "createDate": "2026-01-05T10:23:56",
      "lastUpdate": "2026-01-05T10:23:56",
      "subbieResourceRef": 3,
      "matched": "Y",
      "autoVerified": "Y",
      "verified": "Y",
      "verificationNumber": "V1000000001",
      "taxTreatment": "net",
      "verificationDate": "2025-05-05T00:00:00",
      "version": 0,
      "updatedTaxTreatment": "TEST",
      "lastMonthlyReturnDate": "2025-05-05T00:00:00",
      "pendingVerifications": 0
    }
  ],
  "monthlyReturnItems": [
    {
      "monthlyReturnId": 30001,
      "monthlyReturnItemId": 1,
      "totalPayments": "1,000.00",
      "costOfMaterials": "100.00",
      "totalDeducted": "100.00",
      "unmatchedTaxRateIndicator": "Y",
      "subcontractorId": 10903,
      "subcontractorName": "Alice, A",
      "verificationNumber": "V1000000001",
      "itemResourceReference": 1
    },
    {
      "monthlyReturnId": 30001,
      "monthlyReturnItemId": 2,
      "totalPayments": "2,000.00",
      "costOfMaterials": "200.00",
      "totalDeducted": "200.00",
      "unmatchedTaxRateIndicator": "Y",
      "subcontractorId": 10903,
      "subcontractorName": "Alice, A",
      "verificationNumber": "V1000000001",
      "itemResourceReference": 2
    },
    {
      "monthlyReturnId": 30001,
      "monthlyReturnItemId": 3,
      "totalPayments": "3,000.00",
      "costOfMaterials": "300.00",
      "totalDeducted": "300.00",
      "unmatchedTaxRateIndicator": "Y",
      "subcontractorId": 10903,
      "subcontractorName": "Alice, A",
      "verificationNumber": "V1000000001",
      "itemResourceReference": 3
    }
  ],
  "submission": [
    {
      "submissionId": 1,
      "submissionType": "MONTHLY_RETURN",
      "activeObjectId": 30001,
      "status": "SUBMITTED",
      "hmrcMarkGenerated": "9AjFBxlBohmcRZ8s/2IV0QaYzz0=",
      "hmrcMarkGgis": "9AjFBxlBohmcRZ8s/2IV0QaYzz0=",
      "emailRecipient": "test@test.com",
      "acceptedTime": "2018-04-06T08:46:08.081",
      "createDate": "2018-02-23T16:26:25",
      "lastUpdate": "2018-02-23T16:26:30",
      "schemeId": 13343,
      "agentId": "-",
      "l_Migrated": 1,
      "submissionRequestDate": "2018-02-23T16:26:27"
    }
  ]
}
```

#### Stub Response Variance

The stub varies the response based on `taxMonth` to support different amendment journey scenarios:

| `taxMonth` | Fixture | Scenario |
|---|---|---|
| `1` (or any other) | `getMonthlyReturnForEdit-200-response.json` | Standard return with subcontractors and a prior `submissionId` — resubmission path |
| `3` | `getMonthlyReturnForEdit-nosubmission-200-response.json` | Standard return with subcontractors but **no** prior `submissionId` — new submission path |
| `4` | `getMonthlyReturnForEdit-nil-200-response.json` | Nil return, no subcontractors, no prior `submissionId` — routes to MRAR06 or MRAR06b depending on `isOriginalNilReturn` |

**Endpoint**: `POST /cis/monthly-return/standard/create`

**Description**: Creates a standard monthly return record in the monthly return table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: any valid Tax Office no.
- Identifier Name: TaxOfficeReference
- Identifier Value: any valid Tax Office ref.

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11
}
```
- Response status: `201`
- Response body: empty body
```

**Endpoint**: `POST /cis/monthly-return/update`

**Description**: Updates an existing nil monthly return record.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body (same shape as create):
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11,
  "amendment": "N",
  "decInformationCorrect": "true",
  "decNilReturnNoPayments": "true",
  "nilReturnIndicator": "Y",
  "status": "STARTED"
}
```
- Response status: `204`
- Response body: _empty_

**Endpoint**: `POST /cis/monthly-return-complete`

**Description**: Returns the complete monthly return data for a submitted return, including scheme, monthly return, subcontractors, monthly return items, and submission details.

#### Happy Path

- Affinity Group: Organisation / Agent
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber (for Organisation) or IRAgentReference (for Agent)
- Identifier Value: any valid Tax Office no. or any valid IRAgentReference
- Identifier Name: TaxOfficeReference (for Organisation) or none (for Agent)
- Identifier Value: any valid Tax Office ref. (for Organisation)

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "900001",
  "taxYear": 2025,
  "taxMonth": 1,
  "amendment": "N"
}
```
- For `taxMonth` = `2` or `9`, the stub returns a nil return response (`getMonthlyReturnComplete-nil-200-response.json`).
- For all other `taxMonth` values, the stub returns a standard return response (`getMonthlyReturnComplete-200-response.json`).

- Response status: `200`
- Response body: `resources/getMonthlyReturnComplete-200-response.json`

**Endpoint**: `POST /cis/monthly-return-item/delete`

**Description**: Delete a record in the monthly return item table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: any valid Tax Office no.
- Identifier Name: TaxOfficeReference
- Identifier Value: any valid Tax Office ref.

- or

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11,
  "amendment": "N",
  "resourceReference": "123"
}
```
- Response status: `204`
- Response body: empty body


**Endpoint**: `POST /cis/monthly-returns/unsubmitted/delete`

**Description**: Delete unsubmitted monthly return.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: any valid Tax Office no.
- Identifier Name: TaxOfficeReference
- Identifier Value: any valid Tax Office ref.

- or

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11,
  "amendment": "N"
}
```
- Response status: `204`
- Response body: _empty_

**Endpoint**: `POST /cis/amend-monthly-return/create`

**Description**: Create an amended record in the monthly return table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: any valid Tax Office no.
- Identifier Name: TaxOfficeReference
- Identifier Value: any valid Tax Office ref.

- or

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId":  "1",
  "taxYear": 2025,
  "taxMonth": 11,
  "version": 0
}
```
- Response status: `201`
- Response body: empty body

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

**Endpoint**: `POST /monthly-return/nil/create`

**Description**: Creates a monthly return record in the monthly return table.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: AB123456

To trigger the happy path, ensure you provide a valid request body, below is an example of a valid request body:
```json
{
  "instanceId":  1,
  "taxYear": 2025,
  "taxMonth": 11,
  "amendment": "N",
  "createResourceReferences": [1,2,3],
  "deleteResourceReferences": [4,5,6]
}
```
- Response status: `204`
- Response body: empty body

**Endpoint**: `POST /cis/retrieve-submitted-monthly-returns-data`

**Description**: Returns the Submitted Monthly Returns Data

#### Happy Path

- Affinity Group: Organisation / Agent
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber (for Organisation) or IRAgentReference (for Agent)
- Identifier Value: any valid Tax Office no. or any valid IRAgentReference
- Identifier Name: TaxOfficeReference (for Organisation) or none (for Agent)
- Identifier Value: any valid Tax Office ref. (for Organisation)

To trigger the happy path, ensure you provide a valid request body similar to example below:
```json
{
  "instanceId": "900001",
  "taxYear": 2025,
  "taxMonth": 1,
  "amendment": "Y"
}
```
- Response status: `200`
- Response body:
```json
{
  "scheme":
  {
    "schemeId": 13343,
    "instanceId": "900001",
    "accountsOfficeReference": "125PA12345000",
    "taxOfficeNumber": "101",
    "taxOfficeReference": "AB0001",
    "utr": "1234657890",
    "name": "company",
    "emailAddress": "test@test",
    "displayWelcomePage": "N",
    "prePopCount": 3,
    "prePopSuccessful": "Y",
    "subcontractorCounter": 1,
    "verificationBatchCounter": 0,
    "lastUpdate": "2026-01-05T10:23:56Z",
    "version": 3
  },
  "monthlyReturn": [
    {
      "monthlyReturnId": 30001,
      "taxYear": 2025,
      "taxMonth": 1,
      "nilReturnIndicator": "N",
      "decEmpStatusConsidered": "Y",
      "decAllSubsVerified": "Y",
      "decInformationCorrect": "Y",
      "decNoMoreSubPayments": "Y",
      "decNilReturnNoPayments": "N",
      "status": "STARTED",
      "lastUpdate": "2026-01-01T23:24:56",
      "amendment": "N"
    }
  ],
  "monthlyReturnItems": [
    {
      "monthlyReturnId": 30001,
      "monthlyReturnItemId": 1,
      "totalPayments": "10,000.00",
      "costOfMaterials": "100.00",
      "totalDeducted": "100.00",
      "unmatchedTaxRateIndicator": "Y",
      "subcontractorId": 10903,
      "subcontractorName": "BuildRight Construction",
      "verificationNumber": "V1000000001",
      "itemResourceReference": 1
    },
    {
      "monthlyReturnId": 30001,
      "monthlyReturnItemId": 2,
      "totalPayments": "2000.00",
      "costOfMaterials": "20,000.00",
      "totalDeducted": "200.00",
      "unmatchedTaxRateIndicator": "Y",
      "subcontractorId": 10903,
      "subcontractorName": "Northern Trades Ltd",
      "verificationNumber": "V1000000001",
      "itemResourceReference": 2
    },
    {
      "monthlyReturnId": 30001,
      "monthlyReturnItemId": 3,
      "totalPayments": "3000.00",
      "costOfMaterials": "300.00",
      "totalDeducted": "30,000.00",
      "unmatchedTaxRateIndicator": "Y",
      "subcontractorId": 10903,
      "subcontractorName": "TyneWear Ltd",
      "verificationNumber": "V1000000001",
      "itemResourceReference": 3
    }
  ],
  "submission": [
    {
      "submissionId": 1,
      "submissionType": "MONTHLY_RETURN",
      "activeObjectId": 30001,
      "status": "SUBMITTED",
      "hmrcMarkGenerated": "9AjFBxlBohmcRZ8s/2IV0QaYzz0=",
      "hmrcMarkGgis": "9AjFBxlBohmcRZ8s/2IV0QaYzz0=",
      "emailRecipient": "test@test.com",
      "acceptedTime": "2026-04-06T09:50:08.081",
      "createDate": "2018-02-23T16:26:25",
      "lastUpdate": "2018-02-23T16:26:30",
      "schemeId": 13343,
      "agentId": "-",
      "l_Migrated": 1,
      "submissionRequestDate": "2018-02-23T16:26:27"
    }
  ]
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
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ10650


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
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ10550


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2020,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "754",
  "taxOfficeReference": "EZ10550",
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
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ10450


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2040,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "754",
  "taxOfficeReference": "EZ10450",
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
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ10400 / EZ10750


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2010,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "754",
  "taxOfficeReference": "tax office ref in the request enrollment",
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
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ10350


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 1000,
  "instanceId": "CIS-123",
  "accountsOfficeReference": "123MXY1234567XY",
  "taxOfficeNumber": "754",
  "taxOfficeReference": "EZ10350",
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
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ10700


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 1000,
  "instanceId": "CIS-123",
  "accountsOfficeReference": "123MXY1234567XY",
  "taxOfficeNumber": "754",
  "taxOfficeReference": "EZ10700",
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

#### Happy Path (name & utr is null, prepopSuccessful = 'N', subcontractorCounter = 1)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT207

or

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: EZ10500


- Request body: N/A
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "schemeId": 2040,
  "instanceId": "some-instance-id",
  "accountsOfficeReference": "123PA00123456",
  "taxOfficeNumber": "754",
  "taxOfficeReference": "EZ10500",
  "utr": null,
  "name": null,
  "emailAddress": null,
  "displayWelcomePage": null,
  "prePopCount": 0,
  "prePopSuccessful": "N",
  "subcontractorCounter": 1,
  "verificationBatchCounter": 0,
  "lastUpdate": null,
  "version": 0
}
```

**Endpoint**: `/cis/govtalkstatus/get`

**Description**: Get GovTalk Status Record. The stub allows triggering specific HTTP responses by providing special enrolment identifiers

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid value
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value

or 

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "userIdentifier": "123",
  "formResultID": "YE2025"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "govtalk_status": [
    {
      "userIdentifier": "1",
      "formResultID": "12890",
      "correlationID": "C742D5DEE7EB4D15B4F7EFD50B890525",
      "formLock": "false",
      "createDate": "2026-02-03T00:00:00",
      "endStateDate": null,
      "lastMessageDate": "2026-02-03T00:00:00",
      "numPolls": 0,
      "pollInterval": 0,
      "protocolStatus": "dataRequest",
      "gatewayURL": "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
    }
  ]
}
```

#### Error Scenario: 404 Not Found

Contractor scenario:

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 404
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value

or

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: AGT404

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "userIdentifier": "123",
  "formResultID": "YE2025"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `404`

**Endpoint**: `/cis/govtalkstatus/update-correlationID`

**Description**: Update correlationID, pollInterval, and gatewayUrl of GovTalk Status Record when userIdentifier and formResultId matches request.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid value
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value

or

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "userIdentifier": "123",
  "formResultID": "YE2025",
  "correlationID": "1234567890",
  "pollInterval": 5,
  "gatewayURL": "https://example.com/govtalk"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `204`
- Response body: N/A

#### Happy Path (No data found)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 404
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value

or

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: 404

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "userIdentifier": "123",
  "formResultID": "YE2025"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `200`
- Response body:
```json
{
  "govtalk_status": []
}
```

**Endpoint**: `/cis/govtalkstatus/reset`

**Description**: Resets the record in the GovTalk status.

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
  "userIdentifier": "1",
  "formResultID": "12890",
  "oldProtocolStatus": "dataRequest",
  "gatewayURL": "http://vat.chris.hmrc.gov.uk:9102/ChRIS/UKVAT/Filing/action/VATDEC"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `204`
- Response body: N/A

**Endpoint**: `/cis/govtalkstatus/update-status`

**Description**: Update GovTalk Status Record.

#### Happy Path

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: Any valid value
- Identifier Name: TaxOfficeReference
- Identifier Value: Any valid value

or

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any valid value

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "userIdentifier": "123",
  "formResultID": "YE2025",
  "endStateDate": "2026-02-03T00:00:00",
  "protocolStatus": "dataRequest"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `204`
- Response body: N/A

**Endpoint**: `/cis/govtalkstatus/update-statistics`

**Description**: Updates the GovTalk status statistics including polling information.

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
  "userIdentifier": "test-user-123",
  "formResultID": "FORM-12345",
  "lastMessageDate": "2026-02-16T10:30:00",
  "numPolls": 5,
  "pollInterval": 30,
  "gatewayURL": "http://localhost:9712/submission/ChRIS/CISR/Filing/sync/CIS300MR"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `204`

**Endpoint**: `POST /cis/govtalkstatus/create`

**Description**: Creates a record in the GovTalk status.

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
  "userIdentifier": "1",
  "formResultID": "12890",
  "correlationID": "128903445",
  "gatewayURL": "http://vat.chris.hmrc.gov.uk:9102/ChRIS/UKVAT/Filing/action/VATDEC"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `201`
- Response body: N/A

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

**Endpoint**: `POST /cis/subcontractor/create-and-update`

**Description**: Create and update subcontractor

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

- Request body: N/A

#### Happy Path

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "cisId": "200",
  "subcontractorType": "soletrader",
  "firstName": "Steve",
  "secondName": "James",
  "surname": "Smith",
  "addressLine1": "No 32",
  "addressLine2": "Street 1",
  "addressLine3": "Main Place",
  "addressLine4": "London",
  "postcode": "ABC 123",
  "nino": "AA1234567A",
  "utr": "8888888888",
  "worksReferenceNumber": "1234567-AB",
  "emailAddress": "test@test.com",
  "phoneNumber": "07446677888"
}
```
or 

```json
{
  "cisId": "200",
  "subcontractorType": "soletrader",
  "tradingName": "ABD Ltd",
  "addressLine1": "No 32",
  "addressLine2": "Street 1",
  "addressLine3": "Main Place",
  "addressLine4": "London",
  "postcode": "ABC 123",
  "nino": "AA1234567A",
  "utr": "8888888888",
  "worksReferenceNumber": "1234567-AB",
  "emailAddress": "test@test.com",
  "phoneNumber": "07446677888"
}
```

- Response status: `204`

**Endpoint**: `GET /cis/subcontractors/:cisId`

**Description**: Return a subcontractors list.

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

- Request body: N/A

#### Happy Path

- Response status: `200`
- Response body:
```json
{
  "subcontractors":
  [
    {
      "subcontractorId": "10101",
      "subbieResourceRef": "1",
      "type": "soletrader",
      "utr": "1111111111",
      "tradingName": "AAA",
      "version": "1"
    },
    {
      "subcontractorId": "20202",
      "subbieResourceRef": "2",
      "type": "soletrader",
      "utr": "2222222222",
      "tradingName": "BBB",
      "version": "2"
    },
    {
      "subcontractorId": "30303",
      "subbieResourceRef": "3",
      "type": "soletrader",
      "firstName": "John",
      "surname": "Smith",
      "addressLine1": "1 Main Street",
      "country": "GB",
      "postcode": "AA1 1AA",
      "version": "3"
    },
    {
      "subcontractorId": "40404",
      "subbieResourceRef": "4",
      "type": "soletrader",
      "utr": "4444444444",
      "tradingName": "CCC",
      "version": "4"
    },
    {
      "subcontractorId": "50505",
      "subbieResourceRef": "5",
      "type": "soletrader",
      "utr": "1211317359",
      "tradingName": "DDD",
      "version": "5"
    },
    {
      "subcontractorId": "60606",
      "subbieResourceRef": "6",
      "type": "soletrader",
      "utr": "3536885673",
      "tradingName": "EEE",
      "version": "6"
    }
  ]
}
```

**Endpoint**: `POST /cis/subcontractor/delete`

**Description**: delete subcontractor

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

#### Happy Path

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "abc-123",
  "subbieResourceRef": 10
}
```
- Response status: `204`
- Response body: _empty_

**Endpoint**: `POST cis/monthly-return-item/update `

**Description**: Updates the monthly return.

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
  "instanceId" :"abc-123",
  "taxYear" : 2025,
  "taxMonth" : 2,
  "amendment" : "N",
  "itemResourceReference" : "987654321L",
  "totalPayments" : "15000.00",
  "costOfMaterials" : "5000.00",
  "totalDeducted" : "2500.00",
  "subcontractorName" : "Example Subbie Ltd",
  "verificationNumber" : "V12345678"
}
```
- Response status: `200`
- Response body: N/A



### Get newest verification batch

**Endpoint**: `GET /cis/verification-batch/newest/:instanceId`

**Description**: Returns the newest verification batch for the given CIS instance id. The response includes:
- scheme (contractor registered under CIS),
- subcontractors,
- newest verification batch and its verifications,
- related submissions,
- related monthly return and monthly return submission.

#### Happy Path (Organisation)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: Any

- Response status: `200`
- Response body: `resources/verification/getNewestVerificationBatch-200-response.json`

#### Happy Path (Agent)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any

- Response status: `200`
- Response body: `resources/verification/getNewestVerificationBatch-200-response.json`

#### Unhappy Paths (Organisation)

- TaxOfficeNumber = `500` → Response status: `500`
```json
{ "message": "Unexpected error" }
```

### Get last submitted verification batch

**Endpoint**: `GET /cis/verification-batch/last/:instanceId`

**Description**: Returns the last submitted verification batch for the given CIS instance id. The response includes:
- scheme (contractor registered under CIS),
- subcontractors,
- last submitted verification batch and its verifications,
- related submissions,

#### Happy Path (Organisation)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: Any

- Response status: `200`
- Response body: `resources/verification/getLastSubmittedVerificationBatch-200-response`

#### Happy Path (Agent)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any

- Response status: `200`
- Response body: `resources/verification/getLastSubmittedVerificationBatch-200-response`

#### Unhappy Paths (Organisation)

- TaxOfficeNumber = `500` → Response status: `500`
```json
{ "message": "Unexpected error" }
```

### Create verification batch and verifications

**Endpoint**: `POST /cis/verification-batch/create`

**Description**: Creates a new verification batch and verification records for the provided subcontractor resource references (stubbed response).

#### Request body

```json
  {
  "instanceId": "123",
  "verificationResourceReferences": [1, 2, 3],
  "actionIndicator": "A"
  }
```


### Get current verification batch

**Endpoint**: `GET /cis/verification-batch/current/:instanceId`

**Description**: Returns the current verification batch for the given CIS instance id. The response includes:
- subcontractors,
- current verification batch and its verifications,


#### Happy Path (Organisation)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: Any

- Response status: `200`
- Response body (instance id not 1): `resources/verification/getCurrentVerificationBatch-200-verificationBatchStatus-started-response.json`
- Response body (instance id equal 1): `resources/verification/getCurrentVerificationBatch-200-verificationBatchStatus-none-response.json`

#### Happy Path (Agent)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any

- Response status: `200`
- Response body (instance id not 1): `resources/verification/getCurrentVerificationBatch-200-verificationBatchStatus-started-response.json`
- Response body (instance id equal 1): `resources/verification/getCurrentVerificationBatch-200-verificationBatchStatus-none-response.json`

#### Unhappy Paths (Organisation)

- TaxOfficeNumber = `500` → Response status: `500`
```json
{ "message": "Unexpected error" }
```

### Modify verifications

**Endpoint**: `POST /cis/verification-batch/modify`

**Description**: Delete and create verification records for the provided subcontractor resource references.

#### Happy Path (Organisation)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: Any

#### Happy Path (Agent)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any

#### Unhappy Paths (Organisation)

- TaxOfficeNumber = `500` → Response status: `500`
```json
{ "message": "Unexpected error" }
```

#### Request body
To trigger the happy path, ensure you provide a valid request body:

- Delete and create verifications
```json
{
  "instanceId" : "abc-123",
  "deleteVerifications": {
    "verificationResourceReferences": [111, 222]
  },
  "createVerifications" : {
    "verificationBatchResourceRef": 10,
    "verificationResourceReferences":[333, 444],
    "actionIndicator": null
  }
}
```

- Delete verifications only
```json
{
  "instanceId" : "abc-123",
  "deleteVerifications": {
    "verificationResourceReferences": [111, 222]
  }
}
```

- Create verifications only
```json
{
  "instanceId" : "abc-123",
  "createVerifications" : {
    "verificationBatchResourceRef": 10,
    "verificationResourceReferences":[333, 444],
    "actionIndicator": null
  }
}
```

- Response status: `204`
- Response body: _empty_


### Create submission for verification

**Endpoint**: `POST /cis/verification-batch/submission/create`

**Description**: Creates a **VERIFICATIONS** submission in FormP for the provided verification batch, then updates:
- the verification batch status/confirm flags, and
- each verification record in the payload (action indicator + proceed + subcontractor name).

This is executed in a single transaction in FormP Proxy.

#### Request body

```json
{
  "instanceId": "abc-123",
  "verificationBatchId": 99,
  "verificationBatchResourceRef": 7,
  "emailRecipient": "ops@example.com",
  "irMarkGenerated": "IR_MARK",
  "verifications": [
    {
      "subcontractorName": "ACME LTD",
      "verificationResourceRef": 111,
      "proceedVerification": "Y"
    },
    {
      "subcontractorName": "BOB BUILDER",
      "verificationResourceRef": 222,
      "proceedVerification": "N"
    }
  ],
  "agentId": null
}
```
#### Response body
```json
{
  "submissionId": 555
}
```

### Update verification submission

**Endpoint**: `POST /cis/verification/submission/update`

**Description**: Updates a verification submission record with the submittable status and optional GovTalk error details.

#### Happy Path (Organisation)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 200
- Identifier Name: TaxOfficeReference
- Identifier Value: Any

#### Happy Path (Agent)

- Affinity Group: Agent
- Enrolment Key: IR-PAYE-AGENT
- Identifier Name: IRAgentReference
- Identifier Value: Any

#### Unhappy Paths (Organisation)

- TaxOfficeNumber = `500` → Response status: `500`
```json
{ "message": "Unexpected error" }
```

- TaxOfficeNumber = `502` → Response status: `502`
```json
{ "message": "formp failed" }
```

#### Request body

To trigger the happy path, ensure you provide a valid request body:
```json
{
  "instanceId": "abc-123",
  "verificationBatchResourceRef": 7,
  "submittableStatus": "ACCEPTED"
}
```

Optional fields: `govtalkErrorCode`, `govtalkErrorType`, `govtalkErrorMessage`.

- Response status: `204`
- Response body: _empty_

**Endpoint**: `POST /cis/verification/proceed-with-insufficient-data  `

**Description**: Proceed Verification with insufficient data.

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
  "instanceId": "1",
  "verificationBatchResourceRef": 9,
  "verificationResourceRef": 10,
  "proceed": "Y"
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `204`
- Response body: N/A

### Process verification response from ChRIS

**Endpoint**: `POST /cis/verification/response/process`

**Description**: Processes the ChRIS verification response in FormP. This updates:
- the existing **VERIFICATIONS** submission with the response status and GovTalk error details,
- the related verification batch status, and
- the related subcontractor/verification details returned from ChRIS.

This is executed in a single transaction in FormP Proxy.

#### Request body

```json
{
  "instanceId": "abc-123",
  "submissionType": "VERIFICATIONS",
  "activeObjectId": 99,
  "hmrcMarkGenerated": "IR_MARK_GENERATED",
  "hmrcMarkGgis": "IR_MARK_GGIS",
  "emailRecipient": "ops@example.com",
  "submissionRequestDate": "2026-06-15T10:00:00",
  "acceptedTime": "2026-06-15T10:05:00Z",
  "agentId": null,
  "submittableStatus": "ACCEPTED",
  "govTalkErrorCode": null,
  "govTalkErrorType": null,
  "govTalkErrorMessage": null,
  "verifBatchResourceRef": 7,
  "verificationResourceRef": 111,
  "subbieResourceRef": 222,
  "matched": "Y",
  "verificationNumber": "V123456",
  "taxTreatment": "NET",
  "actionIndicator": "VERIFY",
  "proceed": "Y",
  "subcontractorName": "ACME LTD"
}
```

#### Response
- 204 No Content

### Get submission with verification batch

**Endpoint**: `GET /cis/verification/submission-batch/:instanceId/:verificationBatchResourceRef`

**Description**: Returns the submission details with the related verification batch, verifications, subcontractors and scheme details for the given CIS instance id and verification batch resource reference.

The response includes:

* scheme,
* subcontractors,
* verifications,
* verification batch,
* submission.

#### Happy Path (Organisation)

* Affinity Group: Organisation
* Enrolment Key: HMRC-CIS-ORG
* Identifier Name: TaxOfficeNumber
* Identifier Value: 200
* Identifier Name: TaxOfficeReference
* Identifier Value: Any
* Request body: N/A
* Response status: `200`
* Response body: `resources/verification/getSubmissionWithVerificationBatch-200-response.json`

#### Happy Path (Agent)

* Affinity Group: Agent
* Enrolment Key: IR-PAYE-AGENT
* Identifier Name: IRAgentReference
* Identifier Value: Any
* Request body: N/A
* Response status: `200`
* Response body: `resources/verification/getSubmissionWithVerificationBatch-200-response.json`

#### Unhappy Paths (Organisation)

* TaxOfficeNumber = `500` → Response status: `500`

```json
{
  "message": "Unexpected error"
}
```

* TaxOfficeNumber = `502` → Response status: `502`

```json
{
  "message": "formp failed"
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
                    <AOref>123PA12345678</AOref>
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

###### HTTP 200 - Fatal Error (will trigger polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 755
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


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
                <Number>1001</Number>
                <Type>fatal</Type>
                <Text>Forced fatal error (stub)</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```
###### HTTP 200 - Recoverable Error with error number 3000 (will trigger polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 759
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


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
                <Number>3000</Number>
                <Type>fatal</Type>
                <Text>The processing of your document submission failed. Please re-submit</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```

###### HTTP 200 - Recoverable Error with error number 2005 (will trigger polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 760
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


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
                <Number>2005</Number>
                <Type>fatal</Type>
                <Text>
                    The Transaction Engine has not received an acknowledgement of your submission from
                    the back-end system within the permitted timescale. Either resubmit or contact the
                    appropriate organisation directly to determine if your submission has been accepted.
                </Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```

###### HTTP 200 - Recoverable Error with error number 1000 (will trigger polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 761
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


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
                <Number>1000</Number>
                <Type>fatal</Type>
                <Text>System failure. The submission of this document has failed due to an internal system error.</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```

###### HTTP 200 - Department Error (will trigger Polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 756
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


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

###### HTTP 200 - irMark Miss Match Error (will trigger Polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 757
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


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

###### HTTP 200 - Acknowledgment (will trigger Polling and poll until time out)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 758
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


- Request body: **See valid request body above.**


- Response status: `200`
- Response body:

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>CISR</Class>
            <Qualifier>acknowledgement</Qualifier>
            <Function>submit</Function>
            <CorrelationID>[correlationId]</CorrelationID>
            <ResponseEndPoint PollInterval="5">[pollUrl]</ResponseEndPoint>
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

###### HTTP 200 - Fatal Error (no Polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00125**


- Request body: **See valid request body above.**


- Response status: `200`
- Response body:

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
            <ResponseEndPoint PollInterval="2">http://localhost:9712/submission/ChRIS/IR-CIS-CIS300MR/Filing/data/true</ResponseEndPoint>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>2025-11-25T12:03:25.242</GatewayTimestamp>
        </MessageDetails>
    </Header>
    <GovTalkDetails>
        <Keys/>
        <GovTalkErrors>
            <Error>
                <RaisedBy>Gateway</RaisedBy>
                <Number>3000</Number>
                <Type>fatal</Type>
                <Text>Forced fatal error (stub)</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```
**Endpoint**: `POST /submission/ChRIS/CISR/Filing/sync/CISVERIFY`

**Description**: A soap message constructed for subcontractor verification and submitted to the ChRIS service.

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
            <Class>IR-CIS-VERIFY</Class>
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
            <CISrequest>
                <Contractor>
                    <UTR>1123456789</UTR>
                    <AOref>123PA12345678</AOref>
                </Contractor>
                <Subcontractor>
                    <Action>match</Action>
                    <Type>soletrader</Type>
                    <Name>
                        <Fore>A</Fore>
                        <Sur>Alice</Sur>
                    </Name>
                    <UTR>1111111111</UTR>
                    <NINO>PX123456A</NINO>
                </Subcontractor>
                <Subcontractor>
                    <Action>match</Action>
                    <Type>soletrader</Type>
                    <Name>
                        <Fore>B</Fore>
                        <Sur>Bob</Sur>
                    </Name>
                    <UTR>2222222222</UTR>
                    <NINO>PX223456A</NINO>
                </Subcontractor>
                <Declaration>yes</Declaration>
            </CISrequest>
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
        <Class>IR-CIS-VERIFY</Class>
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

###### HTTP 200 - Fatal Error (will trigger polling)

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 755
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


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
            <Class>IR-CIS-VERIFY</Class>
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
                <Number>1001</Number>
                <Type>fatal</Type>
                <Text>Forced fatal error (stub)</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```

**Endpoint**: `POST /submission/ChRIS/poll/IR-CIS-VERIFY/:count`

**Description**: A soap message constructed for subcontractor verification and submitted to the ChRIS service.

Ensure you provide a valid request body:
```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
      <MessageDetails>
        <Class>IR-CIS-VERIFY</Class>
        <Qualifier>poll</Qualifier>
        <Function>submit</Function>
        <CorrelationID>C0A341CF946B46A18BF01C270D15B1E6</CorrelationID>
        <Transformation>XML</Transformation>
      </MessageDetails>
      <SenderDetails/>
    </Header>
    <GovTalkDetails>
      <Keys/>
    </GovTalkDetails>
</GovTalkMessage>
```

#### Happy Path - SUBMITTED 

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


- Request body: **See valid request body above.**

- Response status: `200`
- Response body:

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-VERIFY</Class>
            <Qualifier>response</Qualifier>
            <Function>submit</Function>
            <CorrelationID>[correlationId]</CorrelationID>
            <ResponseEndPoint/>
            <GatewayTimestamp>2025-12-01T11:41:05.431</GatewayTimestamp>
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
                            <dsig:DigestValue>[digestValue]</dsig:DigestValue>
                        </dsig:Reference>
                    </dsig:SignedInfo>
                    <dsig:SignatureValue>xjd0lzhAQrnHZsE5inNCOVsmwcQ9HTu+CFUoyqEcOhVvxj2jvYGcjkhu7sZkZJ9RBjBcEP/eQTbesMTrnUgofuMqaROt8ZyD/RJKFIwh5TtNzYzDM55Pa3GDd2ZXcmfR38mS9KPwqc5Ty+Eqv69FxqivCQk46H20F8fnWnx85H4=</dsig:SignatureValue> <dsig:KeyInfo>
                    <dsig:X509Data>
                        <dsig:X509Certificate>MIID0zCCAzygAwIBAgIBADANBgkqhkiG9w0BAQQFADCBqDELMAkGA1UEBhMCbmwxFjAUBgNVBAgTDU5vb3JkLUhvbGxhbmQxFzAVBgNVBAoTDk1vYmlsZWZpc2guY29tMRAwDgYDVQQHEwdaYWFuZGFtMRIwEAYDVQQLEwlNYXJrZXRpbmcxGzAZBgNVBAMTEnd3dy5tb2JpbGVmaXNoLmNvbTElMCMGCSqGSIb3DQEJARYWY29udGFjdEBtb2JpbGVmaXNoLmNvbTAeFw0xMTEwMTMxMDI2NTZaFw0xMjEwMTIxMDI2NTZaMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3o83CcmMMOC/fnjVv2puirJTs36+al6RDBe2tbFLKKODd29DZbmH9/6R77VPZACvXxBdRzMls//YRVHoJyJVudy+B4siUfHP80pssg2ZXCmCtUZGS71ohmlHcGQGTVLj8wmicf/DfmMAgq19OFZJP5LUn3md/MQBOUYrFXt21dQIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFAIuWYA/BMx8Gn/YOILevnJthkIZMIHVBgNVHSMEgc0wgcqAFAIuWYA/BMx8Gn/YOILevnJthkIZoYGupIGrMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEEBQADgYEABCb+f82DKWIWBczTeKGc6Ka5U7oys/itCY7XOYMIvXYPj+tb+5PBrmTO3jZNoZso9cYYFcDGXySbk6wSZiEPlbMqkoYE62E6dVXAmbza3ZNNIX/yEpkE3ZeBBtYzJMPQme9jrMgwgMIhgVzQNL2KPkbWOtQfoYgnThHQKLBry6Y=</dsig:X509Certificate>
                    </dsig:X509Data>
                </dsig:KeyInfo>
                </dsig:Signature>
                <Message code="1">HMRC has received the IR-CIS-VERIFY document ref: 123/GL01 at 08.46 on 06/04/2017. The associated IRmark was: IT53IRTZFGY7FJYJS2DG5GPY5FNBHPXT. We advise you to keep this receipt in both electronic and hardcopy versions for your records. You may wish to use them to identify your submission in the future.</Message>
            </IRmarkReceipt>
            <Message code="9004">The Subcontractor Verification has been processed and passed full validation</Message>
            <AcceptedTime>2017-04-06T08:46:08.081</AcceptedTime>
            <ResponseData>
                <CISresponse xmlns="http://www.govtalk.gov.uk/taxation/CISresponse">
                    <Contractor>
                        <UTR>1234657890</UTR>
                        <AOref>125PA12345000</AOref>
                    </Contractor>
                    <Subcontractor>
                        <Name>
                            <Fore>Noel</Fore>
                            <Sur>Armstrong</Sur>
                        </Name>
                        <TradingName>DBB Construction</TradingName>
                        <UTR>8786438047</UTR>
                        <NINO>AB623456C</NINO>
                        <Matched>Y</Matched>
                        <TaxTreatment>net</TaxTreatment>
                        <VerificationNumber>V1000000007</VerificationNumber>
                    </Subcontractor>
                </CISresponse>
            </ResponseData>
        </SuccessResponse>
    </Body>
</GovTalkMessage>
```

**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/

#### Happy Path - SUBMITTED_NO_RECEIPT

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 757
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


- Request body: **See valid request body above.**

- Response status: `200`
- Response body:

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-VERIFY</Class>
            <Qualifier>response</Qualifier>
            <Function>submit</Function>
            <CorrelationID>[correlationId]</CorrelationID>
            <ResponseEndPoint/>
            <GatewayTimestamp>2025-12-01T11:41:05.431</GatewayTimestamp>
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
                            <dsig:DigestValue/>
                        </dsig:Reference>
                    </dsig:SignedInfo>
                    <dsig:SignatureValue>xjd0lzhAQrnHZsE5inNCOVsmwcQ9HTu+CFUoyqEcOhVvxj2jvYGcjkhu7sZkZJ9RBjBcEP/eQTbesMTrnUgofuMqaROt8ZyD/RJKFIwh5TtNzYzDM55Pa3GDd2ZXcmfR38mS9KPwqc5Ty+Eqv69FxqivCQk46H20F8fnWnx85H4=</dsig:SignatureValue> <dsig:KeyInfo>
                    <dsig:X509Data>
                        <dsig:X509Certificate>MIID0zCCAzygAwIBAgIBADANBgkqhkiG9w0BAQQFADCBqDELMAkGA1UEBhMCbmwxFjAUBgNVBAgTDU5vb3JkLUhvbGxhbmQxFzAVBgNVBAoTDk1vYmlsZWZpc2guY29tMRAwDgYDVQQHEwdaYWFuZGFtMRIwEAYDVQQLEwlNYXJrZXRpbmcxGzAZBgNVBAMTEnd3dy5tb2JpbGVmaXNoLmNvbTElMCMGCSqGSIb3DQEJARYWY29udGFjdEBtb2JpbGVmaXNoLmNvbTAeFw0xMTEwMTMxMDI2NTZaFw0xMjEwMTIxMDI2NTZaMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3o83CcmMMOC/fnjVv2puirJTs36+al6RDBe2tbFLKKODd29DZbmH9/6R77VPZACvXxBdRzMls//YRVHoJyJVudy+B4siUfHP80pssg2ZXCmCtUZGS71ohmlHcGQGTVLj8wmicf/DfmMAgq19OFZJP5LUn3md/MQBOUYrFXt21dQIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFAIuWYA/BMx8Gn/YOILevnJthkIZMIHVBgNVHSMEgc0wgcqAFAIuWYA/BMx8Gn/YOILevnJthkIZoYGupIGrMIGoMQswCQYDVQQGEwJubDEWMBQGA1UECBMNTm9vcmQtSG9sbGFuZDEXMBUGA1UEChMOTW9iaWxlZmlzaC5jb20xEDAOBgNVBAcTB1phYW5kYW0xEjAQBgNVBAsTCU1hcmtldGluZzEbMBkGA1UEAxMSd3d3Lm1vYmlsZWZpc2guY29tMSUwIwYJKoZIhvcNAQkBFhZjb250YWN0QG1vYmlsZWZpc2guY29tggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEEBQADgYEABCb+f82DKWIWBczTeKGc6Ka5U7oys/itCY7XOYMIvXYPj+tb+5PBrmTO3jZNoZso9cYYFcDGXySbk6wSZiEPlbMqkoYE62E6dVXAmbza3ZNNIX/yEpkE3ZeBBtYzJMPQme9jrMgwgMIhgVzQNL2KPkbWOtQfoYgnThHQKLBry6Y=</dsig:X509Certificate>
                    </dsig:X509Data>
                </dsig:KeyInfo>
                </dsig:Signature>
                <Message code="1">HMRC has received the IR-CIS-VERIFY document ref: 123/GL01 at 08.46 on 06/04/2017. The associated IRmark was: . We advise you to keep this receipt in both electronic and hardcopy versions for your records. You may wish to use them to identify your submission in the future.</Message>
            </IRmarkReceipt>
            <Message code="9004">The Subcontractor Verification has been processed and passed full validation</Message>
            <AcceptedTime>2017-04-06T08:46:08.081</AcceptedTime>
            <ResponseData>
                <CISresponse xmlns="http://www.govtalk.gov.uk/taxation/CISresponse">
                    <Contractor>
                        <UTR>1234657890</UTR>
                        <AOref>125PA12345000</AOref>
                    </Contractor>
                    <Subcontractor>
                        <Name>
                            <Fore>Noel</Fore>
                            <Sur>Armstrong</Sur>
                        </Name>
                        <TradingName>DBB Construction</TradingName>
                        <UTR>8786438047</UTR>
                        <NINO>AB623456C</NINO>
                        <Matched>Y</Matched>
                        <TaxTreatment>net</TaxTreatment>
                        <VerificationNumber>V1000000007</VerificationNumber>
                    </Subcontractor>
                </CISresponse>
            </ResponseData>
        </SuccessResponse>
    </Body>
</GovTalkMessage>
```

**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/

#### Happy Path - DELETE

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 754
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**

- Request body:
```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
  <EnvelopeVersion>2.0</EnvelopeVersion>
  <Header>
    <MessageDetails>
      <Class>IR-CIS-VERIFY</Class>
      <Qualifier>request</Qualifier>
      <Function>delete</Function>
      <Transformation>XML</Transformation>
    </MessageDetails>
    <SenderDetails/>
  </Header>
  <GovTalkDetails>
    <Keys/>
  </GovTalkDetails>
</GovTalkMessage>
```

- Response status: `200`
- Response body:

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-VERIFY</Class>
            <Qualifier>response</Qualifier>
            <Function>delete</Function>
            <TransactionID/>
            <CorrelationID>[correlationId]</CorrelationID>
            <ResponseEndPoint PollInterval="10"></ResponseEndPoint>
            <GatewayTimestamp>2001-02-25T16:32:18.795</GatewayTimestamp>
        </MessageDetails>
        <SenderDetails/>
    </Header>
    <GovTalkDetails>
        <Keys/>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```



**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/


#### Unhappy Path - FATAL_ERROR

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 756
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


- Request body: **See valid request body above.**

- Response status: `200`
- Response body:

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-VERIFY</Class>
            <Qualifier>error</Qualifier>
            <Function>submit</Function>
            <TransactionID></TransactionID>
            <CorrelationID>[correlationId]</CorrelationID>
            <ResponseEndPoint PollInterval="2">[pollingUrlHost]submission/ChRIS/IR-CIS-VERIFY/Filing/data/true</ResponseEndPoint>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>2025-12-01T10:41:42.885</GatewayTimestamp>
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

#### Unhappy Path - DEPARTMENTAL_ERROR

- Affinity Group: Organisation
- Enrolment Key: HMRC-CIS-ORG
- Identifier Name: TaxOfficeNumber
- Identifier Value: 755
- Identifier Name: TaxOfficeReference
- Identifier Value: **EZ00100**


- Request body: **See valid request body above.**

- Response status: `200`
- Response body:

```xml
<GovTalkMessage xmlns="http://www.govtalk.gov.uk/CM/envelope">
    <EnvelopeVersion>2.0</EnvelopeVersion>
    <Header>
        <MessageDetails>
            <Class>IR-CIS-VERIFY</Class>
            <Qualifier>error</Qualifier>
            <Function>submit</Function>
            <TransactionID></TransactionID>
            <CorrelationID>[correlationId]</CorrelationID>
            <ResponseEndPoint PollInterval="2">[pollingUrlHost]submission/ChRIS/IR-CIS-VERIFY/Filing/data/true</ResponseEndPoint>
            <Transformation>XML</Transformation>
            <GatewayTimestamp>2025-12-01T10:45:18.799</GatewayTimestamp>
        </MessageDetails>
    </Header>
    <GovTalkDetails>
        <Keys/>
        <GovTalkErrors>
            <Error>
                <RaisedBy>Gateway</RaisedBy>
                <Number>1001</Number>
                <Type>fatal</Type>
                <Text>Forced fatal error (stub)</Text>
                <Location></Location>
            </Error>
        </GovTalkErrors>
    </GovTalkDetails>
    <Body/>
</GovTalkMessage>
```

**{environmentUrl}**:

local = http://localhost:6997/

staging = https://construction-industry-scheme-external-stub.protected.mdtp:443/


**Endpoint**: `/hmrc/email`

**Description**: Send email.

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
  "to": ["email1@test.com", "email2@test.com"],
  "templateId": "emailTemplateId",
  "parameters": {
    "year": "2026",
    "month": "March"
  }
}
```
- Enrolments: request must have either HMRC-CIS-ORG or IR-PAYE-AGENT Enrolment

- Response status: `202`
- Response body: N/A

### Iass 


### Client Exchange Proxy

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

**Endpoint**: `POST /cis/subcontractor/update`

**Description**: Updates an existing subcontractor and returns the updated subcontractor version.

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

#### Happy Path

To trigger the happy path, ensure you provide a valid request body:

```json
{
  "cisId": "abc-123",
  "subcontractor": {
    "subcontractorId": 999,
    "subbieResourceRef": 10,
    "utr": "1234567890",
    "firstName": "John",
    "secondName": "James",
    "surname": "Smith",
    "nino": "AA123456A",
    "tradingName": "Smith Trading",
    "subcontractorType": "soletrader",
    "addressLine1": "No 32",
    "addressLine2": "Street 1",
    "addressLine3": "Main Place",
    "addressLine4": "London",
    "postcode": "ABC 123",
    "country": "GB",
    "emailAddress": "test@test.com",
    "phoneNumber": "07446677888",
    "mobilePhoneNumber": "07123456789",
    "worksReferenceNumber": "1234567-AB",
    "version": 1
  }
}