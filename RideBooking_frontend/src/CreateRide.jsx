import { useState } from "react";
import axios from "axios";
import { Plus, Trash2, MapPin, Calendar, Clock, Users, IndianRupee, Car, FileText } from "lucide-react";
import "./CreateRide.css";

// Adjust this to wherever your app centralizes the API base URL / axios instance.
const API_BASE_URL = "http://localhost:8080";

const emptyStop = () => ({ stopName: "", stopOrder: 1, price: "" });

export default function CreateRide() {
  const [form, setForm] = useState({
    source: "",
    destination: "",
    rideDate: "",
    rideTime: "",
    totalSeats: "",
    vehicleNumber: "",
    pricePerSeat: "",
    description: "",
  });
  const [stops, setStops] = useState([emptyStop()]);
  const [submitting, setSubmitting] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [serverError, setServerError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleStopChange = (index, field, value) => {
    setStops((prev) =>
      prev.map((stop, i) => (i === index ? { ...stop, [field]: value } : stop))
    );
  };

  const addStop = () => {
    setStops((prev) => [...prev, { ...emptyStop(), stopOrder: prev.length + 1 }]);
  };

  const removeStop = (index) => {
    setStops((prev) =>
      prev
        .filter((_, i) => i !== index)
        .map((stop, i) => ({ ...stop, stopOrder: i + 1 }))
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccessMsg("");
    setServerError("");

    // rideTime is a LocalDateTime on the backend -> combine date + time into
    // an ISO-8601 string without timezone, e.g. "2026-08-11T14:30:00"
    const rideDateTime = `${form.rideDate}T${form.rideTime}:00`;

    const payload = {
      source: form.source.trim(),
      destination: form.destination.trim(),
      rideDate: form.rideDate,
      rideTime: rideDateTime,
      totalSeats: Number(form.totalSeats),
      vehicleNumber: form.vehicleNumber.trim() || null,
      pricePerSeat: Number(form.pricePerSeat),
      description: form.description.trim() || null,
      rideStops: stops.map((s) => ({
        stopName: s.stopName.trim(),
        stopOrder: Number(s.stopOrder),
        price: Number(s.price),
      })),
    };

    setSubmitting(true);
    try {
      const token = localStorage.getItem("token"); // adjust to your auth storage

      const res = await axios.post(
        `${API_BASE_URL}/rides/createride`,
        payload,
        {
          headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
          },
        }
      );

      // axios auto-parses JSON; if backend returns plain text, res.data will
      // just be that string. Adjust based on what your endpoint returns.
      setSuccessMsg(res.data || "Ride created successfully");
      setForm({
        source: "",
        destination: "",
        rideDate: "",
        rideTime: "",
        totalSeats: "",
        vehicleNumber: "",
        pricePerSeat: "",
        description: "",
      });
      setStops([emptyStop()]);
    } catch (err) {
      if (err.response) {
        // Server responded with a non-2xx status
        setServerError(err.response.data || "Failed to create ride");
      } else if (err.request) {
        // Request was made but no response received
        setServerError("Could not reach the server. Please try again.");
      } else {
        setServerError("Something went wrong. Please try again.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="create-ride-page">
      <div className="ride-page-container">
        <div className="ride-card">
          <div className="ride-header">
            <span className="ride-badge">New ride</span>
            <h1>Publish a ride</h1>
            <p>Set your route, timing, and price per seat so riders along the way can find and book it.</p>
          </div>

          {successMsg && <div className="server-message success">{successMsg}</div>}
          {serverError && <div className="server-message error">{serverError}</div>}

          <form className="ride-form" onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label><MapPin size={14} /> Source</label>
                <input
                  name="source"
                  value={form.source}
                  onChange={handleChange}
                  placeholder="e.g. Hitech City"
                />
              </div>

              <div className="form-group">
                <label><MapPin size={14} /> Destination</label>
                <input
                  name="destination"
                  value={form.destination}
                  onChange={handleChange}
                  placeholder="e.g. Gachibowli"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label><Calendar size={14} /> Ride date</label>
                <input
                  type="date"
                  name="rideDate"
                  value={form.rideDate}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label><Clock size={14} /> Ride time</label>
                <input
                  type="time"
                  name="rideTime"
                  value={form.rideTime}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label><Users size={14} /> Total seats</label>
                <input
                  type="number"
                  min={1}
                  name="totalSeats"
                  value={form.totalSeats}
                  onChange={handleChange}
                  placeholder="e.g. 3"
                />
              </div>

              <div className="form-group">
                <label><IndianRupee size={14} /> Price per seat</label>
                <input
                  type="number"
                  min={0}
                  step="0.01"
                  name="pricePerSeat"
                  value={form.pricePerSeat}
                  onChange={handleChange}
                  placeholder="e.g. 150"
                />
              </div>
            </div>

            <div className="form-group">
              <label><Car size={14} /> Vehicle number (optional)</label>
              <input
                name="vehicleNumber"
                value={form.vehicleNumber}
                onChange={handleChange}
                placeholder="e.g. TS09AB1234"
              />
            </div>

            <div className="form-group">
              <label><FileText size={14} /> Description (optional)</label>
              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                rows={3}
                placeholder="Any extra details for riders..."
              />
            </div>

            <div className="stops-section">
              <div className="stops-heading-row">
                <label>Ride stops</label>
                <button type="button" className="add-stop-btn" onClick={addStop}>
                  <Plus size={14} /> Add stop
                </button>
              </div>

              {stops.map((stop, index) => (
                <div className="stop-row" key={index}>
                  <div className="form-group">
                    <input
                      value={stop.stopName}
                      onChange={(e) => handleStopChange(index, "stopName", e.target.value)}
                      placeholder="Stop name"
                    />
                  </div>

                  <div className="form-group">
                    <input
                      type="number"
                      min={1}
                      value={stop.stopOrder}
                      onChange={(e) => handleStopChange(index, "stopOrder", e.target.value)}
                      placeholder="Order"
                    />
                  </div>

                  <div className="form-group">
                    <input
                      type="number"
                      min={0}
                      step="0.01"
                      value={stop.price}
                      onChange={(e) => handleStopChange(index, "price", e.target.value)}
                      placeholder="Price"
                    />
                  </div>

                  <button
                    type="button"
                    className="remove-stop-btn"
                    onClick={() => removeStop(index)}
                    disabled={stops.length === 1}
                    title="Remove stop"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              ))}
            </div>

            <button type="submit" disabled={submitting}>
              {submitting ? "Publishing ride..." : "Publish ride"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}