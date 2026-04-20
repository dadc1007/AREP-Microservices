import type { CreatePostRequest } from "@features/post/types/post.request";
import type { PostResponse } from "@features/post/types/post.response";
import { httpClient } from "@shared/api/httpClient";
import { mapToApiError } from "@shared/api/errors";

const POSTS_PATH = "/posts";

export const createPost = async (
  payload: CreatePostRequest,
): Promise<PostResponse> => {
  try {
    const { data } = await httpClient.post<PostResponse>(POSTS_PATH, payload);
    return data;
  } catch (error) {
    throw mapToApiError(error);
  }
};
