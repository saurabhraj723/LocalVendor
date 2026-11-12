import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { printInvoice } from "./invoice";

const API_BASE = "/api/v1";
const LanguageContext = createContext(null);
const HINDI = {
  "Dashboard": "डैशबोर्ड", "Orders": "ऑर्डर", "Products": "उत्पाद", "Customers": "ग्राहक", "Workspace": "कार्य क्षेत्र", "Sign out": "साइन आउट", "Your business": "आपका व्यवसाय", "Business owner": "व्यवसाय के मालिक", "Switch color theme": "रंग थीम बदलें", "Hindi": "हिंदी", "English": "English",
  "Vendor workspace": "विक्रेता कार्यक्षेत्र", "Everything your local business needs to keep moving.": "आपके स्थानीय व्यवसाय को आगे बढ़ाने के लिए सब कुछ।", "Manage products, customers and orders in one simple workspace built for businesses like yours.": "अपने जैसे व्यवसायों के लिए बने एक आसान कार्यक्षेत्र में उत्पाद, ग्राहक और ऑर्डर संभालें।", "Create your business account": "व्यवसाय खाता बनाएं", "Welcome back": "वापसी पर स्वागत है", "Set up your vendor workspace to get started.": "शुरू करने के लिए अपना विक्रेता कार्यक्षेत्र सेट करें।", "Sign in to manage your business.": "अपने व्यवसाय को संभालने के लिए साइन इन करें।", "Business name": "व्यवसाय का नाम", "Business type": "व्यवसाय का प्रकार", "GST number": "GST नंबर", "Enter a valid 15-character GSTIN.": "मान्य 15-अक्षर का GSTIN दर्ज करें।", "Your name": "आपका नाम", "Email": "ईमेल", "Business and sign-in email": "व्यवसाय और साइन-इन ईमेल", "Enter a valid email address.": "मान्य ईमेल पता दर्ज करें।", "An account or business profile with this email already exists.": "इस ईमेल से खाता या व्यवसाय प्रोफ़ाइल पहले से मौजूद है।", "Password": "पासवर्ड", "Confirm password": "पासवर्ड की पुष्टि करें", "Passwords do not match.": "दोनों पासवर्ड मेल नहीं खाते।", "Show": "दिखाएं", "Hide": "छिपाएं", "Please wait…": "कृपया प्रतीक्षा करें…", "Create account": "खाता बनाएं", "Sign in": "साइन इन", "Already have an account?": "पहले से खाता है?", "New to LocalFlow?": "LocalFlow पर नए हैं?", "Create an account": "खाता बनाएं",
  "Forgot password": "पासवर्ड भूल गए", "Set a new password": "नया पासवर्ड बनाएं", "Enter your account email and we’ll send reset instructions.": "अपने खाते का ईमेल दर्ज करें। हम पासवर्ड रीसेट निर्देश भेजेंगे।", "Choose a new password for your account.": "अपने खाते के लिए नया पासवर्ड चुनें।", "Forgot password?": "पासवर्ड भूल गए?", "Back to sign in": "साइन इन पर वापस जाएं", "Send reset instructions": "रीसेट निर्देश भेजें", "Reset password": "पासवर्ड रीसेट करें", "If an account matches these details, password reset instructions have been sent.": "यदि इन विवरणों से खाता मिलता है, तो पासवर्ड रीसेट निर्देश भेज दिए गए हैं।", "Password reset delivery isn't configured. Add SMTP settings to receive email, or enable the local development reset link.": "पासवर्ड रीसेट ईमेल सेट नहीं है। ईमेल पाने के लिए SMTP सेटिंग जोड़ें या स्थानीय डेवलपमेंट रीसेट लिंक चालू करें।", "Your password has been reset. You can sign in now.": "आपका पासवर्ड रीसेट हो गया है। अब साइन इन करें।", "This password reset link is invalid or has expired.": "यह पासवर्ड रीसेट लिंक अमान्य है या इसकी समय-सीमा समाप्त हो गई है।", "Could not reset password.": "पासवर्ड रीसेट नहीं हो सका।", "Could not send reset instructions.": "रीसेट निर्देश नहीं भेजे जा सके।", "Open reset link": "रीसेट लिंक खोलें",
  "Today's orders": "आज के ऑर्डर", "Orders created today": "आज बनाए गए ऑर्डर", "Today's sales": "आज की बिक्री", "Excludes rejected and cancelled": "रद्द और अस्वीकृत ऑर्डर शामिल नहीं", "Pending orders": "लंबित ऑर्डर", "Awaiting completion": "पूरा होने की प्रतीक्षा में", "New customers today": "आज के नए ग्राहक", "Added today": "आज जोड़े गए", "Business overview": "व्यवसाय की जानकारी", "View orders": "ऑर्डर देखें", "Public customer store": "सार्वजनिक ग्राहक स्टोर", "Customer signup code": "ग्राहक पंजीकरण कोड", "Loading store link…": "स्टोर लिंक लोड हो रहा है…", "Loading signup code…": "पंजीकरण कोड लोड हो रहा है…", "Copy link": "लिंक कॉपी करें", "Copy code": "कोड कॉपी करें", "Recent orders": "हाल के ऑर्डर", "View all →": "सभी देखें →", "No orders yet": "अभी कोई ऑर्डर नहीं", "Create an order to see your daily activity here.": "दैनिक गतिविधि देखने के लिए ऑर्डर बनाएं।", "Create order": "ऑर्डर बनाएं",
  "New order": "नया ऑर्डर", "order": "ऑर्डर", "orders": "ऑर्डर", "Search by order or customer": "ऑर्डर या ग्राहक खोजें", "Search orders": "ऑर्डर खोजें", "Main navigation": "मुख्य नेविगेशन", "Order": "ऑर्डर", "Customer": "ग्राहक", "Channel": "चैनल", "Status": "स्थिति", "Created": "बनाया गया", "Total": "कुल", "No matching orders": "कोई मिलता-जुलता ऑर्डर नहीं", "Try another search.": "दूसरी खोज करें।", "Create an order for a customer.": "ग्राहक के लिए ऑर्डर बनाएं।", "Change status for": "की स्थिति बदलें",
  "Manage categories": "श्रेणियां प्रबंधित करें", "Add product": "उत्पाद जोड़ें", "Product": "उत्पाद", "product": "उत्पाद", "Category": "श्रेणी", "MRP / Offer": "एमआरपी / ऑफ़र", "Tax": "कर", "Unit": "इकाई", "Available": "उपलब्ध", "Actions": "कार्रवाई", "No SKU": "SKU नहीं", "None": "कोई नहीं", "Edit": "संपादित करें", "Mark unavailable": "अनुपलब्ध करें", "Make available": "उपलब्ध करें", "Archive": "संग्रहित करें", "No products yet": "अभी कोई उत्पाद नहीं", "Add your first product to build your catalogue.": "अपनी उत्पाद सूची बनाने के लिए पहला उत्पाद जोड़ें।", "Search products": "उत्पाद खोजें", "Discount (% off MRP)": "छूट (% एमआरपी पर)", "Tax type": "कर का प्रकार", "No tax": "कोई कर नहीं", "GST": "जीएसटी", "VAT": "वैट", "Other": "अन्य", "Tax percentage (%)": "कर प्रतिशत (%)", "Minimum quantity": "न्यूनतम मात्रा", "Quantity step": "मात्रा वृद्धि", "Description": "विवरण", "Available for new orders": "नए ऑर्डर के लिए उपलब्ध", "Select category": "श्रेणी चुनें",
  "Name": "नाम", "Phone": "फ़ोन", "Notes": "नोट्स", "Delivery address": "डिलीवरी का पता", "Address label": "पते का लेबल", "Recipient name": "प्राप्तकर्ता का नाम", "Address line 1": "पता पंक्ति 1", "Address line 2": "पता पंक्ति 2", "Locality": "इलाका", "City": "शहर", "State": "राज्य", "Postal code": "पिन कोड", "Country code": "देश कोड", "Delivery instructions": "डिलीवरी निर्देश", "Use as default delivery address": "डिफ़ॉल्ट डिलीवरी पता बनाएं", "Add customer": "ग्राहक जोड़ें", "customer": "ग्राहक", "Search customers": "ग्राहक खोजें", "No customers yet": "अभी कोई ग्राहक नहीं", "Add a customer and delivery address to place orders.": "ऑर्डर करने के लिए ग्राहक और डिलीवरी पता जोड़ें।",
  "Price": "कीमत", "MRP": "एमआरपी", "off": "छूट", "Save changes": "बदलाव सहेजें", "Save customer": "ग्राहक सहेजें", "Save Customer": "ग्राहक सहेजें", "Cancel": "रद्द करें", "Saving…": "सहेजा जा रहा है…", "Close": "बंद करें", "Delete": "हटाएं", "New category": "नई श्रेणी", "Add category": "श्रेणी जोड़ें", "No categories yet. Add one below.": "अभी कोई श्रेणी नहीं है। नीचे नई श्रेणी जोड़ें।", "Adding…": "जोड़ा जा रहा है…", "This product will no longer be available for new orders.": "यह उत्पाद नए ऑर्डर के लिए उपलब्ध नहीं रहेगा।", "Products assigned to this category cannot be deleted.": "इस श्रेणी से जुड़े उत्पादों के रहते इसे हटाया नहीं जा सकता।", "Copy your customer store link": "ग्राहक स्टोर लिंक कॉपी करें", "Share this customer signup code": "ग्राहक पंजीकरण कोड साझा करें", "created.": "बनाया गया।", "Required when tax type is selected": "कर का प्रकार चुनने पर आवश्यक",
  "Order items": "ऑर्डर के उत्पाद", "Add item": "उत्पाद जोड़ें", "Place order": "ऑर्डर दें", "Remove item": "उत्पाद हटाएं", "Product": "उत्पाद", "Quantity": "मात्रा", "Qty": "मात्रा", "Line total (incl. tax)": "पंक्ति कुल (कर सहित)", "Estimated total": "अनुमानित कुल", "Select customer": "ग्राहक चुनें", "Add a customer before placing an order.": "ऑर्डर करने से पहले ग्राहक जोड़ें।", "Add and make a product available before placing an order.": "ऑर्डर करने से पहले उत्पाद जोड़ें और उपलब्ध करें।", "Order details": "ऑर्डर का विवरण", "Subtotal (MRP)": "उप-कुल (एमआरपी)", "Discount": "छूट", "Delivery": "डिलीवरी", "Items": "उत्पाद", "Discount amount": "छूट की राशि", "Line total": "पंक्ति कुल", "Status history": "स्थिति का इतिहास", "Retry": "फिर कोशिश करें", "Try again": "फिर कोशिश करें", "Loading…": "लोड हो रहा है…", "Close dialog": "डायलॉग बंद करें",
  "Print / Save PDF": "प्रिंट / PDF सहेजें", "Invoice": "इनवॉइस", "Invoice date": "इनवॉइस की तारीख", "Payment status": "भुगतान की स्थिति", "Paid": "भुगतान किया गया", "Unpaid": "भुगतान बाकी", "Bill to": "ग्राहक", "Unit price": "प्रति इकाई कीमत", "Amount": "राशि", "Thank you for your order.": "आपके ऑर्डर के लिए धन्यवाद।", "Allow pop-ups to print or save the invoice.": "इनवॉइस प्रिंट या सहेजने के लिए पॉप-अप की अनुमति दें।",
  "Profile": "प्रोफ़ाइल", "Business profile": "व्यवसाय प्रोफ़ाइल", "Business details": "व्यवसाय का विवरण", "Business address": "व्यवसाय का पता", "Currency": "मुद्रा", "Time zone": "समय क्षेत्र", "Edit profile": "प्रोफ़ाइल संपादित करें", "Business profile updated.": "व्यवसाय प्रोफ़ाइल अपडेट हुई।", "No business profile details found.": "व्यवसाय प्रोफ़ाइल का विवरण नहीं मिला।",
  "New": "नया", "Confirmed": "पुष्ट", "Rejected": "अस्वीकृत", "Packing": "पैक हो रहा है", "Ready": "तैयार", "Out For Delivery": "डिलीवरी के लिए रवाना", "Pickup Ready": "पिकअप के लिए तैयार", "Delivered": "डिलीवर हो गया", "Completed": "पूरा", "Cancelled": "रद्द", "Available": "उपलब्ध", "Unavailable": "अनुपलब्ध", "Vendor App": "विक्रेता ऐप", "Customer Web": "ग्राहक वेबसाइट", "Whatsapp": "व्हाट्सऐप", "Order moved to": "ऑर्डर की स्थिति बदली:", "Order created.": "ऑर्डर बनाया गया।", "Product availability updated.": "उत्पाद की उपलब्धता अपडेट हुई।", "Product archived.": "उत्पाद संग्रहित किया गया।", "Product updated.": "उत्पाद अपडेट किया गया।", "Product added.": "उत्पाद जोड़ा गया।", "Customer updated.": "ग्राहक अपडेट किया गया।", "Customer added.": "ग्राहक जोड़ा गया।", "Category added.": "श्रेणी जोड़ी गई।", "Category deleted.": "श्रेणी हटाई गई।", "Store link is still loading. Try again shortly.": "स्टोर लिंक लोड हो रहा है। थोड़ी देर बाद फिर कोशिश करें।", "Customer store link copied.": "ग्राहक स्टोर लिंक कॉपी हुआ।", "Signup code is still loading. Try again shortly.": "पंजीकरण कोड लोड हो रहा है। थोड़ी देर बाद फिर कोशिश करें।", "Customer signup code copied.": "ग्राहक पंजीकरण कोड कॉपी हुआ।", "Enter a valid email address.": "मान्य ईमेल पता दर्ज करें।", "Choose a customer and add at least one product.": "ग्राहक चुनें और कम से कम एक उत्पाद जोड़ें।", "Check each quantity against its product minimum and quantity step.": "हर उत्पाद की न्यूनतम मात्रा और मात्रा वृद्धि जांचें।", "Add each product only once; update its quantity instead.": "हर उत्पाद एक ही बार जोड़ें; मात्रा बदलने के लिए उसकी मात्रा अपडेट करें।", "Enter a valid category, price, minimum quantity and quantity step.": "मान्य श्रेणी, कीमत, न्यूनतम मात्रा और मात्रा वृद्धि दर्ज करें।", "Discount must be between 0% and 100% of MRP.": "छूट एमआरपी के 0% से 100% के बीच होनी चाहिए।", "A tax rate greater than 0% is required when a tax type is selected.": "कर का प्रकार चुनने पर कर दर 0% से अधिक होनी चाहिए।", "Delete ": "हटाएं ", "? Categories used by products cannot be deleted.": "? जिन श्रेणियों का उपयोग उत्पादों में हो रहा है, उन्हें हटाया नहीं जा सकता।",
};
function translate(language, text) { return language === "hi" ? HINDI[text] || text : text; }
function useLanguage() { return useContext(LanguageContext); }
const TRANSITIONS = {
  NEW: ["CONFIRMED", "REJECTED", "CANCELLED"], CONFIRMED: ["PACKING", "CANCELLED"],
  PACKING: ["READY"], READY: ["OUT_FOR_DELIVERY", "PICKUP_READY"],
  OUT_FOR_DELIVERY: ["DELIVERED"], PICKUP_READY: ["COMPLETED"],
  DELIVERED: [], COMPLETED: [], REJECTED: [], CANCELLED: [],
};

