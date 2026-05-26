import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function Menu() {
  const [meals, setMeals] = useState([]);
  const [availableCredits, setAvailableCredits] = useState(0);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Modal & Booking State
  const [selectedMeal, setSelectedMeal] = useState(null);
  const [deliveryMethod, setDeliveryMethod] = useState('Delivery');
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [selectedDate, setSelectedDate] = useState('');
  
  // NEW: Added state for Delivery Time
  const [deliveryTime, setDeliveryTime] = useState('09:00 AM');
  
  const [isBooking, setIsBooking] = useState(false);
  const [message, setMessage] = useState({ text: '', type: '' });

  const today = new Date().toISOString().split('T')[0];

  // NEW: Helper function to generate time slots from 9AM to 10PM
  const generateTimeSlots = () => {
    const slots = [];
    for (let i = 9; i <= 22; i++) {
      const hour = i > 12 ? i - 12 : i;
      const ampm = i >= 12 ? 'PM' : 'AM';
      slots.push(`${hour}:00 ${ampm}`);
      if (i !== 22) slots.push(`${hour}:30 ${ampm}`); // Don't add 10:30 PM
    }
    return slots;
  };

  useEffect(() => {
    const fetchMenuAndCredits = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) return navigate('/login');

        const headers = { Authorization: `Bearer ${token}` };
        const [mealsResponse, subResponse] = await Promise.all([
          axios.get('http://localhost:8080/api/v1/meals', { headers }),
          axios.get('http://localhost:8080/api/v1/subscriptions/me', { headers })
        ]);

        setMeals(mealsResponse.data);
        if (subResponse.data.hasSubscription) {
          setAvailableCredits(subResponse.data.availableCredits);
        }
        setLoading(false);
      } catch (error) {
        console.error("Error fetching menu data:", error);
        setLoading(false);
      }
    };
    fetchMenuAndCredits();
  }, [navigate]);

  const handleBookMeal = async () => {
    if (!selectedDate) {
      alert("Please select a date for your meal.");
      return;
    }
    if (deliveryMethod === 'Delivery' && !deliveryAddress.trim()) {
      alert("Please enter a delivery address.");
      return;
    }

    setIsBooking(true);
    setMessage({ text: '', type: '' });

    try {
      const token = localStorage.getItem('accessToken');
      const response = await axios.post(
        'http://localhost:8080/api/v1/schedules/book',
        { 
          mealId: selectedMeal.id, 
          deliveryDate: selectedDate,
          deliveryTime: deliveryTime, // NEW: Pushing the time to Spring Boot
          deliveryMethod: deliveryMethod,
          deliveryAddress: deliveryMethod === 'Pickup' ? 'Goated Meals Ph, Lahug, Cebu City' : deliveryAddress
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setAvailableCredits(response.data.remainingCredits);
      setMessage({ text: "Meal successfully scheduled!", type: 'success' });
      
      setTimeout(() => {
        setMessage({ text: '', type: '' });
        setSelectedMeal(null);
        setSelectedDate('');
        setDeliveryTime('09:00 AM'); // Reset time
        setDeliveryAddress('');
      }, 2000);

    } catch (error) {
      const errorMsg = error.response?.data?.error || "Failed to schedule meal. Please try again.";
      setMessage({ text: errorMsg, type: 'error' });
      setTimeout(() => setMessage({ text: '', type: '' }), 4000);
    } finally {
      setIsBooking(false);
    }
  };

  if (loading) return <div className="text-white bg-[#121212] min-h-screen p-8">Loading the chef's menu...</div>;

  return (
    <div className="bg-[#121212] min-h-screen text-white p-8 font-sans relative">
      
      {/* Header & Credit Counter */}
      <div className="flex justify-between items-end mb-8 border-b border-gray-800 pb-6">
        <div>
          <h1 className="text-3xl font-bold mb-2">Premium Menu</h1>
          <p className="text-gray-400">Select your meals for the upcoming week.</p>
        </div>
        <div className="bg-[#1E1E1E] border border-[#00FF66]/30 px-6 py-3 rounded-xl flex items-center gap-4">
          <div>
            <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Available Credits</p>
            <p className="text-2xl font-black text-[#00FF66] leading-none">{availableCredits}</p>
          </div>
          <div className="text-3xl">🍽️</div>
        </div>
      </div>

      {/* Floating Notification Toast */}
      {message.text && (
        <div className={`fixed top-8 right-8 z-50 px-6 py-4 rounded-xl font-bold shadow-2xl transition-all ${message.type === 'success' ? 'bg-[#00FF66] text-black' : 'bg-red-500 text-white'}`}>
          {message.text}
        </div>
      )}

      {/* Meals Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {meals.map(meal => (
          <div 
            key={meal.id} 
            onClick={() => setSelectedMeal(meal)}
            className="bg-[#1E1E1E] rounded-2xl border border-gray-800 overflow-hidden flex flex-col hover:border-[#00FF66] transition-colors cursor-pointer group relative"
          >
            <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity z-10 flex items-center justify-center">
              <span className="bg-[#00FF66] text-black font-bold px-6 py-2 rounded-full transform translate-y-4 group-hover:translate-y-0 transition-all">Select Meal</span>
            </div>

            <div className="relative h-56 bg-gray-900">
              <img src={meal.imageUrl || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c"} alt={meal.name} className="w-full h-full object-cover" />
              <span className="absolute top-4 left-4 bg-[#00FF66] text-black text-xs font-bold px-3 py-1 rounded-full shadow-lg">{meal.category || 'Premium'}</span>
            </div>
            <div className="p-6 flex-1 flex flex-col">
              <h3 className="font-bold text-white mb-2 text-xl">{meal.name}</h3>
              <p className="text-gray-400 text-sm mb-2 flex-1">{meal.description}</p>
            </div>
          </div>
        ))}
      </div>

      {/* --- MODAL --- */}
      {selectedMeal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
          <div className="bg-[#121212] border border-gray-800 rounded-2xl max-w-4xl w-full flex flex-col md:flex-row overflow-hidden shadow-2xl relative">
            
            <button 
              onClick={() => setSelectedMeal(null)}
              className="absolute top-4 right-4 w-8 h-8 bg-gray-800 rounded-full flex items-center justify-center text-gray-400 hover:text-white hover:bg-gray-700 z-20 transition"
            >
              ✕
            </button>

            <div className="md:w-1/2 relative h-64 md:h-auto bg-gray-900">
              <img src={selectedMeal.imageUrl || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c"} alt={selectedMeal.name} className="w-full h-full object-cover" />
              <div className="absolute bottom-4 left-4 flex gap-2">
                <span className="bg-[#00FF66] text-black text-xs font-bold px-3 py-1 rounded-full shadow-lg">{selectedMeal.category || 'Premium'}</span>
                <span className="bg-black/80 text-[#00FF66] text-xs font-bold px-3 py-1 rounded-full shadow-lg">⭐ {selectedMeal.rating || '4.8'}</span>
              </div>
            </div>

            <div className="md:w-1/2 p-8 overflow-y-auto max-h-[90vh]">
              <h2 className="text-2xl font-bold text-white mb-2">{selectedMeal.name}</h2>
              <p className="text-gray-400 text-sm mb-6">{selectedMeal.description}</p>

              <div className="grid grid-cols-3 gap-3 mb-8">
                <div className="bg-[#1E1E1E] rounded-xl p-3 text-center border border-gray-800">
                  <span className="text-orange-500 mb-1 block">🔥</span>
                  <p className="text-white font-bold text-lg leading-tight">{selectedMeal.calories || '450'}</p>
                  <p className="text-[10px] text-gray-500 font-bold uppercase tracking-wider">Calories</p>
                </div>
                <div className="bg-[#1E1E1E] rounded-xl p-3 text-center border border-gray-800">
                  <span className="text-green-500 mb-1 block">🥩</span>
                  <p className="text-white font-bold text-lg leading-tight">{selectedMeal.protein || '35g'}</p>
                  <p className="text-[10px] text-gray-500 font-bold uppercase tracking-wider">Protein</p>
                </div>
                <div className="bg-[#1E1E1E] rounded-xl p-3 text-center border border-gray-800">
                  <span className="text-blue-500 mb-1 block">⏱️</span>
                  <p className="text-white font-bold text-lg leading-tight">{selectedMeal.prepTime || '25m'}</p>
                  <p className="text-[10px] text-gray-500 font-bold uppercase tracking-wider">Prep Time</p>
                </div>
              </div>

              <div className="mb-6">
                <h3 className="text-sm font-bold text-white mb-3 flex items-center gap-2">🚚 Delivery Method</h3>
                <div className="flex gap-4">
                  <button 
                    onClick={() => setDeliveryMethod('Delivery')}
                    className={`flex-1 py-3 rounded-xl border font-bold text-sm transition-all ${deliveryMethod === 'Delivery' ? 'border-[#00FF66] text-[#00FF66] bg-[#00FF66]/10' : 'border-gray-800 text-gray-400 hover:border-gray-600'}`}
                  >
                    Delivery
                  </button>
                  <button 
                    onClick={() => setDeliveryMethod('Pickup')}
                    className={`flex-1 py-3 rounded-xl border font-bold text-sm transition-all ${deliveryMethod === 'Pickup' ? 'border-[#00FF66] text-[#00FF66] bg-[#00FF66]/10' : 'border-gray-800 text-gray-400 hover:border-gray-600'}`}
                  >
                    Pickup
                  </button>
                </div>
              </div>

              {deliveryMethod === 'Delivery' && (
                <div className="mb-6">
                  <p className="text-xs text-gray-500 mb-2 font-bold uppercase tracking-wider">Delivery Address</p>
                  <div className="relative">
                    <span className="absolute left-4 top-3.5 text-gray-500">📍</span>
                    <input 
                      type="text" 
                      placeholder="Enter delivery address" 
                      value={deliveryAddress}
                      onChange={(e) => setDeliveryAddress(e.target.value)}
                      className="w-full bg-[#1E1E1E] border border-gray-800 text-white rounded-xl py-3 pl-10 pr-4 text-sm focus:outline-none focus:border-[#00FF66] transition"
                    />
                  </div>
                </div>
              )}

              {/* NEW: Date & Time Selection Split Row */}
              <div className="mb-8 flex gap-4">
                <div className="flex-1">
                  <h3 className="text-sm font-bold text-white mb-3 flex items-center gap-2">📅 Date</h3>
                  <input 
                    type="date" 
                    min={today} 
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    required
                    className="w-full bg-[#1E1E1E] border border-gray-800 text-white rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[#00FF66] transition cursor-pointer [color-scheme:dark]"
                  />
                </div>
                
                <div className="flex-1">
                  <h3 className="text-sm font-bold text-white mb-3 flex items-center gap-2">⏰ Time</h3>
                  <select
                    value={deliveryTime}
                    onChange={(e) => setDeliveryTime(e.target.value)}
                    className="w-full bg-[#1E1E1E] border border-gray-800 text-white rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[#00FF66] transition cursor-pointer"
                  >
                    {generateTimeSlots().map(time => (
                      <option key={time} value={time} style={{ background: '#1E1E1E', color: 'white' }}>
                        {time}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <button 
                onClick={handleBookMeal}
                disabled={availableCredits <= 0 || isBooking}
                className="w-full bg-[#00FF66] text-black font-bold py-4 rounded-xl hover:bg-green-500 transition-all shadow-[0_0_15px_rgba(0,255,102,0.15)] hover:shadow-[0_0_25px_rgba(0,255,102,0.3)] disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isBooking ? 'Processing...' : availableCredits <= 0 ? 'No Credits Available' : 'Confirm Schedule (1 Credit)'}
              </button>

            </div>
          </div>
        </div>
      )}
    </div>
  );
}