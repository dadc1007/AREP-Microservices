import { describe, expect, it, vi } from "vitest";
import { getPublicFeed } from "@features/stream/services/streamService";
import { httpClient } from "@shared/api/httpClient";
import { mapToApiError } from "@shared/api/errors";

vi.mock("@shared/api/httpClient", () => ({
  httpClient: {
    get: vi.fn(),
  },
}));

vi.mock("@shared/api/errors", () => ({
  mapToApiError: vi.fn(),
}));

describe("streamService.getPublicFeed", () => {
  it("retorna el feed público cuando la petición es exitosa", async () => {
    const response = [
      { id: 1, content: "post 1", username: "ana" },
      { id: 2, content: "post 2", username: "bob" },
    ];

    vi.mocked(httpClient.get).mockResolvedValueOnce({ data: response });

    await expect(getPublicFeed()).resolves.toEqual(response);
    expect(httpClient.get).toHaveBeenCalledWith("/feed/public");
  });

  it("mapea y relanza errores cuando falla la petición", async () => {
    const rawError = new Error("timeout");
    const mappedError = { status: 500, code: "TIMEOUT", message: "fallo" };

    vi.mocked(httpClient.get).mockRejectedValueOnce(rawError);
    vi.mocked(mapToApiError).mockReturnValueOnce(mappedError);

    await expect(getPublicFeed()).rejects.toBe(mappedError);
    expect(mapToApiError).toHaveBeenCalledWith(rawError);
  });
});
