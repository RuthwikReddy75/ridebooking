import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import "./Navbar.css";

const links = [
  { to: "/rides", label: "My Rides" },
  { to: "/bookings", label: "My Bookings" },
  { to: "/create-ride", label: "Create Ride" },
  { to: "/profile", label: "Profile" },
  { to: "/book", label: "Book a Ride" }
];

export default function Navbar() {
  const navigate = useNavigate();
  const { username, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <nav className="navbar">
      <NavLink to="/dashboard" className="navbar-brand-link" >
      <div className="navbar-brand">
        <span className="navbar-logo-dot" />
        Ride<span className="navbar-accent">Share</span>
      </div>
      </NavLink>

      <ul className="navbar-links">
        {links.map(({ to, label }) => (
          <li key={to}>
            <NavLink
              to={to}
              className={({ isActive }) => (isActive ? "nav-link active" : "nav-link")}
            >
              {label}
            </NavLink>
          </li>
        ))}
        {username && <li className="navbar-username">Hi, {username}</li>}
        <li>
          <button className="nav-link logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </li>
      </ul>
    </nav>
  );
}