import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MealCatalog from '../components/MealCatalog'; // Ensure the path is correct

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('catalog'); // Default tab
    const [adminName, setAdminName] = useState('Admin');

    useEffect(() => {
        const storedName = localStorage.getItem('userFirstName');
        if (storedName) setAdminName(storedName);
    }, []);

    // --- Logout Feature ---
    const handleLogout = () => {
        localStorage.clear(); // Clears token and user info [cite: 95, 100]
        navigate('/login');
    };

    const styles = {
        container: { display: 'flex', height: '100vh', backgroundColor: '#121212', color: '#FFFFFF', fontFamily: 'Inter, sans-serif' },
        
        // Sidebar UI [cite: 666-673]
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

        logoutBtn: { 
            marginTop: 'auto', 
            padding: '12px', 
            color: '#FF4444', 
            border: '1px solid #FF4444', 
            borderRadius: '8px', 
            cursor: 'pointer', 
            textAlign: 'center',
            fontWeight: 'bold',
            backgroundColor: 'transparent'
        },

        // Main Content Area
        main: { flex: 1, overflowY: 'auto', padding: '40px' },
        header: { marginBottom: '30px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }
    };

    return (
        <div style={styles.container}>
            {/* --- SIDEBAR --- */}
            <aside style={styles.sidebar}>
                <div style={styles.logo}>Goated Meals! Admin </div>
                
                <nav style={styles.navGroup}>
                    <div 
                        style={styles.navItem(activeTab === 'catalog')} 
                        onClick={() => setActiveTab('catalog')}
                    >
                        🍱 Meal Catalog
                    </div>
                    <div 
                        style={styles.navItem(activeTab === 'orders')} 
                        onClick={() => setActiveTab('orders')}
                    >
                        📝 Orders
                    </div>
                </nav>

                <button style={styles.logoutBtn} onClick={handleLogout}>
                    Sign Out
                </button>
            </aside>

            {/* --- CONTENT AREA --- */}
            <main style={styles.main}>
                <header style={styles.header}>
                    <div>
                        <h1 style={{ fontSize: '24px', margin: 0 }}>
                            {activeTab === 'catalog' ? 'Meal Management' : 'Order Monitoring'}
                        </h1>
                        <p style={{ color: '#A0AEC0', fontSize: '13px', marginTop: '4px' }}>
                            Logged in as {adminName}
                        </p>
                    </div>
                </header>

                {/* --- TAB SWITCHING LOGIC --- */}
                {activeTab === 'catalog' && (
                    <div className="fade-in">
                        <MealCatalog /> 
                    </div>
                )}

                {activeTab === 'orders' && (
                    <div style={{ textAlign: 'center', marginTop: '100px', color: '#A0AEC0' }}>
                        <h3>Incoming Orders</h3>
                        <p>Orders table integration coming next...</p>
                    </div>
                )}
            </main>
        </div>
    );
};

export default AdminDashboard;