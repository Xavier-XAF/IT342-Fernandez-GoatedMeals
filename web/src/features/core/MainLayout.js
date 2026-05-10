import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

export default function MainLayout({ children }) {
  const location = useLocation();
  const navigate = useNavigate();
  
  // State to control the visibility of the logout modal
  const [showLogoutModal, setShowLogoutModal] = useState(false);

  // Navigation items matching the SDD wireframes
  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: '🏠' },
    { name: 'My Schedule', path: '/schedule', icon: '📅' },
    { name: 'Menu', path: '/menu', icon: '🍽️' },
    { name: 'Billing', path: '/billing', icon: '💳' },
    { name: 'Profile', path: '/profile', icon: '👤' },
    { name: 'Settings', path: '/settings', icon: '⚙️' },
  ];

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('userFirstName'); 
    navigate('/login');
  };

  return (
    <div className="flex h-screen bg-[#121212] font-sans text-white overflow-hidden relative">
      
      {/* Sidebar matching the wireframe */}
      <aside className="w-64 bg-[#121212] border-r border-gray-800 flex flex-col justify-between z-10">
        
        {/* Logo Section */}
        <div className="p-6">
          <h1 className="text-2xl font-black text-white tracking-tight">
            Goated <span className="text-[#00FF66]">Meals!</span>
          </h1>
          <p className="text-xs text-gray-500 mt-1">Premium Meal Service</p>
        </div>

        {/* Navigation Links */}
        <nav className="flex-1 px-4 py-4 space-y-2">
          {navItems.map((item) => {
            const isActive = location.pathname.includes(item.path);
            return (
              <Link
                key={item.name}
                to={item.path}
                className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
                  isActive 
                    ? 'bg-[#00FF66] text-black font-bold shadow-[0_0_15px_rgba(0,255,102,0.15)]' 
                    : 'text-gray-400 hover:text-white hover:bg-[#1E1E1E]'
                }`}
              >
                <span className="text-lg">{item.icon}</span>
                <span className="text-sm">{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* Logout Button (Bottom of Sidebar) */}
        <div className="p-4">
          <button 
            onClick={() => setShowLogoutModal(true)}
            className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-gray-400 hover:text-red-400 hover:bg-red-500/10 transition-all duration-200 border border-transparent hover:border-red-500/30"
          >
            <span className="text-lg">🚪</span>
            <span className="text-sm font-bold">Logout</span>
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto bg-[#121212]">
        {children}
      </main>

      {/* --- LOGOUT CONFIRMATION MODAL --- */}
      {showLogoutModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
          {/* Modal Box */}
          <div className="bg-[#1E1E1E] border border-gray-800 p-8 rounded-2xl shadow-2xl max-w-sm w-full mx-4 transform transition-all">
            <div className="text-center mb-6">
              <div className="w-16 h-16 bg-red-500/10 rounded-full flex items-center justify-center mx-auto mb-4 border border-red-500/20">
                <span className="text-2xl">⚠️</span>
              </div>
              <h3 className="text-2xl font-bold text-white mb-2">Sign Out</h3>
              <p className="text-gray-400 text-sm">
                Are you sure you want to log out of your Goated Meals account? You will need to log back in to manage your schedule.
              </p>
            </div>
            
            <div className="flex gap-4">
              <button
                onClick={() => setShowLogoutModal(false)}
                className="flex-1 bg-transparent border border-gray-600 text-white font-bold py-3 px-4 rounded-xl hover:bg-gray-800 hover:border-gray-500 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleLogout}
                className="flex-1 bg-red-500 text-white font-bold py-3 px-4 rounded-xl hover:bg-red-600 transition-colors shadow-[0_0_15px_rgba(239,68,68,0.2)]"
              >
                Log Out
              </button>
            </div>
          </div>
        </div>
      )}
      
    </div>
  );
}