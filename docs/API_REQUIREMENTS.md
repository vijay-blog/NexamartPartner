# NexaMart Admin + Delivery — API Requirements and Gap Analysis (Phase 0)

## Audit outcome

No backend/API implementation files are present in this repository, so endpoint discovery could not be performed from source here.

This document defines required backend contracts for this app. APIs below must be provided by the existing NexaMart backend (preferred) to avoid duplicate backend logic.

## Authentication and session

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/auth/login` | POST | Public | ADMIN, DELIVERY_PARTNER | Login with phone/email + password/OTP flow |
| `/api/v1/auth/send-otp` | POST | Public | ADMIN, DELIVERY_PARTNER | Send OTP when OTP-based auth is enabled |
| `/api/v1/auth/verify-otp` | POST | Public | ADMIN, DELIVERY_PARTNER | Verify OTP and issue tokens |
| `/api/v1/auth/refresh` | POST | Refresh token | ADMIN, DELIVERY_PARTNER | Rotate/refresh access token |
| `/api/v1/auth/logout` | POST | Bearer token | ADMIN, DELIVERY_PARTNER | Invalidate session/tokens |
| `/api/v1/auth/me` | GET | Bearer token | ADMIN, DELIVERY_PARTNER | Resolve authenticated user/role |

### Required login response

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "user": {
    "id": 123,
    "name": "string",
    "phone": "string",
    "role": "ADMIN"
  }
}
```

Allowed roles for this app:

- `ADMIN`
- `DELIVERY_PARTNER`

Any other role (including `CUSTOMER`) must be rejected by backend and blocked by app.

## Authentication Contract (Phase 2)

### Confirmed from current project documentation

- Base API namespace is expected under `/api/v1/*`.
- Auth path family is expected under `/api/v1/auth/*`.
- App supports only `ADMIN` and `DELIVERY_PARTNER`.
- Conceptual login response includes `accessToken`, `refreshToken`, and `user.role`.

### Not yet confirmed (must be provided by backend team)

- Exact login request fields (phone vs email vs identifier key names)
- Password vs OTP-only vs hybrid authentication policy
- Refresh request payload field names and rotation policy
- Logout endpoint auth requirements and expected response shape
- Token expiry durations and refresh expiry behavior
- Standard auth error response payload schema
- OTP endpoint payloads (`send-otp`, `verify-otp`, `resend-otp`) if OTP is enabled

### Current app integration behavior for unconfirmed fields

- Authentication request body mapping is intentionally isolated behind `AuthRequestContract`.
- Default implementation is `PendingBackendAuthRequestContract` and blocks live auth calls until contract keys are configured.
- This avoids hard-coding guessed payload keys and allows backend contract wiring in one place when confirmed.

## Admin APIs

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/admin/dashboard` | GET | Bearer token | ADMIN | KPI summary counts/sales/assignments |
| `/api/v1/admin/orders` | GET | Bearer token | ADMIN | Orders list + filtering + pagination |
| `/api/v1/admin/orders/{id}` | GET | Bearer token | ADMIN | Detailed order view |
| `/api/v1/admin/orders/{id}/status` | PATCH | Bearer token | ADMIN | Update order status with transition validation |
| `/api/v1/admin/orders/{id}/assign-delivery` | POST | Bearer token | ADMIN | Assign delivery partner |
| `/api/v1/admin/orders/{id}/reassign-delivery` | POST | Bearer token | ADMIN | Reassign delivery partner |
| `/api/v1/admin/orders/{id}/cancel` | POST/PATCH | Bearer token | ADMIN | Cancel order where permitted |
| `/api/v1/admin/products` | GET/POST | Bearer token | ADMIN | List/create products |
| `/api/v1/admin/products/{id}` | GET/PATCH/DELETE | Bearer token | ADMIN | Product details/update/delete-deactivate |
| `/api/v1/admin/categories` | GET/POST | Bearer token | ADMIN | List/create categories |
| `/api/v1/admin/categories/{id}` | GET/PATCH/DELETE | Bearer token | ADMIN | Category details/update/delete |
| `/api/v1/admin/customers` | GET | Bearer token | ADMIN | Customer list/search/filter |
| `/api/v1/admin/customers/{id}` | GET/PATCH | Bearer token | ADMIN | Customer details/status updates if supported |
| `/api/v1/admin/delivery-partners` | GET | Bearer token | ADMIN | Partner list/search/filter |
| `/api/v1/admin/delivery-partners/{id}` | GET/PATCH | Bearer token | ADMIN | Partner details/status/verification management |
| `/api/v1/admin/reports/*` | GET | Bearer token | ADMIN | Reporting endpoints (sales/order ops) |

## Delivery partner APIs

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/delivery/dashboard` | GET | Bearer token | DELIVERY_PARTNER | Delivery dashboard counters and summary |
| `/api/v1/delivery/orders` | GET | Bearer token | DELIVERY_PARTNER | Assigned/active orders |
| `/api/v1/delivery/orders/{id}` | GET | Bearer token | DELIVERY_PARTNER | Delivery order details |
| `/api/v1/delivery/orders/{id}/accept` | POST | Bearer token | DELIVERY_PARTNER | Accept assigned order |
| `/api/v1/delivery/orders/{id}/picked-up` | POST/PATCH | Bearer token | DELIVERY_PARTNER | Mark picked up |
| `/api/v1/delivery/orders/{id}/out-for-delivery` | POST/PATCH | Bearer token | DELIVERY_PARTNER | Start final leg |
| `/api/v1/delivery/orders/{id}/delivered` | POST/PATCH | Bearer token | DELIVERY_PARTNER | Mark delivered (OTP/proof if needed) |
| `/api/v1/delivery/history` | GET | Bearer token | DELIVERY_PARTNER | Delivery history with date filters |
| `/api/v1/delivery/earnings` | GET | Bearer token | DELIVERY_PARTNER | Earnings summary and breakdown |
| `/api/v1/delivery/profile` | GET/PATCH | Bearer token | DELIVERY_PARTNER | Profile and availability updates |
| `/api/v1/delivery/availability` | PATCH | Bearer token | DELIVERY_PARTNER | ONLINE/OFFLINE state |

## Notifications APIs (if FCM used)

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/notifications/register-device` | POST | Bearer token | ADMIN, DELIVERY_PARTNER | Register device push token |
| `/api/v1/notifications` | GET | Bearer token | ADMIN, DELIVERY_PARTNER | Notification list/history |
| `/api/v1/notifications/{id}/read` | PATCH | Bearer token | ADMIN, DELIVERY_PARTNER | Mark notification read |

## Required request/response model families

- Auth: login, OTP send/verify, refresh, logout
- User/session: profile + role + permission set
- Dashboard metrics: counts/sales/delivery ops
- Orders: list/detail/timeline/status mutation
- Delivery assignments and transitions
- Product/category CRUD payloads
- Delivery partner CRUD/status payloads
- Customer list/detail payloads
- Notification payloads and click-routing metadata
- Optional delivery completion payloads: OTP/proof photo/notes

## Missing data from this repository (backend gap)

The following could not be confirmed from repository code:

- Actual backend base URL and API version
- Concrete endpoint paths/methods/payload schemas
- Existing role enum names and permission matrix
- Existing order/delivery status constants
- Refresh-token strategy and token expiry windows
- Notification infrastructure (FCM/SSE/WebSocket)
- Payment status/method enums and source-of-truth fields

Backend/API contract confirmation is required before implementing full production integrations in Phases 2+.
