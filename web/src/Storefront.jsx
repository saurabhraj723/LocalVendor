import { useEffect, useMemo, useState } from "react";
import shopArt from "../assets/LocalFlowRetailerArt.png";
import { apiUrl } from "./apiUrl";
import "./storefront.css";

const HINDI = {
  "Opening store…": "स्टोर खोला जा रहा है…", "Store unavailable": "स्टोर उपलब्ध नहीं है", "This store could not be found.": "यह स्टोर नहीं मिला।", "Product categories": "उत्पाद श्रेणियां", "Your cart": "आपका कार्ट",
  "Local pickup and delivery orders": "स्थानीय पिकअप और डिलीवरी के ऑर्डर", "My orders": "मेरे ऑर्डर", "Sign out": "साइन आउट", "Sign in": "साइन इन", "Create account": "खाता बनाएं", "Cart": "कार्ट",
  "Shop local": "स्थानीय दुकान से खरीदें", "Your neighborhood business, now online": "आपका स्थानीय व्यवसाय, अब ऑनलाइन", "The catalogue": "उत्पाद सूची", "What would you like?": "आप क्या लेना चाहेंगे?", "Search products": "उत्पाद खोजें", "All products": "सभी उत्पाद", "Store pick": "दुकान की पसंद", "per": "प्रति", "off": "छूट", "Min": "न्यूनतम", "Add": "जोड़ें", "No matching products. Try another search or category.": "कोई उत्पाद नहीं मिला। दूसरी खोज या श्रेणी आज़माएं।", "View cart": "कार्ट देखें", "items": "आइटम",
  "Your basket": "आपकी टोकरी", "Order summary": "ऑर्डर का सारांश", "item": "आइटम", "MRP": "एमआरपी", "Discount amount": "छूट की राशि", "Taxes": "कर", "Total": "कुल", "Taxes are calculated after discount. Delivery charges, if any, will be confirmed by the store.": "छूट के बाद कर की गणना की जाती है। डिलीवरी शुल्क, यदि कोई हो, दुकान द्वारा बताया जाएगा।", "Continue to checkout": "चेकआउट पर जाएं", "Your basket is empty": "आपकी टोकरी खाली है", "Add products to start an order.": "ऑर्डर शुरू करने के लिए उत्पाद जोड़ें।",
  "Checkout": "चेकआउट", "Delivery details": "डिलीवरी का विवरण", "incl. tax": "कर सहित", "discount": "छूट", "tax": "कर", "Ordering as": "इस ग्राहक के रूप में ऑर्डर", "Address": "पता", "Apartment, suite, etc.": "फ्लैट, सुइट आदि", "Area / locality": "इलाका / क्षेत्र", "City": "शहर", "State": "राज्य", "Postal code": "पिन कोड", "Delivery instructions": "डिलीवरी निर्देश", "Order note": "ऑर्डर नोट", "Anything the store should know?": "दुकान के लिए कोई विशेष सूचना?", "Back to cart": "कार्ट पर वापस जाएं", "Place order": "ऑर्डर करें", "Placing order…": "ऑर्डर भेजा जा रहा है…",
  "Create your customer account": "ग्राहक खाता बनाएं", "Sign in to continue": "आगे बढ़ने के लिए साइन इन करें", "Ask the store for its customer signup code.": "ग्राहक पंजीकरण कोड दुकान से लें।", "Sign in to place your order and see your order history.": "ऑर्डर करने और ऑर्डर इतिहास देखने के लिए साइन इन करें।", "Customer signup code": "ग्राहक पंजीकरण कोड", "Full name": "पूरा नाम", "Phone number": "फ़ोन नंबर", "Email": "ईमेल", "Enter a valid email address.": "मान्य ईमेल पता दर्ज करें।", "Password": "पासवर्ड", "Confirm password": "पासवर्ड की पुष्टि करें", "Passwords do not match.": "दोनों पासवर्ड मेल नहीं खाते।", "Show": "दिखाएं", "Hide": "छिपाएं", "Already registered? Sign in": "पहले से पंजीकृत हैं? साइन इन करें", "New to this store? Create account": "इस दुकान पर नए हैं? खाता बनाएं", "Please wait…": "कृपया प्रतीक्षा करें…", "Forgot password?": "पासवर्ड भूल गए?", "Forgot password": "पासवर्ड भूल गए", "Set a new password": "नया पासवर्ड बनाएं", "Enter your account email and we’ll send reset instructions.": "अपने खाते का ईमेल दर्ज करें। हम पासवर्ड रीसेट निर्देश भेजेंगे।", "Choose a new password for your account.": "अपने खाते के लिए नया पासवर्ड चुनें।", "Back to sign in": "साइन इन पर वापस जाएं", "Send reset instructions": "रीसेट निर्देश भेजें", "Reset password": "पासवर्ड रीसेट करें", "If an account matches these details, password reset instructions have been sent.": "यदि इन विवरणों से खाता मिलता है, तो पासवर्ड रीसेट निर्देश भेज दिए गए हैं।", "Password reset delivery isn't configured. Add SMTP settings to receive email, or enable the local development reset link.": "पासवर्ड रीसेट ईमेल सेट नहीं है। ईमेल पाने के लिए SMTP सेटिंग जोड़ें या स्थानीय डेवलपमेंट रीसेट लिंक चालू करें।", "Your password has been reset. You can sign in now.": "आपका पासवर्ड रीसेट हो गया है। अब साइन इन करें।", "This password reset link is invalid or has expired.": "यह पासवर्ड रीसेट लिंक अमान्य है या इसकी समय-सीमा समाप्त हो गई है।", "Could not reset password.": "पासवर्ड रीसेट नहीं हो सका।", "Could not send reset instructions.": "रीसेट निर्देश नहीं भेजे जा सके।", "Open reset link": "रीसेट लिंक खोलें",
  "Order received": "ऑर्डर प्राप्त हुआ", "Thank you,": "धन्यवाद,", "Your order": "आपका ऑर्डर", "has been sent to": "भेज दिया गया है", "Order total": "ऑर्डर की कुल राशि", "Continue shopping": "खरीदारी जारी रखें", "Your account": "आपका खाता", "Loading your orders…": "आपके ऑर्डर लोड हो रहे हैं…", "You haven’t placed an order with this store yet.": "आपने अभी तक इस दुकान से कोई ऑर्डर नहीं किया है।", "Close": "बंद करें", "Remove one": "एक हटाएं", "Add quantity": "मात्रा जोड़ें",
  "New": "नया", "Confirmed": "पुष्ट", "Rejected": "अस्वीकृत", "Packing": "पैक हो रहा है", "Ready": "तैयार", "Out For Delivery": "डिलीवरी के लिए रवाना", "Pickup Ready": "पिकअप के लिए तैयार", "Delivered": "डिलीवर हो गया", "Completed": "पूरा", "Cancelled": "रद्द",
};

