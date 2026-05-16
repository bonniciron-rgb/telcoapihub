# Telco Network API Hub

A two-sided network API hub aligned to the **GSMA Open Gateway** initiative and
**CAMARA** API specifications. Enterprises integrate once on the northbound
side; the hub aggregates supply from operators and aggregators on the
southbound side, and meters every call for tiered billing.

This is an **MVP scaffold** — the architecture, orchestration and billing are
real and runnable; the southbound supplier is a deterministic mock until live
operator/aggregator integrations are connected.

## MVP scope

| Capability | CAMARA API | Path |
|---|---|---|
| Number verification | `number-verification` | `POST /number-verification/v0/verify`, `GET /number-verification/v0/device-phone-number` |
| KYC match | `kyc-match` | `POST /kyc-match/v0/match` |
| Age verification | `kyc-age-verification` | `POST /kyc-age-verification/v0/verify` |
| SIM swap | `sim-swap` | `POST /sim-swap/v2/check`, `POST /sim-swap/v2/retrieve-date` |
| Metered billing | — | `GET /admin/usage`, `POST /admin/invoices`, `GET /admin/invoices` |
| Consent management | — | `POST /consent`, `GET /consent`, `DELETE /consent/{id}` |

## Architecture

```
Enterprises ─HTTP/API key─▶ Controllers ─▶ HubService ─▶ ConsentService
                                              │
                                       RoutingEngine ─▶ SouthboundAdapter ─▶ operators / aggregators
                                              │
                                       MeteringService ─▶ RatingService ─▶ InvoicingService
```

- **Northbound** (`api/`, `security/`) — CAMARA REST surface, API-key auth.
- **Orchestration** (`service/`, `consent/`, `routing/`) — consent enforcement,
  hybrid supplier selection (direct operator preferred, aggregator fallback).
- **Southbound** (`adapter/`) — one adapter per supplier, normalising native
  payloads to CAMARA. `RestSupplierAdapter` is the base for live integrations.
- **Billing** (`billing/`) — per-call CDR capture, graduated tier rating,
  invoice generation.

## Run it

```bash
mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:telcoapihub`)

The in-memory database is seeded on each boot with a demo enterprise, pricing
plan, mock supplier and consent records.

| Credential | Value | Header |
|---|---|---|
| Enterprise API key | `demo-enterprise-key` | `X-API-Key` |
| Admin API key | `demo-admin-key` | `X-API-Key` |
| Pre-consented test number | `+447700900123` | — |

### Example

```bash
curl -X POST localhost:8080/sim-swap/v2/check \
  -H 'Content-Type: application/json' -H 'X-API-Key: demo-enterprise-key' \
  -d '{"phoneNumber":"+447700900123","maxAge":240}'

curl -X POST 'localhost:8080/admin/invoices?enterpriseId=1&periodStart=2026-05-01&periodEnd=2026-05-31' \
  -H 'X-API-Key: demo-admin-key'
```

## Scaffold stand-ins (not production-ready)

- **Auth** — API keys stand in for CAMARA ICM (OAuth2/OIDC with client
  credentials and CIBA/auth-code flows).
- **Consent** — direct grant API stands in for ICM consent capture.
- **Supplier** — `MockSupplierAdapter` returns deterministic results; real
  operator/aggregator adapters extend `RestSupplierAdapter`.
- **Number routing** — `MsisdnResolver` keys off dialling prefix; production
  needs HLR / number-portability lookup.
- **Persistence** — in-memory H2; swap for a managed database.
