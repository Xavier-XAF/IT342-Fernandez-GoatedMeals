import React, { useState, useEffect } from 'react';
import apiClient from '../core/api/axiosConfig'; // Using your secure interceptor
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const [subscription, setSubscription] = useState(null);
  const [scheduledMeals, setScheduledMeals] = useState([]); // Store actual booked deliveries
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchDashboardData = async () => {
    try {
      // Fetch BOTH subscription status and the user's personal schedule
      const [subResponse, scheduleResponse] = await Promise.all([
        apiClient.get('/subscriptions/me'),
        apiClient.get('/schedules/my-schedule')
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
      
      // 2. Process Personal Schedule Data
      if (scheduleResponse.data && Array.isArray(scheduleResponse.data)) {
        // Sort the array: push 'DELIVERED' to the bottom
        const sortedOrders = scheduleResponse.data.sort((a, b) => {
            if (a.status === 'DELIVERED' && b.status !== 'DELIVERED') return 1;
            if (a.status !== 'DELIVERED' && b.status === 'DELIVERED') return -1;
            return b.id - a.id; // Secondary sort: newest orders first
        });
        setScheduledMeals(sortedOrders);
      }

    } catch (err) {
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, [navigate]);

  // NEW: Cancel a meal delivery and refund a credit
  const handleCancelMeal = async (scheduleId) => {
    if (!window.confirm("Are you sure you want to cancel this delivery? Your credit will be refunded.")) return;
    
    try {
      const response = await apiClient.delete(`/schedules/${scheduleId}`);
      alert(response.data.message);
      fetchDashboardData(); // Refresh the dashboard to instantly update the credits and remove the meal
    } catch (error) {
      console.error("Failed to cancel meal:", error);
      alert("Failed to cancel meal. It might already be preparing!");
    }
  };

  // Helper for status colors
  const getStatusBadge = (status) => {
    switch (status) {
        case 'SCHEDULED': return <span className="bg-[#00FF66]/20 text-[#00FF66] text-xs font-bold px-2 py-1 rounded">SCHEDULED</span>;
        case 'PREPARING': return <span className="bg-yellow-500/20 text-yellow-500 text-xs font-bold px-2 py-1 rounded">PREPARING</span>;
        case 'DELIVERING': return <span className="bg-purple-500/20 text-purple-500 text-xs font-bold px-2 py-1 rounded">DELIVERING</span>;
        case 'DELIVERED': return <span className="bg-blue-500/20 text-blue-500 text-xs font-bold px-2 py-1 rounded">DELIVERED</span>;
        default: return <span className="bg-gray-500/20 text-gray-400 text-xs font-bold px-2 py-1 rounded">{status}</span>;
    }
  };

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

      {/* REAL User Schedule Section */}
      {subscription?.hasSubscription && (
        <div>
          <div className="flex justify-between items-end mb-4">
            <div>
              <h2 className="text-lg font-bold text-white">Your Scheduled Deliveries</h2>
              <p className="text-sm text-gray-400">Track and manage your upcoming meals</p>
            </div>
            <button 
              onClick={() => navigate('/menu')}
              className="bg-transparent border border-[#00FF66] text-[#00FF66] px-4 py-2 rounded-lg text-sm hover:bg-[#00FF66] hover:text-black transition font-medium"
            >
              + Book New Meal
            </button>
          </div>

          {scheduledMeals.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {scheduledMeals.map(schedule => (
                <div key={schedule.id} className="bg-[#1E1E1E] rounded-xl border border-gray-800 overflow-hidden flex flex-col">
                  <div className="relative h-40 bg-gray-900">
                    <img 
                      src={schedule.meal?.imageUrl || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&q=80&w=400"} 
                      alt={schedule.meal?.name} 
                      className="w-full h-full object-cover opacity-80" 
                    />
                    <div className="absolute top-3 left-3">
                        {getStatusBadge(schedule.status)}
                    </div>
                  </div>
                  
                  <div className="p-5 flex-1 flex flex-col">
                    <h3 className="font-bold text-white mb-1 text-lg">{schedule.meal?.name}</h3>
                    
                    <div className="mt-4 space-y-2 text-sm text-gray-400 flex-1">
                        <p className="flex justify-between"><span>Delivery Day:</span> <span className="text-white">{schedule.deliveryDay}</span></p>
                        <p className="flex justify-between"><span>Delivery Time:</span> <span className="text-white">{schedule.deliveryTime || 'N/A'}</span></p>
                        <p className="flex justify-between"><span>Method:</span> <span className="text-white">{schedule.deliveryMethod}</span></p>
                        <p className="flex justify-between"><span>Address:</span> <span className="text-white truncate max-w-[120px]">{schedule.deliveryAddress}</span></p>
                    </div>

                    {/* Only allow cancellation if it hasn't been prepped/delivered yet */}
                    {/* Only allow cancellation if it is STRICTLY in the SCHEDULED state */}
                    {schedule.status === 'SCHEDULED' && (
                        <button 
                            onClick={() => handleCancelMeal(schedule.id)}
                            className="mt-5 w-full bg-transparent border border-red-500/50 text-red-400 py-2 rounded text-sm hover:bg-red-500/10 transition"
                        >
                            Cancel & Refund Credit
                        </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="bg-[#1E1E1E] rounded-xl border border-gray-800 p-8 text-center flex flex-col items-center justify-center">
              <div className="text-4xl mb-3">📦</div>
              <h3 className="text-white font-bold text-lg mb-2">No Deliveries Scheduled</h3>
              <p className="text-gray-400 text-sm max-w-sm mb-4">You have {subscription.availableCredits} credits available! Head over to the menu to book your meals for the week.</p>
              <button 
                onClick={() => navigate('/menu')}
                className="text-[#00FF66] font-bold hover:underline"
              >
                Go to Menu →
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}