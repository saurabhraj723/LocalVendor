# LocalFlow Development Status

**Status:** V1 is under active development  
**Last reviewed:** 5 November 2026

This document records what is implemented in the repository today. It separates
working application features from integrations and release work that are still
planned. It is a development snapshot, not a claim that the product is ready for
production.

## Product overview

LocalFlow helps independent local businesses manage a catalogue, customer
records, and orders. The current system has a Spring Boot API, a React web app
for vendors and customers, and a native macOS vendor app built with AppKit and
XIB screens.

The planned order channels converge on the same vendor order queue:

1. Vendor-created orders from the vendor workspace.
2. Customer orders placed in the public customer storefront.
3. WhatsApp orders. A local simulator is implemented; Meta's live WhatsApp
   integration is not connected yet.

## Implemented features

### Backend and data

- Spring Boot REST API with PostgreSQL persistence and Flyway schema migrations.
- Health endpoint at `GET /api/v1/health`.
- Business and owner registration, password hashing, login, and expiring JWT
  access tokens.
- Business-scoped authorization for vendor data. Protected requests derive the
  business and user identity from the authenticated token.
- Random public storefront slug and separate customer registration code for
  each business. The storefront slug is not a database business ID.
- Email uniqueness checks when registering a vendor business.
- API validation and structured error responses for invalid requests.

### Vendor workspace (React web)

- Vendor registration and login, password visibility control, and forgot/reset
  password screens.
- Dashboard with today's order and sales metrics, pending order count, new
  customer count, and recent orders.
- Copyable customer storefront link and customer registration code.
- Product catalogue with categories, search, create/edit, availability toggle,
  and archive behavior.
- Product fields include SKU, unit, price/MRP, minimum quantity, quantity step,
  optional description, discount percentage, and optional tax type/rate.
- Customer directory with create/edit, required contact details, and delivery
  address management.
- Vendor-created orders with customer and product selection, quantity rules,
  order details, channel display, and status updates.
- Business profile screen. Business name, type, and email are read-only in the
  UI; GST number, phone, and business address can be edited.
- Printable invoice generated from order details, including seller/customer
  details, item prices, discounts, taxes, totals, channel, and payment status.
- English/Hindi toggle and light/dark theme toggle.
- Responsive layouts for phone-sized viewports, including card-based data
  tables and mobile navigation.

### Customer storefront and portal (React web)

- Public storefront link shows only that business's active catalogue and
  available products. Unknown or modified store codes return an unavailable
  store rather than another business's information.
- Customers can search and filter products, view MRP and any discount, and add
  products to a cart.
- Cart summary shows MRP, discount amount when applicable, tax, and total.
  Tax is calculated after the discount.
- Customer registration requires the business's registration code; customer
  login is scoped to the selected store.
- Checkout requires customer authentication and delivery details.
- Orders are associated with the correct business and customer and appear in
  the vendor order queue with the `CUSTOMER_WEB` channel.
- Signed-in customers can view their own order history and order details; API
  authorization prevents access to another customer's orders.
- Customer account includes forgot/reset password screens.
- English/Hindi toggle and responsive mobile storefront layout.

### Orders, pricing, and WhatsApp simulator

- Backend validates product availability and minimum/step quantity rules and
  calculates order amounts from stored product data.
- Product discount is a percentage of MRP. Tax is calculated on the discounted
  amount. If a tax type is selected, a positive tax rate is required.
- Order items store price/tax/discount snapshots so later catalogue edits do
  not rewrite an existing order's amounts.
- Order status history records who changed the status and when. Supported
  statuses include New, Confirmed, Rejected, Packing, Ready, Out for Delivery,
  Pickup Ready, Delivered, Completed, and Cancelled, subject to allowed
  transitions.
- Order intake supports idempotency keys so retrying the same WhatsApp test
  message does not create duplicate orders.
- A multi-message WhatsApp conversation simulator supports customer matching,
  address confirmation, product selection, quantity entry, cart review, and
  order confirmation. It uses the same vendor order queue and marks the source
  as `WHATSAPP`.

### Native macOS vendor app

- Native AppKit app with XIB-based screens and Auto Layout.
- Implemented screens include sign-in/registration, dashboard, products and
  categories, customers and customer editing, order creation, order list, and
  order details/status actions.
- API client connects to the backend; vendor access token is stored in the
  macOS Keychain.
- Asset catalog includes LocalFlow branding, empty-catalogue artwork, and an
  app icon.

## Current technical stack

| Layer | Technology |
| --- | --- |
| API | Java, Spring Boot, Spring Security, JWT |
| Database | PostgreSQL 16, Flyway |
| Vendor/customer web | React 19, Vite 8 |
| Native desktop app | Swift, AppKit, XIB, Auto Layout, Keychain |
| Local database runtime | Docker Compose |

## Important setup notes

- Start PostgreSQL with `docker compose up -d` from the repository root.
- Start the backend from `backend/` with `mvn spring-boot:run` (or use the
  IntelliJ Spring Boot run configuration).
- Start the web app from `web/` with `pnpm install` once, then `pnpm dev`.
- The Vite development server is configured for `http://127.0.0.1:4173` and
  proxies API calls to the local backend.
- Open `macos/Package.swift` in Xcode to run the macOS client.
- Password reset email delivery requires working SMTP settings in the backend
  environment. A generic success message in the UI does not by itself prove an
  email was delivered.
- Local development uses HTTP and local credentials. Production deployment
  still needs appropriate HTTPS, secrets, email delivery, monitoring, and
  operational configuration.

See [README.md](../README.md), [web/README.md](../web/README.md),
[PASSWORD_RESET.md](PASSWORD_RESET.md), and
[WHATSAPP_ORDER_INTAKE.md](WHATSAPP_ORDER_INTAKE.md), and
[FREE_PILOT_DEPLOYMENT.md](FREE_PILOT_DEPLOYMENT.md) for setup and focused
workflows.

## Verified during this documentation update

- React/Vite production build completed successfully with `pnpm build`.
- Chrome device emulation at 360 × 800 was used to inspect vendor dashboard,
  vendor product list, and customer storefront layouts. The inspected pages
  had a 360-pixel document width with no horizontal overflow.
- This was browser emulation, not a physical-device test. Backend automated
  tests and the Xcode build were not run as part of this documentation update.

## Not yet implemented or not yet connected

- Live Meta WhatsApp Business Platform connection: number onboarding, public
  HTTPS webhook, webhook signature verification, and real inbound/outbound
  messages.
- Online payment collection and payment gateway integration. Orders currently
  carry payment status but checkout does not charge the customer.
- Live delivery tracking, courier integration, and customer notifications for
  every order status change.
- Stock quantity/inventory management. Product availability is a manual
  available/unavailable flag.
- Production hosting, custom domains, CI/CD, backups, and production security
  review.
- Full physical-device coverage across iOS/Android browsers and additional
  viewport/accessibility testing.

## Suggested V1 completion checklist

- Run backend and frontend automated tests and resolve failures.
- Exercise vendor registration, catalogue, customer, order, status, and invoice
  flows end to end.
- Exercise customer registration, storefront browsing, checkout, and private
  order history, including attempts to use a different store code or account.
- Verify SMTP password reset delivery in the intended environment.
- Test the critical flows on at least one physical iPhone and one Android
  phone.
- Decide whether a live WhatsApp pilot is a V1 launch requirement. If so,
  complete Meta onboarding and deploy a public HTTPS webhook before launch.
