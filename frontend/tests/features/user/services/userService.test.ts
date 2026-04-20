import { describe, expect, it, vi } from "vitest";
import {
  createUser,
  getCurrentUser,
  updateUsername,
} from "@features/user/services/userService";
import { httpClient } from "@shared/api/httpClient";
import { mapToApiError } from "@shared/api/errors";

vi.mock("@shared/api/httpClient", () => ({
  httpClient: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
  },
}));

vi.mock("@shared/api/errors", () => ({
  mapToApiError: vi.fn(),
}));

describe("userService", () => {
  it("createUser envía el payload correcto", async () => {
    const payload = { email: "ana@mail.com", username: "ana" };
    const response = { id: 1, username: "ana", email: "ana@mail.com" };

    vi.mocked(httpClient.post).mockResolvedValueOnce({ data: response });

    await expect(createUser(payload)).resolves.toEqual(response);
    expect(httpClient.post).toHaveBeenCalledWith("/users", payload);
  });

  it("getCurrentUser consulta el endpoint /me", async () => {
    const response = { id: 1, username: "ana", email: "ana@mail.com" };

    vi.mocked(httpClient.get).mockResolvedValueOnce({ data: response });

    await expect(getCurrentUser()).resolves.toEqual(response);
    expect(httpClient.get).toHaveBeenCalledWith("/me");
  });

  it("updateUsername usa el endpoint esperado", async () => {
    const payload = { username: "nuevo" };
    const response = { id: 1, username: "nuevo", email: "ana@mail.com" };

    vi.mocked(httpClient.put).mockResolvedValueOnce({ data: response });

    await expect(updateUsername(payload)).resolves.toEqual(response);
    expect(httpClient.put).toHaveBeenCalledWith("/users/username", payload);
  });

  it("mapea y relanza errores", async () => {
    const rawError = new Error("boom");
    const mappedError = { status: 500, code: "X", message: "fallo" };

    vi.mocked(httpClient.get).mockRejectedValueOnce(rawError);
    vi.mocked(mapToApiError).mockReturnValueOnce(mappedError);

    await expect(getCurrentUser()).rejects.toBe(mappedError);
    expect(mapToApiError).toHaveBeenCalledWith(rawError);
  });
});
