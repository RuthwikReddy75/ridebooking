import { useEffect, useState } from "react";
import axios from "axios";
import {
  MapPin,
  Calendar,
  Clock,
  Users,
  IndianRupee,
  Car,
  User,
  Phone,
  Loader2,
  X,
} from "lucide-react";
import "./Bookings.css";

// Adjust this to wherever your app centralizes the API base URL / axios instance.
const API_BASE_URL = "http://localhost:8080";

// Matches BookingStatus enum exactly: BOOKED, CANCELLED, COMPLETED, RIDECANCELLED
const STATUS_STYLES = {
  BOOKED: "status-booked",
  CANCELLED: "status-cancelled",
  COMPLETED: "status-completed",
  RIDECANCELLED: "status-ridecancelled",
};

const STATUS_LABELS = {
  BOOKED: "Booked",
  CANCELLED: "Cancelled by you",
  COMPLETED: "Completed",
  RIDECANCELLED: "Ride cancelled",
};

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  return new Date(dateStr).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
};

const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return "-";
  return new Date(dateTimeStr).toLocaleTimeString("en-IN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

export default function MyBookings() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Track which booking is currently being cancelled, and any per-row error.
  const [cancellingId, setCancellingId] = useState(null);
  const [cancelError, setCancelError] = useState({});

  // Separate search fields
  const [sourceQuery, setSourceQuery] = useState("");
  const [destinationQuery, setDestinationQuery] = useState("");
  const [rideDateQuery, setRideDateQuery] = useState(""); // yyyy-mm-dd from <input type="date">

  useEffect(() => {
    fetchMyBookings();
  }, []);

  const fetchMyBookings = async () => {
    setLoading(true);
    setError("");

    try {
      const token = localStorage.getItem("token"); // adjust to your auth storage

      const res = await axios.get(`${API_BASE_URL}/bookings/mybookings`, {
        headers: {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
      });

      setBookings(res.data || []);
    } catch (err) {
      if (err.response) {
        setError(
          err.response.data?.message ||
            err.response.data ||
            "Failed to load your bookings."
        );
      } else if (err.request) {
        setError("Could not reach the server. Please try again.");
      } else {
        setError("Something went wrong. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async (bookingId) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this booking?"
    );
    if (!confirmed) return;

    setCancellingId(bookingId);
    setCancelError((prev) => ({ ...prev, [bookingId]: "" }));

    try {
      const token = localStorage.getItem("token"); // adjust to your auth storage

      await axios.patch(
        `${API_BASE_URL}/bookings/${bookingId}/cancel`,
        null,
        {
          headers: {
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
          },
        }
      );

      // Reflect the cancellation locally instead of refetching everything.
      setBookings((prev) =>
        prev.map((b) =>
          b.bookingId === bookingId ? { ...b, status: "CANCELLED" } : b
        )
      );
    } catch (err) {
      let message = "Could not cancel this booking. Please try again.";
      if (err.response) {
        message = err.response.data?.message || err.response.data || message;
      } else if (err.request) {
        message = "Could not reach the server. Please try again.";
      }
      setCancelError((prev) => ({ ...prev, [bookingId]: message }));
    } finally {
      setCancellingId(null);
    }
  };

  const clearAllFilters = () => {
    setSourceQuery("");
    setDestinationQuery("");
    setRideDateQuery("");
  };

  const hasActiveFilters =
    sourceQuery.trim() || destinationQuery.trim() || rideDateQuery.trim();

  // Each field filters independently (AND logic) — a booking must match
  // every field the user has actually typed something into.
  const filteredBookings = bookings.filter((booking) => {
    const source = booking.source?.toLowerCase() || "";
    const destination = booking.destination?.toLowerCase() || "";
    // booking.rideDate is expected as an ISO-ish string e.g. "2026-08-22..."
    const rideDateRaw = (booking.rideDate || "").slice(0, 10); // yyyy-mm-dd

    const sourceMatch = sourceQuery.trim()
      ? source.includes(sourceQuery.trim().toLowerCase())
      : true;

    const destinationMatch = destinationQuery.trim()
      ? destination.includes(destinationQuery.trim().toLowerCase())
      : true;

    const rideDateMatch = rideDateQuery.trim()
      ? rideDateRaw === rideDateQuery.trim()
      : true;

    return sourceMatch && destinationMatch && rideDateMatch;
  });

  return (
    <div className="my-bookings-page">
      <div className="bookings-page-container">
        <div className="bookings-header">
          <span className="bookings-badge">Your trips</span>
          <h1>My bookings</h1>
          <p>Track the rides you've booked, along with driver and status details.</p>
        </div>

        {!loading && !error && bookings.length > 0 && (
          <div className="bookings-search-row">
            <div className="search-field">
              <MapPin size={14} className="search-field-icon" />
              <input
                type="text"
                placeholder="Source"
                value={sourceQuery}
                onChange={(e) => setSourceQuery(e.target.value)}
                className="search-field-input"
              />
            </div>

            <div className="search-field">
              <MapPin size={14} className="search-field-icon" />
              <input
                type="text"
                placeholder="Destination"
                value={destinationQuery}
                onChange={(e) => setDestinationQuery(e.target.value)}
                className="search-field-input"
              />
            </div>

            <div className="search-field">
              <Calendar size={14} className="search-field-icon" />
              <input
                type="date"
                value={rideDateQuery}
                onChange={(e) => setRideDateQuery(e.target.value)}
                className="search-field-input"
              />
            </div>

            {hasActiveFilters && (
              <button
                className="clear-filters-btn"
                onClick={clearAllFilters}
                type="button"
              >
                <X size={14} />
                <span>Clear</span>
              </button>
            )}
          </div>
        )}

        {loading && (
          <div className="bookings-state">
            <Loader2 className="spin" size={22} />
            <span>Loading your bookings...</span>
          </div>
        )}

        {!loading && error && (
          <div className="bookings-state error">
            <span>{error}</span>
            <button onClick={fetchMyBookings}>Retry</button>
          </div>
        )}

        {!loading && !error && bookings.length === 0 && (
          <div className="bookings-state empty">
            <span>You haven't booked any rides yet.</span>
          </div>
        )}

        {!loading &&
          !error &&
          bookings.length > 0 &&
          filteredBookings.length === 0 && (
            <div className="bookings-state empty">
              <span>No bookings match your filters.</span>
            </div>
          )}

        {!loading && !error && filteredBookings.length > 0 && (
          <div className="bookings-list">
            {filteredBookings.map((booking) => (
              <div className="booking-card" key={booking.bookingId}>
                <div className="booking-card-top">
                  <div className="route">
                    <MapPin size={16} />
                    <span>{booking.source}</span>
                    <span className="arrow">→</span>
                    <span>{booking.destination}</span>
                  </div>

                  <span
                    className={`status-badge ${
                      STATUS_STYLES[booking.status] || "status-default"
                    }`}
                  >
                    {STATUS_LABELS[booking.status] || booking.status}
                  </span>
                </div>

                <div className="booking-card-details">
                  <div className="detail-item">
                    <Calendar size={14} />
                    <span>{formatDate(booking.rideDate)}</span>
                  </div>

                  <div className="detail-item">
                    <Clock size={14} />
                    <span>{formatTime(booking.rideTime)}</span>
                  </div>

                  <div className="detail-item">
                    <Users size={14} />
                    <span>{booking.seatsBooked} seat(s)</span>
                  </div>

                  <div className="detail-item">
                    <IndianRupee size={14} />
                    <span>{booking.price}</span>
                  </div>
                </div>

                <div className="booking-card-footer">
                  <div className="driver-info">
                    <div className="detail-item">
                      <User size={14} />
                      <span>{booking.driverName || "Not assigned"}</span>
                    </div>

                    {booking.driverPhone && (
                      <div className="detail-item">
                        <Phone size={14} />
                        <span>{booking.driverPhone}</span>
                      </div>
                    )}

                    {booking.vehicleNumber && (
                      <div className="detail-item">
                        <Car size={14} />
                        <span>{booking.vehicleNumber}</span>
                      </div>
                    )}
                  </div>

                  <span className="booked-at">
                    Booked on {formatDate(booking.bookedAt)}
                  </span>
                </div>

                {/* Cancel button: only for bookings still in BOOKED state */}
                {booking.status === "BOOKED" && (
                  <div className="booking-card-actions">
                    <button
                      className="cancel-booking-btn"
                      onClick={() => handleCancelBooking(booking.bookingId)}
                      disabled={cancellingId === booking.bookingId}
                    >
                      {cancellingId === booking.bookingId ? (
                        <>
                          <Loader2 className="spin" size={14} />
                          <span>Cancelling...</span>
                        </>
                      ) : (
                        <>
                          <X size={14} />
                          <span>Cancel booking</span>
                        </>
                      )}
                    </button>

                    {cancelError[booking.bookingId] && (
                      <span className="cancel-error">
                        {cancelError[booking.bookingId]}
                      </span>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
