import axios from "axios";
import type { ApiError, ErrorResponse } from "@shared/types/api";

const DEFAULT_ERROR_MESSAGE = "Ha ocurrido un error inesperado";

const isErrorResponse = (value: unknown): value is ErrorResponse => {
  if (!value || typeof value !== "object") {
    return false;
  }

  const data = value as Partial<ErrorResponse> & { code?: unknown };

  return (
    typeof data.status === "number" &&
    typeof data.message === "string" &&
    (typeof data.error === "string" || typeof data.code === "string")
  );
};

export const getErrorMessage = (error: unknown): string => {
  if (error instanceof Error) {
    return error.message;
  }

  if (
    typeof error === "object" &&
    error !== null &&
    "message" in error &&
    typeof error.message === "string"
  ) {
    return error.message;
  }

  return DEFAULT_ERROR_MESSAGE;
};

export const mapToApiError = (error: unknown): ApiError => {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status ?? 500;
    const data = error.response?.data;

    if (isErrorResponse(data)) {
      return {
        status: data.status,
        code: data.error ?? String((data as { code?: string }).code),
        message: data.message,
        timestamp: data.timestamp,
      };
    }

    return {
      status,
      code: error.code ?? "HTTP_ERROR",
      message: error.message || DEFAULT_ERROR_MESSAGE,
    };
  }

  if (error instanceof Error) {
    return {
      status: 500,
      code: "UNKNOWN_ERROR",
      message: error.message || DEFAULT_ERROR_MESSAGE,
    };
  }

  return {
    status: 500,
    code: "UNKNOWN_ERROR",
    message: DEFAULT_ERROR_MESSAGE,
  };
};
