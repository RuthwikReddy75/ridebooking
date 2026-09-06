import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";

export default function AppLayout() {
  return (
    <>
      <Navbar />
      <main className="page-content">
        <Outlet />
      </main>
    </>
  );
}