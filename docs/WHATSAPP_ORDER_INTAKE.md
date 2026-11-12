# WhatsApp Order Intake: Local Postman Test

The local Postman simulator below walks through a customer order over multiple
messages. It is protected by the vendor's JWT and is not the public Meta webhook.

## Prerequisites

- PostgreSQL and the backend are running.
- Restart the backend after adding this change so Flyway applies migrations V11, V12, and V13.
- The vendor account has at least one customer and one available product.

## 1. Sign in

Send `POST http://localhost:8080/api/v1/auth/login` with JSON:

```json
{
  "email": "YOUR_VENDOR_EMAIL",
  "password": "YOUR_VENDOR_PASSWORD"
}
```

Copy `accessToken` from the response. In Postman, select **Authorization** →
**Bearer Token** and paste it.

## 2. Find a test customer's phone number

Use the same Bearer Token to call:

- `GET http://localhost:8080/api/v1/customers` and choose a customer with a saved phone number and delivery address.
- `GET http://localhost:8080/api/v1/products` and choose an available product ID.

## 3. Start by matching the customer phone

Send `POST http://localhost:8080/api/v1/whatsapp/test/conversations` with
**Body** → **raw** → **JSON**:

```json
{
  "messageId": "postman-chat-001-start",
  "customerPhone": "+919876543210",
  "text": "Hi"
}
```

Use the phone number saved on that customer. Matching ignores spaces and
punctuation, but does not guess or remove country codes. The response includes
a `conversationId` and asks the customer to confirm their default (or only)
saved delivery address. If the customer has multiple addresses and no default,
the response lists address IDs to choose from. This endpoint is still protected
by the vendor's JWT for local testing; it is not the public customer endpoint.

## 4. Confirm the delivery address

Send `POST http://localhost:8080/api/v1/whatsapp/test/conversations/{conversationId}/messages`
with the same Bearer Token:

```json
{
  "messageId": "postman-chat-001-address",
  "text": "YES"
}
```

Reply `CHANGE` to select a different saved address, or reply with an address ID
if the conversation asks you to choose one. The next reply shows the available
products and their product IDs.

## 5. Add products to your cart

Choose an available product ID:

```json
{
  "messageId": "postman-chat-001-product",
  "text": "1"
}
```

The response asks for a quantity. Reply with a valid amount, following the
product minimum and step:

```json
{
  "messageId": "postman-chat-001-quantity",
  "text": "2"
}
```

The product is added to the cart. You can send another product ID and quantity
to add another item. To remove an item, send `REMOVE` followed by its product
ID, for example `REMOVE 1`. When the cart is ready, send `REVIEW`:

```json
{
  "messageId": "postman-chat-001-review",
  "text": "REVIEW"
}
```

The response lists all items and the server-calculated total. Confirm the order:

```json
{
  "messageId": "postman-chat-001-confirm",
  "text": "YES"
}
```

The response should show `state: "COMPLETED"`, an order number, and an order ID.
The regular vendor Orders screen and `GET /api/v1/orders` show the created order
with `source: "WHATSAPP"` and the confirmed delivery address snapshot. Send
`CANCEL` at any point to stop without creating an order.

## 6. Verify retries do not advance twice

Repeat any exact message request with the same `messageId`. It returns the
stored reply with `replayed: true`; it will not create another order. Each
different incoming message needs its own unique ID within the vendor business.

## Existing one-request simulator

The older endpoint remains available for quickly simulating a fully confirmed
order. Send `POST http://localhost:8080/api/v1/orders/whatsapp/intake-test` with
**Body** → **raw** → **JSON**:

```json
{
  "messageId": "wamid.postman-test-001",
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "notes": "WhatsApp test order"
}
```

Replace the IDs with the values from step 2. Use a new `messageId` for each
different order.

The first response should be HTTP `201 Created` and include `status: "NEW"`
and `source: "WHATSAPP"`. The backend calculates prices from its product
records; values sent by the client cannot override them. The order should
appear in the website's Orders list with channel **WhatsApp**.

## Verify the one-request endpoint's idempotency

Send the exact request again with the same `messageId`. The response should be
HTTP `200 OK` and refer to the same `orderNumber`. Then call
`GET http://localhost:8080/api/v1/orders?page=0&size=20` and verify only one
order was created for that message ID.

## Current boundary

This is a local, authenticated simulator. Real WhatsApp messages, webhook
signature verification, Meta number onboarding, and customer replies are not
connected yet. Those come after this contract is tested and a pilot number and
public HTTPS deployment are available.
