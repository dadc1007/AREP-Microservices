import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "@styles/global.css";
import App from "@app/App.tsx";
import { Auth0Provider } from "@auth0/auth0-react";
import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "@shared/api/queryClient";
import { Toaster } from "sileo";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <Auth0Provider
        domain={import.meta.env.VITE_AUTH0_DOMAIN}
        clientId={import.meta.env.VITE_AUTH0_CLIENT_ID}
        authorizationParams={{
          redirect_uri: window.location.origin,
          audience: import.meta.env.VITE_AUTH0_AUDIENCE,
        }}
      >
        <Toaster position="top-center" />
        <App />
      </Auth0Provider>
    </QueryClientProvider>
  </StrictMode>,
);
