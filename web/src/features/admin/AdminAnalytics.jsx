import React, { useState, useEffect } from 'react';
import apiClient from '../core/api/axiosConfig';

export default function AdminAnalytics() {
    const [stats, setStats] = useState({
        totalUsers: 0,
        activeSubscribers: 0,
        totalOrders: 0,
        pendingDeliveries: 0,
        estimatedRevenue: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const response = await apiClient.get('/admin/analytics');
                setStats(response.data);
            } catch (error) {
                console.error("Failed to load analytics:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchStats();
    }, []);

    const styles = {
        grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', marginTop: '20px' },
        card: { backgroundColor: '#1E1E1E', padding: '24px', borderRadius: '12px', border: '1px solid #333', display: 'flex', flexDirection: 'column' },
        title: { color: '#A0AEC0', fontSize: '14px', fontWeight: 'bold', textTransform: 'uppercase', marginBottom: '8px' },
        value: { color: '#FFF', fontSize: '32px', fontWeight: 'bold', margin: 0 },
        highlight: { color: '#00FF66' }
    };

    if (loading) return <div style={{ color: '#A0AEC0' }}>Loading analytics...</div>;

    return (
        <div className="fade-in">
            <h2 style={{ margin: '0 0 4px 0', fontSize: '24px' }}>System Overview</h2>
            <p style={{ margin: 0, color: '#A0AEC0', fontSize: '14px' }}>Live metrics from the Goated Meals! database</p>
            
            <div style={styles.grid}>
                <div style={styles.card}>
                    <span style={styles.title}>Active Subscribers</span>
                    <h3 style={styles.value}>{stats.activeSubscribers} <span style={{ fontSize: '14px', color: '#00FF66' }}>Users</span></h3>
                </div>
                
                <div style={styles.card}>
                    <span style={styles.title}>Monthly Revenue</span>
                    <h3 style={{...styles.value, ...styles.highlight}}>₱{stats.estimatedRevenue.toLocaleString()}</h3>
                </div>

                <div style={styles.card}>
                    <span style={styles.title}>Pending Deliveries</span>
                    <h3 style={styles.value}>{stats.pendingDeliveries} <span style={{ fontSize: '14px', color: '#FFB800' }}>Meals</span></h3>
                </div>

                <div style={styles.card}>
                    <span style={styles.title}>Total Registered Users</span>
                    <h3 style={styles.value}>{stats.totalUsers}</h3>
                </div>
            </div>
        </div>
    );
}