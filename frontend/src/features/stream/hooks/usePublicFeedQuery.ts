import { useQuery } from "@tanstack/react-query";
import { getPublicFeed } from "@features/stream/services/streamService";
import type { PostResponse } from "@features/post/types/post.response";
import type { ApiError } from "@shared/types/api";

export const usePublicFeedQuery = () =>
  useQuery<PostResponse[], ApiError>({
    queryKey: ["public-feed"],
    queryFn: getPublicFeed,
  });
