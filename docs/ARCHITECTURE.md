# NexaMart Admin + Delivery — Architecture

## Repository and platform baseline

This repository contains a native Android Admin + Delivery client, with:

- Gradle Kotlin DSL build
- Single `:app` module
- Android package: `com.daily.nexamartpartner`
- Phased implementation across foundation, authentication, protected navigation, and Admin Dashboard foundation

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

## Root navigation architecture (Phase 5)

Navigation technology: **XML Navigation Component**.

Root graph structure:

- `splashFragment` (startup gate)
- `authGraph`
  - `loginFragment`
  - `unsupportedRoleFragment` (access restricted)
- `adminGraph`
  - `adminDashboardFragment`
  - `adminOrdersFragment`
  - `adminOrderDetailsFragment`
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

## Admin dashboard architecture (Phase 4 foundation)

Implemented stack:

- `AdminDashboardScreen` (UI)
- `AdminDashboardViewModel`
- `GetAdminDashboardUseCase`
- `AdminDashboardRepository`
- `AdminDashboardRemoteDataSource`
- `AdminDashboardApi`

Data/state behavior:

- Dashboard state is modeled as `Loading`, `Success`, `Empty`, `Error`, and `Unavailable`.
- Pull-to-refresh and retry are ViewModel-driven and de-duplicated to prevent concurrent dashboard requests.
- Unauthorized dashboard failures emit a session-expired event and reuse centralized auth logout/routing behavior.
- Number and currency formatting are centralized in `ValueFormatter` (Indian locale formatting).

Contract strategy:

- Dashboard endpoint path is contract-gated by `AdminDashboardContract`.
- Current implementation uses `PendingBackendAdminDashboardContract`, so no guessed endpoint is called.
- UI shows a production-safe unavailable state when backend contract is missing.

## Admin order management architecture (Phase 5)

Implemented stack:

- `AdminOrdersScreen` (orders list/search/filter/pagination/refresh)
- `AdminOrderDetailsScreen` (order detail/timeline/payment/customer/delivery/status actions)
- `AdminOrdersViewModel`
- `AdminOrderDetailsViewModel`
- `GetAdminOrdersUseCase`
- `GetAdminOrderDetailsUseCase`
- `UpdateAdminOrderStatusUseCase`
- `CancelAdminOrderUseCase`
- `AdminOrdersRepository`
- `AdminOrdersRemoteDataSource`
- `AdminOrdersApi`

Data flow:

- UI -> ViewModel -> Use Case -> Repository -> Remote Data Source -> Retrofit API -> backend.
- Search and filters are sent through query contracts (no large local dataset filtering).
- Pagination is backend-driven and guarded against duplicate page requests.
- Order details load on demand only when an order is selected.

State and error handling:

- Orders state: `Loading`, `Success`, `Empty`, `Error`, `Unavailable`.
- Details state: `Loading`, `Success`, `Error`, `Unavailable`.
- Pull-to-refresh and retry are lifecycle-safe and ViewModel-driven.
- Unauthorized failures trigger centralized session-expiration handling.
- Contract-missing failures render explicit unavailable UI instead of fake order data.

Contract strategy:

- `AdminOrdersContract` defines order-list/details/status-update/cancel request contracts.
- Current wiring uses `PendingBackendAdminOrdersContract`; APIs stay blocked until backend contracts are confirmed.

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
