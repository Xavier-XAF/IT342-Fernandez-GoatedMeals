import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const navigate = useNavigate();
    const [firstName, setFirstName] = useState('');

    useEffect(() => {
        const storedName = localStorage.getItem('userFirstName');
        if (storedName) {
            setFirstName(storedName);
        } else {
            navigate('/login');
        }
    }, [navigate]);

    // --- NEW: Logout Function ---
    const handleLogout = () => {
        // 1. Clear the user's data from memory
        localStorage.removeItem('userFirstName');
        
        // (In Week 3, we will also delete the JWT Security Token here!)
        
        // 2. Send them back to the login page
        navigate('/login');
    };

    // --- STANDARD INLINE STYLES MATCHING YOUR SDD ---
    const styles = {
        container: { display: 'flex', minHeight: '100vh', backgroundColor: '#121212', color: '#FFFFFF', fontFamily: 'Inter, Roboto, sans-serif' },
        sidebar: { width: '250px', backgroundColor: '#1E1E1E', padding: '20px', display: 'flex', flexDirection: 'column', gap: '15px', borderRight: '1px solid #333' },
        main: { flex: 1, padding: '40px' },
        logo: { fontSize: '24px', fontWeight: 'bold', color: '#00FF66', marginBottom: '30px' },
        navItem: { padding: '12px 15px', borderRadius: '8px', cursor: 'pointer', color: '#A0AEC0', transition: '0.3s', fontSize: '15px' },
        activeNavItem: { padding: '12px 15px', borderRadius: '8px', cursor: 'pointer', backgroundColor: '#10B981', color: '#121212', fontWeight: 'bold', fontSize: '15px' },
        
        // NEW: Logout Button Style (marginTop: 'auto' pushes it to the bottom)
        logoutButton: { marginTop: 'auto', padding: '12px 15px', borderRadius: '8px', cursor: 'pointer', backgroundColor: 'transparent', color: '#FF6666', border: '1px solid #FF6666', fontWeight: 'bold', fontSize: '15px', textAlign: 'center' }, 
        
        header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '40px' },
        card: { backgroundColor: '#1E1E1E', padding: '30px', borderRadius: '12px', border: '1px solid #333', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
        creditsBox: { backgroundColor: '#10B981', padding: '20px', borderRadius: '12px', textAlign: 'center', color: '#121212', minWidth: '120px' },
        creditsNumber: { fontSize: '42px', fontWeight: 'bold', margin: '5px 0' }
    };

    return (
        <div style={styles.container}>
            {/* Sidebar */}
            <div style={styles.sidebar}>
                <div style={styles.logo}>Goated Meals!</div>
                <div style={styles.activeNavItem}>Dashboard</div>
                <div style={styles.navItem}>My Schedule</div>
                <div style={styles.navItem}>Menu</div>
                <div style={styles.navItem}>Billing</div>
                <div style={styles.navItem}>Profile</div>
                <div style={styles.navItem}>Settings</div>
                
                {/* NEW: Logout Button at the bottom */}
                <div style={styles.logoutButton} onClick={handleLogout}>
                    Sign Out
                </div>
            </div>

            {/* Main Content */}
            <div style={styles.main}>
                <div style={styles.header}>
                    <div>
                        <h1 style={{ margin: 0 }}>Dashboard</h1>
                        <p style={{ color: '#A0AEC0', margin: '5px 0 0 0' }}>Welcome back, {firstName}! Manage your meal plan.</p>
                    </div>
                </div>

                {/* Subscription Status Card */}
                <div style={styles.card}>
                    <div>
                        <h3 style={{ color: '#00FF66', fontSize: '14px', margin: '0 0 10px 0', textTransform: 'uppercase', letterSpacing: '1px' }}>Subscription Status</h3>
                        <h2 style={{ fontSize: '32px', margin: '0 0 10px 0' }}>Premium Weekly Plan</h2>
                        <p style={{ color: '#A0AEC0', margin: 0, fontSize: '15px' }}>Next renewal: March 16, 2026</p>
                    </div>
                    <div style={styles.creditsBox}>
                        <div style={{ fontSize: '14px', fontWeight: 'bold' }}>Available Credits</div>
                        <div style={styles.creditsNumber}>5</div>
                        <div style={{ fontSize: '12px' }}>Meals Remaining</div>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default Dashboard;