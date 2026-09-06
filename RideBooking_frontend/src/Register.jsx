import { useState } from "react";
import axios from "axios";
import "./Register.css";
import { Link } from 'react-router-dom';

const initialForm = { name: "", email: "", password: "", phone: "" };

export default function Register() {
  const [form, setForm] = useState(initialForm);
  const [submitting, setSubmitting] = useState(false);
  const [serverMessage, setServerMessage] = useState(null); // { type: 'success' | 'error', text }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerMessage(null);
    setSubmitting(true);

    try {
      // Backend returns plain text (ResponseEntity<String>), not JSON,
      // so we tell axios not to try parsing the response body as JSON.
      console.log(form);
      const res = await axios.post(
        "http://localhost:8080/auth/register",
        form,
        {
          headers: { "Content-Type": "application/json" },
          responseType: "text",
        }
      );
      console.log(res);

      setServerMessage({ type: "success", text: res.data || "Registered successfully" });
      setForm(initialForm);
    } catch (err) {
      // Axios throws on any non-2xx status, unlike fetch.
      // err.response exists if the server responded (e.g. 400 validation error);
      // it's undefined if the request never reached the server (network/CORS issue).
      if (err.response) {
        setServerMessage({
          type: "error",
          text: err.response.data || "Registration failed",
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

  const fields = [
    { name: "name", label: "Full name", type: "text", placeholder: "e.g. Ruthwik Reddy" },
    { name: "email", label: "Email", type: "email", placeholder: "you@example.com" },
    { name: "password", label: "Password", type: "password", placeholder: "At least 8 characters" },
    { name: "phone", label: "Phone number", type: "tel", placeholder: "10-digit mobile number" },
  ];

  return (
    <div className="register-page">
      <div className="register-card">
        <div className="register-header">
          <span className="register-badge">RideShare</span>
          <h1>Create your account</h1>
          <p>Post a ride when you're headed somewhere, or book a seat on someone else's.</p>
        </div>

        <form onSubmit={handleSubmit}>
          {fields.map(({ name, label, type, placeholder }) => (
            <div className="form-group" key={name}>
              <label htmlFor={name}>{label}</label>
              <input
                id={name}
                name={name}
                type={type}
                placeholder={placeholder}
                value={form[name]}
                onChange={handleChange}
                autoComplete={name === "password" ? "new-password" : "on"}
              />
            </div>
          ))}

          {serverMessage && (
            <div className={`server-message ${serverMessage.type}`}>
              {serverMessage.text}
            </div>
          )}

          <button type="submit" disabled={submitting}>
            {submitting ? "Creating account..." : "Create account"}
          </button>
        </form>

        <p className="register-footer">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </div>
    </div>
  );
}
