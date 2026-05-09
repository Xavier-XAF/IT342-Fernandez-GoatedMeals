import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function Schedule() {
  const [scheduledMeals, setScheduledMeals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ text: '', type: '' });
  const [isProcessingId, setIsProcessingId] = useState(null); // Tracks which button is loading
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSchedule = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) return navigate('/login');

        const response = await axios.get('http://localhost:8080/api/v1/schedules/my-schedule', {
          headers: { Authorization: `Bearer ${token}` }
        });

        const sortedMeals = response.data.sort((a, b) => new Date(a.deliveryDay) - new Date(b.deliveryDay));
        setScheduledMeals(sortedMeals);
        setLoading(false);

      } catch (error) {
        console.error("Error fetching schedule:", error);
        setLoading(false);
      }
    };

    fetchSchedule();
  }, [navigate]);

  const handleCancelMeal = async (scheduleId) => {
    // Standard browser confirmation prompt to prevent accidental clicks
    if (!window.confirm("Are you sure you want to cancel this meal? 1 credit will be instantly refunded to your account.")) {
      return; 
    }

    setIsProcessingId(scheduleId);
    setMessage({ text: '', type: '' });

    try {
      const token = localStorage.getItem('accessToken');
      
      // Call the new DELETE endpoint
      await axios.delete(`http://localhost:8080/api/v1/schedules/${scheduleId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      // Filter out the deleted meal from the UI instantly
      setScheduledMeals(prevMeals => prevMeals.filter(meal => meal.id !== scheduleId));
      
      setMessage({ text: "Meal cancelled successfully! 1 credit refunded.", type: 'success' });
      
      // Clear toast after 3 seconds
      setTimeout(() => setMessage({ text: '', type: '' }), 3000);

    } catch (error) {
      console.error("Error cancelling meal:", error);
      setMessage({ text: "Failed to cancel meal. Please try again.", type: 'error' });
      setTimeout(() => setMessage({ text: '', type: '' }), 4000);
    } finally {
      setIsProcessingId(null);
    }
  };

  if (loading) return <div className="text-white bg-[#121212] min-h-screen p-8">Loading your schedule...</div>;

  return (
    <div className="bg-[#121212] min-h-screen text-white p-8 font-sans relative">
      
      {/* Floating Notification Toast */}
      {message.text && (
        <div className={`fixed top-8 right-8 z-50 px-6 py-4 rounded-xl font-bold shadow-2xl transition-all ${
          message.type === 'success' ? 'bg-[#00FF66] text-black' : 'bg-red-500 text-white'
        }`}>
          {message.text}
        </div>
      )}

      {/* Header */}
      <div className="mb-10 border-b border-gray-800 pb-6">
        <h1 className="text-3xl font-bold mb-2">My Schedule</h1>
        <p className="text-gray-400">View and manage your upcoming chef-curated deliveries.</p>
      </div>

      {scheduledMeals.length === 0 ? (
        <div className="bg-[#1E1E1E] border border-gray-800 rounded-2xl p-12 text-center max-w-2xl mx-auto mt-10 shadow-lg">
          <div className="text-6xl mb-6">📅</div>
          <h2 className="text-2xl font-bold text-white mb-4">Your calendar is empty!</h2>
          <p className="text-gray-400 mb-8">You haven't scheduled any meals yet. Head over to the Menu to use your available credits.</p>
          <button 
            onClick={() => navigate('/menu')}
            className="bg-[#00FF66] text-black font-bold py-3 px-8 rounded-xl hover:bg-green-500 transition-all shadow-[0_0_15px_rgba(0,255,102,0.15)]"
          >
            Browse Premium Menu
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          {scheduledMeals.map(schedule => (
            <div key={schedule.id} className="bg-[#1E1E1E] rounded-2xl border border-gray-800 overflow-hidden flex flex-col hover:border-[#00FF66]/50 transition-colors shadow-lg relative">
              
              {/* Delivery Date Ribbon */}
              <div className="bg-[#00FF66] text-black font-black text-center py-2 text-sm tracking-widest uppercase">
                {new Date(schedule.deliveryDay).toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric' })}
              </div>

              {/* Meal Image */}
              <div className="h-40 bg-gray-900 relative">
                <img 
                  src={schedule.meal.imageUrl || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c"} 
                  alt={schedule.meal.name} 
                  className="w-full h-full object-cover" 
                />
                <span className="absolute bottom-3 left-3 bg-black/80 text-[#00FF66] text-xs font-bold px-3 py-1 rounded-full shadow-lg border border-[#00FF66]/30">
                  {schedule.status}
                </span>
              </div>

              {/* Details & Action Button */}
              <div className="p-5 flex-1 flex flex-col">
                <h3 className="font-bold text-white text-lg mb-4">{schedule.meal.name}</h3>
                
                <div className="space-y-3 bg-[#121212] p-4 rounded-xl border border-gray-800 mb-4">
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-gray-500 font-semibold uppercase tracking-wider text-[10px]">Method</span>
                    <span className="text-white font-medium">{schedule.deliveryMethod}</span>
                  </div>
                  
                  {schedule.deliveryMethod === 'Delivery' && (
                    <div className="flex justify-between items-center text-sm border-t border-gray-800 pt-3">
                      <span className="text-gray-500 font-semibold uppercase tracking-wider text-[10px]">Address</span>
                      <span className="text-white font-medium truncate max-w-[150px]" title={schedule.deliveryAddress}>
                        {schedule.deliveryAddress}
                      </span>
                    </div>
                  )}
                </div>

                {/* Cancel Button */}
                <button 
                  onClick={() => handleCancelMeal(schedule.id)}
                  disabled={isProcessingId === schedule.id}
                  className="mt-auto w-full border border-red-500/30 text-red-400 font-bold py-3 rounded-xl hover:bg-red-500/10 hover:border-red-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isProcessingId === schedule.id ? 'Refunding...' : 'Cancel & Refund Credit'}
                </button>

              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}