function fromSession(key) {
  try { return JSON.parse(sessionStorage.getItem(`localflow.${key}`) || "null"); }
  catch { return null; }
}

export default function App() {
  const [language, setLanguage] = useState(() => localStorage.getItem("localflow.vendor-language") === "hi" ? "hi" : "en");
  const [token, setToken] = useState(() => sessionStorage.getItem("localflow.token"));
  const [user, setUser] = useState(() => fromSession("user"));
  const [business, setBusiness] = useState(() => fromSession("business"));
  const [page, setPage] = useState("dashboard");
  const [theme, setTheme] = useState(() => localStorage.getItem("localflow.theme") || "light");
  const [toast, setToast] = useState(null);
  const resetToken = new URLSearchParams(window.location.search).get("resetToken");
  const showToast = useCallback((message, error = false) => setToast({ message, error }), []);

  useEffect(() => { document.documentElement.dataset.theme = theme; localStorage.setItem("localflow.theme", theme); }, [theme]);
  useEffect(() => { localStorage.setItem("localflow.vendor-language", language); document.documentElement.lang = language === "hi" ? "hi" : "en"; }, [language]);
  useEffect(() => { if (business) sessionStorage.setItem("localflow.business", JSON.stringify(business)); }, [business]);
  useEffect(() => { if (!toast) return undefined; const timer = setTimeout(() => setToast(null), 3200); return () => clearTimeout(timer); }, [toast]);

  const signOut = useCallback(() => {
    sessionStorage.removeItem("localflow.token"); sessionStorage.removeItem("localflow.user"); sessionStorage.removeItem("localflow.business");
    setToken(null); setUser(null); setBusiness(null); setPage("dashboard");
  }, []);
  const api = useCallback(async (path, options = {}) => {
    let response;
    try {
      response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: { Accept: "application/json", ...(options.body ? { "Content-Type": "application/json" } : {}), ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers },
      });
    } catch {
      throw new Error("Could not reach LocalFlow. Make sure the backend is running at localhost:8080.");
    }
    if (response.status === 401 && token) { signOut(); throw new Error("Your session expired. Please sign in again."); }
    if (!response.ok) { const result = await response.json().catch(() => null); throw new Error(result?.message || `Request failed (HTTP ${response.status}).`); }
    if (response.status === 204) return null;
    return response.json();
  }, [token, signOut]);

  const authenticate = result => {
    sessionStorage.setItem("localflow.token", result.accessToken);
    sessionStorage.setItem("localflow.user", JSON.stringify(result.user));
    sessionStorage.setItem("localflow.business", JSON.stringify(result.business));
    setToken(result.accessToken); setUser(result.user); setBusiness(result.business); setPage("dashboard");
  };
  const updateBusiness = useCallback(profile => setBusiness(current => ({ ...current, ...profile })), []);

  return <LanguageContext.Provider value={{ language, setLanguage, t: text => translate(language, text) }}><>
    {resetToken ? <AuthScreen api={api} onAuthenticated={authenticate} resetToken={resetToken} /> : token ? <AppShell page={page} setPage={setPage} theme={theme} setTheme={setTheme} user={user} business={business} updateBusiness={updateBusiness} signOut={signOut} api={api} toast={showToast} /> : <AuthScreen api={api} onAuthenticated={authenticate} />}
    {toast && <div id="toast" className={`show ${toast.error ? "error" : ""}`} role="status" aria-live="polite">{toast.message}</div>}
  </></LanguageContext.Provider>;
}

