import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';

// 1. THE BOUNCER FOR THE DASHBOARD
// If they don't have a name in localStorage, kick them to /login
const ProtectedRoute = ({ children }) => {
    const isAuthenticated = localStorage.getItem('userFirstName') !== null;
    return isAuthenticated ? children : <Navigate to="/login" replace />;
};

// 2. THE BOUNCER FOR LOGIN/REGISTER
// If they DO have a name in localStorage, kick them to /dashboard
const PublicRoute = ({ children }) => {
    const isAuthenticated = localStorage.getItem('userFirstName') !== null;
    return !isAuthenticated ? children : <Navigate to="/dashboard" replace />;
};

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirect base URL to login */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        {/* --- PUBLIC ROUTES (Only for guests) --- */}
        <Route path="/login" element={
            <PublicRoute>
                <Login />
            </PublicRoute>
        } />
        
        <Route path="/register" element={
            <PublicRoute>
                <Register />
            </PublicRoute>
        } />
        
        {/* --- PROTECTED ROUTES (Only for logged-in users) --- */}
        <Route path="/dashboard" element={
            <ProtectedRoute>
                <Dashboard />
            </ProtectedRoute>
        } />
      </Routes>
    </Router>
  );
}

export default App;