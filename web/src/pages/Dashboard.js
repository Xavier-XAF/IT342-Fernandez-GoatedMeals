import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const [subscription, setSubscription] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Mock data for the upcoming menu section based on the SDD wireframe
  const upcomingMeals = [
    {
      id: 1,
      name: "Grilled Salmon with Vegetables",
      description: "Fresh Atlantic salmon with seasonal roasted vegetables and lemon butter sauce.",
      category: "Seafood",
      rating: "4.8",
      imageUrl: "https://images.unsplash.com/photo-1467003909585-2f8a72700288?auto=format&fit=crop&q=80&w=400"
    },
    {
      id: 2,
      name: "Chicken Teriyaki Bowl",
      description: "Tender chicken glazed in teriyaki sauce with jasmine rice and steamed vegetables.",
      category: "Asian",
      rating: "4.9",
      imageUrl: "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&q=80&w=400"
    },
    {
      id: 3,
      name: "Mediterranean Pasta",
      description: "Whole wheat pasta with cherry tomatoes, olives, feta cheese, and fresh pesto.",
      category: "Vegan",
      rating: "4.7",
      imageUrl: "https://images.unsplash.com/photo-1621996316585-927494fdd3a0?auto=format&fit=crop&q=80&w=400"
    }
  ];

  useEffect(() => {
    // Simulating API fetch for subscription data
    const fetchSubscriptionData = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          // If using actual auth, uncomment this redirect
          // navigate('/login'); 
        }

        // Simulating the response from /api/v1/auth/me
        setTimeout(() => {
          setSubscription({
            activePlan: "Premium Weekly Plan",
            availableCredits: 5,
            nextRenewal: "March 9, 2026",
            planType: "Premium Weekly",
            mealsPerWeek: 7,
            deliveryDay: "Monday"
          });
          setLoading(false);
        }, 800);

      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setLoading(false);
      }
    };

    fetchSubscriptionData();
  }, [navigate]);

  if (loading) return <div className="text-white p-8">Loading your meal plan...</div>;

  return (
    <div className="bg-[#121212] min-h-screen text-white p-8 font-sans">
      
      {/* Top Navigation Bar matching Wireframe */}
      <div className="flex justify-between items-start mb-8">
        <div>
          <h1 className="text-2xl font-bold mb-1">Dashboard</h1>
          <p className="text-gray-400 text-sm">Welcome back! Manage your meal plan.</p>
        </div>
        <div className="flex items-center gap-4">
          <div className="relative">
            <span className="absolute left-3 top-2.5 text-gray-400 text-sm">🔍</span>
            <input 
              type="text" 
              placeholder="Search meals..." 
              className="bg-[#1E1E1E] border border-gray-800 rounded-full py-2 pl-9 pr-4 text-sm focus:outline-none focus:border-[#00FF66] text-white w-64"
            />
          </div>
          <button className="bg-[#1E1E1E] p-2 rounded-full border border-gray-800 hover:text-[#00FF66] transition">
            🔔
          </button>
        </div>
      </div>

      {/* Subscription Status Box matching Wireframe */}
      <div className="bg-[#1E1E1E] rounded-2xl p-8 border border-gray-800 mb-10">
        <div className="flex justify-between items-start border-b border-gray-800 pb-6 mb-6">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="w-4 h-1 bg-[#00FF66] rounded-full"></span>
              <h2 className="text-sm text-gray-300 font-medium tracking-wide">Subscription Status</h2>
            </div>
            <h3 className="text-3xl font-bold text-white mb-2">{subscription.activePlan}</h3>
            <p className="text-sm text-gray-400 flex items-center gap-2">
              📅 Next renewal: {subscription.nextRenewal}
            </p>
          </div>
          
          <div className="bg-[#00FF66] text-black rounded-xl px-8 py-4 text-center shadow-[0_0_20px_rgba(0,255,102,0.15)]">
            <p className="text-xs font-bold uppercase tracking-wider opacity-80 mb-1">Available Credits</p>
            <p className="text-5xl font-black mb-1">{subscription.availableCredits}</p>
            <p className="text-xs font-medium opacity-80">Meals Remaining</p>
          </div>
        </div>

        {/* Plan Details Grid */}
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div>
            <p className="text-gray-500 mb-1">Plan Type</p>
            <p className="font-semibold text-white">{subscription.planType}</p>
          </div>
          <div>
            <p className="text-gray-500 mb-1">Meals per Week</p>
            <p className="font-semibold text-white">{subscription.mealsPerWeek} Meals</p>
          </div>
          <div>
            <p className="text-gray-500 mb-1">Delivery Day</p>
            <p className="font-semibold text-white">{subscription.deliveryDay}</p>
          </div>
        </div>
      </div>

      {/* Upcoming Weekly Menu Section matching Wireframe */}
      <div>
        <div className="flex justify-between items-end mb-4">
          <div>
            <h2 className="text-lg font-bold text-white">Upcoming Weekly Menu</h2>
            <p className="text-sm text-gray-400">Select your meals for the upcoming week</p>
          </div>
          <button className="text-[#00FF66] text-sm hover:underline font-medium">
            View All Menus →
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {upcomingMeals.map(meal => (
            <div key={meal.id} className="bg-[#1E1E1E] rounded-xl border border-gray-800 overflow-hidden hover:border-gray-600 transition cursor-pointer">
              <div className="relative h-48">
                <img src={meal.imageUrl} alt={meal.name} className="w-full h-full object-cover" />
                <span className="absolute top-3 left-3 bg-[#00FF66] text-black text-xs font-bold px-2 py-1 rounded">
                  {meal.category}
                </span>
                <span className="absolute top-3 right-3 bg-black/70 text-[#00FF66] text-xs font-bold px-2 py-1 rounded flex items-center gap-1">
                  ⭐ {meal.rating}
                </span>
              </div>
              <div className="p-5">
                <h3 className="font-bold text-white mb-2 text-lg">{meal.name}</h3>
                <p className="text-gray-400 text-sm line-clamp-2">{meal.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}