function AuthScreen({ api, onAuthenticated, resetToken = "" }) {
  const { language, setLanguage, t } = useLanguage();
  const [mode, setMode] = useState(resetToken ? "reset" : "login");
  const [error, setError] = useState("");
  const [notice, setNotice] = useState(null);
  const [busy, setBusy] = useState(false);
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError("");
    const form = new FormData(event.currentTarget);
    try {
      if (mode === "forgot") {
        setNotice(await api("/auth/forgot-password", { method: "POST", body: JSON.stringify({ email: value(form, "email") }) }));
        setBusy(false);
        return;
      }
      if (mode === "reset") {
        if (form.get("password") !== form.get("confirmPassword")) throw new Error("Passwords do not match.");
        await api("/auth/reset-password", { method: "POST", body: JSON.stringify({ token: resetToken, password: form.get("password") }) });
        window.history.replaceState({}, "", window.location.pathname);
        setMode("login"); setNotice({ message: "Your password has been reset. You can sign in now." }); setBusy(false);
        return;
      }
      const registering = mode === "register";
      if (registering && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value(form, "email"))) throw new Error("Enter a valid email address.");
      if (registering && form.get("password") !== form.get("confirmPassword")) throw new Error("Passwords do not match.");
      const gstin = registering ? optional(form.get("gstin"))?.toUpperCase() : null;
      if (gstin && !/^[A-Z0-9]{15}$/.test(gstin)) throw new Error("Enter a valid 15-character GSTIN.");
      const body = registering
        ? { businessName: value(form, "businessName"), businessType: value(form, "businessType"), gstin, businessPhone: null, businessEmail: value(form, "email"), currency: "INR", timezone: "Asia/Kolkata", ownerName: value(form, "ownerName"), ownerEmail: value(form, "email"), password: form.get("password") }
        : { email: value(form, "email"), password: form.get("password") };
      onAuthenticated(await api(registering ? "/auth/register" : "/auth/login", { method: "POST", body: JSON.stringify(body) }));
    } catch (requestError) { setError(requestError.message); setBusy(false); }
  }
  return <main className="auth-shell">
    <section className="auth-art" aria-label="LocalFlow">
      <Brand />
      <div className="auth-art-copy"><span className="eyebrow" style={{ color: "#d8f2e4" }}>{t("Vendor workspace")}</span><h1>{t("Everything your local business needs to keep moving.")}</h1><p>{t("Manage products, customers and orders in one simple workspace built for businesses like yours.")}</p></div>
      <div className="auth-foot">LocalFlow Vendor · V1</div>
    </section>
    <section className="auth-panel">
      <div className="auth-language-row"><Brand className="auth-brand" /><LanguageToggle language={language} setLanguage={setLanguage} t={t} /></div>
      <h2>{t(mode === "register" ? "Create your business account" : mode === "forgot" ? "Forgot password" : mode === "reset" ? "Set a new password" : "Welcome back")}</h2>
      <p>{t(mode === "register" ? "Set up your vendor workspace to get started." : mode === "forgot" ? "Enter your account email and we’ll send reset instructions." : mode === "reset" ? "Choose a new password for your account." : "Sign in to manage your business.")}</p>
      <form className="form-stack" onSubmit={submit}>
        {mode === "register" && <>
          <Field label={t("Business name")} name="businessName" required maxLength={160} autoComplete="organization" />
          <Field label={t("Business type")} name="businessType" required maxLength={80} defaultValue="GROCERY" placeholder="e.g. Grocery, Bakery" />
          <Field label={t("GST number")} name="gstin" maxLength={15} placeholder="15-character GSTIN" />
          <Field label={t("Your name")} name="ownerName" required maxLength={160} autoComplete="name" />
        </>}
        {(mode === "login" || mode === "register" || mode === "forgot") && <Field label={t(mode === "register" ? "Business and sign-in email" : "Email")} name="email" type="email" required autoComplete="email" />}
        {(mode === "login" || mode === "register" || mode === "reset") && <PasswordField label={t("Password")} name="password" required minLength={8} autoComplete={mode === "login" ? "current-password" : "new-password"} />}
        {(mode === "register" || mode === "reset") && <PasswordField label={t("Confirm password")} name="confirmPassword" required minLength={8} autoComplete="new-password" />}
        {error && <div className="error-banner" role="alert">{t(error)}</div>}
        {notice && <div className="success-banner" role="status">{notice.deliveryMode === "NOT_CONFIGURED" ? t("Password reset delivery isn't configured. Add SMTP settings to receive email, or enable the local development reset link.") : t(notice.message)}{notice.developmentResetUrl && <a href={notice.developmentResetUrl}>{t("Open reset link")}</a>}</div>}
        <button className="primary-button" type="submit" disabled={busy}>{busy ? t("Please wait…") : t(mode === "register" ? "Create account" : mode === "forgot" ? "Send reset instructions" : mode === "reset" ? "Reset password" : "Sign in")}</button>
      </form>
      {(mode === "login" || mode === "register") && <div className="auth-switch">{t(mode === "register" ? "Already have an account?" : "New to LocalFlow?")} <button className="text-button" type="button" onClick={() => { setMode(mode === "register" ? "login" : "register"); setError(""); setNotice(null); }}>{t(mode === "register" ? "Sign in" : "Create an account")}</button></div>}
      {(mode === "forgot" || mode === "reset") && <div className="auth-switch"><button className="text-button" type="button" onClick={() => { setMode("login"); setError(""); setNotice(null); }}>{t("Back to sign in")}</button></div>}
    </section>
  </main>;
}

function AppShell({ page, setPage, theme, setTheme, user, business, updateBusiness, signOut, api, toast }) {
  const { language, setLanguage, t } = useLanguage();
  const notify = (message, error = false) => toast(translate(language, message), error);
  const links = [["dashboard", "◫", "Dashboard"], ["orders", "▤", "Orders"], ["products", "▦", "Products"], ["customers", "♙", "Customers"], ["profile", "⚙", "Profile"]];
  const navigate = destination => setPage(destination);
  return <div className="app-shell">
    <aside className="sidebar">
      <Brand />
      <div className="nav-label">{t("Workspace")}</div>
      <nav className="nav-list" aria-label={t("Main navigation")}>{links.map(([id, icon, title]) => <button key={id} className={`nav-item ${page === id ? "active" : ""}`} onClick={() => navigate(id)} title={t(title)}><span className="nav-icon" aria-hidden="true">{icon}</span><span className="nav-text">{t(title)}</span></button>)}</nav>
      <div className="sidebar-bottom"><div className="business-switch"><span className="business-avatar">{(business?.name || "L").slice(0, 1).toUpperCase()}</span><div className="business-meta"><strong>{business?.name || t("Your business")}</strong><span>{user?.name || t("Business owner")}</span></div></div><button className="nav-item" onClick={signOut} title={t("Sign out")}><span className="nav-icon" aria-hidden="true">↪</span><span className="nav-text">{t("Sign out")}</span></button></div>
    </aside>
    <main className="main">
      <div className="topbar"><span className="muted small">{business?.name || ""}</span><LanguageToggle language={language} setLanguage={setLanguage} t={t} /><button className="icon-button" title={t("Switch color theme")} aria-label={t("Switch color theme")} onClick={() => setTheme(theme === "light" ? "dark" : "light")}>{theme === "light" ? "☾" : "☀"}</button><button className="secondary-button" onClick={signOut}>{t("Sign out")}</button></div>
      {page === "dashboard" && <Dashboard api={api} business={business} user={user} navigate={navigate} toast={notify} />}
      {page === "orders" && <Orders api={api} business={business} user={user} navigate={navigate} toast={notify} />}
      {page === "products" && <Products api={api} business={business} toast={notify} />}
      {page === "customers" && <Customers api={api} business={business} toast={notify} />}
      {page === "profile" && <BusinessProfile api={api} onBusinessUpdated={updateBusiness} toast={notify} />}
    </main>
  </div>;
}

