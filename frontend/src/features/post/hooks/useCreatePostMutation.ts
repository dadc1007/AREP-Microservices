import { useMutation } from "@tanstack/react-query";
import { createPost } from "@features/post/services/postService";
import type { CreatePostRequest } from "@features/post/types/post.request";
import type { PostResponse } from "@features/post/types/post.response";
import { queryClient } from "@shared/api/queryClient";
import type { ApiError } from "@shared/types/api";

export const useCreatePostMutation = () => {
  return useMutation<PostResponse, ApiError, CreatePostRequest>({
    mutationFn: createPost,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["public-feed"] });
    },
  });
};
