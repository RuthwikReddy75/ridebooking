import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import {
  MapPin,
  Calendar,
  Clock,
  Users,
  IndianRupee,
  Car,
  Inbox,
  AlertCircle,
  X,
  Search,
  SearchX,
} from "lucide-react";
import "./MyRides.css";

const API_BASE_URL = "http://localhost:8080";

const STATUS_CLASS = {
  ACTIVE: "active",
  CANCELLED: "cancelled",
  COMPLETED: "completed",
};

function formatDate(dateStr) {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
}

function formatTime(dateTimeStr) {
  if (!dateTimeStr) return "";
  const d = new Date(dateTimeStr);
  return d.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" });
}

export default function MyRides() {
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cancellingId, setCancellingId] = useState(null);
  const navigate = useNavigate();

  // ---- search / filter state ----
  const [filters, setFilters] = useState({
    source: "",
    destination: "",
    rideDate: "",
  });

  useEffect(() => {
    fetchRides();
  }, []);

  const fetchRides = async () => {
    setLoading(true);
    setError("");
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(`${API_BASE_URL}/rides/myrides`, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      setRides(res.data || []);
    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        navigate("/login?expired=true");
        return;
      }
      setError(
        err.response?.data || "Could not load your rides. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (rideId) => {
    if (!window.confirm("Cancel this ride? Riders who booked will be notified.")) return;

    setCancellingId(rideId);
    try {
      const token = localStorage.getItem("token");
      await axios.delete(`${API_BASE_URL}/rides/cancel/${rideId}`, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      setRides((prev) =>
        prev.map((r) => (r.id === rideId ? { ...r, status: "CANCELLED" } : r))
      );
    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        navigate("/login?expired=true");
        return;
      }
      alert("Failed to cancel the ride. Please try again.");
    } finally {
      setCancellingId(null);
    }
  };

  function handleFilterChange(e) {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  }

  function clearFilters() {
    setFilters({ source: "", destination: "", rideDate: "" });
  }

  // Client-side filter — rides are already loaded, so no extra API call
  // is needed. Swap this for a backend call to /rides/search if you'd
  // rather filter server-side.
  const filteredRides = useMemo(() => {
    const source = filters.source.trim().toLowerCase();
    const destination = filters.destination.trim().toLowerCase();
    const rideDate = filters.rideDate;

    return rides.filter((ride) => {
      const matchesSource =
        !source || (ride.source || "").toLowerCase().includes(source);
      const matchesDestination =
        !destination ||
        (ride.destination || "").toLowerCase().includes(destination);
      const matchesDate =
        !rideDate || (ride.rideDate || "").slice(0, 10) === rideDate;

      return matchesSource && matchesDestination && matchesDate;
    });
  }, [rides, filters]);

  const hasActiveFilters =
    filters.source || filters.destination || filters.rideDate;

  return (
    <div className="myrides-page">
      <div className="myrides-container">
        <div className="myrides-header">
          <span className="myrides-badge">RideShare</span>
          <h1>My rides</h1>
          <p>Rides you've published, with their current status and route details.</p>
        </div>

        {/* ---------------- Search bar ---------------- */}
        {!loading && !error && rides.length > 0 && (
          <div className="myrides-search-bar">
            <div className="myrides-search-field">
              <MapPin size={15} />
              <input
                type="text"
                name="source"
                placeholder="Source"
                value={filters.source}
                onChange={handleFilterChange}
              />
            </div>
            <div className="myrides-search-field">
              <MapPin size={15} />
              <input
                type="text"
                name="destination"
                placeholder="Destination"
                value={filters.destination}
                onChange={handleFilterChange}
              />
            </div>
            <div className="myrides-search-field">
              <Calendar size={15} />
              <input
                type="date"
                name="rideDate"
                value={filters.rideDate}
                onChange={handleFilterChange}
              />
            </div>
            {hasActiveFilters && (
              <button
                type="button"
                className="myrides-clear-btn"
                onClick={clearFilters}
              >
                <X size={14} /> Clear
              </button>
            )}
          </div>
        )}

        {loading && (
          <div className="myrides-state">
            <div className="myrides-spinner" />
            Loading your rides...
          </div>
        )}

        {!loading && error && (
          <div className="myrides-state error">
            <AlertCircle size={24} />
            <div>{error}</div>
          </div>
        )}

        {!loading && !error && rides.length === 0 && (
          <div className="myrides-state">
            <Inbox size={28} />
            <div>You haven't published any rides yet.</div>
          </div>
        )}

        {!loading && !error && rides.length > 0 && filteredRides.length === 0 && (
          <div className="myrides-state">
            <SearchX size={28} />
            <div>No rides match your search.</div>
          </div>
        )}

        {!loading && !error && filteredRides.length > 0 && (
          <div className="ride-list">
            {filteredRides.map((ride) => (
              <div className="ride-card-item" key={ride.id}>
                <div className="ride-card-top">
                  <div className="ride-route">
                    <span>{ride.source}</span>
                    <span className="route-arrow">&rarr;</span>
                    <span>{ride.destination}</span>
                  </div>
                  <span
                    className={`ride-status-badge ${
                      STATUS_CLASS[ride.status] || "completed"
                    }`}
                  >
                    {ride.status}
                  </span>
                </div>

                <div className="ride-meta-row">
                  <span className="ride-meta-item">
                    <Calendar size={14} /> {formatDate(ride.rideDate)}
                  </span>
                  <span className="ride-meta-item">
                    <Clock size={14} /> {formatTime(ride.rideTime)}
                  </span>
                  <span className="ride-meta-item">
                    <Users size={14} /> {ride.totalSeats} seats
                  </span>
                  <span className="ride-meta-item">
                    <IndianRupee size={14} /> {ride.pricePerSeat} / seat
                  </span>
                  {ride.vehicleNumber && (
                    <span className="ride-meta-item">
                      <Car size={14} /> {ride.vehicleNumber}
                    </span>
                  )}
                </div>

                {ride.rideStops && ride.rideStops.length > 0 && (
                  <div className="ride-stops-row">
                    <MapPin size={13} color="#B4ADA2" />
                    {[...ride.rideStops]
                      .sort((a, b) => a.stopOrder - b.stopOrder)
                      .map((stop, i, arr) => (
                        <span key={stop.id ?? i} style={{ display: "inline-flex", alignItems: "center", gap: 4 }}>
                          <span className="stop-chip">
                            <span className="stop-order-dot">{stop.stopOrder}</span>
                            {stop.stopName}
                            <span className="stop-price">&#8377;{stop.price}</span>
                          </span>
                          {i < arr.length - 1 && <span className="ride-chip-arrow">&rarr;</span>}
                        </span>
                      ))}
                  </div>
                )}

                {ride.description && (
                  <p className="ride-description">{ride.description}</p>
                )}

                 
                  <div className="ride-card-actions">
                    <button
                      className="ride-action-btn cancel-btn"
                      onClick={() => handleCancel(ride.id)}
                      disabled={cancellingId === ride.id}
                    >
                      <X size={14} />
                      {cancellingId === ride.id ? "Cancelling..." : "Cancel ride"}
                    </button>
                  </div>
                
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
