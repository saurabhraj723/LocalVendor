import React from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import Storefront from "./Storefront.jsx";

const shopId = new URLSearchParams(window.location.search).get("shop");

createRoot(document.getElementById("app")).render(
  <React.StrictMode>
    {shopId ? <Storefront businessId={shopId} /> : <App />}
  </React.StrictMode>,
);
