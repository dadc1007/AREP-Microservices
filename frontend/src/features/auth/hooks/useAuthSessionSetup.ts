import { useEffect, useRef } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { createUser } from "@features/user/services/userService";
import { setAccessTokenResolver } from "@shared/api/authTokenProvider";

export const useAuthSessionSetup = () => {
  const { isAuthenticated, isLoading, user, getAccessTokenSilently } =
    useAuth0();
  const processedUserSubRef = useRef<string | null>(null);
  const audience = import.meta.env.VITE_AUTH0_AUDIENCE as string | undefined;

  useEffect(() => {
    setAccessTokenResolver(async () => {
      if (!isAuthenticated) {
        return undefined;
      }

      return getAccessTokenSilently({
        authorizationParams: {
          audience,
        },
      });
    });

    return () => {
      setAccessTokenResolver(undefined);
    };
  }, [audience, getAccessTokenSilently, isAuthenticated]);

  useEffect(() => {
    if (isLoading || !isAuthenticated || !user?.sub || !user?.email) {
      return;
    }

    if (processedUserSubRef.current === user.sub) {
      return;
    }

    processedUserSubRef.current = user.sub;

    const syncUser = async () => {
      try {
        await createUser({ email: user.email! });
      } catch {
        // Silencioso por diseño: si el usuario ya existe o falla la creación,
        // no se muestra nada al usuario final.
      }
    };

    void syncUser();
  }, [isAuthenticated, isLoading, user?.email, user?.sub]);
};
