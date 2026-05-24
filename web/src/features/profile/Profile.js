import React, { useState } from 'react';

const Profile = ({ user }) => {
    const [activeTab, setActiveTab] = useState('details');
    const [loading, setLoading] = useState(false);
    
    // Controlled form state
    const [profileData, setProfileData] = useState({
        phone: '',
        defaultAddress: ''
    });

    const handleSave = (e) => {
        e.preventDefault();
        setLoading(true);
        setTimeout(() => {
            alert('Account changes successfully updated!');
            setLoading(false);
        }, 800);
    };

    return (
        <div className="p-8 max-w-6xl mx-auto text-gray-300">
            <h1 className="text-3xl font-black mb-8 text-white tracking-tight">Account Control Center</h1>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                {/* Left Section: Account Snapshot */}
                <div className="bg-[#1E1E1E] rounded-2xl shadow-xl p-6 h-fit border-t-4 border-[#00FF66]">
                    <h2 className="text-xl font-bold mb-4 text-white">Subscription Overview</h2>
                    <div className="flex items-center justify-between mb-4">
                        <span className="text-gray-400">Tier Status:</span>
                        <span className="px-3 py-1 bg-[#00FF66]/10 text-[#00FF66] rounded-full text-sm font-bold border border-[#00FF66]/20">
                            ACTIVE
                        </span>
                    </div>
                    <div className="flex items-center justify-between mb-4">
                        <span className="text-gray-400">Identity Role:</span>
                        <span className="text-sm text-gray-300 bg-[#121212] px-3 py-1 rounded-md font-mono border border-gray-800">
                            {user?.role || 'USER'}
                        </span>
                    </div>
                    <hr className="my-5 border-gray-800" />
                    <button className="w-full bg-[#121212] hover:bg-gray-800 text-white border border-gray-700 font-semibold py-3 rounded-xl transition-all duration-200 text-sm">
                        Verify via PayMongo Secure
                    </button>
                </div>

                {/* Right Section: Core Tabs */}
                <div className="md:col-span-2 bg-[#1E1E1E] rounded-2xl shadow-xl p-6">
                    <div className="flex border-b border-gray-800 mb-6 space-x-6">
                        <button 
                            className={`pb-3 px-2 font-bold transition-all duration-200 ${activeTab === 'details' ? 'border-b-2 border-[#00FF66] text-[#00FF66]' : 'text-gray-500 hover:text-gray-300'}`}
                            onClick={() => setActiveTab('details')}
                        >
                            Personal Profile
                        </button>
                        <button 
                            className={`pb-3 px-2 font-bold transition-all duration-200 ${activeTab === 'settings' ? 'border-b-2 border-[#00FF66] text-[#00FF66]' : 'text-gray-500 hover:text-gray-300'}`}
                            onClick={() => setActiveTab('settings')}
                        >
                            System Settings
                        </button>
                    </div>

                    {activeTab === 'details' ? (
                        <form onSubmit={handleSave} className="space-y-5">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                                <div>
                                    <label className="block text-xs font-bold text-gray-400 uppercase mb-2 tracking-wider">First Name</label>
                                    <input type="text" className="w-full p-3 rounded-xl bg-[#121212] border border-gray-800 text-gray-500 outline-none cursor-not-allowed" value={user?.firstName || 'Xavier'} readOnly />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold text-gray-400 uppercase mb-2 tracking-wider">Last Name</label>
                                    <input type="text" className="w-full p-3 rounded-xl bg-[#121212] border border-gray-800 text-gray-500 outline-none cursor-not-allowed" value={user?.lastName || 'Fernandez'} readOnly />
                                </div>
                            </div>

                            <div>
                                <label className="block text-xs font-bold text-gray-400 uppercase mb-2 tracking-wider">Registered Email</label>
                                <input type="email" className="w-full p-3 rounded-xl bg-[#121212] border border-gray-800 text-gray-500 outline-none cursor-not-allowed" value={user?.email || 'xavier@goatedmeals.com'} readOnly />
                            </div>

                            <div>
                                <label className="block text-xs font-bold text-gray-400 uppercase mb-2 tracking-wider">Contact Number</label>
                                <input 
                                    type="tel" 
                                    className="w-full p-3 rounded-xl bg-[#121212] border border-gray-700 text-white focus:border-[#00FF66] focus:ring-1 focus:ring-[#00FF66] outline-none transition-all" 
                                    placeholder="+63 900 000 0000"
                                    value={profileData.phone}
                                    onChange={(e) => setProfileData({...profileData, phone: e.target.value})} 
                                />
                            </div>

                            <div>
                                <label className="block text-xs font-bold text-gray-400 uppercase mb-2 tracking-wider">Primary Delivery Target Address</label>
                                <textarea 
                                    className="w-full p-3 rounded-xl bg-[#121212] border border-gray-700 text-white focus:border-[#00FF66] focus:ring-1 focus:ring-[#00FF66] outline-none resize-none transition-all" 
                                    rows="3"
                                    placeholder="Enter full delivery specifications..."
                                    value={profileData.defaultAddress}
                                    onChange={(e) => setProfileData({...profileData, defaultAddress: e.target.value})}
                                ></textarea>
                            </div>

                            <div className="flex justify-end pt-4">
                                <button 
                                    type="submit" 
                                    disabled={loading}
                                    className="bg-[#00FF66] hover:bg-[#00cc52] text-black font-bold py-3 px-8 rounded-xl shadow-[0_0_15px_rgba(0,255,102,0.2)] transition-all text-sm"
                                >
                                    {loading ? 'Processing...' : 'Commit Save'}
                                </button>
                            </div>
                        </form>
                    ) : (
                        <div className="space-y-8 py-2">
                            <div>
                                <h3 className="text-base font-bold text-white mb-1">System Language</h3>
                                <p className="text-sm text-gray-500 mb-3">Configure localization preferences.</p>
                                <select className="p-3 w-full sm:w-auto rounded-xl bg-[#121212] border border-gray-700 text-white outline-none focus:border-[#00FF66] focus:ring-1 focus:ring-[#00FF66]">
                                    <option>English (US)</option>
                                    <option>Filipino</option>
                                </select>
                            </div>
                            <div className="border-t border-gray-800 pt-6">
                                <h3 className="text-base font-bold text-red-500 mb-1">Danger Zone</h3>
                                <p className="text-sm text-gray-500 mb-4">Irreversibly remove account access data from Goated Meals database layers.</p>
                                <button type="button" className="bg-transparent border border-red-500/50 hover:bg-red-500/10 text-red-500 font-bold py-3 px-6 rounded-xl text-sm transition-all">
                                    Deactivate Session Profile
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Profile;