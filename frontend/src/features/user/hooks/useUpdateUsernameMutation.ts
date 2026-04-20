import { useMutation } from "@tanstack/react-query";
import { updateUsername } from "@features/user/services/userService";
import type { UpdateUsernameRequest } from "@features/user/types/user.request";
import type { UserResponse } from "@features/user/types/user.response";
import { queryClient } from "@shared/api/queryClient";
import type { ApiError } from "@shared/types/api";

export const useUpdateUsernameMutation = () => {
  return useMutation<UserResponse, ApiError, UpdateUsernameRequest>({
    mutationFn: updateUsername,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["current-user"] });
      queryClient.invalidateQueries({ queryKey: ["public-feed"] });
    },
  });
};
