import { describe, expect, it, vi } from "vitest";
import { createPost } from "@features/post/services/postService";
import { httpClient } from "@shared/api/httpClient";
import { mapToApiError } from "@shared/api/errors";

vi.mock("@shared/api/httpClient", () => ({
  httpClient: {
    post: vi.fn(),
  },
}));

vi.mock("@shared/api/errors", () => ({
  mapToApiError: vi.fn(),
}));

describe("postService.createPost", () => {
  it("retorna la data cuando la creación es exitosa", async () => {
    const payload = { content: "hola" };
    const response = { id: 1, content: "hola", username: "ana" };

    vi.mocked(httpClient.post).mockResolvedValueOnce({ data: response });

    await expect(createPost(payload)).resolves.toEqual(response);
    expect(httpClient.post).toHaveBeenCalledWith("/posts", payload);
  });

  it("mapea y relanza errores de infraestructura", async () => {
    const rawError = new Error("network");
    const mappedError = { status: 500, code: "X", message: "fallo" };

    vi.mocked(httpClient.post).mockRejectedValueOnce(rawError);
    vi.mocked(mapToApiError).mockReturnValueOnce(mappedError);

    await expect(createPost({ content: "hola" })).rejects.toBe(mappedError);
    expect(mapToApiError).toHaveBeenCalledWith(rawError);
  });
});