function LanguageToggle({ language, setLanguage, t }) {
  return <button type="button" className="language-toggle" onClick={() => setLanguage(current => current === "en" ? "hi" : "en")} aria-label={language === "en" ? "हिंदी में बदलें" : "Switch to English"}>{language === "en" ? t("Hindi") : t("English")}</button>;
}

function Dashboard({ api, business, user, navigate, toast }) {
  const { t } = useLanguage();
  const [summary, setSummary] = useState(null); const [error, setError] = useState(""); const [order, setOrder] = useState(null);
  const [storefrontSettings, setStorefrontSettings] = useState(null);
  useEffect(() => { api("/business/storefront-settings").then(setStorefrontSettings).catch(() => {}); }, [api]);
  async function shareStorefront() {
    if (!storefrontSettings?.storefrontSlug) { toast("Store link is still loading. Try again shortly.", true); return; }
    const url = new URL(window.location.href);
    url.search = new URLSearchParams({ shop: storefrontSettings.storefrontSlug }).toString();
    try {
      await navigator.clipboard.writeText(url.toString());
      toast("Customer store link copied.");
    } catch {
      window.prompt("Copy your customer store link", url.toString());
    }
  }
  async function copySignupCode() {
    if (!storefrontSettings?.customerRegistrationCode) { toast("Signup code is still loading. Try again shortly.", true); return; }
    try { await navigator.clipboard.writeText(storefrontSettings.customerRegistrationCode); toast("Customer signup code copied."); }
    catch { window.prompt("Share this customer signup code", storefrontSettings.customerRegistrationCode); }
  }
  useEffect(() => { api("/dashboard/summary").then(setSummary).catch(requestError => setError(requestError.message)); }, [api]);
  if (error) return <PageError title="Dashboard" error={error} retry={() => { setError(""); api("/dashboard/summary").then(setSummary).catch(e => setError(e.message)); }} />;
  if (!summary) return <Loading />;
  const metrics = [[t("Today's orders"), summary.todayOrderCount, t("Orders created today")], [t("Today's sales"), money(summary.todaySales, summary.currency), t("Excludes rejected and cancelled")], [t("Pending orders"), summary.pendingOrderCount, t("Awaiting completion")], [t("New customers today"), summary.newCustomerCount, t("Added today")]];
  return <>
    <header className="page-heading"><div><h1>{t("Dashboard")}</h1><p>{business?.name || t("Business overview")}</p></div><div className="heading-actions"><button className="secondary-button" onClick={() => navigate("products")}>{t("Products")}</button><button className="secondary-button" onClick={() => navigate("customers")}>{t("Customers")}</button><button className="primary-button" onClick={() => navigate("orders")}>{t("View orders")}</button></div></header>
    <section className="store-access" aria-label="Customer storefront sharing details">
      <div><span className="store-access-label">{t("Public customer store")}</span><code>{storefrontSettings?.storefrontSlug ? `${window.location.origin}/?shop=${storefrontSettings.storefrontSlug}` : t("Loading store link…")}</code><button className="mini-button" type="button" onClick={shareStorefront}>{t("Copy link")}</button></div>
      <div><span className="store-access-label">{t("Customer signup code")}</span><code>{storefrontSettings?.customerRegistrationCode || t("Loading signup code…")}</code><button className="mini-button" type="button" onClick={copySignupCode}>{t("Copy code")}</button></div>
    </section>
    <section className="metrics">{metrics.map(([label, value, note]) => <div className="metric" key={label}><div className="metric-label">{label}</div><div className="metric-value">{value}</div><div className="metric-note">{note}</div></div>)}</section>
    <div className="section-head"><h2>{t("Recent orders")}</h2><button className="text-button" onClick={() => navigate("orders")}>{t("View all →")}</button></div>
    {summary.recentOrders?.length ? <OrderTable orders={summary.recentOrders} onOpen={setOrder} /> : <EmptyState title="No orders yet" description="Create an order to see your daily activity here." action="View orders" onAction={() => navigate("orders")} />}
    {order && <OrderDetails api={api} orderId={order.id} business={business} user={user} close={() => setOrder(null)} />}
  </>;
}

function Orders({ api, business, user, navigate, toast }) {
  const { language, t } = useLanguage();
  const [orders, setOrders] = useState(null); const [search, setSearch] = useState(""); const [order, setOrder] = useState(null); const [editor, setEditor] = useState(false); const [error, setError] = useState("");
  const refresh = useCallback(() => api("/orders?page=0&size=100").then(result => { setOrders(result.content || []); setError(""); }).catch(e => setError(e.message)), [api]);
  useEffect(() => { refresh(); }, [refresh]);
  if (error) return <PageError title="Orders" error={error} retry={refresh} />;
  if (!orders) return <Loading />;
  const filtered = orders.filter(item => `${item.orderNumber} ${item.customerName} ${item.status}`.toLowerCase().includes(search.toLowerCase()));
  return <>
    <header className="page-heading"><div><h1>{t("Orders")}</h1><p>{business?.name || ""} · {orders.length} {t(orders.length === 1 ? "order" : "orders")}</p></div><div className="heading-actions"><button className="primary-button" onClick={() => setEditor(true)}>＋ {t("New order")}</button></div></header>
    <div className="toolbar"><input className="search-input" type="search" placeholder={t("Search by order or customer")} aria-label={t("Search orders")} value={search} onChange={event => setSearch(event.target.value)} /></div>
    {filtered.length ? <div className="table-wrap"><table className="mobile-table"><thead><tr>{["Order", "Customer", "Channel", "Status", "Created", "Total"].map(label => <th key={label}>{t(label)}</th>)}</tr></thead><tbody>{filtered.map(item => <tr key={item.id}><td data-label={t("Order")}><button className="link-button order-link" onClick={() => setOrder(item)}>{item.orderNumber}</button></td><td data-label={t("Customer")}>{item.customerName}</td><td data-label={t("Channel")}>{t(displayStatus(item.source || "VENDOR_APP"))}</td><td data-label={t("Status")}><StatusControl order={item} api={api} onChanged={refresh} toast={toast} /></td><td data-label={t("Created")}>{dateTime(item.createdAt, language)}</td><td data-label={t("Total")} className="row-title">{money(item.totalAmount, business?.currency || "INR")}</td></tr>)}</tbody></table></div> : <EmptyState title={orders.length ? "No matching orders" : "No orders yet"} description={orders.length ? "Try another search." : "Create an order for a customer."} action="Create order" onAction={() => setEditor(true)} />}
    {editor && <OrderEditor api={api} business={business} close={() => setEditor(false)} onCreated={refresh} toast={toast} />}
    {order && <OrderDetails api={api} orderId={order.id} business={business} user={user} close={() => setOrder(null)} />}
  </>;
}

