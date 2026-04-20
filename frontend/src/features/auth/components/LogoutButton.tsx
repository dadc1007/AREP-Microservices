import { useAuth0 } from "@auth0/auth0-react";

const LogoutButton = () => {
  const { logout } = useAuth0();
  return (
    <button
      type="button"
      onClick={() =>
        logout({ logoutParams: { returnTo: window.location.origin } })
      }
      className="inline-flex items-center rounded-full bg-rose-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-rose-400 focus:outline-none focus:ring-2 focus:ring-rose-400 focus:ring-offset-2 focus:ring-offset-slate-950"
    >
      Logout
    </button>
  );
};

export default LogoutButton;
