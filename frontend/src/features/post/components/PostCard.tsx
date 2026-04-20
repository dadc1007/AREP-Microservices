import type { PostResponse } from "@features/post/types/post.response";

type PostCardProps = {
  post: PostResponse;
};

const PostCard = ({ post }: PostCardProps) => (
  <article className="rounded-2xl border border-slate-800 bg-slate-900/80 p-5 shadow-lg shadow-slate-950/30 transition hover:border-sky-500/50 hover:bg-slate-900">
    <p className="text-base leading-7 text-slate-100">{post.content}</p>
    <p className="mt-3 text-sm font-medium text-sky-300">@{post.username}</p>
  </article>
);

export default PostCard;