function BusinessProfile({ api, onBusinessUpdated, toast }) {
  const { t } = useLanguage();
  const [profile, setProfile] = useState(null);
  const [editing, setEditing] = useState(false);
  const [loadingError, setLoadingError] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const refresh = useCallback(() => api("/business/profile").then(value => { setProfile(value); onBusinessUpdated(value); setLoadingError(""); }).catch(requestError => setLoadingError(requestError.message)), [api, onBusinessUpdated]);
  useEffect(() => { refresh(); }, [refresh]);

  async function save(event) {
    event.preventDefault();
    setBusy(true); setError("");
    const form = new FormData(event.currentTarget);
    const gstin = optional(form.get("gstin"))?.toUpperCase();
    if (gstin && !/^[A-Z0-9]{15}$/.test(gstin)) {
      setError("Enter a valid 15-character GSTIN."); setBusy(false); return;
    }
    try {
      const updated = await api("/business/profile", { method: "PUT", body: JSON.stringify({
        gstin, phone: optional(form.get("phone")), address: optional(form.get("address")),
      }) });
      setProfile(updated); onBusinessUpdated(updated); setEditing(false); toast("Business profile updated.");
    } catch (requestError) { setError(requestError.message); }
    setBusy(false);
  }

  if (loadingError && !profile) return <PageError title="Business profile" error={loadingError} retry={refresh} />;
  if (!profile) return <Loading />;

  return <form onSubmit={save}>
    <header className="page-heading"><div><h1>{t("Business profile")}</h1></div><div className="heading-actions">{editing ? <><button type="button" className="secondary-button" onClick={() => { setEditing(false); setError(""); }}>{t("Cancel")}</button><button type="submit" className="primary-button" disabled={busy}>{busy ? t("Saving…") : t("Save changes")}</button></> : <button type="button" className="primary-button" onClick={() => setEditing(true)}>{t("Edit")}</button>}</div></header>
    {editing ? <>
      <div className="section-head"><h2>{t("Business details")}</h2></div>
      <div className="detail-grid profile-detail-grid">
        {detail(t("Business name"), profile.name)}{detail(t("Business type"), profile.businessType)}{detail(t("Email"), profile.email)}
      </div>
      <div className="form-grid profile-form-grid">
        <Field label="GST number" name="gstin" maxLength={15} defaultValue={profile.gstin} placeholder="15-character GSTIN" />
        <Field label="Phone" name="phone" type="tel" maxLength={32} defaultValue={profile.phone} autoComplete="tel" />
        <Field label="Business address" name="address" area maxLength={1000} defaultValue={profile.address} span />
      </div>
      {error && <div className="error-banner" role="alert" style={{ marginTop: 16 }}>{t(error)}</div>}
    </> : <>
      <div className="section-head"><h2>{t("Business details")}</h2></div>
      <div className="detail-grid profile-detail-grid">
        {detail(t("Business name"), profile.name)}{detail(t("Business type"), profile.businessType)}{detail(t("GST number"), profile.gstin)}
        {detail(t("Phone"), profile.phone)}{detail(t("Email"), profile.email)}{detail(t("Business address"), <span className="profile-address">{profile.address || "—"}</span>)}
      </div>
    </>}
    <section className="profile-readonly"><div>{detail(t("Currency"), profile.currency)}</div><div>{detail(t("Time zone"), profile.timezone)}</div></section>
  </form>;
}

function OrderTable({ orders, onOpen }) {
  const { language, t } = useLanguage();
  return <div className="table-wrap"><table className="mobile-table"><thead><tr>{["Order", "Customer", "Channel", "Status", "Created", "Total"].map(label => <th key={label}>{t(label)}</th>)}</tr></thead><tbody>{orders.map(item => <tr key={item.id}><td data-label={t("Order")}><button className="link-button order-link" onClick={() => onOpen(item)}>{item.orderNumber}</button></td><td data-label={t("Customer")}>{item.customerName}</td><td data-label={t("Channel")}>{t(displayStatus(item.source || "VENDOR_APP"))}</td><td data-label={t("Status")}><StatusPill status={item.status} /></td><td data-label={t("Created")}>{dateTime(item.createdAt, language)}</td><td data-label={t("Total")} className="row-title">{money(item.totalAmount, item.currency)}</td></tr>)}</tbody></table></div>;
}

function StatusControl({ order, api, onChanged, toast }) {
  const { t } = useLanguage();
  const next = TRANSITIONS[order.status] || [];
  const [busy, setBusy] = useState(false);
  if (!next.length) return <StatusPill status={order.status} />;
  async function change(event) {
    const status = event.target.value; if (!status) return;
    setBusy(true);
    try { await api(`/orders/${order.id}/status`, { method: "PATCH", body: JSON.stringify({ status, notes: null }) }); toast(`${t("Order moved to")} ${t(displayStatus(status))}.`); await onChanged(); setBusy(false); }
    catch (error) { toast(error.message, true); setBusy(false); }
  }
  return <select className="status-select" aria-label={`${t("Change status for")} ${order.orderNumber}`} defaultValue="" disabled={busy} onChange={change}><option value="">{t(displayStatus(order.status))}</option>{next.map(status => <option key={status} value={status}>{t(displayStatus(status))}</option>)}</select>;
}

function Products({ api, business, toast }) {
  const { t } = useLanguage();
  const [products, setProducts] = useState(null); const [categories, setCategories] = useState([]); const [search, setSearch] = useState(""); const [editing, setEditing] = useState(null); const [modal, setModal] = useState(""); const [error, setError] = useState("");
  const refresh = useCallback(async () => { try { const [items, cats] = await Promise.all([api("/products"), api("/categories")]); setProducts(items); setCategories(cats); setError(""); } catch (e) { setError(e.message); } }, [api]);
  useEffect(() => { refresh(); }, [refresh]);
  if (error) return <PageError title="Products" error={error} retry={refresh} />;
  if (!products) return <Loading />;
  const filtered = products.filter(item => `${item.name} ${item.sku || ""} ${item.categoryName || ""}`.toLowerCase().includes(search.toLowerCase()));
  async function availability(product) { try { await api(`/products/${product.id}/availability`, { method: "PATCH", body: JSON.stringify({ available: !product.available }) }); toast("Product availability updated."); await refresh(); } catch (e) { toast(e.message, true); } }
  async function archive(product) { if (!confirm(`${t("Archive")} ${product.name}? ${t("This product will no longer be available for new orders.")}`)) return; try { await api(`/products/${product.id}`, { method: "DELETE" }); toast("Product archived."); await refresh(); } catch (e) { toast(e.message, true); } }
  return <>
    <header className="page-heading"><div><h1>{t("Products")}</h1><p>{business?.name || ""} · {products.length} {t("product")}</p></div><div className="heading-actions"><button className="secondary-button" onClick={() => setModal("categories")}>{t("Manage categories")}</button><button className="primary-button" onClick={() => { setEditing(null); setModal("product"); }}>＋ {t("Add product")}</button></div></header>
    <div className="toolbar"><input className="search-input" type="search" placeholder={t("Search products")} aria-label={t("Search products")} value={search} onChange={event => setSearch(event.target.value)} /></div>
    {filtered.length ? <div className="table-wrap"><table className="mobile-table"><thead><tr>{["Product", "Category", "MRP / Offer", "Tax", "Unit", "Available", "Actions"].map(label => <th key={label}>{t(label)}</th>)}</tr></thead><tbody>{filtered.map(product => <tr key={product.id}><td data-label={t("Product")}><span className="row-title">{product.name}</span><span className="row-sub">{product.sku || t("No SKU")}</span></td><td data-label={t("Category")}>{product.categoryName || "—"}</td><td data-label={t("MRP / Offer")}><span className={Number(product.discountPercent) > 0 ? "row-sub strike-price" : "row-title"}>{money(product.price, business?.currency || "INR")}</span>{Number(product.discountPercent) > 0 && <span className="row-title">{money(product.discountedPrice, business?.currency || "INR")} · {product.discountPercent}% {t("off")}</span>}</td><td data-label={t("Tax")}>{product.taxType ? `${t(product.taxType)} ${product.taxPercent}%` : t("None")}</td><td data-label={t("Unit")}>{product.unit}</td><td data-label={t("Available")}><StatusPill status={product.available ? "AVAILABLE" : "UNAVAILABLE"} /></td><td data-label={t("Actions")}><div className="row-actions"><button className="mini-button" onClick={() => { setEditing(product); setModal("product"); }}>{t("Edit")}</button><button className="mini-button" onClick={() => availability(product)}>{product.available ? t("Mark unavailable") : t("Make available")}</button><button className="mini-button" onClick={() => archive(product)}>{t("Archive")}</button></div></td></tr>)}</tbody></table></div> : <EmptyState title="No products yet" description="Add your first product to build your catalogue." action="Add product" onAction={() => { setEditing(null); setModal("product"); }} />}
    {modal === "product" && <ProductEditor api={api} categories={categories} product={editing} business={business} close={() => setModal("")} onSaved={async message => { toast(message); setModal(""); await refresh(); }} />}
    {modal === "categories" && <CategoryManager api={api} categories={categories} close={() => setModal("")} onChanged={async message => { toast(message); await refresh(); }} />}
  </>;
}

