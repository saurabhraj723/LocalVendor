# LocalFlow

LocalFlow is a local-business order management platform. It currently includes
a Spring Boot API, a React vendor workspace and customer storefront, and a
native AppKit macOS vendor app. For a detailed implementation snapshot and
remaining V1 work, see [Development status](docs/DEVELOPMENT_STATUS.md).
For the limited two-business free pilot setup, see
[Free pilot deployment](docs/FREE_PILOT_DEPLOYMENT.md).

## V1 Build Order

1. Backend foundation: Spring Boot, PostgreSQL, Flyway, local configuration.
2. Authentication: owner registration, login, JWT, roles, tenant context.
3. Catalogue: categories, products, pricing, availability.
4. Customers: customer profiles and addresses.
5. Orders: order creation, server-side totals, valid status transitions.
6. Dashboard: daily order/sales summary and recent orders.
7. Native macOS app: AppKit login and registration in XIBs, dashboard, product
   catalogue, customer directory, and orders.
8. Customer storefront: public business catalogues, customer accounts, and
   authenticated customer web orders.
9. WhatsApp order intake: local conversation and idempotency simulator; live
   Meta integration remains future work.

## Local Tooling Needed

- Java 21
- Maven 3.9+
- Docker Desktop or another PostgreSQL 16 runtime
- Full Xcode for the AppKit and XIB macOS app

## Run the Backend

Start PostgreSQL from the repository root:

```sh
docker compose up -d
```

Then run Spring Boot:

```sh
cd backend
mvn spring-boot:run
```

The API listens at `http://localhost:8080`.

## Run the macOS App

Open `macos/Package.swift` in Xcode, select the `LocalFlowMac` scheme, and run
it. The app uses AppKit view controllers and XIB files, connects to the local
backend at `http://localhost:8080`, and stores its access token in Keychain.