function translate(language, text) { return language === "hi" ? HINDI[text] || text : text; }

export default function Storefront({ businessId }) {
  const resetToken = new URLSearchParams(window.location.search).get("resetToken");
  const [language, setLanguage] = useState(() => localStorage.getItem("localflow.store-language") === "hi" ? "hi" : "en");
  const t = text => translate(language, text);
  const [store, setStore] = useState(null);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("All products");
  const [cart, setCart] = useState(() => readCart(businessId));
  const [cartOpen, setCartOpen] = useState(false);
  const [checkoutOpen, setCheckoutOpen] = useState(false);
  const [authOpen, setAuthOpen] = useState(Boolean(resetToken));
  const [authMode, setAuthMode] = useState(resetToken ? "reset" : "login");
  const [authForCheckout, setAuthForCheckout] = useState(false);
  const [customerSession, setCustomerSession] = useState(() => readCustomerSession(businessId));
  const [authBusy, setAuthBusy] = useState(false);
  const [authError, setAuthError] = useState("");
  const [authNotice, setAuthNotice] = useState(null);
  const [customerOrders, setCustomerOrders] = useState(null);
  const [ordersOpen, setOrdersOpen] = useState(false);
  const [busy, setBusy] = useState(false);
  const [checkoutError, setCheckoutError] = useState("");
  const [requestId, setRequestId] = useState("");
  const [placedOrder, setPlacedOrder] = useState(null);

  useEffect(() => {
    localStorage.setItem("localflow.store-language", language);
    document.documentElement.lang = language === "hi" ? "hi" : "en";
  }, [language]);

  useEffect(() => {
    let active = true;
    fetch(apiUrl(`/storefront/${encodeURIComponent(businessId)}`), { headers: { Accept: "application/json" } })
      .then(async response => {
        const payload = await response.json().catch(() => null);
        if (!response.ok) throw new Error(payload?.message || "This store could not be loaded.");
        return payload;
      })
      .then(payload => { if (active) { setStore(payload); setPageError(""); } })
      .catch(error => { if (active) setPageError(error.message); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [businessId]);

  useEffect(() => {
    if (store?.name) document.title = `${store.name} | LocalFlow`;
    return () => { document.title = "LocalFlow Vendor"; };
  }, [store?.name]);

  useEffect(() => {
    localStorage.setItem(`localflow.store-cart.${businessId}`, JSON.stringify(cart));
  }, [businessId, cart]);

  const products = store?.products || [];
  const categories = useMemo(() => ["All products", ...new Set(products.map(product => product.categoryName).filter(Boolean))], [products]);
  const visibleProducts = useMemo(() => products.filter(product =>
    (category === "All products" || product.categoryName === category)
      && `${product.name} ${product.description || ""} ${product.categoryName || ""}`.toLowerCase().includes(search.toLowerCase())
  ), [products, category, search]);
  const cartLines = products.filter(product => cart[product.id]).map(product => ({ ...product, quantity: cart[product.id] }));
  const cartAmounts = cartLines.reduce((amounts, item) => {
    const gross = roundMoney(Number(item.price) * Number(item.quantity));
    const discount = roundMoney(gross * (Number(item.discountPercent || 0) / 100));
    const taxable = roundMoney(gross - discount);
    const tax = roundMoney(taxable * (Number(item.taxPercent || 0) / 100));
    return { subtotal: amounts.subtotal + gross, discount: amounts.discount + discount, taxable: amounts.taxable + taxable, tax: amounts.tax + tax };
  }, { subtotal: 0, discount: 0, taxable: 0, tax: 0 });
  const total = roundMoney(cartAmounts.taxable + cartAmounts.tax);
  const itemCount = cartLines.reduce((sum, item) => sum + 1, 0);

  function updateQuantity(product, amount) {
    setCart(current => {
      const next = { ...current };
      const quantity = Number(next[product.id] || 0) + amount;
      if (quantity < Number(product.minQuantity)) delete next[product.id];
      else next[product.id] = Number(quantity.toFixed(3));
      return next;
    });
  }

  function beginCheckout() {
    setCheckoutError("");
    if (!customerSession?.accessToken) {
      setAuthMode("login");
      setAuthError("");
      setAuthForCheckout(true);
      setAuthOpen(true);
      return;
    }
    setCheckoutOpen(true);
  }

  async function submitAuth(event) {
    event.preventDefault();
    setAuthBusy(true);
    setAuthError("");
    const form = new FormData(event.currentTarget);
    const registering = authMode === "register";
    if ((registering || authMode === "forgot") && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(form.get("email") || "").trim())) {
      setAuthError(t("Enter a valid email address."));
      setAuthBusy(false);
      return;
    }
    if (authMode === "forgot") {
      try {
        const response = await fetch(apiUrl("/customer-auth/forgot-password"), { method: "POST", headers: { Accept: "application/json", "Content-Type": "application/json" }, body: JSON.stringify({ storefrontSlug: businessId, email: String(form.get("email")).trim() }) });
        const result = await response.json().catch(() => null);
        if (!response.ok) throw new Error(result?.message || t("Could not send reset instructions."));
        setAuthNotice(result); setAuthError("");
      } catch (error) { setAuthError(error.message); }
      finally { setAuthBusy(false); }
      return;
    }
    if (authMode === "reset") {
      if (form.get("password") !== form.get("confirmPassword")) {
        setAuthError(t("Passwords do not match.")); setAuthBusy(false); return;
      }
      try {
        const response = await fetch(apiUrl("/customer-auth/reset-password"), { method: "POST", headers: { Accept: "application/json", "Content-Type": "application/json" }, body: JSON.stringify({ token: resetToken, password: form.get("password") }) });
        if (!response.ok) { const result = await response.json().catch(() => null); throw new Error(result?.message || t("Could not reset password.")); }
        const url = new URL(window.location.href); url.searchParams.delete("resetToken"); window.history.replaceState({}, "", url);
        localStorage.removeItem(`localflow.customer-session.${businessId}`); setCustomerSession(null);
        setAuthMode("login"); setAuthNotice({ message: t("Your password has been reset. You can sign in now.") }); setAuthError("");
      } catch (error) { setAuthError(error.message); }
      finally { setAuthBusy(false); }
      return;
    }
    if (registering && form.get("password") !== form.get("confirmPassword")) {
      setAuthError(t("Passwords do not match."));
      setAuthBusy(false);
      return;
    }
    const payload = registering
      ? { storefrontSlug: businessId, registrationCode: form.get("registrationCode").trim(), name: form.get("name").trim(), phone: form.get("phone").trim(), email: form.get("email").trim(), password: form.get("password") }
      : { storefrontSlug: businessId, email: form.get("email").trim(), password: form.get("password") };
    try {
      const response = await fetch(apiUrl(`/customer-auth/${registering ? "register" : "login"}`), {
        method: "POST", headers: { Accept: "application/json", "Content-Type": "application/json" }, body: JSON.stringify(payload),
      });
      const result = await response.json().catch(() => null);
      if (!response.ok) throw new Error(result?.message || `Could not ${registering ? "create account" : "sign in"} (HTTP ${response.status}).`);
      localStorage.setItem(`localflow.customer-session.${businessId}`, JSON.stringify(result));
      setCustomerSession(result);
      setAuthOpen(false);
      setAuthNotice(null);
      setCheckoutOpen(authForCheckout);
      setAuthForCheckout(false);
    } catch (error) {
      setAuthError(error.message);
    } finally {
      setAuthBusy(false);
    }
  }

  async function openCustomerOrders() {
    setOrdersOpen(true);
    setCustomerOrders(null);
    try {
      const response = await fetch(apiUrl("/customer-portal/orders?page=0&size=100"), {
        headers: { Accept: "application/json", Authorization: `Bearer ${customerSession.accessToken}` },
      });
      const result = await response.json().catch(() => null);
      if (response.status === 401) {
        localStorage.removeItem(`localflow.customer-session.${businessId}`);
        setCustomerSession(null);
        throw new Error("Your customer session expired. Sign in again to view orders.");
      }
      if (!response.ok) throw new Error(result?.message || "Could not load your orders.");
      setCustomerOrders(result.content || []);
    } catch (error) {
      setCustomerOrders({ error: error.message });
    }
  }

  async function submitOrder(event) {
    event.preventDefault();
    if (!cartLines.length) return;
    setBusy(true);
    setCheckoutError("");
    const form = new FormData(event.currentTarget);
    const idempotencyKey = requestId || crypto.randomUUID();
    setRequestId(idempotencyKey);
    const payload = {
      requestId: idempotencyKey,
      addressLine1: form.get("addressLine1").trim(),
      addressLine2: optional(form.get("addressLine2")),
      locality: optional(form.get("locality")),
      city: form.get("city").trim(),
      state: optional(form.get("state")),
      postalCode: optional(form.get("postalCode")),
      countryCode: optional(form.get("countryCode")) || "IN",
      deliveryInstructions: optional(form.get("deliveryInstructions")),
      notes: optional(form.get("notes")),
      items: cartLines.map(item => ({ productId: item.id, quantity: item.quantity })),
    };
    try {
      const response = await fetch(apiUrl(`/storefront/${encodeURIComponent(businessId)}/orders`), {
        method: "POST",
        headers: { Accept: "application/json", "Content-Type": "application/json", Authorization: `Bearer ${customerSession.accessToken}` },
        body: JSON.stringify(payload),
      });
      const result = await response.json().catch(() => null);
      if (response.status === 401) {
        localStorage.removeItem(`localflow.customer-session.${businessId}`);
        setCustomerSession(null);
        setCheckoutOpen(false);
        setAuthMode("login");
        setAuthForCheckout(true);
        setAuthOpen(true);
        throw new Error("Your customer session expired. Sign in again to place the order.");
      }
      if (!response.ok) throw new Error(result?.message || `Could not place order (HTTP ${response.status}).`);
      setPlacedOrder(result);
      setCart({});
      setCheckoutOpen(false);
      setCartOpen(false);
      setRequestId("");
    } catch (error) {
      setCheckoutError(error.message);
    } finally {
      setBusy(false);
    }
  }

  if (loading) return <main className="shop-loading">{t("Opening store…")}</main>;
  if (pageError || !store) return <main className="shop-message"><strong>{t("Store unavailable")}</strong><p>{pageError || t("This store could not be found.")}</p></main>;

  return <main className="shop-page">
    <header className="shop-header">
      <a className="shop-brand" href={`?shop=${encodeURIComponent(businessId)}`} aria-label={`${store.name} home`}><span className="shop-brand-mark">L</span><span>{store.name}</span></a>
      {customerSession?.customer ? <div className="shop-account"><button className="shop-orders-link" type="button" onClick={openCustomerOrders}>{t("My orders")}</button><span>{customerSession.customer.name}</span><button className="shop-signout" type="button" onClick={() => { localStorage.removeItem(`localflow.customer-session.${businessId}`); setCustomerSession(null); }}>{t("Sign out")}</button></div> : <><span className="shop-header-note">{t("Local pickup and delivery orders")}</span><div className="shop-guest-actions"><button className="shop-signin-button" type="button" onClick={() => { setAuthMode("login"); setAuthForCheckout(false); setAuthError(""); setAuthOpen(true); }}>{t("Sign in")}</button><button className="shop-register-button" type="button" onClick={() => { setAuthMode("register"); setAuthForCheckout(false); setAuthError(""); setAuthOpen(true); }}>{t("Create account")}</button></div></>}
      <button className="shop-language-button" type="button" onClick={() => setLanguage(current => current === "en" ? "hi" : "en")} aria-label={language === "en" ? "हिंदी में देखें" : "View in English"}>{language === "en" ? "हिंदी" : "English"}</button>
      <button className="shop-cart-button" type="button" onClick={() => setCartOpen(true)}>{t("Cart")} <span>{itemCount}</span></button>
    </header>

    <div className="shop-hero" style={{ backgroundImage: `linear-gradient(90deg, rgb(13 55 39 / 88%), rgb(13 55 39 / 22%)), url(${shopArt})` }}>
      <div className="shop-hero-copy"><span className="shop-kicker">{t("Shop local")}</span><h1>{store.name}</h1><p>{store.businessType} · {t("Your neighborhood business, now online")}</p></div>
    </div>

    <section className="shop-main">
      <div className="shop-catalog">
        <div className="shop-section-heading"><div><span className="shop-kicker">{t("The catalogue")}</span><h2>{t("What would you like?")}</h2></div><label className="shop-search"><span className="visually-hidden">{t("Search products")}</span><input type="search" placeholder={t("Search products")} value={search} onChange={event => setSearch(event.target.value)} /></label></div>
        <nav className="shop-categories" aria-label={t("Product categories")}>{categories.map(item => <button type="button" key={item} className={category === item ? "selected" : ""} onClick={() => setCategory(item)}>{item === "All products" ? t(item) : item}</button>)}</nav>
        {visibleProducts.length ? <div className="shop-product-grid">{visibleProducts.map(product => <article className="shop-product" key={product.id}>
          <div className="shop-product-top"><span className="shop-product-category">{product.categoryName || t("Store pick")}</span><span className="shop-product-unit">{t("per")} {product.unit}</span></div>
          <h3>{product.name}</h3>{product.description && <p>{product.description}</p>}
          <div className="shop-product-bottom"><div className="shop-product-price">{Number(product.discountPercent) > 0 && <><span className="shop-original-price">{formatMoney(product.price, store.currency)}</span><span className="shop-discount-badge">{product.discountPercent}% {t("off")}</span></>}<strong>{formatMoney(product.discountedPrice ?? product.price, store.currency)}</strong>{product.taxType && <span>+ {product.taxPercent}% {product.taxType}</span>}<span>{t("Min")} {product.minQuantity} {product.unit}</span></div>
            {cart[product.id] ? <div className="shop-quantity"><button type="button" aria-label={`Remove one ${product.name}`} onClick={() => updateQuantity(product, -Number(product.quantityStep))}>−</button><span>{cart[product.id]} {product.unit}</span><button type="button" aria-label={`Add ${product.quantityStep} ${product.unit} of ${product.name}`} onClick={() => updateQuantity(product, Number(product.quantityStep))}>+</button></div>
              : <button className="shop-add" type="button" onClick={() => updateQuantity(product, Number(product.minQuantity))}>{t("Add")}</button>}</div>
        </article>)}</div> : <div className="shop-empty">{t("No matching products. Try another search or category.")}</div>}
      </div>
      <aside className="shop-cart-panel"><CartSummary store={store} cartLines={cartLines} amounts={cartAmounts} total={total} updateQuantity={updateQuantity} checkout={beginCheckout} t={t} language={language} /></aside>
    </section>

    <footer className="shop-footer"><span>{store.name}</span>{store.phone && <a href={`tel:${store.phone}`}>{store.phone}</a>}</footer>
    <button className="shop-mobile-cart" type="button" onClick={() => setCartOpen(true)}>{t("View cart")} <span>{itemCount} {t("items")}</span><strong>{formatMoney(total, store.currency)}</strong></button>

    {cartOpen && <div className="shop-overlay" onMouseDown={event => { if (event.target === event.currentTarget) setCartOpen(false); }}><section className="shop-sheet" role="dialog" aria-modal="true" aria-label={t("Your cart")}><button className="shop-close" type="button" aria-label={t("Close")} onClick={() => setCartOpen(false)}>×</button><CartSummary store={store} cartLines={cartLines} amounts={cartAmounts} total={total} updateQuantity={updateQuantity} checkout={() => { setCartOpen(false); beginCheckout(); }} t={t} language={language} /></section></div>}

    {authOpen && <div className="shop-overlay" onMouseDown={event => { if (event.target === event.currentTarget) setAuthOpen(false); }}><section className="shop-auth" role="dialog" aria-modal="true" aria-labelledby="shop-auth-title"><header><div><span className="shop-kicker">{store.name}</span><h2 id="shop-auth-title">{t(authMode === "register" ? "Create your customer account" : authMode === "forgot" ? "Forgot password" : authMode === "reset" ? "Set a new password" : "Sign in to continue")}</h2><p>{t(authMode === "register" ? "Ask the store for its customer signup code." : authMode === "forgot" ? "Enter your account email and we’ll send reset instructions." : authMode === "reset" ? "Choose a new password for your account." : "Sign in to place your order and see your order history.")}</p></div><button className="shop-close" type="button" aria-label={t("Close")} onClick={() => setAuthOpen(false)}>×</button></header><form onSubmit={submitAuth}><div className="shop-auth-body">{authMode === "register" && <><ShopField t={t} label="Customer signup code" name="registrationCode" required maxLength={32} autoComplete="off" /><div className="shop-form-grid"><ShopField t={t} label="Full name" name="name" required maxLength={160} autoComplete="name" /><ShopField t={t} label="Phone number" name="phone" required type="tel" autoComplete="tel" /></div></>}{["login", "register", "forgot"].includes(authMode) && <ShopField t={t} label="Email" name="email" required type="email" autoComplete="email" />}{["login", "register", "reset"].includes(authMode) && <ShopPasswordField t={t} name="password" required minLength={8} autoComplete={authMode === "login" ? "current-password" : "new-password"} />}{["register", "reset"].includes(authMode) && <ShopPasswordField t={t} name="confirmPassword" label="Confirm password" required minLength={8} autoComplete="new-password" />}{authError && <div className="shop-error" role="alert">{authError}</div>}{authNotice && <div className="shop-auth-notice" role="status">{authNotice.deliveryMode === "NOT_CONFIGURED" ? t("Password reset delivery isn't configured. Add SMTP settings to receive email, or enable the local development reset link.") : t(authNotice.message)}{authNotice.developmentResetUrl && <a href={authNotice.developmentResetUrl}>{t("Open reset link")}</a>}</div>}</div><footer>{authMode === "login" ? <button className="shop-place-order" type="submit" disabled={authBusy}>{authBusy ? t("Please wait…") : t("Sign in")}</button> : authMode === "register" ? <><button className="shop-auth-switch" type="button" onClick={() => { setAuthMode("login"); setAuthError(""); setAuthNotice(null); }}>{t("Already registered? Sign in")}</button><button className="shop-place-order" type="submit" disabled={authBusy}>{authBusy ? t("Please wait…") : t("Create account")}</button></> : <><button className="shop-auth-switch" type="button" onClick={() => { setAuthMode("login"); setAuthError(""); setAuthNotice(null); }}>{t("Back to sign in")}</button><button className="shop-place-order" type="submit" disabled={authBusy}>{authBusy ? t("Please wait…") : t(authMode === "forgot" ? "Send reset instructions" : "Reset password")}</button></>}</footer></form></section></div>}

    {checkoutOpen && <div className="shop-overlay" onMouseDown={event => { if (event.target === event.currentTarget) setCheckoutOpen(false); }}><section className="shop-checkout" role="dialog" aria-modal="true" aria-labelledby="checkout-title"><header><div><span className="shop-kicker">{t("Checkout")}</span><h2 id="checkout-title">{t("Delivery details")}</h2></div><button className="shop-close" type="button" aria-label={t("Close")} onClick={() => setCheckoutOpen(false)}>×</button></header>
      <form onSubmit={submitOrder}><div className="shop-checkout-body"><div className="shop-checkout-total"><span>{itemCount} {t(itemCount === 1 ? "item" : "items")} · {t("incl. tax")}</span><strong>{formatMoney(total, store.currency)}</strong></div><p className="shop-checkout-pricing">{t("MRP")} {formatMoney(cartAmounts.subtotal, store.currency)}{cartAmounts.discount > 0 ? ` − ${formatMoney(cartAmounts.discount, store.currency)} ${t("discount")}` : ""} + {formatMoney(cartAmounts.tax, store.currency)} {t("tax")}</p>
        <p className="shop-checkout-customer">{t("Ordering as")} <strong>{customerSession?.customer?.name}</strong> · {customerSession?.customer?.email}</p><div className="shop-form-grid"><ShopField t={t} label="Address" name="addressLine1" required maxLength={240} autoComplete="address-line1" /><ShopField t={t} label="Apartment, suite, etc." name="addressLine2" maxLength={240} autoComplete="address-line2" /><ShopField t={t} label="Area / locality" name="locality" maxLength={160} /><ShopField t={t} label="City" name="city" required maxLength={120} autoComplete="address-level2" /><ShopField t={t} label="State" name="state" maxLength={120} autoComplete="address-level1" /><ShopField t={t} label="Postal code" name="postalCode" maxLength={24} autoComplete="postal-code" /><ShopField t={t} label="Delivery instructions" name="deliveryInstructions" maxLength={500} /></div>
        <label className="shop-notes-field">{t("Order note")} <textarea name="notes" maxLength={1000} placeholder={t("Anything the store should know?")} /></label>
        {checkoutError && <div className="shop-error" role="alert">{checkoutError}</div>}
      </div><footer><button type="button" className="shop-back" onClick={() => setCheckoutOpen(false)}>{t("Back to cart")}</button><button className="shop-place-order" type="submit" disabled={busy || !cartLines.length}>{busy ? t("Placing order…") : `${t("Place order")} · ${formatMoney(total, store.currency)}`}</button></footer></form>
    </section></div>}

    {placedOrder && <div className="shop-overlay"><section className="shop-success" role="dialog" aria-modal="true" aria-labelledby="success-title"><span className="shop-success-mark">✓</span><span className="shop-kicker">{t("Order received")}</span><h2 id="success-title">{t("Thank you,")} {placedOrder.customerName}.</h2><p>{t("Your order")} <strong>{placedOrder.orderNumber}</strong> {t("has been sent to")} {store.name}.</p><div className="shop-success-total"><span>{t("Order total")}</span><strong>{formatMoney(placedOrder.totalAmount, placedOrder.currency)}</strong></div><button className="shop-place-order" type="button" onClick={() => setPlacedOrder(null)}>{t("Continue shopping")}</button></section></div>}
    {ordersOpen && <div className="shop-overlay" onMouseDown={event => { if (event.target === event.currentTarget) setOrdersOpen(false); }}><section className="shop-orders-dialog" role="dialog" aria-modal="true" aria-labelledby="my-orders-title"><header><div><span className="shop-kicker">{t("Your account")}</span><h2 id="my-orders-title">{t("My orders")}</h2></div><button className="shop-close" type="button" aria-label={t("Close")} onClick={() => setOrdersOpen(false)}>×</button></header>{customerOrders === null ? <p className="shop-orders-state">{t("Loading your orders…")}</p> : customerOrders.error ? <p className="shop-orders-state shop-error">{customerOrders.error}</p> : customerOrders.length ? <div className="shop-order-list">{customerOrders.map(order => <article key={order.id}><div><strong>{order.orderNumber}</strong><span>{translate(language, displayStatus(order.status))} · {new Date(order.createdAt).toLocaleDateString(language === "hi" ? "hi-IN" : "en-IN")}</span></div><strong>{formatMoney(order.totalAmount, order.currency)}</strong></article>)}</div> : <p className="shop-orders-state">{t("You haven’t placed an order with this store yet.")}</p>}</section></div>}
  </main>;
}

function CartSummary({ store, cartLines, amounts, total, updateQuantity, checkout, t, language }) {
  return <><div className="shop-cart-heading"><div><span className="shop-kicker">{t("Your basket")}</span><h2>{t("Order summary")}</h2></div><span>{cartLines.length} {t(cartLines.length === 1 ? "item" : "items")}</span></div>
    {cartLines.length ? <><div className="shop-cart-lines">{cartLines.map(item => {
      const line = cartLineAmounts(item);
      return <div className="shop-cart-line" key={item.id}><div><strong>{item.name}</strong><span>{item.quantity} {item.unit} × {formatMoney(item.price, store.currency)}</span>{Number(item.discountPercent) > 0 && <span className="shop-savings">{item.discountPercent}% {t("off")} · −{formatMoney(line.discount, store.currency)}</span>}{item.taxType && <span>{item.taxType} {item.taxPercent}% {t("tax")} · {formatMoney(line.tax, store.currency)}</span>}</div><div className="shop-cart-line-end"><strong>{formatMoney(line.total, store.currency)}</strong><div className="shop-quantity compact"><button type="button" aria-label={`${t("Remove one")} ${item.name}`} onClick={() => updateQuantity(item, -Number(item.quantityStep))}>−</button><span>{item.quantity}</span><button type="button" aria-label={`${t("Add quantity")} ${item.quantityStep} ${item.unit} ${item.name}`} onClick={() => updateQuantity(item, Number(item.quantityStep))}>+</button></div></div></div>;
    })}</div><div className="shop-cart-breakdown"><div><span>{t("MRP")}</span><strong>{formatMoney(amounts.subtotal, store.currency)}</strong></div>{amounts.discount > 0 && <div className="shop-savings"><span>{t("Discount amount")}</span><strong>−{formatMoney(amounts.discount, store.currency)}</strong></div>}<div><span>{t("Taxes")}</span><strong>{formatMoney(amounts.tax, store.currency)}</strong></div><div className="shop-cart-grand-total"><span>{t("Total")}</span><strong>{formatMoney(total, store.currency)}</strong></div></div><p className="shop-cart-note">{t("Taxes are calculated after discount. Delivery charges, if any, will be confirmed by the store.")}</p><button className="shop-place-order" type="button" onClick={checkout}>{t("Continue to checkout")}</button></>
      : <div className="shop-cart-empty"><span className="shop-cart-empty-mark">0</span><strong>{t("Your basket is empty")}</strong><p>{t("Add products to start an order.")}</p></div>}
  </>;
}

function ShopField({ label, name, required = false, t = text => text, ...props }) {
  const id = `checkout-${name}`;
  return <label className="shop-field" htmlFor={id}><span>{t(label)}{required && <b aria-hidden="true"> *</b>}</span><input id={id} name={name} required={required} {...props} /></label>;
}

function ShopPasswordField({ t, name, label = "Password", required, minLength, autoComplete }) {
  const [visible, setVisible] = useState(false);
  const id = `checkout-${name}`;
  return <div className="shop-field"><label htmlFor={id}>{t(label)}{required && <b aria-hidden="true"> *</b>}</label><div className="shop-password-wrap"><input id={id} name={name} type={visible ? "text" : "password"} required={required} minLength={minLength} autoComplete={autoComplete} /><button className="shop-password-toggle" type="button" onClick={() => setVisible(value => !value)} aria-label={t(visible ? "Hide" : "Show")} title={t(visible ? "Hide" : "Show")} aria-pressed={visible}><ShopEyeIcon visible={visible} /></button></div></div>;
}

function ShopEyeIcon({ visible }) { return <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{visible ? <><path d="M3 3l18 18" /><path d="M10.6 10.6a2 2 0 0 0 2.8 2.8" /><path d="M9.9 5.2A10.8 10.8 0 0 1 12 5c5 0 8.5 4.5 9.5 7-.4 1-1.3 2.3-2.5 3.5" /><path d="M6.2 6.2C4.2 7.5 2.9 9.5 2.5 12c.5 1.2 1.6 2.8 3.4 4.1A10.3 10.3 0 0 0 12 18c.8 0 1.5-.1 2.2-.3" /></> : <><path d="M2.5 12s3.5-7 9.5-7 9.5 7 9.5 7-3.5 7-9.5 7-9.5-7-9.5-7Z" /><circle cx="12" cy="12" r="3" /></>}</svg>; }

function readCart(businessId) {
  try { return JSON.parse(localStorage.getItem(`localflow.store-cart.${businessId}`) || "{}"); }
  catch { return {}; }
}

function readCustomerSession(slug) {
  try { return JSON.parse(localStorage.getItem(`localflow.customer-session.${slug}`) || "null"); }
  catch { return null; }
}

function optional(value) { const text = String(value || "").trim(); return text || null; }
function displayStatus(status) { return String(status || "").replaceAll("_", " ").toLowerCase().replace(/\b\w/g, character => character.toUpperCase()); }
function formatMoney(value, currency = "INR") { try { return new Intl.NumberFormat("en-IN", { style: "currency", currency }).format(Number(value || 0)); } catch { return `${currency} ${value || 0}`; } }
function roundMoney(value) { return Math.round((Number(value) + Number.EPSILON) * 100) / 100; }
function cartLineAmounts(item) {
  const gross = roundMoney(Number(item.price) * Number(item.quantity));
  const discount = roundMoney(gross * (Number(item.discountPercent || 0) / 100));
  const tax = roundMoney((gross - discount) * (Number(item.taxPercent || 0) / 100));
  return { discount, tax, total: roundMoney(gross - discount + tax) };
}
