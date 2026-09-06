import { useState } from "react";
import axios from "axios";
import "./Login.css";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";
import { useAuth } from "./context/AuthContext";

const initialForm = { email: "", password: "" };

export default function Login() {
  const [form, setForm] = useState(initialForm);
  const [submitting, setSubmitting] = useState(false);
  const [serverMessage, setServerMessage] = useState(null); // { type: 'success' | 'error', text }
  const [showPassword, setShowPassword] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || "/dashboard";

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerMessage(null);
    setSubmitting(true);

    try {
      // AuthService.login() returns the JWT as plain text on success,
      // or an error message as plain text on failure — both go through
      // ResponseEntity<String>, so we keep responseType as text either way.
      const res = await axios.post(
        "http://localhost:8080/auth/login",
        form,
        {
          headers: { "Content-Type": "application/json" },
          responseType: "text",
        }
      );

      // Store the token so future requests can attach it as a Bearer header.
      localStorage.setItem("token", res.data);

      // Sync AuthContext state (token/username) so the rest of the app
      // (Navbar, ProtectedRoute, etc.) knows the user is logged in.
      login(res.data, form.email);

      setServerMessage({ type: "success", text: "Logged in successfully" });
      navigate(from, { replace: true });
    } catch (err) {
      if (err.response) {
        setServerMessage({
          type: "error",
          text: err.response.data || "Login failed",
        });
      } else {
        setServerMessage({
          type: "error",
          text: "Could not reach the server. Please try again.",
        });
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <span className="login-badge">RideShare</span>
          <h1>Welcome back</h1>
          <p>Log in to book a ride or offer a seat on your next trip.</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={handleChange}
              autoComplete="on"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <div className="password-field-wrapper">
              <input
                id="password"
                name="password"
                type={showPassword ? "text" : "password"}
                placeholder="Enter your password"
                value={form.password}
                onChange={handleChange}
                autoComplete="current-password"
              />
              <button
                type="button"
                className="password-toggle-btn"
                onClick={() => setShowPassword((prev) => !prev)}
                aria-label={showPassword ? "Hide password" : "Show password"}
                tabIndex={-1}
              >
                {showPassword ? <EyeOff size={17} /> : <Eye size={17} />}
              </button>
            </div>
          </div>

          {serverMessage && (
            <div className={`server-message ${serverMessage.type}`}>
              {serverMessage.text}
            </div>
          )}

          <button type="submit" disabled={submitting}>
            {submitting ? "Logging in..." : "Log in"}
          </button>
        </form>

        <p className="login-footer">
          Don't have an account? <Link to="/register">Create one</Link>
        </p>
      </div>
    </div>
  );
}