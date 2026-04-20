import type { FormEvent } from "react";
import {
  Button,
  FieldError,
  Form,
  Input,
  Label,
  TextField,
} from "@heroui/react";
import { useCurrentUserQuery } from "@features/user/hooks/useCurrentUserQuery";
import { useUpdateUsernameMutation } from "@features/user/hooks/useUpdateUsernameMutation";
import { useAuth0 } from "@auth0/auth0-react";
import { sileo } from "sileo";
import { getErrorMessage } from "@shared/api/errors";

const Profile = () => {
  const { data: user, isLoading, error } = useCurrentUserQuery();
  const { user: userAuth0 } = useAuth0();
  const updateUsernameMutation = useUpdateUsernameMutation();

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!user) {
      return;
    }

    const formData = new FormData(event.currentTarget);
    const nextUsername = String(formData.get("username") ?? "").trim();

    if (!nextUsername || nextUsername === user.username) {
      return;
    }

    await sileo.promise(
      updateUsernameMutation.mutateAsync({ username: nextUsername }),
      {
        loading: {
          title: "Actualizando username...",
          fill: "black",
          styles: {
            description: "text-slate-300",
          },
        },
        success: {
          title: "Username actualizado",
          description: "Tu username ha sido actualizado exitosamente.",
          fill: "black",
          styles: {
            description: "text-slate-300",
          },
        },
        error: (err) => ({
          title: "Error actualizando username",
          description: getErrorMessage(err),
          fill: "black",
          styles: {
            description: "text-slate-300",
          },
        }),
      },
    );

    event.currentTarget.reset();
  };

  if (isLoading) {
    return (
      <div className="rounded-2xl border border-slate-800 bg-slate-900/80 px-4 py-3 text-slate-300">
        Loading profile...
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-rose-500/40 bg-rose-500/10 px-4 py-3 text-sm text-rose-200">
        Error: {error.message}
      </div>
    );
  }

  if (!user) {
    return (
      <div className="rounded-2xl border border-slate-800 bg-slate-900/80 px-4 py-3 text-sm text-slate-400">
        No se encontro informacion del usuario.
      </div>
    );
  }

  return (
    <div className="mx-auto flex w-full max-w-xl flex-col items-center rounded-3xl border border-slate-800 bg-slate-900/80 p-6 shadow-xl shadow-slate-950/30 sm:p-8">
      <img
        src={
          userAuth0?.picture ||
          `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='110' height='110' viewBox='0 0 110 110'%3E%3Ccircle cx='55' cy='55' r='55' fill='%2363b3ed'/%3E%3Cpath d='M55 50c8.28 0 15-6.72 15-15s-6.72-15-15-15-15 6.72-15 15 6.72 15 15 15zm0 7.5c-10 0-30 5.02-30 15v3.75c0 2.07 1.68 3.75 3.75 3.75h52.5c2.07 0 3.75-1.68 3.75-3.75V72.5c0-9.98-20-15-30-15z' fill='%23fff'/%3E%3C/svg%3E`
        }
        alt={user.username}
        className="h-28 w-28 rounded-full border-4 border-sky-400 object-cover shadow-lg shadow-sky-400/20"
      />
      <div className="mt-5 flex flex-col items-center justify-center text-center w-full">
        <div className="text-3xl font-semibold text-slate-100">
          {user.username}
        </div>
        <div className="mt-2 text-sm text-slate-400 sm:text-base">
          {user.email}
        </div>
        <Form
          onSubmit={(event) => {
            void handleSubmit(event);
          }}
          className="mt-6 grid w-full max-w-xs gap-4 text-left"
        >
          <TextField
            isRequired
            name="username"
            defaultValue={user.username}
            validate={(value) => {
              if (!value.trim()) {
                return "El username es obligatorio";
              }

              if (value.trim() === user.username) {
                return "Debes escribir un username diferente";
              }

              return null;
            }}
          >
            <Label className="text-slate-300">Nuevo username</Label>
            <Input
              placeholder="Nuevo username"
              className="bg-slate-950 text-slate-100"
            />
            <FieldError />
          </TextField>
          <Button
            type="submit"
            isDisabled={updateUsernameMutation.isPending}
            className="m-auto"
          >
            {updateUsernameMutation.isPending
              ? "Actualizando..."
              : "Actualizar username"}
          </Button>
        </Form>
      </div>
    </div>
  );
};

export default Profile;
