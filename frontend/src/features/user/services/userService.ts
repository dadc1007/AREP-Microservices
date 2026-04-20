import type {
  CreateUserRequest,
  UpdateUsernameRequest,
} from "@features/user/types/user.request";
import type { UserResponse } from "@features/user/types/user.response";
import { httpClient } from "@shared/api/httpClient";
import { mapToApiError } from "@shared/api/errors";

const USER_PATH = "/users";

export const createUser = async (
  payload: CreateUserRequest,
): Promise<UserResponse> => {
  try {
    const { data } = await httpClient.post<UserResponse>(USER_PATH, payload);

    return data;
  } catch (error) {
    throw mapToApiError(error);
  }
};

export const getCurrentUser = async (): Promise<UserResponse> => {
  try {
    const { data } = await httpClient.get<UserResponse>("/me");
    return data;
  } catch (error) {
    throw mapToApiError(error);
  }
};

export const updateUsername = async (
  payload: UpdateUsernameRequest,
): Promise<UserResponse> => {
  try {
    const { data } = await httpClient.put<UserResponse>(
      `${USER_PATH}/username`,
      payload,
    );
    return data;
  } catch (error) {
    throw mapToApiError(error);
  }
};
