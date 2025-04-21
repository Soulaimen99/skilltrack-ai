import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/logs": "http://localhost:8081",
      "/api/auth": "http://localhost:8081",
      "/admin/users": "http://localhost:8081",
    },
    port: 3000,
  },
});
