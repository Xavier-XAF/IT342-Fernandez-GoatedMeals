import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../core/api/axiosConfig';

export default function Profile() {
    const navigate = useNavigate();
    const [profile, setProfile] = useState({ firstname: '', lastname: '', email: '' });
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({ firstname: '', lastname: '', email: '' });
    const [loading, setLoading] = useState(true);
    
    // Status Messages
    const [message, setMessage] = useState({ text: '', type: '' });
    
    // Password State
    const [passwordData, setPasswordData] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
    const [isChangingPassword, setIsChangingPassword] = useState(false);

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const response = await apiClient.get('/auth/me'); 
            setProfile(response.data);
            setFormData({
                firstname: response.data.firstname,
                lastname: response.data.lastname,
                email: response.data.email
            });
        } catch (error) {
            console.error("Failed to load profile:", error);
        } finally {
            setLoading(false);
        }
    };

    const showToast = (text, type) => {
        setMessage({ text, type });
        setTimeout(() => setMessage({ text: '', type: '' }), 4000);
    };

    // --- SAVE PERSONAL DETAILS ---
    const handleSaveProfile = async () => {
        try {
            await apiClient.put('/auth/profile', formData);
            setProfile(formData); 
            setIsEditing(false); 
            showToast("Profile successfully updated!", 'success');
            localStorage.setItem('userFirstName', formData.firstname);
        } catch (error) {
            showToast("Failed to update profile.", 'error');
        }
    };

    // --- CHANGE PASSWORD ---
    const handlePasswordChange = async (e) => {
        e.preventDefault();
        if (passwordData.newPassword !== passwordData.confirmPassword) {
            showToast("New passwords do not match!", 'error');
            return;
        }

        try {
            const response = await apiClient.put('/auth/password', {
                currentPassword: passwordData.currentPassword,
                newPassword: passwordData.newPassword
            });
            showToast(response.data.message, 'success');
            setIsChangingPassword(false);
            setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
        } catch (error) {
            showToast(error.response?.data?.error || "Failed to change password.", 'error');
        }
    };

    // --- DANGER ZONE (DELETE ACCOUNT) ---
    const handleDeleteAccount = async () => {
        const confirmation = window.prompt("This action CANNOT be undone. Type 'DELETE' to confirm account closure.");
        if (confirmation !== 'DELETE') {
            showToast("Account deletion canceled.", 'success');
            return;
        }

        try {
            await apiClient.delete('/auth/account');
            alert("Your account has been deleted. We are sorry to see you go!");
            localStorage.clear();
            navigate('/login');
        } catch (error) {
            showToast(error.response?.data?.error || "Failed to delete account.", 'error');
        }
    };

    if (loading) return <div className="text-[#A0AEC0] p-8">Loading profile...</div>;

    return (
        <div className="bg-[#121212] min-h-screen text-white p-8 font-sans">
            <h1 className="text-2xl font-bold mb-1">My Profile</h1>
            <p className="text-gray-400 text-sm mb-8">Manage your personal information and security.</p>

            {/* Toast Notification */}
            {message.text && (
                <div className={`fixed top-8 right-8 z-50 px-6 py-4 rounded-xl font-bold shadow-2xl transition-all ${message.type === 'success' ? 'bg-[#00FF66] text-black' : 'bg-red-500 text-white'}`}>
                    {message.text}
                </div>
            )}

            <div className="grid grid-cols-1 gap-8 max-w-3xl">
                
                {/* --- 1. PERSONAL DETAILS SECTION --- */}
                <div className="bg-[#1E1E1E] p-8 rounded-2xl border border-gray-800">
                    <div className="flex justify-between items-center mb-6 border-b border-gray-800 pb-4">
                        <h2 className="text-lg font-bold">Personal Details</h2>
                        {!isEditing ? (
                            <button onClick={() => setIsEditing(true)} className="text-[#00FF66] border border-[#00FF66] px-4 py-1.5 rounded-lg text-sm font-bold hover:bg-[#00FF66] hover:text-black transition">
                                Edit Profile
                            </button>
                        ) : (
                            <div className="flex gap-3">
                                <button onClick={() => { setIsEditing(false); setFormData(profile); }} className="text-gray-400 hover:text-white text-sm font-bold transition">Cancel</button>
                                <button onClick={handleSaveProfile} className="bg-[#00FF66] text-black px-4 py-1.5 rounded-lg text-sm font-bold hover:bg-green-500 transition">Save Changes</button>
                            </div>
                        )}
                    </div>

                    <div className="grid grid-cols-2 gap-6 mb-6">
                        <div>
                            <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">First Name</label>
                            {isEditing ? (
                                <input type="text" name="firstname" value={formData.firstname} onChange={(e) => setFormData({...formData, firstname: e.target.value})} className="w-full bg-[#121212] border border-gray-800 text-white rounded-lg py-2 px-4 focus:outline-none focus:border-[#00FF66]" />
                            ) : <p className="text-lg font-medium">{profile.firstname}</p>}
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Last Name</label>
                            {isEditing ? (
                                <input type="text" name="lastname" value={formData.lastname} onChange={(e) => setFormData({...formData, lastname: e.target.value})} className="w-full bg-[#121212] border border-gray-800 text-white rounded-lg py-2 px-4 focus:outline-none focus:border-[#00FF66]" />
                            ) : <p className="text-lg font-medium">{profile.lastname}</p>}
                        </div>
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Email Address</label>
                        {isEditing ? (
                            <input type="email" name="email" value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} className="w-full bg-[#121212] border border-gray-800 text-white rounded-lg py-2 px-4 focus:outline-none focus:border-[#00FF66]" />
                        ) : <p className="text-lg font-medium">{profile.email}</p>}
                    </div>
                </div>

                {/* --- 2. SECURITY SECTION --- */}
                <div className="bg-[#1E1E1E] p-8 rounded-2xl border border-gray-800">
                    <div className="flex justify-between items-center mb-6 border-b border-gray-800 pb-4">
                        <h2 className="text-lg font-bold">Security</h2>
                        {!isChangingPassword ? (
                            <button onClick={() => setIsChangingPassword(true)} className="text-[#00FF66] border border-[#00FF66] px-4 py-1.5 rounded-lg text-sm font-bold hover:bg-[#00FF66] hover:text-black transition">
                                Change Password
                            </button>
                        ) : (
                            <button onClick={() => setIsChangingPassword(false)} className="text-gray-400 hover:text-white text-sm font-bold transition">Cancel</button>
                        )}
                    </div>

                    {isChangingPassword ? (
                        <form onSubmit={handlePasswordChange} className="space-y-4">
                            <div>
                                <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Current Password</label>
                                <input type="password" required value={passwordData.currentPassword} onChange={(e) => setPasswordData({...passwordData, currentPassword: e.target.value})} className="w-full bg-[#121212] border border-gray-800 text-white rounded-lg py-2 px-4 focus:outline-none focus:border-[#00FF66]" />
                            </div>
                            <div className="grid grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">New Password</label>
                                    <input type="password" required minLength="6" value={passwordData.newPassword} onChange={(e) => setPasswordData({...passwordData, newPassword: e.target.value})} className="w-full bg-[#121212] border border-gray-800 text-white rounded-lg py-2 px-4 focus:outline-none focus:border-[#00FF66]" />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Confirm New Password</label>
                                    <input type="password" required minLength="6" value={passwordData.confirmPassword} onChange={(e) => setPasswordData({...passwordData, confirmPassword: e.target.value})} className="w-full bg-[#121212] border border-gray-800 text-white rounded-lg py-2 px-4 focus:outline-none focus:border-[#00FF66]" />
                                </div>
                            </div>
                            <div className="pt-2">
                                <button type="submit" className="bg-[#00FF66] text-black px-6 py-2 rounded-lg font-bold hover:bg-green-500 transition">Update Password</button>
                            </div>
                        </form>
                    ) : (
                        <p className="text-gray-400 text-sm">Ensure your account is using a long, random password to stay secure.</p>
                    )}
                </div>

                {/* --- 3. DANGER ZONE --- */}
                <div className="bg-red-900/10 p-8 rounded-2xl border border-red-900/50">
                    <h2 className="text-lg font-bold text-red-500 mb-2">Danger Zone</h2>
                    <p className="text-gray-400 text-sm mb-6">Once you delete your account, there is no going back. All of your scheduled meals, credits, and subscription data will be permanently wiped.</p>
                    <button onClick={handleDeleteAccount} className="border border-red-500 text-red-500 px-6 py-2 rounded-lg font-bold hover:bg-red-500 hover:text-white transition">
                        Delete Account
                    </button>
                </div>

            </div>
        </div>
    );
}