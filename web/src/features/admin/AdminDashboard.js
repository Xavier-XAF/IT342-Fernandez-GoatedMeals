import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MealCatalog from '../menu/MealCatalog'; 
import AdminOrders from './AdminOrders';
import AdminAnalytics from './AdminAnalytics';

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('analytics'); 
    const [adminName, setAdminName] = useState('Admin');

    useEffect(() => {
        const storedName = localStorage.getItem('userFirstName');
        if (storedName) setAdminName(storedName);
    }, []);

    const handleLogout = () => {
        localStorage.clear(); 
        navigate('/login');
    };

    const styles = {
        container: { display: 'flex', height: '100vh', backgroundColor: '#121212', color: '#FFFFFF', fontFamily: 'Inter, sans-serif' },
        
        sidebar: { width: '260px', backgroundColor: '#1E1E1E', borderRight: '1px solid #333', display: 'flex', flexDirection: 'column', padding: '20px' },
        logo: { fontSize: '22px', fontWeight: 'bold', color: '#00FF66', marginBottom: '40px', letterSpacing: '1px' },
        
        navGroup: { flex: 1, display: 'flex', flexDirection: 'column', gap: '8px' },
        navItem: (isActive) => ({
            padding: '12px 16px',
            borderRadius: '8px',
            cursor: 'pointer',
            fontSize: '14px',
            fontWeight: '600',
            transition: '0.2s',
            color: isActive ? '#121212' : '#A0AEC0',
            backgroundColor: isActive ? '#00FF66' : 'transparent',
            display: 'flex',
            alignItems: 'center',
            gap: '12px'
        }),

        // NEW: Grouped the user info and logout button together
        userSection: { marginTop: 'auto', display: 'flex', flexDirection: 'column', gap: '15px' },
        userInfo: { textAlign: 'center', color: '#A0AEC0', fontSize: '13px' },
        logoutBtn: { 
            padding: '12px', 
            color: '#FF4444', 
            border: '1px solid #FF4444', 
            borderRadius: '8px', 
            cursor: 'pointer', 
            textAlign: 'center',
            fontWeight: 'bold',
            backgroundColor: 'transparent'
        },

        // Cleaned up the main content area
        main: { flex: 1, overflowY: 'auto', padding: '40px' }
    };

    return (
        <div style={styles.container}>
            {/* --- SIDEBAR --- */}
            <aside style={styles.sidebar}>
                <div style={styles.logo}>Goated Meals! Admin </div>
                
                <nav style={styles.navGroup}>
                    <div style={styles.navItem(activeTab === 'analytics')} onClick={() => setActiveTab('analytics')}>
                        📊 Overview
                    </div>
                    <div style={styles.navItem(activeTab === 'catalog')} onClick={() => setActiveTab('catalog')}>
                        🍱 Meal Catalog
                    </div>
                    <div style={styles.navItem(activeTab === 'orders')} onClick={() => setActiveTab('orders')}>
                        📝 Orders
                    </div>
                </nav>

                {/* --- USER INFO & LOGOUT --- */}
                <div style={styles.userSection}>
                    <div style={styles.userInfo}>
                        Logged in as <strong style={{ color: '#FFF' }}>{adminName}</strong>
                    </div>
                    <button style={styles.logoutBtn} onClick={handleLogout}>
                        Sign Out
                    </button>
                </div>
            </aside>

            {/* --- CONTENT AREA --- */}
            <main style={styles.main}>
                {activeTab === 'analytics' && (
                    <div className="fade-in">
                        <AdminAnalytics />
                    </div>
                )}

                {activeTab === 'catalog' && (
                    <div className="fade-in">
                        <MealCatalog /> 
                    </div>
                )}

                {activeTab === 'orders' && (
                    <div className="fade-in">
                        <AdminOrders />
                    </div>
                )}
            </main>
        </div>
    );
};

export default AdminDashboard;