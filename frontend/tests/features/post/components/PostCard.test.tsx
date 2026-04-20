import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import PostCard from "@features/post/components/PostCard";

describe("PostCard", () => {
  it("renderiza el contenido y el username", () => {
    render(
      <PostCard
        post={{
          id: 1,
          content: "Mi primer post",
          username: "ana",
        }}
      />,
    );

    expect(screen.getByText("Mi primer post")).toBeInTheDocument();
    expect(screen.getByText("@ana")).toBeInTheDocument();
  });
});
