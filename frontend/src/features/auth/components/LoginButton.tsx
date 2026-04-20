import { useAuth0 } from "@auth0/auth0-react";

const auth0Audience = import.meta.env.VITE_AUTH0_AUDIENCE as string | undefined;
const auth0Scope = import.meta.env.VITE_AUTH0_SCOPE as string | undefined;

const LoginButton = () => {
  const { loginWithRedirect } = useAuth0();
  return (
    <button
      type="button"
      onClick={() =>
        loginWithRedirect({
          authorizationParams: {
            audience: auth0Audience,
            scope: auth0Scope,
          },
        })
      }
      className="inline-flex items-center rounded-full bg-sky-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-sky-400 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:ring-offset-2 focus:ring-offset-slate-950"
    >
      Login
    </button>
  );
};

export default LoginButton;
