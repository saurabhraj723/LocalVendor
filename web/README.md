# LocalFlow Vendor Web App

The browser client is built with React 19 and Vite and uses the existing
LocalFlow backend.

## Run locally

Prerequisites: Node.js 22 LTS and pnpm.

1. Start PostgreSQL and the Spring Boot backend as usual in IntelliJ.
2. Restart the backend after pulling these changes so it loads the local web
   development CORS configuration.
3. From the repository root, install the frontend packages once and start Vite:

   ```sh
   cd web
   pnpm install
   pnpm dev
   ```

4. Open <http://localhost:4173>.

During local development, Vite proxies `/api` requests to the backend at
`http://localhost:8080`. Its access token is kept in the browser tab's session
storage. For a hosted build, set `VITE_API_BASE_URL` to the deployed backend
URL ending in `/api/v1`. See
[`docs/FREE_PILOT_DEPLOYMENT.md`](../docs/FREE_PILOT_DEPLOYMENT.md). Create a
production bundle with `pnpm build`; the output is written to `dist/`.

## Customer Storefront

Sign in to the vendor app and copy **Customer store link** from the dashboard.
It opens a public storefront in the same web app, for example
`http://localhost:4173/?shop=<random-store-code>`. The shop code is random and
is not the database business ID. Copy **Customer signup code** separately and
give it to customers who need to register. Keep the backend and Vite running
while testing. The storefront only shows active, available products.

To test end to end:

1. Create at least one category and an available product in the vendor app.
2. Copy the customer store link and signup code from the vendor dashboard.
3. Open the store link in a private browser window. Change one character in
   the random store code; the app should show that the store cannot be found.
   Restore the correct link afterward.
4. Add a product and continue to checkout. Register using the signup code,
   then enter delivery details and place the order.
5. Confirm the storefront displays an order number, then open **Orders** in
   the vendor app. The new row should show **Customer Web** as its channel,
   and the customer should appear in **Customers**.
6. Open **My orders** on the storefront and verify the customer sees their
   order. Sign out, register a different account, and confirm it cannot see
   the first account's order.

The public API is `GET /api/v1/storefront/{storefrontSlug}`. Customer signup
and login use `/api/v1/customer-auth/register` and `/api/v1/customer-auth/login`;
authenticated checkout uses `POST /api/v1/storefront/{storefrontSlug}/orders`.
Changing the public slug can only produce an unavailable storefront; private
customer and order data is checked against the signed-in account on each API
request.
