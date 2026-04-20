type AccessTokenResolver = () => Promise<string | undefined>;

let accessTokenResolver: AccessTokenResolver | undefined;

export const setAccessTokenResolver = (
  resolver: AccessTokenResolver | undefined,
): void => {
  accessTokenResolver = resolver;
};

export const getAccessToken = async (): Promise<string | undefined> => {
  if (!accessTokenResolver) {
    return undefined;
  }

  try {
    return await accessTokenResolver();
  } catch {
    return undefined;
  }
};
