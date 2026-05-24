import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const [subscription, setSubscription] = useState(null);
  const [meals, setMeals] = useState([]); // New state to hold real database meals
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          navigate('/login'); 
          return;
        }

        const headers = { Authorization: `Bearer ${token}` };

        // Fetch BOTH subscription status and the real meal menu at the same time
        const [subResponse, mealsResponse] = await Promise.all([
          axios.get('http://localhost:8080/api/v1/subscriptions/me', { headers }),
          axios.get('http://localhost:8080/api/v1/meals', { headers })
        ]);

        const subData = subResponse.data;

        // 1. Process Subscription Data
        if (subData.hasSubscription) {
          const readablePlan = subData.planTier ? subData.planTier.replace('_', ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()) : 'Premium Plan';
          
          setSubscription({
            hasSubscription: true,
            activePlan: `${readablePlan} Plan`,
            availableCredits: subData.availableCredits,
            nextRenewal: "Next Billing Cycle", 
            planType: readablePlan,
            mealsPerWeek: subData.totalCreditsAllowed || 7, 
            deliveryDay: "Monday"
          });
        } else {
          setSubscription({ hasSubscription: false });
        }
        
        // 2. Process Real Meal Data
        // If the database returns meals, save them. Only take the first 3 for the dashboard preview.
        if (mealsResponse.data && mealsResponse.data.length > 0) {
          setMeals(mealsResponse.data.slice(0, 3));
        }

        setLoading(false);

      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [navigate]);

  if (loading) return <div className="text-white bg-[#121212] min-h-screen p-8">Loading your dashboard...</div>;

  return (
    <div className="bg-[#121212] min-h-screen text-white p-8 font-sans">
      
      {/* Top Navigation Bar */}
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

      {/* DYNAMIC Subscription Status Box */}
      {subscription?.hasSubscription ? (
        <div className="bg-[#1E1E1E] rounded-2xl p-8 border border-gray-800 mb-10 relative overflow-hidden">
          <div className="absolute top-0 right-0 w-48 h-48 bg-[#00FF66] opacity-5 rounded-bl-full blur-3xl pointer-events-none"></div>
          
          <div className="flex justify-between items-start border-b border-gray-800 pb-6 mb-6 relative z-10">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <span className="w-4 h-1 bg-[#00FF66] rounded-full"></span>
                <h2 className="text-sm text-gray-300 font-medium tracking-wide">Subscription Status</h2>
                <span className="bg-[#00FF66]/20 text-[#00FF66] text-xs font-bold px-2 py-0.5 rounded uppercase tracking-wide ml-2">Active</span>
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

          <div className="grid grid-cols-3 gap-4 text-sm relative z-10">
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
      ) : (
        <div className="bg-[#1E1E1E] rounded-2xl p-8 border border-gray-800 mb-10 text-center">
          <div className="text-5xl mb-4">🍽️</div>
          <h3 className="text-2xl font-bold text-white mb-2">No Active Meal Plan</h3>
          <p className="text-gray-400 mb-6 max-w-md mx-auto">You currently don't have an active subscription. Choose a plan to unlock the premium menu and start scheduling your chef-curated meals!</p>
          <button 
            onClick={() => navigate('/billing')}
            className="bg-[#00FF66] text-black font-bold py-3 px-8 rounded-xl hover:bg-green-500 transition-all shadow-[0_0_15px_rgba(0,255,102,0.15)]"
          >
            View Subscription Plans
          </button>
        </div>
      )}

      {/* REAL Database Weekly Menu Section */}
      <div>
        <div className="flex justify-between items-end mb-4">
          <div>
            <h2 className="text-lg font-bold text-white">Upcoming Weekly Menu</h2>
            <p className="text-sm text-gray-400">Select your meals for the upcoming week</p>
          </div>
          <button 
            onClick={() => navigate('/menu')}
            className="text-[#00FF66] text-sm hover:underline font-medium"
          >
            View All Menus →
          </button>
        </div>

        {meals.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {meals.map(meal => (
              <div key={meal.id} className="bg-[#1E1E1E] rounded-xl border border-gray-800 overflow-hidden hover:border-gray-600 transition cursor-pointer flex flex-col">
                <div className="relative h-48 bg-gray-900">
                  {/* Fallback image logic just in case an admin forgets to add a photo */}
                  <img 
                    src={meal.imageUrl || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&q=80&w=400"} 
                    alt={meal.name} 
                    className="w-full h-full object-cover" 
                    onError={(e) => { e.target.src = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&q=80&w=400" }}
                  />
                  
                  {/* Category Tag (If your meal model has category, otherwise defaults to "Premium") */}
                  <span className="absolute top-3 left-3 bg-[#00FF66] text-black text-xs font-bold px-2 py-1 rounded">
                    {meal.category || 'Premium'}
                  </span>
                  
                  <span className="absolute top-3 right-3 bg-black/70 text-[#00FF66] text-xs font-bold px-2 py-1 rounded flex items-center gap-1">
                    ⭐ {meal.rating || '4.9'}
                  </span>
                </div>
                <div className="p-5 flex-1 flex flex-col">
                  <h3 className="font-bold text-white mb-2 text-lg">{meal.name}</h3>
                  <p className="text-gray-400 text-sm line-clamp-2 flex-1">{meal.description}</p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-[#1E1E1E] rounded-xl border border-gray-800 p-8 text-center text-gray-400">
            <p>The chefs are currently preparing the new menu. Check back soon!</p>
          </div>
        )}
      </div>
    </div>
  );
}