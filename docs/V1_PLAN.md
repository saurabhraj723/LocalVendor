# LocalFlow V1 Plan

## Product Decision

Build and validate the vendor workflow alongside customer order channels.
Customer storefront and WhatsApp orders both enter the vendor's existing
order queue. Payments, native customer apps, Kafka, Redis, and Kubernetes
remain out of scope for this stage.

## V1 Scope

- Business owner can register and log in.
- Every protected request is scoped to the authenticated business.
- Business can manage categories and products.
- Business can manage customers and addresses.
- Business can create orders from the vendor app.
- Backend calculates totals and snapshots order item prices.
- Orders move through controlled status transitions.
- Dashboard shows today's orders, today's sales, pending orders, and recent
  orders.
- SwiftUI macOS app supports the core vendor workflow.
- React web app supports the core vendor workflow.
- Customers can browse an active business catalogue and place guest orders in
  a public, mobile-responsive storefront; customer accounts are linked through
  a business signup code and these orders enter the vendor queue with the
  customer website channel identified.
- A WhatsApp-originated customer order can enter the same vendor order queue.

## Small Development Parts

### Part 1: Backend Foundation

- Create Spring Boot backend.
- Add PostgreSQL local configuration.
- Add Flyway.
- Add first migration for businesses and users.
- Add a health endpoint.

### Part 2: Authentication and Tenant Context

- Register owner and business in one transaction.
- Login with email and password.
- Hash passwords.
- Issue JWT access token.
- Resolve `businessId`, `userId`, and `role` from the authenticated identity.

### Part 3: Catalogue

- Add category and product migrations.
- Implement category CRUD.
- Implement product CRUD.
- Add availability update endpoint.
- Enforce tenant isolation on all queries.

### Part 4: Customers

- Add customer and address migrations.
- Implement customer list, detail, create, and update.
- Add customer order history after orders exist.

### Part 5: Orders

- Add order, order item, and status history migrations.
- Validate product availability and quantity on the server.
- Calculate totals on the server.
- Store product name, unit, and price snapshots.
- Enforce order state machine.

### Part 6: Dashboard

- Add summary endpoint for daily KPIs.
- Add recent orders.
- Add top products and simple sales trend if useful for pilot.

### Part 7: macOS App Foundation

- Create an AppKit project with XIB-based screens.
- Add API client, token manager, session state, and navigation shell.
- Implement login.
- Implement products screen first.
- Implement customer directory and profile management.

### Part 8: WhatsApp Order Intake Pilot

- Test the order-intake contract locally through an authenticated Postman
  simulator before connecting Meta.
- Preserve server-side product, quantity, price, and tenant validation.
- Make message delivery idempotent so retries do not create duplicate orders.
- Connect one pilot vendor number through the official WhatsApp Business
  Platform Cloud API after the backend has a public HTTPS webhook endpoint.
- Show WhatsApp as the order channel in the vendor app and require vendor
  approval through the existing order state machine.
- Keep guided product and quantity selection in scope; defer free-text/AI
  order interpretation.

### Part 9: Customer Storefront

- Provide a public catalogue scoped to one active business.
- Require customer login or registration before checkout; registration uses
  the store's signup code and creates a business-scoped customer record.
- Let signed-in customers build a cart, place an order, and see only their own
  order history.
- Use an unguessable public storefront slug instead of an exposed numeric
  business ID.
- Validate products and quantities and calculate totals on the server.
- Identify orders as `CUSTOMER_WEB` and make retries idempotent.
- Share the storefront URL from the vendor dashboard.

## V1 Done Means

- A vendor can register, log in, create products and customers, create orders
  from the macOS and web apps, and move orders through valid statuses.
- A confirmed WhatsApp order enters the same queue with its channel identified
  and duplicate message delivery does not create a second order.
- A customer storefront order enters the same queue with its channel identified
  and retrying checkout does not create a duplicate order.
- Business A cannot read or mutate Business B data.
- Automated tests cover authentication, tenant isolation, order totals, and
  order status transitions, plus WhatsApp intake idempotency.

