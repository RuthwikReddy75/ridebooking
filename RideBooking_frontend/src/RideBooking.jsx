import React, { useState } from "react";
import "./ride-booking.css";

// ---------------------------------------------------------------------------
// Point this at your Spring Boot backend.
// ---------------------------------------------------------------------------
const API_BASE = "http://localhost:8080";

// Wherever you store the JWT after login (localStorage, context, etc.)
function getAuthHeaders() {
  const token = localStorage.getItem("token");
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

export default function RideBooking() {
  // ---- search form state (maps to AvailableRidesRequest) ----
  const [search, setSearch] = useState({
    source: "",
    destination: "",
    rideDate: "",
    requiredSeats: 1,
  });

  const [rides, setRides] = useState([]); // AvailableRideResponse[]
  const [searchLoading, setSearchLoading] = useState(false);
  const [searchError, setSearchError] = useState("");
  const [hasSearched, setHasSearched] = useState(false);

  // ---- booking modal state (maps to BookingRequest) ----
  const [activeRide, setActiveRide] = useState(null);
  const [bookingSeats, setBookingSeats] = useState(1);
  const [bookingLoading, setBookingLoading] = useState(false);
  const [bookingError, setBookingError] = useState("");
  const [bookingSuccess, setBookingSuccess] = useState("");

  function handleSearchChange(e) {
    const { name, value } = e.target;
    setSearch((prev) => ({
      ...prev,
      [name]: name === "requiredSeats" ? Number(value) : value,
    }));
  }

  async function handleSearchSubmit(e) {
    e.preventDefault();
    setSearchError("");
    setSearchLoading(true);
    setHasSearched(true);

    try {
      const params = new URLSearchParams({
        source: search.source,
        destination: search.destination,
        rideDate: search.rideDate,
        requiredSeats: String(search.requiredSeats),
      });

      const res = await fetch(`${API_BASE}/rides/available?${params}`, {
        method: "GET",
        headers: getAuthHeaders(),
      });

      if (!res.ok) {
        throw new Error(`Search failed (${res.status})`);
      }

      const data = await res.json();
      setRides(data);
    } catch (err) {
      setSearchError(err.message || "Something went wrong while searching.");
      setRides([]);
    } finally {
      setSearchLoading(false);
    }
  }

  function openBookingModal(ride) {
    setActiveRide(ride);
    setBookingSeats(1);
    setBookingError("");
    setBookingSuccess("");
  }

  function closeBookingModal() {
    setActiveRide(null);
  }

  async function confirmBooking() {
    if (!activeRide) return;
    setBookingError("");
    setBookingSuccess("");

    if (bookingSeats < 1) {
      setBookingError("Please select at least 1 seat.");
      return;
    }
    if (bookingSeats > activeRide.availableSeats) {
      setBookingError(
        `Only ${activeRide.availableSeats} seat(s) left on this ride.`
      );
      return;
    }

    setBookingLoading(true);

    // Maps directly to BookingRequest DTO
    const payload = {
      rideId: activeRide.rideId,
      source: activeRide.yourSource || activeRide.source,
      destination: activeRide.yourDestination || activeRide.destination,
      numberOfSeats: bookingSeats,
    };

    try {
      const res = await fetch(`${API_BASE}/bookings/book`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(payload),
      });

      const text = await res.text(); // controller returns a plain String

      if (!res.ok) {
        throw new Error(text || "Booking failed.");
      }

      setBookingSuccess(text || "Ride booked successfully!");

      // Reflect the reduced seat count locally without re-searching
      setRides((prev) =>
        prev.map((r) =>
          r.rideId === activeRide.rideId
            ? { ...r, availableSeats: r.availableSeats - bookingSeats }
            : r
        )
      );

      setTimeout(() => {
        setActiveRide(null);
      }, 1200);
    } catch (err) {
      setBookingError(err.message || "Booking failed. Please try again.");
    } finally {
      setBookingLoading(false);
    }
  }

  return (
    <div className="rb-page">
      <div className="rb-container">
        <div className="rb-badge">Book a ride</div>
        <div className="rb-title">Find a Ride</div>
        <div className="rb-subtitle">
          Search available rides and book your seat in a couple of clicks.
        </div>

        {/* ---------------- Search form ---------------- */}
        <form className="rb-search-card" onSubmit={handleSearchSubmit}>
          <div className="rb-form-grid">
            <div className="rb-field">
              <label htmlFor="source">Source</label>
              <input
                id="source"
                name="source"
                type="text"
                placeholder="e.g. Hyderabad"
                value={search.source}
                onChange={handleSearchChange}
                required
              />
            </div>
            <div className="rb-field">
              <label htmlFor="destination">Destination</label>
              <input
                id="destination"
                name="destination"
                type="text"
                placeholder="e.g. Warangal"
                value={search.destination}
                onChange={handleSearchChange}
                required
              />
            </div>
            <div className="rb-field">
              <label htmlFor="rideDate">Ride date</label>
              <input
                id="rideDate"
                name="rideDate"
                type="date"
                value={search.rideDate}
                onChange={handleSearchChange}
              />
            </div>
            <div className="rb-field">
              <label htmlFor="requiredSeats">Seats</label>
              <input
                id="requiredSeats"
                name="requiredSeats"
                type="number"
                min={1}
                value={search.requiredSeats}
                onChange={handleSearchChange}
                required
              />
            </div>
            <button
              type="submit"
              className="rb-search-btn"
              disabled={searchLoading}
            >
              {searchLoading ? "Searching…" : "Search rides"}
            </button>
          </div>
        </form>

        {searchError && <div className="rb-error">{searchError}</div>}

        {/* ---------------- Results ---------------- */}
        {hasSearched && !searchLoading && (
          <>
            <div className="rb-results-heading">
              {rides.length} ride{rides.length !== 1 ? "s" : ""} found
            </div>

            {rides.length === 0 ? (
              <div className="rb-empty">
                No rides match that search. Try a different date or route.
              </div>
            ) : (
              <div className="rb-ride-list">
                {rides.map((ride) => (
                  <div className="rb-ride-card" key={ride.rideId}>
                    <div>
                      <div className="rb-ride-route">
                        {(ride.yourSource || ride.source)} →{" "}
                        {(ride.yourDestination || ride.destination)}
                      </div>
                      <div className="rb-ride-meta">
                        <span>{ride.rideDate}</span>
                        <span>{ride.vehicleType} · {ride.vehicleName}</span>
                      </div>
                      <div className="rb-ride-driver">
                        Driver: {ride.driverName} · {ride.driverMobileNumber}
                      </div>
                      <div className="rb-seats-badge">
                        {ride.availableSeats} seat
                        {ride.availableSeats !== 1 ? "s" : ""} left
                      </div>
                    </div>

                    <div
                      style={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "flex-end",
                        gap: 10,
                      }}
                    >
                      <div className="rb-ride-price">
                        ₹{ride.pricePerSeat}
                        <span
                          style={{
                            fontSize: 12,
                            color: "#93a1b3",
                            fontWeight: 400,
                          }}
                        >
                          {" "}
                          /seat
                        </span>
                      </div>
                      <button
                        className="rb-book-btn"
                        disabled={ride.availableSeats < 1}
                        onClick={() => openBookingModal(ride)}
                      >
                        {ride.availableSeats < 1 ? "Full" : "Book ride"}
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>

      {/* ---------------- Booking modal ---------------- */}
      {activeRide && (
        <div className="rb-modal-backdrop" onClick={closeBookingModal}>
          <div className="rb-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Confirm booking</h3>
            <p className="rb-modal-sub">
              {(activeRide.yourSource || activeRide.source)} →{" "}
              {(activeRide.yourDestination || activeRide.destination)} ·{" "}
              {activeRide.rideDate}
            </p>

            {bookingSuccess && (
              <div className="rb-success">{bookingSuccess}</div>
            )}
            {bookingError && <div className="rb-error">{bookingError}</div>}

            <div className="rb-modal-row">
              <span>Driver</span>
              <span>{activeRide.driverName}</span>
            </div>
            <div className="rb-modal-row">
              <span>Vehicle</span>
              <span>
                {activeRide.vehicleName} ({activeRide.vehicleNumber})
              </span>
            </div>
            <div className="rb-modal-row">
              <span>Price / seat</span>
              <span>₹{activeRide.pricePerSeat}</span>
            </div>

            <div className="rb-seat-input">
              <span>Seats</span>
              <button
                type="button"
                onClick={() => setBookingSeats((s) => Math.max(1, s - 1))}
              >
                −
              </button>
              <input
                type="number"
                min={1}
                max={activeRide.availableSeats}
                value={bookingSeats}
                onChange={(e) => setBookingSeats(Number(e.target.value))}
              />
              <button
                type="button"
                onClick={() =>
                  setBookingSeats((s) =>
                    Math.min(activeRide.availableSeats, s + 1)
                  )
                }
              >
                +
              </button>
            </div>

            <div className="rb-modal-total">
              Total: ₹{(activeRide.pricePerSeat * bookingSeats).toFixed(2)}
            </div>

            <div className="rb-modal-actions">
              <button className="rb-cancel-btn" onClick={closeBookingModal}>
                Cancel
              </button>
              <button
                className="rb-confirm-btn"
                onClick={confirmBooking}
                disabled={bookingLoading}
              >
                {bookingLoading ? "Booking…" : "Confirm & book"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
