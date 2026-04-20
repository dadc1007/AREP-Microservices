import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import path from "node:path";
import { fileURLToPath } from "node:url";

const rootDir = path.dirname(fileURLToPath(import.meta.url));

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": path.resolve(rootDir, "src"),
      "@app": path.resolve(rootDir, "src/app"),
      "@assets": path.resolve(rootDir, "src/assets"),
      "@features": path.resolve(rootDir, "src/features"),
      "@shared": path.resolve(rootDir, "src/shared"),
      "@styles": path.resolve(rootDir, "src/styles"),
    },
  },
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: "./tests/setup.ts",
    clearMocks: true,
    mockReset: true,
    restoreMocks: true,
    coverage: {
      reporter: ["text", "html"],
    },
  },
});
