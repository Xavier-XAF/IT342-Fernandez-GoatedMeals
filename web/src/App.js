import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// Import Pages
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import AdminDashboard from './pages/AdminDashboard';
import Billing from './pages/Billing'; 
import Menu from './pages/Menu'; // <-- 1. IMPORT YOUR NEW MENU PAGE HERE
import Schedule from './pages/Schedule';

// Import Layouts
import MainLayout from './components/MainLayout'; 

// 1. THE BOUNCER FOR THE DASHBOARD
// Upgraded to check for your JWT accessToken for better security!
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

        {/* ---> 2. THE MISSING LINK: The Menu route now points to your component <--- */}
        <Route path="/menu" element={
            <ProtectedRoute>
                <MainLayout>
                    <Menu />
                </MainLayout>
            </ProtectedRoute>
        } />

        {/* Placeholders for the other sidebar links */}
        <Route path="/schedule" element={
            <ProtectedRoute>
                <MainLayout>
                    <Schedule />
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