function Customers({ api, business, toast }) {
  const { t } = useLanguage();
  const [customers, setCustomers] = useState(null); const [search, setSearch] = useState(""); const [editing, setEditing] = useState(null); const [editorOpen, setEditorOpen] = useState(false); const [error, setError] = useState("");
  const refresh = useCallback(() => api("/customers").then(items => { setCustomers(items); setError(""); }).catch(e => setError(e.message)), [api]);
  useEffect(() => { refresh(); }, [refresh]);
  if (error) return <PageError title="Customers" error={error} retry={refresh} />;
  if (!customers) return <Loading />;
  const filtered = customers.filter(item => `${item.name} ${item.phone || ""} ${item.email || ""}`.toLowerCase().includes(search.toLowerCase()));
  return <>
    <header className="page-heading"><div><h1>{t("Customers")}</h1><p>{business?.name || ""} · {customers.length} {t("customer")}</p></div><div className="heading-actions"><button className="primary-button" onClick={() => { setEditing(null); setEditorOpen(true); }}>＋ {t("Add customer")}</button></div></header>
    <div className="toolbar"><input className="search-input" type="search" placeholder={t("Search customers")} aria-label={t("Search customers")} value={search} onChange={event => setSearch(event.target.value)} /></div>
    {filtered.length ? <div className="table-wrap"><table className="mobile-table"><thead><tr>{["Customer", "Phone", "Email", "Delivery address", "Actions"].map(label => <th key={label}>{t(label)}</th>)}</tr></thead><tbody>{filtered.map(customer => { const address = customer.addresses?.find(item => item.defaultAddress) || customer.addresses?.[0]; return <tr key={customer.id}><td data-label={t("Customer")} className="row-title">{customer.name}</td><td data-label={t("Phone")}>{customer.phone || "—"}</td><td data-label={t("Email")}>{customer.email || "—"}</td><td data-label={t("Delivery address")}>{address ? `${address.addressLine1}, ${address.city}` : "—"}</td><td data-label={t("Actions")}><button className="mini-button" onClick={() => { setEditing(customer); setEditorOpen(true); }}>{t("Edit")}</button></td></tr>; })}</tbody></table></div> : <EmptyState title="No customers yet" description="Add a customer and delivery address to place orders." action="Add customer" onAction={() => { setEditing(null); setEditorOpen(true); }} />}
    {editorOpen && <CustomerEditor api={api} customer={editing} close={() => setEditorOpen(false)} onSaved={async message => { toast(message); setEditorOpen(false); await refresh(); }} />}
  </>;
}

function ProductEditor({ api, categories, product, business, close, onSaved }) {
  const { t } = useLanguage();
  const [error, setError] = useState(""); const [busy, setBusy] = useState(false);
  const [selectedTaxType, setSelectedTaxType] = useState(product?.taxType || "");
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError("");
    const form = new FormData(event.currentTarget);
    const taxType = optional(value(form, "taxType"));
    const payload = { name: value(form, "name"), description: optional(value(form, "description")), sku: value(form, "sku"), unit: value(form, "unit"), price: Number(form.get("price")), categoryId: Number(form.get("categoryId")), minQuantity: Number(form.get("minQuantity")), quantityStep: Number(form.get("quantityStep")), discountPercent: Number(form.get("discountPercent") || 0), taxType, taxPercent: taxType ? Number(form.get("taxPercent")) : null };
    try {
      if (!payload.categoryId || !Number.isFinite(payload.price) || payload.price < 0 || payload.minQuantity <= 0 || payload.quantityStep <= 0) throw new Error("Enter a valid category, price, minimum quantity and quantity step.");
      if (!Number.isFinite(payload.discountPercent) || payload.discountPercent < 0 || payload.discountPercent > 100) throw new Error("Discount must be between 0% and 100% of MRP.");
      if (taxType && (!Number.isFinite(payload.taxPercent) || payload.taxPercent <= 0 || payload.taxPercent > 100)) throw new Error("A tax rate greater than 0% is required when a tax type is selected.");
      if (product) {
        await api(`/products/${product.id}`, { method: "PUT", body: JSON.stringify(payload) });
        const available = form.get("available") === "on";
        if (available !== product.available) await api(`/products/${product.id}/availability`, { method: "PATCH", body: JSON.stringify({ available }) });
      } else await api("/products", { method: "POST", body: JSON.stringify(payload) });
      await onSaved(product ? "Product updated." : "Product added.");
    } catch (e) { setError(e.message); setBusy(false); }
  }
  return <EditorDialog title={product ? "Edit product" : "Add product"} submitLabel={product ? "Save changes" : "Add product"} error={error} busy={busy} close={close} onSubmit={submit}>
    <div className="form-grid">
      <Field label="Name" name="name" required maxLength={160} defaultValue={product?.name} />
      <Field label="SKU" name="sku" required maxLength={80} defaultValue={product?.sku} />
      <Field label="Unit" name="unit" required maxLength={32} placeholder="kg, piece, box" defaultValue={product?.unit} />
      <Field label="MRP" name="price" required type="number" min="0" step="0.01" defaultValue={product?.price} />
      <Field label="Discount (% off MRP)" name="discountPercent" type="number" min="0" max="100" step="0.01" defaultValue={product?.discountPercent ?? "0"} />
      <div className="field"><label htmlFor="product-tax-type">{t("Tax type")}</label><select id="product-tax-type" name="taxType" value={selectedTaxType} onChange={event => setSelectedTaxType(event.target.value)}><option value="">{t("No tax")}</option><option value="GST">{t("GST")}</option><option value="VAT">{t("VAT")}</option><option value="OTHER">{t("Other")}</option></select></div>
      <Field label="Tax percentage (%)" name="taxPercent" required={Boolean(selectedTaxType)} type="number" min="0.01" max="100" step="0.01" defaultValue={product?.taxPercent ?? ""} placeholder={t("Required when tax type is selected")} />
      <Field label="Minimum quantity" name="minQuantity" required type="number" min="0.001" step="0.001" defaultValue={product?.minQuantity ?? "1"} />
      <Field label="Quantity step" name="quantityStep" required type="number" min="0.001" step="0.001" defaultValue={product?.quantityStep ?? "1"} />
      <div className="field"><label htmlFor="product-category">{t("Category")} <Required /></label><select id="product-category" name="categoryId" required defaultValue={product?.categoryId || ""}><option value="">{t("Select category")}</option>{categories.map(category => <option key={category.id} value={category.id}>{category.name}</option>)}</select></div>
      <Field label="Description" name="description" area span defaultValue={product?.description} />
      {product && <label className="check-line span-2"><input name="available" type="checkbox" defaultChecked={product.available} />{t("Available for new orders")}</label>}
    </div>
  </EditorDialog>;
}

function CategoryManager({ api, categories, close, onChanged }) {
  const { t } = useLanguage();
  const [error, setError] = useState(""); const [busy, setBusy] = useState(false);
  async function add(event) {
    event.preventDefault(); setBusy(true); setError("");
    const form = new FormData(event.currentTarget);
    try {
      await api("/categories", { method: "POST", body: JSON.stringify({ name: value(form, "name"), description: optional(value(form, "description")), sortOrder: categories.length }) });
      await onChanged("Category added."); close();
    } catch (e) { setError(e.message); setBusy(false); }
  }
  async function remove(category) {
    if (!confirm(`${t("Delete")} ${category.name}? ${t("Products assigned to this category cannot be deleted.")}`)) return;
    try { await api(`/categories/${category.id}`, { method: "DELETE" }); await onChanged("Category deleted."); }
    catch (e) { setError(e.message); }
  }
  return <Modal title="Manage categories" close={close}>
    {categories.length ? <div className="table-wrap"><table className="mobile-table" style={{ minWidth: 0 }}><thead><tr><th>{t("Category")}</th><th></th></tr></thead><tbody>{categories.map(category => <tr key={category.id}><td data-label={t("Category")}>{category.name}</td><td data-label={t("Actions")} style={{ textAlign: "right" }}><button type="button" className="mini-button" onClick={() => remove(category)}>{t("Delete")}</button></td></tr>)}</tbody></table></div> : <p className="muted small">{t("No categories yet. Add one below.")}</p>}
    <form className="form-grid" style={{ marginTop: 18 }} onSubmit={add}>
      <Field label="New category" name="name" required maxLength={100} />
      <Field label="Description" name="description" maxLength={500} />
      {error && <div className="error-banner span-2" role="alert">{error}</div>}
      <div className="span-2" style={{ display: "flex", justifyContent: "flex-end" }}><button className="primary-button" type="submit" disabled={busy}>{busy ? t("Adding…") : t("Add category")}</button></div>
    </form>
  </Modal>;
}

