function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>\"']/g, character => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
  })[character]);
}

function formatMoney(value, currency) {
  try { return new Intl.NumberFormat("en-IN", { style: "currency", currency }).format(Number(value || 0)); }
  catch { return `${currency} ${value || 0}`; }
}

function formatDate(value, language) {
  return new Intl.DateTimeFormat(language === "hi" ? "hi-IN" : "en-IN", {
    dateStyle: "medium", timeStyle: "short",
  }).format(new Date(value));
}

export function printInvoice(order, business, user, t, language) {
  const printWindow = window.open("", "_blank", "width=920,height=760");
  if (!printWindow) return false;

  const currency = order.currency || business?.currency || "INR";
  const text = value => escapeHtml(t(value));
  const money = value => escapeHtml(formatMoney(value, currency));
  const address = order.deliveryAddress;
  const addressLines = address ? [
    address.recipientName,
    address.addressLine1,
    address.addressLine2,
    address.locality,
    [address.city, address.state, address.postalCode].filter(Boolean).join(", "),
    address.countryCode,
  ].filter(Boolean) : [];
  const sellerLines = [business?.businessType, business?.gstin ? `${t("GSTIN")}: ${business.gstin}` : null, business?.address, business?.phone || user?.phone, business?.email || user?.email].filter(Boolean);
  const rows = (order.items || []).map(item => {
    const taxLabel = item.taxType ? `${t(item.taxType)} ${item.taxPercent}%` : t("None");
    return `<tr>
      <td><strong>${escapeHtml(item.productName)}</strong><small>${escapeHtml(item.unit || "")}</small></td>
      <td class="num">${escapeHtml(item.quantity)} ${escapeHtml(item.unit || "")}</td>
      <td class="num">${money(item.unitPrice)}</td>
      <td class="num">${Number(item.discountAmount) > 0 ? `−${money(item.discountAmount)}` : "—"}</td>
      <td><span>${escapeHtml(taxLabel)}</span>${Number(item.taxAmount) > 0 ? `<small>${money(item.taxAmount)}</small>` : ""}</td>
      <td class="num strong">${money(Number(item.lineTotal || 0) + Number(item.taxAmount || 0))}</td>
    </tr>`;
  }).join("");
  const summaryLine = (label, amount, emphasized = false) => `<div class="summary-row${emphasized ? " total" : ""}"><span>${text(label)}</span><strong>${money(amount)}</strong></div>`;
  const maybeLine = (label, amount) => Number(amount) > 0 ? summaryLine(label, amount) : "";

  printWindow.document.open();
  printWindow.document.write(`<!doctype html>
    <html lang="${language === "hi" ? "hi" : "en"}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${text("Invoice")} ${escapeHtml(order.orderNumber)}</title>
    <style>
      *{box-sizing:border-box}body{margin:0;background:#edf2ef;color:#1f2925;font:14px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif}
      .sheet{max-width:900px;margin:32px auto;padding:48px;background:#fff;box-shadow:0 12px 36px #1f29251a}
      .top{display:flex;justify-content:space-between;gap:32px;border-bottom:3px solid #25805e;padding-bottom:26px}
      h1{font-size:32px;line-height:1.1;margin:0 0 10px;color:#1c674d}.seller-name{font-size:20px;font-weight:700;margin-bottom:5px}.muted,small{color:#68756f}small{display:block;font-size:12px}
      .invoice-meta{text-align:right;min-width:200px}.invoice-meta strong{font-size:18px;display:block;margin-bottom:5px;overflow-wrap:anywhere}
      .parties{display:grid;grid-template-columns:1fr 1fr;gap:32px;padding:26px 0}.label{font-size:11px;font-weight:700;color:#62716a;text-transform:uppercase;margin-bottom:8px;letter-spacing:.06em}
      .party-name{font-size:16px;font-weight:700;margin-bottom:3px}.party-lines{color:#4f5c56;white-space:pre-line}
      table{width:100%;border-collapse:collapse;margin-top:8px}thead{background:#e9f3ee;color:#214b3a}th,td{padding:12px 10px;text-align:left;border-bottom:1px solid #dfe7e2;vertical-align:top}th{font-size:11px;text-transform:uppercase}td small{margin-top:3px}.num{text-align:right;white-space:nowrap}.strong{font-weight:700}
      .bottom{display:flex;justify-content:space-between;gap:32px;margin-top:26px}.notes{max-width:48%;color:#59665f;white-space:pre-wrap}.summary{width:min(340px,52%)}.summary-row{display:flex;justify-content:space-between;gap:24px;padding:6px 0}.total{border-top:2px solid #25805e;margin-top:8px;padding-top:12px;font-size:18px;color:#18573f}
      .payment{margin-top:16px;color:#59665f}.footer{border-top:1px solid #dfe7e2;margin-top:38px;padding-top:18px;text-align:center;color:#64726b}
      @media(max-width:640px){.sheet{margin:0;padding:24px}.top,.bottom{flex-direction:column}.invoice-meta{text-align:left}.parties{grid-template-columns:1fr;gap:18px}.summary{width:100%}.notes{max-width:none}table{font-size:11px}th,td{padding:8px 5px}}
      @media print{body{background:#fff}.sheet{max-width:none;margin:0;padding:0;box-shadow:none}@page{size:A4;margin:16mm}.top,.parties,.bottom,table{break-inside:avoid}thead{display:table-header-group}tr{break-inside:avoid}}
    </style></head><body><main class="sheet">
      <header class="top"><div><h1>${text("Invoice")}</h1><div class="seller-name">${escapeHtml(business?.name || t("Business name"))}</div><div class="muted">${sellerLines.map(escapeHtml).join("<br>")}</div></div>
        <div class="invoice-meta"><strong>${escapeHtml(order.orderNumber)}</strong><span>${text("Invoice date")}: ${escapeHtml(formatDate(order.createdAt, language))}</span><br><span>${text("Payment status")}: ${text(order.paymentStatus === "PAID" ? "Paid" : "Unpaid")}</span></div></header>
      <section class="parties"><div><div class="label">${text("Bill to")}</div><div class="party-name">${escapeHtml(order.customerName)}</div><div class="party-lines">${[order.customerPhone || address?.phone, order.customerEmail, ...addressLines].filter(Boolean).map(escapeHtml).join("<br>")}</div></div>
        <div><div class="label">${text("Order details")}</div><div>${text("Order")}: ${escapeHtml(order.orderNumber)}</div><div>${text("Channel")}: ${text(displayChannel(order.source))}</div><div>${text("Status")}: ${text(displayStatus(order.status))}</div></div></section>
      <table><thead><tr><th>${text("Product")}</th><th class="num">${text("Qty")}</th><th class="num">${text("Unit price")}</th><th class="num">${text("Discount")}</th><th>${text("Tax")}</th><th class="num">${text("Amount")}</th></tr></thead><tbody>${rows}</tbody></table>
      <section class="bottom"><div class="notes">${order.notes ? `<div class="label">${text("Notes")}</div>${escapeHtml(order.notes)}` : ""}</div><div class="summary">
        ${summaryLine("Subtotal (MRP)", order.subtotal)}${maybeLine("Discount", order.discount)}${maybeLine("Tax", order.tax)}${maybeLine("Delivery", order.deliveryCharge)}${summaryLine("Total", order.totalAmount, true)}
        <div class="payment">${text("Payment status")}: <strong>${text(order.paymentStatus === "PAID" ? "Paid" : "Unpaid")}</strong></div></div></section>
      <footer class="footer">${text("Thank you for your order.")} · ${escapeHtml(business?.name || "LocalFlow")}</footer>
    </main><script>window.addEventListener('load',()=>setTimeout(()=>window.print(),250));</script></body></html>`);
  printWindow.document.close();
  return true;
}

function displayStatus(value) {
  return ({ NEW: "New", CONFIRMED: "Confirmed", REJECTED: "Rejected", PACKING: "Packing", READY: "Ready", OUT_FOR_DELIVERY: "Out For Delivery", PICKUP_READY: "Pickup Ready", DELIVERED: "Delivered", COMPLETED: "Completed", CANCELLED: "Cancelled" })[value] || value || "";
}

function displayChannel(value) {
  return ({ VENDOR_APP: "Vendor App", CUSTOMER_WEB: "Customer Web", WHATSAPP: "Whatsapp" })[value] || value || "";
}
