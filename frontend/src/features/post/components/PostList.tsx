import { usePublicFeedQuery } from "@features/stream/hooks/usePublicFeedQuery";
import PostCard from "@features/post/components/PostCard";

const PostList = () => {
  const { data, isLoading, error } = usePublicFeedQuery();

  if (isLoading) {
    return (
      <div className="rounded-2xl border border-slate-800 bg-slate-900/80 px-4 py-3 text-slate-300">
        Cargando publicaciones...
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-rose-500/40 bg-rose-500/10 px-4 py-3 text-sm text-rose-200">
        Error cargando publicaciones: {error.message}
      </div>
    );
  }

  return (
    <div className="grid gap-4">
      {!data?.length ? (
        <div className="rounded-2xl border border-dashed border-slate-700 bg-slate-900/50 px-4 py-8 text-center text-sm text-slate-400">
          No hay publicaciones disponibles.
        </div>
      ) : null}

      {data?.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
};

export default PostList;
