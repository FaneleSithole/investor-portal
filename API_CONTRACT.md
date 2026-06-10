# Capital Flow — Spring Boot API Contract

This document defines all REST endpoints the `index.html` frontend calls via `fetch('/api/...')`.
Configure your Spring Boot `@RestController` classes to match these signatures.

---

## Base Path
All endpoints are prefixed with `/api`. Set in `application.properties`:
```
server.servlet.context-path=/api
```
Or use `@RequestMapping("/api")` on each controller.

---

## 1. User Profile
### GET `/api/user/profile`
Returns the logged-in investor's profile.
```json
{
  "firstName": "Thabo",
  "lastName": "Nkosi",
  "firmName": "Fanele & Partners"
}
```

---

## 2. Portfolio
### GET `/api/portfolio/summary`
```json
{
  "totalValue": 248500000,
  "growthPercent": 12.4,
  "fundCount": 5,
  "chartData": [
    { "label": "Jan", "value": 210000000 },
    { "label": "Feb", "value": 218000000 }
  ],
  "allocations": [
    { "label": "Private Equity", "percent": 45 },
    { "label": "Venture Capital", "percent": 35 },
    { "label": "Real Estate",     "percent": 20 }
  ],
  "performanceByClass": [
    { "name": "Alpha Growth Fund IV",       "committed": 50000000, "invested": 42500000, "irr": 24.2 },
    { "name": "Tech Ventures Series B",     "committed": 25000000, "invested": 15000000, "irr": 18.5 },
    { "name": "Global Real Estate Partners","committed": 75000000, "invested": 75000000, "irr": 8.1  }
  ],
  "recentActivity": [
    { "type": "Capital Call",  "source": "Alpha Growth Fund IV",   "date": "2023-10-24", "amount": -2500000 },
    { "type": "Distribution",  "source": "Tech Ventures Series B", "date": "2023-10-18", "amount":  850000  },
    { "type": "Document Ready","source": "Global Real Estate",     "date": "2023-10-15", "amount": 0        }
  ]
}
```

---

## 3. Withdrawals
### GET `/api/withdrawals/balance`
```json
{
  "availableBalance": 2450000.00,
  "growthPercent": 2.4
}
```

### GET `/api/withdrawals/transactions`
```json
[
  {
    "date": "2023-10-12",
    "amount": 150000.00,
    "bankName": "Chase",
    "lastFour": "4592",
    "status": "Pending"
  },
  {
    "date": "2023-09-01",
    "amount": 500000.00,
    "bankName": "GS",
    "lastFour": "1108",
    "status": "Completed"
  }
]
```
`status` values: `"Pending"` | `"Completed"` | `"Rejected"`

### GET `/api/accounts/linked`
```json
[
  { "id": "acc_001", "bankName": "JPMorgan Chase", "lastFour": "4592" },
  { "id": "acc_002", "bankName": "Goldman Sachs",  "lastFour": "1108" }
]
```

### POST `/api/withdrawals/request`
**Request body:**
```json
{
  "amount": 250000.00,
  "accountId": "acc_001",
  "reason": "Q3 Capital Distribution"
}
```
**Success 200:**
```json
{ "message": "Withdrawal request submitted", "referenceId": "WDR-2024-001" }
```
**Error 400/422:**
```json
{ "message": "Amount exceeds available balance" }
```

---

## 4. Compliance
### GET `/api/compliance/summary`
```json
{
  "passRate": 94,
  "rulesPassed": 124,
  "rulesWarning": 3,
  "rulesFailed": 1,
  "activeBreach": true,
  "breachMessage": "Fund Alpha (FA-2023) has exceeded the maximum single-issuer exposure limit of 15%. Current exposure is 16.2% in TechHoldings LLC."
}
```
Set `"activeBreach": false` when no breach is present; `breachMessage` can be `null`.

---

## 5. Reports
### GET `/api/reports`
```json
[
  {
    "id": "rpt_001",
    "title": "Q4 2023 Consolidated Statement",
    "description": "Comprehensive overview of portfolio performance ending Dec 31, 2023.",
    "type": "STATEMENT",
    "date": "2024-01-15",
    "fileSizeBytes": 2516582
  },
  {
    "id": "rpt_002",
    "title": "2023 Schedule K-1",
    "description": "Partner's share of income, deductions, credits, etc. for tax year 2023.",
    "type": "TAX_DOC",
    "date": "2024-03-01",
    "fileSizeBytes": 1153434
  },
  {
    "id": "rpt_003",
    "title": "Annual Performance Review",
    "description": "Deep dive into sector allocations and yield comparisons.",
    "type": "ANALYSIS",
    "date": "2024-01-20",
    "fileSizeBytes": 6082150
  }
]
```
`type` values: `"STATEMENT"` | `"TAX_DOC"` | `"ANALYSIS"`

### GET `/api/reports/{id}/download`
Returns the file as an octet-stream / PDF blob.
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="report.pdf"
```

### GET `/api/reports/download-all`
Returns a ZIP archive of all reports.
```
Content-Type: application/zip
Content-Disposition: attachment; filename="all-reports.zip"
```

---

## CORS (Development)
Add to your Spring Boot config to allow the frontend to call the API:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET","POST","PUT","DELETE");
    }
}
```

## Security
Protect all endpoints with Spring Security. The frontend should pass a JWT or session cookie. Example header:
```
Authorization: Bearer <token>
```
