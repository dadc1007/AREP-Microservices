import { QueryClient } from "@tanstack/react-query";
import { mapToApiError } from "@shared/api/errors";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: (failureCount, error) => {
        const apiError = mapToApiError(error);

        if (apiError.status >= 400 && apiError.status < 500) {
          return false;
        }

        return failureCount < 2;
      },
      staleTime: 30_000,
      refetchOnWindowFocus: false,
    },
  },
});
