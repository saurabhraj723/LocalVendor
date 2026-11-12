# Password Reset

Vendor and customer reset links are random, one-time tokens. The backend stores only their SHA-256 hashes, links expire after 30 minutes, and a successful reset invalidates other outstanding links for that account. Forgot-password requests return the same message whether or not the email belongs to an account.

## Local testing without email

Start the backend with this environment variable enabled:

```text
LOCALFLOW_PASSWORD_RESET_DEV_LINK_ENABLED=true
```

After submitting the forgot-password form, the development link appears on screen. This option is intended only for local development; keep it disabled in production. The token is not returned by default.

## Sending real emails

Configure these environment variables in the backend run configuration (for example, IntelliJ Run Configuration). For Gmail, use `smtp.gmail.com`, port `587`, your full Gmail address as the username, and a Google App Password (not your normal account password). Google requires TLS/STARTTLS on port 587. The sending Gmail account must have 2-Step Verification enabled to create an App Password.

```text
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=<your full Gmail address>
SPRING_MAIL_PASSWORD=<Google App Password>
LOCALFLOW_PASSWORD_RESET_FROM=<same Gmail address>
LOCALFLOW_FRONTEND_URL=http://localhost:4173
```

In IntelliJ, open the Spring Boot run configuration, choose **Modify options → Environment variables**, paste the values as semicolon-separated `KEY=value` pairs, save, and restart the backend. Request a reset again and check the recipient's Inbox and Spam folder. Do not commit SMTP credentials or enable the development-link option on a deployed environment. For deployment, use a verified sender and HTTPS frontend URL.
