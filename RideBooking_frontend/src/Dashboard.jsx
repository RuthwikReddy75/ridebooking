import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "./Navbar";
import "./Dashboard.css";

// Decodes a JWT payload without needing a library — good enough for
// pulling display info like the username/email out of the token.
function decodeToken(token) {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

export default function Dashboard() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      const decoded = decodeToken(token);
      // JwtService.generateToken(email) sets the email as the "sub" claim
      setUser(decoded);
    }
  }, []);

  const greeting = getGreeting();

  return (
    <div className="dashboard-page">
      

     
        <section className="welcome-banner">
          <div>
            <span className="welcome-eyebrow">{greeting}</span>
            <h1>
              Welcome back{user?.sub ? `, ${user.sub.split("@")[0]}` : ""} 👋
            </h1>
            <p>Ready to share the road? Post a ride or find one going your way.</p>
          </div>
          <div className="welcome-actions">
            <Link to="/create-ride" className="btn-primary">
              Create a ride
            </Link>
            <Link to="/rides" className="btn-secondary">
              Find a ride
            </Link>
          </div>
        </section>

        <section className="dashboard-grid">
          <Link to="/rides" className="dashboard-card">
            <span className="card-icon">🚗</span>
            <h3>My Rides</h3>
            <p>Rides you've posted as a driver</p>
          </Link>
          <Link to="/bookings" className="dashboard-card">
            <span className="card-icon">🎟️</span>
            <h3>My Bookings</h3>
            <p>Seats you've booked with other drivers</p>
          </Link>
          <Link to="/create-ride" className="dashboard-card">
            <span className="card-icon">➕</span>
            <h3>Create Ride</h3>
            <p>Going somewhere? Offer seats to others</p>
          </Link>
          <Link to="/profile" className="dashboard-card">
            <span className="card-icon">👤</span>
            <h3>Profile</h3>
            <p>Manage your account details</p>
          </Link>
        </section>
      
    </div>
  );
}

function getGreeting() {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 17) return "Good afternoon";
  return "Good evening";
}
