# Free Pilot Deployment (Two Businesses)

This guide prepares LocalFlow for a small pilot. Free hosting can sleep, pause,
restart, or change its limits. Do not treat this setup as a guaranteed or
backed-up production service. Export the database regularly and keep a copy
outside the hosting provider.

## Pilot hosting layout

- React frontend: Render Static Site.
- Spring Boot API: Render Web Service built from `backend/Dockerfile`.
- PostgreSQL: Supabase Free project.

Render's free PostgreSQL databases expire after 30 days, so this guide uses
Supabase for the pilot database. Supabase Free projects can pause after
prolonged low activity and do not include automatic database backups.

## 1. Push deployment preparation to GitHub

From the repository root, stage only the non-macOS application and docs:

```sh
git add .gitignore README.md docker-compose.yml backend docs web
git status --short
```

Confirm that no `macos/` path appears in the staged files, then commit and push:

```sh
git commit -m "Prepare LocalFlow for pilot deployment"
git push origin main
```

## 2. Create the pilot database in Supabase

1. Create a Supabase project and choose a region close to the Render service.
2. Save the database password in a password manager.
3. In **Connect**, copy the **Session pooler** host, port, and username. The
   session pooler is the IPv4-compatible option.
4. Compose the JDBC URL for the backend in this form:

   ```text
   jdbc:postgresql://POOLER_HOST:POOLER_PORT/postgres?sslmode=require
   ```

   Use the host and port from Supabase, not the placeholders above. Store the
   username and password separately as environment variables. Flyway runs the
   schema migrations automatically when the backend starts.

## 3. Deploy the backend to Render

1. Create a **Web Service** from the GitHub repository.
2. Set **Root Directory** to `backend` and select **Docker** as the runtime.
   Render will build `backend/Dockerfile`.
3. Set health check path to `/api/v1/health`.
4. Add these environment variables. Do not commit their values to GitHub.

   | Key | Value |
   | --- | --- |
   | `LOCALFLOW_DB_URL` | JDBC URL assembled from Supabase session-pooler details |
   | `LOCALFLOW_DB_USERNAME` | Supabase session-pooler username |
   | `LOCALFLOW_DB_PASSWORD` | Supabase database password |
   | `LOCALFLOW_JWT_SECRET` | A newly generated, unique, long random secret |
   | `LOCALFLOW_CORS_ALLOWED_ORIGINS` | The exact Render frontend origin, added in step 4 |
   | `LOCALFLOW_FRONTEND_URL` | The exact Render frontend origin, added in step 4 |

   Render sets `PORT` for the service automatically. Wait for the first deploy
   to finish, then open `https://BACKEND_NAME.onrender.com/api/v1/health`.

## 4. Deploy the React frontend to Render

1. Create a **Static Site** from the same GitHub repository.
2. Set **Root Directory** to `web`.
3. Set **Build Command** to `pnpm install --frozen-lockfile && pnpm build`.
4. Set **Publish Directory** to `dist`.
5. Add the build-time environment variable:

   | Key | Value |
   | --- | --- |
   | `VITE_API_BASE_URL` | `https://BACKEND_NAME.onrender.com/api/v1` |

6. Deploy, then copy the frontend's exact HTTPS origin, for example
   `https://localflow-vendor.onrender.com`.
7. Return to the backend's Render environment and set both
   `LOCALFLOW_CORS_ALLOWED_ORIGINS` and `LOCALFLOW_FRONTEND_URL` to that exact
   frontend origin (no trailing slash). Save and redeploy the backend.
8. Redeploy the frontend after setting `VITE_API_BASE_URL` if necessary.

The frontend and backend use separate Render URLs. `VITE_API_BASE_URL` must be
the backend URL ending in `/api/v1`; the CORS value must be the frontend origin
only.

## 5. Create the two business accounts

The hosted database starts empty and is separate from the data in the local
Docker database.

1. Open the hosted frontend and register Business A with its own owner email.
2. Register Business B with a different owner email. The same email cannot be
   used for both business accounts.
3. Sign in to each business separately. Each has separate products, customers,
   orders, storefront link, and customer signup code.
4. Share each business's storefront link and signup code only with its own
   customers.
5. Each business owner should sign in to their own vendor account to manage
   that business's orders. The current app does not provide one account that
   switches between multiple businesses.

## 6. Pilot smoke check

For each business, test the following before accepting real customer orders:

1. Sign in to the vendor website.
2. Create a category and an available product.
3. Open that business's customer storefront link in a separate/private browser
   window and register a customer using its signup code.
4. Place a small test order and confirm it appears in that business's vendor
   Orders screen as **Customer Web**.
5. Confirm the other business cannot see the product, customer, or order.
6. Test order status updates and print/save an invoice.
7. Export the database and confirm you know how to restore the export before
   putting important pilot data into the hosted system.

## Free-tier caveats

- A free Render web service sleeps after 15 minutes without traffic; waking can
  take about a minute.
- Render free Postgres is unsuitable for this pilot because it expires after
  30 days.
- Supabase Free can pause after low activity and does not include automated
  backups. Schedule regular exports and test restoring one.
- Render blocks outbound SMTP on common ports, so password-reset emails using
  the current SMTP setup will not work there. The forgot-password links are
  currently hidden in the UI.
- Free hosting is useful for a limited pilot, but uptime, recovery, and provider
  limits are not guaranteed. Do not promise customers uninterrupted service.
