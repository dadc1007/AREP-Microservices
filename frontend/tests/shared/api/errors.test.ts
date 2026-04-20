import axios from "axios";
import { describe, expect, it, vi } from "vitest";
import { getErrorMessage, mapToApiError } from "@shared/api/errors";

describe("getErrorMessage", () => {
  it("retorna el mensaje cuando recibe una instancia de Error", () => {
    expect(getErrorMessage(new Error("fallo"))).toBe("fallo");
  });

  it("retorna el mensaje cuando recibe un objeto con propiedad message", () => {
    expect(getErrorMessage({ message: "mensaje custom" })).toBe(
      "mensaje custom",
    );
  });

  it("retorna mensaje por defecto para valores no compatibles", () => {
    expect(getErrorMessage(null)).toBe("Ha ocurrido un error inesperado");
  });
});

describe("mapToApiError", () => {
  it("mapea correctamente errores axios con payload tipado", () => {
    const isAxiosErrorSpy = vi
      .spyOn(axios, "isAxiosError")
      .mockReturnValue(true);

    const error = {
      response: {
        status: 404,
        data: {
          status: 404,
          error: "NOT_FOUND",
          message: "No existe",
          timestamp: "2026-01-01T00:00:00.000Z",
        },
      },
      message: "Request failed",
      code: "ERR_BAD_REQUEST",
    };

    expect(mapToApiError(error)).toEqual({
      status: 404,
      code: "NOT_FOUND",
      message: "No existe",
      timestamp: "2026-01-01T00:00:00.000Z",
    });

    isAxiosErrorSpy.mockRestore();
  });

  it("mapea errores axios sin payload de API", () => {
    const isAxiosErrorSpy = vi
      .spyOn(axios, "isAxiosError")
      .mockReturnValue(true);

    const error = {
      response: {
        status: 503,
        data: "unavailable",
      },
      message: "Gateway timeout",
      code: "ECONNABORTED",
    };

    expect(mapToApiError(error)).toEqual({
      status: 503,
      code: "ECONNABORTED",
      message: "Gateway timeout",
    });

    isAxiosErrorSpy.mockRestore();
  });

  it("mapea errores desconocidos como UNKNOWN_ERROR", () => {
    expect(mapToApiError(new Error("boom"))).toEqual({
      status: 500,
      code: "UNKNOWN_ERROR",
      message: "boom",
    });

    expect(mapToApiError("x")).toEqual({
      status: 500,
      code: "UNKNOWN_ERROR",
      message: "Ha ocurrido un error inesperado",
    });
  });
});
