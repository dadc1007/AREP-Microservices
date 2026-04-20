import type { PostResponse } from "@features/post/types/post.response";
import { httpClient } from "@shared/api/httpClient";
import { mapToApiError } from "@shared/api/errors";

const FEED_PATH = "/feed";

export const getPublicFeed = async (): Promise<PostResponse[]> => {
  try {
    const { data } = await httpClient.get<PostResponse[]>(
      `${FEED_PATH}/public`,
    );
    return data;
  } catch (error) {
    throw mapToApiError(error);
  }
};
