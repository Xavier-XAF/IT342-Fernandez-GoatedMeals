import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// --- NEW VERTICAL SLICE IMPORTS ---
import Login from './features/auth/Login';
import Register from './features/auth/Register';
import Dashboard from './features/dashboard/Dashboard';
import AdminDashboard from './features/admin/AdminDashboard';
import Billing from './features/billing/Billing';
import Menu from './features/menu/Menu';
import Schedule from './features/schedule/Schedule';

// Import Global Layout from Core
import MainLayout from './features/core/MainLayout';

// 1. THE BOUNCER FOR THE DASHBOARD
const ProtectedRoute = ({ children }) => {
    const isAuthenticated = localStorage.getItem('accessToken') !== null;
    return isAuthenticated ? children : <Navigate to="/login" replace />;
};

// 2. THE BOUNCER FOR LOGIN/REGISTER
const PublicRoute = ({ children }) => {
    const isAuthenticated = localStorage.getItem('accessToken') !== null;
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
                <MainLayout>
                    <Dashboard />
                </MainLayout>
            </ProtectedRoute>
        } />

        <Route path="/billing" element={
            <ProtectedRoute>
                <MainLayout>
                    <Billing />
                </MainLayout>
            </ProtectedRoute>
        } />

        <Route path="/menu" element={
            <ProtectedRoute>
                <MainLayout>
                    <Menu />
                </MainLayout>
            </ProtectedRoute>
        } />

        <Route path="/schedule" element={
            <ProtectedRoute>
                <MainLayout>
                    <Schedule />
                </MainLayout>
            </ProtectedRoute>
        } />

        {/* Placeholders for the other sidebar links */}
        <Route path="/profile" element={
            <ProtectedRoute>
                <MainLayout>
                    <div className="p-8 text-white">Profile Coming Soon...</div>
                </MainLayout>
            </ProtectedRoute>
        } />

        <Route path="/settings" element={
            <ProtectedRoute>
                <MainLayout>
                    <div className="p-8 text-white">Settings Coming Soon...</div>
                </MainLayout>
            </ProtectedRoute>
        } />

        {/* --- ADMIN ROUTES --- */}
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        
      </Routes>
    </Router>
  );
}

export default App;