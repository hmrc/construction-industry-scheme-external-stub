
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

### Iass 


### Client Exchange Proxy

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").