# NexaMart Admin + Delivery — Architecture

## Repository and platform baseline

This repository currently contains a **new native Android application shell** (not Flutter), with:

- Gradle Kotlin DSL build
- Single `:app` module
- Android package: `com.daily.nexamartpartner`
- No implemented feature code yet (only template tests/resources)

## Current technology identified

- Platform: Android (Kotlin)
- Build: Gradle (`build.gradle.kts`)
- UI baseline: Material Components (migrated in Phase 1 to Material 3 theme)
- Tests: JUnit + Android instrumentation test template

No backend service code exists in this repository; the Java Spring Boot backend is external and remains source of truth.

## System architecture

This app is intended to be a separate mobile client:

- Customer app (existing, external to this repository)
- Admin + Delivery app (this repository)
- Shared backend API (external to this repository)

The app consumes backend APIs only, with backend as source of truth for:

- Authentication/session
- Role authorization
- Orders/product/category/customer data
- Delivery lifecycle/assignments
- Notifications and operational events

## Authentication and role model

Expected backend-authenticated roles:

- `ADMIN`
- `DELIVERY_PARTNER`

Role must be returned from backend auth response and enforced by backend authorization controls.

Android-side defense in depth implemented:

- Root auth gate from centralized `AuthStateStore`
- Destination authorization policy (`AuthorizationPolicy`)
- Runtime navigation guard (`NavigationGuard`) against unauthorized/unknown destinations
- Session clear + root reset on logout and invalid session

## Root navigation architecture (Phase 3)

Navigation technology: **XML Navigation Component**.

Root graph structure:

- `splashFragment` (startup gate)
- `authGraph`
  - `loginFragment`
  - `unsupportedRoleFragment` (access restricted)
- `adminGraph`
  - `adminDashboardPlaceholderFragment`
  - admin feature placeholder destinations for future phases
- `deliveryGraph`
  - `deliveryDashboardPlaceholderFragment`
  - delivery feature placeholder destinations for future phases

Auth state to root destination mapping:

- `Loading` -> `splashFragment`
- `Unauthenticated` / `AuthenticationError` -> `loginFragment`
- `AuthenticatedAdmin` -> `adminGraph`
- `AuthenticatedDeliveryPartner` -> `deliveryGraph`
- `UnsupportedRole` -> `unsupportedRoleFragment`

## Protected navigation and role isolation

`AppDestination` defines destination scope:

- `PUBLIC`
- `ADMIN`
- `DELIVERY`

`AuthorizationPolicy.canAccess(role, destination)` enforces:

- `ADMIN` can access only `ADMIN` + `PUBLIC`
- `DELIVERY_PARTNER` can access only `DELIVERY` + `PUBLIC`
- Unauthenticated users can access only `PUBLIC`

`NavigationGuard` validates every destination change and redirects unauthorized or unknown routes to a safe auth-state destination.

This also protects planned deep-link entry points at runtime (if a deep link resolves to a protected destination without valid role/state, user is redirected safely).

## Session and logout behavior

- `SessionManager` restores persisted encrypted session on startup.
- Auth gate routes to role root only after session restoration is resolved.
- Logout clears stored session and transitions state to `Unauthenticated`.
- Navigation back stack is reset to prevent returning to protected screens after logout.

## Session expiration behavior

- Architecture supports transition to `Unauthenticated` on refresh/session failure.
- If backend refresh succeeds, authenticated state remains intact.
- If refresh fails, session is cleared and routing returns to login.

Exact refresh contract remains backend-dependent and is intentionally isolated behind auth interfaces.

## Delivery and order lifecycle target (domain constants for later phases)

Order status domain to centralize in app:

- `PENDING`
- `CONFIRMED`
- `PREPARING`
- `READY`
- `ASSIGNED`
- `ACCEPTED`
- `PICKED_UP`
- `OUT_FOR_DELIVERY`
- `DELIVERED`
- `CANCELLED`

Delivery lifecycle:

`READY -> ASSIGNED -> ACCEPTED -> PICKED_UP -> OUT_FOR_DELIVERY -> DELIVERED`

Detailed implementation progress is tracked in `docs/IMPLEMENTATION_STATUS.md`.
