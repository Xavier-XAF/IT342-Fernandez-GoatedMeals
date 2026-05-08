import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// Import Pages
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import AdminDashboard from './pages/AdminDashboard';
import Billing from './pages/Billing'; // newly added

// Import Layouts
import MainLayout from './components/MainLayout'; // newly added

// 1. THE BOUNCER FOR THE DASHBOARD
// If they don't have a name in localStorage, kick them to /login
const ProtectedRoute = ({ children }) => {
    // Note: As you transition to full JWT auth, you might eventually change 'userFirstName' to 'accessToken' here
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
        {/* We wrap the page components with MainLayout so they get the persistent sidebar */}
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

        {/* Placeholders for the other sidebar links */}
        <Route path="/schedule" element={
            <ProtectedRoute>
                <MainLayout>
                    <div className="p-8 text-white">Schedule Coming Soon...</div>
                </MainLayout>
            </ProtectedRoute>
        } />
        
        <Route path="/menu" element={
            <ProtectedRoute>
                <MainLayout>
                    <div className="p-8 text-white">Menu Catalog Coming Soon...</div>
                </MainLayout>
            </ProtectedRoute>
        } />

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
        {/* Note: Left without MainLayout as the SDD shows Admin uses a different layout/sidebar */}
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        
      </Routes>
    </Router>
  );
}

export default App;