function CustomerEditor({ api, customer, close, onSaved }) {
  const { t } = useLanguage();
  const address = customer?.addresses?.find(item => item.defaultAddress) || customer?.addresses?.[0] || {};
  const [error, setError] = useState(""); const [busy, setBusy] = useState(false);
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError("");
    const form = new FormData(event.currentTarget);
    const phone = value(form, "phone");
    const payload = {
      name: value(form, "name"), phone, email: value(form, "email"), notes: optional(value(form, "notes")),
      addresses: [{ label: value(form, "label"), recipientName: optional(value(form, "recipientName")), phone, addressLine1: value(form, "addressLine1"), addressLine2: optional(value(form, "addressLine2")), locality: optional(value(form, "locality")), city: value(form, "city"), state: optional(value(form, "state")), postalCode: optional(value(form, "postalCode")), countryCode: (value(form, "countryCode") || "IN").toUpperCase(), deliveryInstructions: optional(value(form, "deliveryInstructions")), defaultAddress: form.get("defaultAddress") === "on" }],
    };
    try {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(payload.email)) throw new Error("Enter a valid email address.");
      await api(customer ? `/customers/${customer.id}` : "/customers", { method: customer ? "PUT" : "POST", body: JSON.stringify(payload) });
      await onSaved(customer ? "Customer updated." : "Customer added.");
    } catch (e) { setError(e.message); setBusy(false); }
  }
  return <EditorDialog title={customer ? "Edit customer" : "Add customer"} submitLabel={customer ? "Save customer" : "Add customer"} error={error} busy={busy} close={close} onSubmit={submit} wide>
    <div className="form-grid">
      <Field label="Name" name="name" required maxLength={160} defaultValue={customer?.name} />
      <Field label="Phone" name="phone" type="tel" required maxLength={32} defaultValue={customer?.phone} />
      <Field label="Email" name="email" type="email" required maxLength={254} defaultValue={customer?.email} />
      <Field label="Notes" name="notes" area span defaultValue={customer?.notes} />
      <div className="address-section span-2"><h3>{t("Delivery address")} <Required /></h3><div className="form-grid">
        <Field label="Address label" name="label" required maxLength={60} defaultValue={address.label || "Home"} />
        <Field label="Recipient name" name="recipientName" maxLength={160} defaultValue={address.recipientName || customer?.name} />
        <Field label="Address line 1" name="addressLine1" required maxLength={240} span defaultValue={address.addressLine1} />
        <Field label="Address line 2" name="addressLine2" maxLength={240} defaultValue={address.addressLine2} />
        <Field label="Locality" name="locality" maxLength={160} defaultValue={address.locality} />
        <Field label="City" name="city" required maxLength={120} defaultValue={address.city} />
        <Field label="State" name="state" maxLength={120} defaultValue={address.state} />
        <Field label="Postal code" name="postalCode" maxLength={24} defaultValue={address.postalCode} />
        <Field label="Country code" name="countryCode" maxLength={2} defaultValue={address.countryCode || "IN"} />
        <Field label="Delivery instructions" name="deliveryInstructions" maxLength={500} span defaultValue={address.deliveryInstructions} />
        <label className="check-line span-2"><input name="defaultAddress" type="checkbox" defaultChecked />{t("Use as default delivery address")}</label>
      </div></div>
    </div>
  </EditorDialog>;
}

function OrderEditor({ api, business, close, onCreated, toast }) {
  const { t } = useLanguage();
  const [customers, setCustomers] = useState([]); const [products, setProducts] = useState([]); const [customerId, setCustomerId] = useState("");
  const [lines, setLines] = useState([]); const [error, setError] = useState(""); const [busy, setBusy] = useState(false);
  useEffect(() => { Promise.all([api("/customers"), api("/products")]).then(([customerRows, productRows]) => { setCustomers(customerRows); const available = productRows.filter(item => item.available); setProducts(available); if (available.length) setLines([{ productId: available[0].id, quantity: String(available[0].minQuantity) }]); }).catch(e => setError(e.message)); }, [api]);
  const total = lines.reduce((sum, line) => {
    const product = products.find(item => item.id === Number(line.productId));
    return sum + (product ? productLineTotal(product, Number(line.quantity) || 0) : 0);
  }, 0);
  function updateLine(index, changes) { setLines(current => current.map((line, row) => row === index ? { ...line, ...changes } : line)); }
  function selectProduct(index, productId) { const product = products.find(item => item.id === Number(productId)); updateLine(index, { productId: Number(productId), quantity: String(product?.minQuantity || 1) }); }
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError("");
    const form = new FormData(event.currentTarget);
    try {
      if (!customerId || !lines.length) throw new Error("Choose a customer and add at least one product.");
      const seen = new Set();
      const items = lines.map(line => {
        const product = products.find(item => item.id === Number(line.productId)); const quantity = Number(line.quantity);
        if (!product || !Number.isFinite(quantity) || !quantityValid(quantity, product)) throw new Error("Check each quantity against its product minimum and quantity step.");
        if (seen.has(product.id)) throw new Error("Add each product only once; update its quantity instead.");
        seen.add(product.id); return { productId: product.id, quantity };
      });
      const order = await api("/orders", { method: "POST", body: JSON.stringify({ customerId: Number(customerId), items, notes: optional(form.get("notes")) }) });
      toast(`${t("Order")} ${order.orderNumber} ${t("created.")}`); close(); await onCreated();
    } catch (e) { setError(e.message); setBusy(false); }
  }
  return <EditorDialog title="New order" submitLabel="Place order" error={error} busy={busy} close={close} onSubmit={submit} wide>
    <div className="form-grid"><div className="field span-2"><label htmlFor="order-customer">{t("Customer")} <Required /></label><select id="order-customer" required value={customerId} onChange={event => setCustomerId(event.target.value)}><option value="">{t("Select customer")}</option>{customers.map(customer => <option key={customer.id} value={customer.id}>{customer.name}{customer.phone ? ` · ${customer.phone}` : ""}</option>)}</select></div></div>
    <div className="address-section"><div className="section-head"><h3 style={{ margin: 0 }}>{t("Order items")}</h3><button type="button" className="secondary-button" onClick={() => { if (products.length) setLines(current => [...current, { productId: products[0].id, quantity: String(products[0].minQuantity) }]); }}>＋ {t("Add item")}</button></div>
      {lines.map((line, index) => { const product = products.find(item => item.id === Number(line.productId)) || products[0]; return <div className="line-item" key={`${index}-${line.productId}`}><div className="field"><label>{t("Product")}</label><select value={line.productId} onChange={event => selectProduct(index, event.target.value)}>{products.map(item => <option key={item.id} value={item.id}>{item.name} · {item.unit}</option>)}</select></div><div className="field"><label>{t("Quantity")}</label><input type="number" min={product?.minQuantity} step={product?.quantityStep} value={line.quantity} onChange={event => updateLine(index, { quantity: event.target.value })} /></div><div className="field"><label>{t("Line total (incl. tax)")}</label><input value={money(product ? productLineTotal(product, Number(line.quantity) || 0) : 0, business?.currency || "INR")} readOnly tabIndex={-1} /></div><button type="button" className="icon-button remove-line" aria-label={t("Remove item")} title={t("Remove item")} onClick={() => setLines(current => current.filter((_, row) => row !== index))}>×</button></div>; })}
      <div className="order-total"><span>{t("Estimated total")}</span><span>{money(total, business?.currency || "INR")}</span></div>
    </div>
    {!customers.length && <div className="error-banner" style={{ marginTop: 14 }}>{t("Add a customer before placing an order.")}</div>}
    {!products.length && <div className="error-banner" style={{ marginTop: 14 }}>{t("Add and make a product available before placing an order.")}</div>}
    <Field label="Notes" name="notes" area maxLength={1000} />
  </EditorDialog>;
}

function OrderDetails({ api, orderId, business, user, close }) {
  const { language, t } = useLanguage();
  const [order, setOrder] = useState(null); const [error, setError] = useState(""); const [printError, setPrintError] = useState("");
  useEffect(() => { api(`/orders/${orderId}`).then(setOrder).catch(e => setError(e.message)); }, [api, orderId]);
  function handlePrint() {
    setPrintError(printInvoice(order, business, user, t, language) ? "" : "Allow pop-ups to print or save the invoice.");
  }
  return <Modal title={order?.orderNumber || t("Order details")} close={close} wide>
    {error ? <div className="error-banner">{error}</div> : !order ? <Loading /> : <>
      <div className="detail-grid">{detail(t("Customer"), order.customerName)}{detail(t("Channel"), t(displayStatus(order.source || "VENDOR_APP")))}{detail(t("Status"), t(displayStatus(order.status)))}{detail(t("Created"), dateTime(order.createdAt, language))}{detail(t("Subtotal (MRP)"), money(order.subtotal, order.currency))}{detail(t("Discount"), `−${money(order.discount, order.currency)}`)}{detail(t("Tax"), money(order.tax, order.currency))}{detail(t("Delivery"), money(order.deliveryCharge, order.currency))}{detail(t("Total"), money(order.totalAmount, order.currency))}</div>
      <div className="section-head" style={{ marginTop: 18 }}><span className="muted small">{t("Invoice")}: {order.orderNumber}</span><button className="secondary-button" type="button" onClick={handlePrint}>{t("Print / Save PDF")}</button></div>
      {printError && <div className="error-banner" role="alert">{t(printError)}</div>}
      {order.deliveryAddress && <><div className="section-head" style={{ marginTop: 18 }}><h2>{t("Delivery address")}</h2></div><p className="muted small">{formatDeliveryAddress(order.deliveryAddress)}</p></>}
      <div className="section-head" style={{ marginTop: 18 }}><h2>{t("Items")}</h2></div>
      <div className="table-wrap"><table className="mobile-table" style={{ minWidth: 0 }}><thead><tr>{["Product", "Qty", "MRP", "Discount", "Tax", "Line total"].map(label => <th key={label}>{t(label)}</th>)}</tr></thead><tbody>{order.items.map(item => <tr key={item.id}><td data-label={t("Product")} className="row-title">{item.productName}</td><td data-label={t("Qty")}>{item.quantity} {item.unit}</td><td data-label={t("MRP")}>{money(item.unitPrice, order.currency)}</td><td data-label={t("Discount")}>{item.discountPercent}% · −{money(item.discountAmount, order.currency)}</td><td data-label={t("Tax")}>{item.taxType ? `${t(item.taxType)} ${item.taxPercent}% · ${money(item.taxAmount, order.currency)}` : t("None")}</td><td data-label={t("Line total")}>{money(item.lineTotal + item.taxAmount, order.currency)}</td></tr>)}</tbody></table></div>
      {order.notes && <p className="muted small" style={{ margin: "14px 0" }}>{order.notes}</p>}
      <div className="section-head" style={{ marginTop: 22 }}><h2>{t("Status history")}</h2></div>
      <div className="timeline">{order.statusHistory.map((event, index) => <div className="timeline-item" key={`${event.changedAt}-${index}`}><span className="timeline-dot" /><div className="timeline-copy"><strong>{event.oldStatus ? `${t(displayStatus(event.oldStatus))} → ` : ""}{t(displayStatus(event.newStatus))}</strong><span>{dateTime(event.changedAt, language)}{event.notes ? ` · ${event.notes}` : ""}</span></div></div>)}</div>
    </>}
  </Modal>;
}

