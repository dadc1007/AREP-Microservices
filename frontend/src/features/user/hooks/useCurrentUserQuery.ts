import { useQuery } from "@tanstack/react-query";
import { getCurrentUser } from "@features/user/services/userService";
import type { UserResponse } from "@features/user/types/user.response";
import type { ApiError } from "@shared/types/api";
import { useAuth0 } from "@auth0/auth0-react";

export const useCurrentUserQuery = () => {
  const { isAuthenticated } = useAuth0();

  return useQuery<UserResponse, ApiError>({
    queryKey: ["current-user"],
    enabled: isAuthenticated,
    queryFn: getCurrentUser,
  });
};
