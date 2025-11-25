
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
To trigger the happy path, ensure you provide a valid request body:
```json
{
  "taxOfficeNumber": "123",
  "taxOfficeReference": "AB456"
}
```
- Response status: `200`
- Response body:
```json
{
  "uniqueId" : "1",
  "taxOfficeNumber" : "123",
  "taxOfficeRef" : "AB456",
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
#### Unhappy Path
To trigger the unhappy paths, ensure you provide a valid request body.

The below error responses can be expected:

###### HTTP 500 Internal Server Error
```json
{
  "taxOfficeNumber": "500",
  "taxOfficeReference": "TAXPAY" // TODO
}
```

- Response status: `500`
- Response body:
```json
{
  "errorDetail": {
    "timestamp": "2025-11-23T18:15:41Z",
    "correlationId": "c182e731-2386-4359-8ee6-f911d6e5f4bc",
    "errorCode": "500",
    "errorMessage": "Unexpected error",
    "source": "Internal Server error"
  }
}
```


### FormP Proxy


### IASS 

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").