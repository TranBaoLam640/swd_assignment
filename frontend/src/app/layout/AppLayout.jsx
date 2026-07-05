// NOTE: requires react-router-dom -> npm install react-router-dom
import { Outlet } from "react-router-dom";

/**
 * Root application shell (header/nav/footer) wrapping every routed page.
 * Feature-specific layouts, if any, should live inside that feature's own
 * folder instead of here.
 */
export default function AppLayout() {
  return (
    <div className="app-layout">
      {/* TODO: header / nav */}
      <main>
        <Outlet />
      </main>
      {/* TODO: footer */}
    </div>
  );
}
