import { useRef, useState } from "react";
import type { FormEvent } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { Button, Form, Modal, TextArea } from "@heroui/react";
import { useCreatePostMutation } from "@features/post/hooks/useCreatePostMutation";
import PostList from "@features/post/components/PostList";
import { sileo } from "sileo";
import { getErrorMessage } from "@shared/api/errors";

const MAX_POST_LENGTH = 140;

const PublicFeed = () => {
  const { isAuthenticated } = useAuth0();
  const createPostMutation = useCreatePostMutation();
  const [content, setContent] = useState("");
  const closeTriggerRef = useRef<HTMLButtonElement | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    await sileo.promise(createPostMutation.mutateAsync({ content }), {
      loading: {
        title: "Posteando...",
        fill: "black",
        styles: {
          description: "text-slate-300",
        },
      },
      success: {
        title: "Post creado",
        description: "Tu post ha sido creado exitosamente.",
        fill: "black",
        styles: {
          description: "text-slate-300",
        },
      },
      error: (err) => ({
        title: "Error creando post",
        description: getErrorMessage(err),
        fill: "black",
        styles: {
          description: "text-slate-300",
        },
      }),
    });

    closeTriggerRef.current?.click();
    setContent("");
  };

  const remainingCharacters = MAX_POST_LENGTH - content.length;

  return (
    <section className="space-y-6">
      <div className="rounded-3xl border border-slate-800 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/30">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-slate-100">
              Public feed
            </h1>
            <p className="mt-1 text-sm text-slate-400">
              Mira las publicaciones recientes y comparte una si tienes sesión
              iniciada.
            </p>
          </div>
          <Modal>
            <Button isDisabled={!isAuthenticated}>Crear post</Button>
            <Modal.Backdrop>
              <Modal.Container placement="center" size="lg">
                <Modal.Dialog className="rounded-3xl border border-slate-800 bg-slate-900 p-0 text-slate-100 shadow-2xl shadow-slate-950/40">
                  <Modal.CloseTrigger
                    ref={closeTriggerRef}
                    className="bg-transparent"
                  />
                  <Form
                    onSubmit={(event) => {
                      void handleSubmit(event);
                    }}
                    className="p-6 sm:p-7"
                  >
                    <Modal.Header>
                      <Modal.Heading className="text-slate-100">
                        Crear un post
                      </Modal.Heading>
                    </Modal.Header>
                    <Modal.Body className="mt-5 space-y-4 overflow-visible">
                      <TextArea
                        required
                        fullWidth
                        rows={6}
                        maxLength={MAX_POST_LENGTH}
                        placeholder="Escribe tu publicación"
                        value={content}
                        onChange={(event) => setContent(event.target.value)}
                        className="bg-slate-950 text-slate-100"
                      />
                      <div className="flex items-center justify-between text-sm text-slate-400">
                        <span>{remainingCharacters} caracteres restantes</span>
                        <span>
                          {content.length}/{MAX_POST_LENGTH}
                        </span>
                      </div>
                    </Modal.Body>
                    <Modal.Footer className="mt-6 flex flex-wrap justify-end gap-3">
                      <Button
                        type="submit"
                        isDisabled={
                          createPostMutation.isPending ||
                          !content.trim().length ||
                          content.length > MAX_POST_LENGTH
                        }
                      >
                        {createPostMutation.isPending
                          ? "Publicando..."
                          : "Publicar"}
                      </Button>
                    </Modal.Footer>
                  </Form>
                </Modal.Dialog>
              </Modal.Container>
            </Modal.Backdrop>
          </Modal>
        </div>
        {!isAuthenticated ? (
          <p className="mt-4 rounded-2xl border border-amber-500/40 bg-amber-500/10 px-4 py-3 text-sm text-amber-200">
            Debes iniciar sesión para publicar.
          </p>
        ) : null}
      </div>
      <PostList />
    </section>
  );
};

export default PublicFeed;
