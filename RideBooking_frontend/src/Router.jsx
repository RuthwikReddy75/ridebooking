import { createBrowserRouter } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";
import AppLayout from "./AppLayout";
import Login from "./Login";
import Dashboard from "./Dashboard";
import Rides from "./Rides";
import Bookings from "./Bookings";
import CreateRide from "./CreateRide";
import Profile from "./Profile";
import Register from "./Register";
import { Navigate } from "react-router-dom";
import RideBooking from "./RideBooking";


export const router = createBrowserRouter([
  
  { path:"/", element: <Navigate to="/login" replace />},
  { path: "/login", element: <Login /> },
  { path: "/register", element: <Register /> },
  {
    element: <ProtectedRoute />, // auth guard
    children: [
      {
        element: <AppLayout />, // navbar + page wrapper
        children: [
          { path: "/dashboard", element: <Dashboard /> },
          { path: "/rides", element: <Rides /> },
          { path: "/bookings", element: <Bookings /> },
          { path: "/create-ride", element: <CreateRide /> },
          { path: "/profile", element: <Profile /> },
          {path: "/book", element: <RideBooking />}
        ],
      },
    ],
  },
]);