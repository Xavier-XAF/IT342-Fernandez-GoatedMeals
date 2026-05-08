import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

export default function MainLayout({ children }) {
  const location = useLocation();
  const navigate = useNavigate();

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
    <div className="flex h-screen bg-[#121212] font-sans text-white overflow-hidden">
      
      {/* Sidebar matching the wireframe */}
      <aside className="w-64 bg-[#121212] border-r border-gray-800 flex flex-col justify-between">
        
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

        {/* User Profile Snippet (Bottom of Sidebar) */}
        <div className="p-4">
          <div className="bg-[#1E1E1E] border border-gray-800 rounded-xl p-3 flex items-center gap-3 cursor-pointer hover:border-gray-600 transition" onClick={handleLogout} title="Click to Logout">
            <div className="w-10 h-10 rounded-full bg-[#00FF66] text-black flex items-center justify-center font-bold text-sm">
              JD
            </div>
            <div className="overflow-hidden">
              <p className="text-sm font-bold text-white truncate">John Doe</p>
              <p className="text-xs text-gray-500 truncate">john.doe@example.com</p>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto bg-[#121212]">
        {children}
      </main>
      
    </div>
  );
}