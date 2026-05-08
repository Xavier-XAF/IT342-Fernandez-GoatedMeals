import React, { useState } from 'react';
import axios from 'axios';
import { useSearchParams, useNavigate } from 'react-router-dom';

export default function Billing() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const paymentStatus = searchParams.get('status');

  if (paymentStatus === 'success') {
    return (
      <div className="p-8 font-sans flex flex-col items-center justify-center min-h-[70vh]">
        <div className="bg-[#1E1E1E] p-10 rounded-2xl border border-[#00FF66] text-center max-w-lg shadow-[0_0_30px_rgba(0,255,102,0.15)]">
          <div className="text-[#00FF66] text-6xl mb-6">🎉</div>
          <h2 className="text-3xl font-black text-white mb-4">Payment Successful!</h2>
          <p className="text-gray-400 mb-8">
            Welcome to the Premium tier! Your Goated Meals subscription is now active. Let's get your menu set up.
          </p>
          <button 
            onClick={() => navigate('/menu')}
            className="bg-[#00FF66] text-black font-bold py-3 px-8 rounded-xl hover:bg-green-500 transition-all"
          >
            Go to Menu
          </button>
        </div>
      </div>
    );
  }

  const handleSubscribe = async (planTier, amount) => {
    setLoading(true);
    setError('');
    
    try {
      // Grab the user's JWT token (adjust 'accessToken' if you saved it under a different name)
      const token = localStorage.getItem('accessToken'); 
      
      // Make the call to your Spring Boot backend
      const response = await axios.post(
        'http://localhost:8080/api/v1/subscriptions/pay', 
        { planTier, amount },
        { 
          headers: token ? { Authorization: `Bearer ${token}` } : {} 
        }
      );

      // If Spring Boot successfully created the PayMongo link, redirect the user!
      if (response.data && response.data.data && response.data.data.checkoutUrl) {
        window.location.href = response.data.data.checkoutUrl;
      } else {
        setError('Checkout URL not received from server. Please check backend logs.');
      }
    } catch (err) {
      console.error('Payment initiation error:', err);
      setError('Failed to initiate payment. Make sure your Spring Boot server is running!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8 font-sans">
      <h1 className="text-3xl font-bold mb-2 text-white">Billing & Subscription</h1>
      <p className="text-gray-400 mb-8">Choose a meal plan that fits your lifestyle.</p>
      
      {error && (
        <div className="bg-red-900/20 border border-red-500/50 text-red-400 p-4 rounded-lg mb-6">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl">
        {/* Premium Weekly Plan Card */}
        <div className="bg-[#1E1E1E] p-8 rounded-2xl border border-gray-800 flex flex-col justify-between hover:border-gray-600 transition-colors">
          <div>
            <div className="flex justify-between items-start mb-4">
              <h2 className="text-2xl font-bold text-white">Premium Weekly</h2>
              <span className="bg-[#00FF66]/20 text-[#00FF66] text-xs font-bold px-3 py-1 rounded-full uppercase tracking-wider">
                Popular
              </span>
            </div>
            <p className="text-gray-400 mb-6">7 Chef-curated meals per week delivered directly to your door.</p>
            <div className="mb-8">
              <span className="text-4xl font-black text-white">₱1,490</span>
              <span className="text-gray-500 ml-2">/ week</span>
            </div>
            
            <ul className="space-y-3 mb-8 text-sm text-gray-300">
              <li className="flex items-center gap-2">✅ <span className="opacity-80">Full menu access</span></li>
              <li className="flex items-center gap-2">✅ <span className="opacity-80">Free delivery</span></li>
              <li className="flex items-center gap-2">✅ <span className="opacity-80">Cancel anytime</span></li>
            </ul>
          </div>
          
          <button 
            onClick={() => handleSubscribe('PREMIUM_WEEKLY', 1490)} // 149000 cents = ₱1,490.00
            disabled={loading}
            className="w-full bg-[#00FF66] text-black font-bold py-4 rounded-xl hover:bg-green-500 transition-all disabled:opacity-50 shadow-[0_0_15px_rgba(0,255,102,0.15)] hover:shadow-[0_0_25px_rgba(0,255,102,0.3)]"
          >
            {loading ? 'Generating Secure Checkout...' : 'Subscribe via PayMongo'}
          </button>
        </div>
      </div>
    </div>
  );
}