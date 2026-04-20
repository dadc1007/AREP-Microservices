import axios from "axios";
import { getAccessToken } from "@shared/api/authTokenProvider";

const baseURL = import.meta.env.VITE_API_URL;

if (!baseURL) {
  throw new Error("VITE_API_URL no esta definido. Revisa tu archivo .env");
}

export const httpClient = axios.create({
  baseURL,
  timeout: 10_000,
  headers: {
    "Content-Type": "application/json",
  },
});

httpClient.interceptors.request.use(async (config) => {
  const hasAuthorizationHeader =
    Boolean(config.headers?.Authorization) ||
    Boolean(config.headers?.authorization);

  if (hasAuthorizationHeader) {
    return config;
  }

  const accessToken = await getAccessToken();

  if (accessToken) {
    if (config.headers && typeof config.headers.set === "function") {
      config.headers.set("Authorization", `Bearer ${accessToken}`);
    } else {
      (config.headers as Record<string, string>).Authorization =
        `Bearer ${accessToken}`;
    }
  }

  return config;
});