function EditorDialog({ title, submitLabel, error, busy, close, onSubmit, wide = false, children }) {
  const { t } = useLanguage();
  return <div className="modal-backdrop" onMouseDown={event => { if (event.target === event.currentTarget) close(); }}><section className={`modal ${wide ? "wide" : ""}`} role="dialog" aria-modal="true" aria-label={t(title)}>
    <header className="modal-head"><h2>{t(title)}</h2><button type="button" className="icon-button" onClick={close} aria-label={t("Close dialog")}>×</button></header>
    <form onSubmit={onSubmit}><div className="modal-body">{children}{error && <div className="error-banner" role="alert" style={{ marginTop: 14 }}>{t(error)}</div>}</div><footer className="modal-foot"><button className="secondary-button" type="button" onClick={close}>{t("Cancel")}</button><button className="primary-button" type="submit" disabled={busy}>{busy ? t("Saving…") : t(submitLabel)}</button></footer></form>
  </section></div>;
}

function Modal({ title, close, wide = false, children }) {
  const { t } = useLanguage();
  return <div className="modal-backdrop" onMouseDown={event => { if (event.target === event.currentTarget) close(); }}><section className={`modal ${wide ? "wide" : ""}`} role="dialog" aria-modal="true" aria-label={t(title)}>
    <header className="modal-head"><h2>{t(title)}</h2><button className="icon-button" type="button" onClick={close} aria-label={t("Close dialog")}>×</button></header><div className="modal-body">{children}</div><footer className="modal-foot"><button type="button" className="secondary-button" onClick={close}>{t("Close")}</button></footer>
  </section></div>;
}

function Brand({ className = "" }) { return <div className={`brand-lockup ${className}`}><span className="brand-mark">L</span><span className="brand-name">LocalFlow</span></div>; }

function Field({ label, name, required = false, maxLength, type = "text", min, max, step, placeholder, defaultValue = "", area = false, span = false, autoComplete }) {
  const { t } = useLanguage();
  const id = `field-${name}`;
  return <div className={`field${span ? " span-2" : ""}`}><label htmlFor={id}>{t(label)}{required && <> <Required /></>}</label>
    {area ? <textarea id={id} name={name} required={required} maxLength={maxLength} placeholder={t(placeholder || "")} defaultValue={defaultValue ?? ""} /> : <input id={id} name={name} type={type} required={required} maxLength={maxLength} min={min} max={max} step={step} placeholder={t(placeholder || "")} defaultValue={defaultValue ?? ""} autoComplete={autoComplete} />}
  </div>;
}

function Required() { return <span className="required">*</span>; }

function PasswordField({ label, name, required, minLength, autoComplete }) {
  const { t } = useLanguage();
  const [visible, setVisible] = useState(false);
  const id = `field-${name}`;
  return <div className="field"><label htmlFor={id}>{t(label)}{required && <> <Required /></>}</label><div className="password-input-wrap"><input id={id} name={name} type={visible ? "text" : "password"} required={required} minLength={minLength} autoComplete={autoComplete} /><button className="password-toggle" type="button" onClick={() => setVisible(value => !value)} aria-label={t(visible ? "Hide" : "Show")} title={t(visible ? "Hide" : "Show")} aria-pressed={visible}><EyeIcon visible={visible} /></button></div></div>;
}

function EyeIcon({ visible }) { return <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{visible ? <><path d="M3 3l18 18" /><path d="M10.6 10.6a2 2 0 0 0 2.8 2.8" /><path d="M9.9 5.2A10.8 10.8 0 0 1 12 5c5 0 8.5 4.5 9.5 7-.4 1-1.3 2.3-2.5 3.5" /><path d="M6.2 6.2C4.2 7.5 2.9 9.5 2.5 12c.5 1.2 1.6 2.8 3.4 4.1A10.3 10.3 0 0 0 12 18c.8 0 1.5-.1 2.2-.3" /></> : <><path d="M2.5 12s3.5-7 9.5-7 9.5 7 9.5 7-3.5 7-9.5 7-9.5-7-9.5-7Z" /><circle cx="12" cy="12" r="3" /></>}</svg>; }

function StatusPill({ status }) {
  const { t } = useLanguage();
  const value = status.toUpperCase();
  const good = ["AVAILABLE", "CONFIRMED", "PACKING", "READY", "OUT_FOR_DELIVERY", "PICKUP_READY", "DELIVERED", "COMPLETED"].includes(value);
  const bad = ["REJECTED", "CANCELLED", "UNAVAILABLE"].includes(value);
  return <span className={`status ${good ? "status-green" : bad ? "status-red" : ""}`}>{t(displayStatus(status))}</span>;
}

function EmptyState({ title, description, action, onAction }) { const { t } = useLanguage(); return <div className="table-wrap"><div className="empty-state"><strong>{t(title)}</strong><p>{t(description)}</p>{action && <button className="primary-button" onClick={onAction}>{t(action)}</button>}</div></div>; }
function Loading() { const { t } = useLanguage(); return <div className="loading">{t("Loading…")}</div>; }
function PageError({ title, error, retry }) { const { t } = useLanguage(); return <><header className="page-heading"><div><h1>{t(title)}</h1></div></header><div className="error-banner">{t(error)}</div><button className="secondary-button" style={{ marginTop: 12 }} onClick={retry}>{t("Try again")}</button></>; }
function detail(label, value) { return <div key={label}><div className="detail-label">{label}</div><div className="detail-value">{value ?? "—"}</div></div>; }
function formatDeliveryAddress(address) { return [address.recipientName, address.phone, address.addressLine1, address.addressLine2, address.locality, address.city, address.state, address.postalCode, address.countryCode, address.deliveryInstructions].filter(Boolean).join(", "); }
function value(form, name) { return String(form.get(name) || "").trim(); }
function optional(input) { const trimmed = input == null ? "" : String(input).trim(); return trimmed || null; }
function displayStatus(status) { return String(status || "").replaceAll("_", " ").toLowerCase().replace(/\b\w/g, character => character.toUpperCase()); }
function money(amount, currency = "INR") { try { return new Intl.NumberFormat("en-IN", { style: "currency", currency }).format(Number(amount || 0)); } catch { return `${currency} ${amount || 0}`; } }
function productLineTotal(product, quantity) {
  const round = amount => Math.round((amount + Number.EPSILON) * 100) / 100;
  const gross = round(Number(product.price) * quantity);
  const discount = round(gross * (Number(product.discountPercent || 0) / 100));
  const tax = round((gross - discount) * (Number(product.taxPercent || 0) / 100));
  return round(gross - discount + tax);
}
function dateTime(value, language = "en") { if (!value) return "—"; const date = new Date(value); return Number.isNaN(date.valueOf()) ? value : new Intl.DateTimeFormat(language === "hi" ? "hi-IN" : "en-IN", { dateStyle: "medium", timeStyle: "short" }).format(date); }
function quantityValid(quantity, product) { const minimum = Number(product.minQuantity), step = Number(product.quantityStep); return quantity >= minimum && Math.abs((quantity - minimum) / step - Math.round((quantity - minimum) / step)) < 0.000001; }
