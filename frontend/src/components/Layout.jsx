import { useState } from "react";
import { Outlet } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import Header from "./Header";
import Footer from "./Footer";
import AuthModal from "./AuthModal";
import ScrollManager from "./ScrollManager";

export default function Layout() {
  const auth = useAuth();
  const [mode, setMode] = useState(null); // null | "login" | "register"

  return (
    <>
      <ScrollManager />
      <Header user={auth.user} onOpenAuth={setMode} onLogout={auth.logout} />
      <main>
        <Outlet context={{ auth, openAuth: setMode }} />
      </main>
      <Footer />
      {mode && (
        <AuthModal key={mode} mode={mode} onModeChange={setMode} onClose={() => setMode(null)} auth={auth} />
      )}
    </>
  );
}
