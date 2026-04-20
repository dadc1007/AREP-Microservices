import type { PropsWithChildren } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { useAuthSessionSetup } from "@features/auth/hooks/useAuthSessionSetup";
import AppHeader from "@shared/components/AppHeader";
import Profile from "@features/user/pages/Profile";
import PublicFeed from "@features/stream/pages/PublicFeed";

function ProtectedRoute({ children }: PropsWithChildren) {
  const { isAuthenticated, isLoading } = useAuth0();

  if (isLoading) {
    return (
      <div className="rounded-2xl border border-slate-800 bg-slate-900/80 px-4 py-3 text-slate-300">
        Loading...
      </div>
    );
  }

  return isAuthenticated ? children : <Navigate to="/" replace />;
}

function App() {
  useAuthSessionSetup();

  return (
    <BrowserRouter>
      <div className="min-h-screen bg-slate-950 text-slate-100">
        <AppHeader />
        <main className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
          <Routes>
            <Route path="/" element={<PublicFeed />} />
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <Profile />
                </ProtectedRoute>
              }
            />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
