import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import PostList from "@features/post/components/PostList";
import { usePublicFeedQuery } from "@features/stream/hooks/usePublicFeedQuery";

vi.mock("@features/stream/hooks/usePublicFeedQuery", () => ({
  usePublicFeedQuery: vi.fn(),
}));

const mockUsePublicFeedQuery = vi.mocked(usePublicFeedQuery);

describe("PostList", () => {
  it("muestra mensaje de carga", () => {
    mockUsePublicFeedQuery.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
    } as unknown as ReturnType<typeof usePublicFeedQuery>);

    render(<PostList />);

    expect(screen.getByText("Cargando publicaciones...")).toBeInTheDocument();
  });

  it("muestra mensaje de error", () => {
    mockUsePublicFeedQuery.mockReturnValue({
      data: undefined,
      isLoading: false,
      error: { message: "fallo api" },
    } as unknown as ReturnType<typeof usePublicFeedQuery>);

    render(<PostList />);

    expect(
      screen.getByText("Error cargando publicaciones: fallo api"),
    ).toBeInTheDocument();
  });

  it("muestra estado vacío cuando no hay publicaciones", () => {
    mockUsePublicFeedQuery.mockReturnValue({
      data: [],
      isLoading: false,
      error: null,
    } as unknown as ReturnType<typeof usePublicFeedQuery>);

    render(<PostList />);

    expect(
      screen.getByText("No hay publicaciones disponibles."),
    ).toBeInTheDocument();
  });

  it("renderiza publicaciones cuando hay data", () => {
    mockUsePublicFeedQuery.mockReturnValue({
      data: [
        { id: 1, content: "Hola", username: "ana" },
        { id: 2, content: "Mundo", username: "bob" },
      ],
      isLoading: false,
      error: null,
    } as unknown as ReturnType<typeof usePublicFeedQuery>);

    render(<PostList />);

    expect(screen.getByText("Hola")).toBeInTheDocument();
    expect(screen.getByText("@ana")).toBeInTheDocument();
    expect(screen.getByText("Mundo")).toBeInTheDocument();
    expect(screen.getByText("@bob")).toBeInTheDocument();
  });
});
