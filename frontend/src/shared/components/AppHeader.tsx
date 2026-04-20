import { useAuth0 } from "@auth0/auth0-react";
import { Link } from "react-router-dom";
import LoginButton from "@features/auth/components/LoginButton";
import LogoutButton from "@features/auth/components/LogoutButton";

const AppHeader = () => {
  const { isAuthenticated } = useAuth0();

  return (
    <header className="sticky top-0 z-20 border-b border-slate-800/80 bg-slate-950/90 backdrop-blur">
      <div className="mx-auto flex w-full max-w-6xl items-center justify-end px-4 py-4 sm:px-6 lg:px-8">
        <Link
          to="/"
          className="mr-auto inline-flex items-center rounded-full border border-slate-700 px-4 py-2 text-sm font-semibold text-slate-200 transition hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:ring-offset-2 focus:ring-offset-slate-950"
        >
          Inicio
        </Link>
        {isAuthenticated ? (
          <div className="flex items-center gap-3">
            <Link
              to="/profile"
              className="inline-flex items-center rounded-full border border-slate-600 px-4 py-2 text-sm font-semibold text-slate-100 transition hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:ring-offset-2 focus:ring-offset-slate-950"
            >
              Profile
            </Link>
            <LogoutButton />
          </div>
        ) : (
          <LoginButton />
        )}
      </div>
    </header>
  );
};

export default AppHeader;
