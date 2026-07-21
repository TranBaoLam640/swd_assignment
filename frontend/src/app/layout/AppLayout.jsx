import { Outlet } from "react-router-dom";
import Header from "../../common/components/Header";
import Footer from "../../common/components/Footer";

/**
 * Root application shell (header/nav/footer) wrapping every routed page.
 * Feature-specific layouts, if any, should live inside that feature's own
 * folder instead of here.
 */
export default function AppLayout() {
  return (
    <div className="app-layout d-flex flex-column min-vh-100">
      <Header />
      <main className="flex-grow-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
