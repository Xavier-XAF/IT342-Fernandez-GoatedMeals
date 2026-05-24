import React, { useState, useEffect } from 'react';
import apiClient from '../core/api/axiosConfig';

export default function AdminOrders() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            const response = await apiClient.get('/admin/orders/active');
            setOrders(response.data);
        } catch (error) {
            console.error("Error fetching orders:", error);
        } finally {
            setLoading(false);
        }
    };

    // NEW: Function to handle the dropdown change and push it to Spring Boot
    const handleStatusChange = async (orderId, newStatus) => {
        try {
            await apiClient.put(`/admin/orders/${orderId}/status`, {
                status: newStatus
            });
            // Refresh the table to show the updated data
            fetchOrders(); 
        } catch (error) {
            console.error("Failed to update status:", error);
            alert("Failed to update order status. Check console.");
        }
    };

    // Helper function for status colors
    const getStatusColor = (status) => {
        switch (status) {
            case 'SCHEDULED': return { bg: '#00FF6620', text: '#00FF66' };
            case 'PREPARING': return { bg: '#FFB80020', text: '#FFB800' }; // Yellow/Orange
            case 'DELIVERED': return { bg: '#3B82F620', text: '#3B82F6' }; // Blue
            default: return { bg: '#333333', text: '#FFFFFF' };
        }
    };

    if (loading) return <div style={{ color: '#A0AEC0' }}>Loading master schedule...</div>;

    return (
        <div style={{ backgroundColor: '#1E1E1E', borderRadius: '12px', padding: '20px', border: '1px solid #333' }}>
            <h2 style={{ margin: '0 0 20px 0', fontSize: '20px' }}>Active Deliveries ({orders.length})</h2>
            
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                <thead>
                    <tr style={{ borderBottom: '1px solid #333', color: '#A0AEC0' }}>
                        <th style={{ padding: '12px' }}>Order ID</th>
                        <th style={{ padding: '12px' }}>Customer</th>
                        <th style={{ padding: '12px' }}>Meal</th>
                        <th style={{ padding: '12px' }}>Delivery Day</th>
                        <th style={{ padding: '12px' }}>Address</th>
                        <th style={{ padding: '12px' }}>Status</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.length === 0 ? (
                        <tr>
                            <td colSpan="6" style={{ padding: '20px', textAlign: 'center', color: '#666' }}>
                                No active orders found.
                            </td>
                        </tr>
                    ) : (
                        orders.map(order => {
                            const colors = getStatusColor(order.status);
                            
                            return (
                                <tr key={order.id} style={{ borderBottom: '1px solid #2A2A2A' }}>
                                    <td style={{ padding: '12px' }}>#{order.id}</td>
                                    <td style={{ padding: '12px' }}>{order.user?.firstname} {order.user?.lastname}</td>
                                    <td style={{ padding: '12px', color: '#00FF66' }}>{order.meal?.name}</td>
                                    <td style={{ padding: '12px' }}>{order.deliveryDay}</td>
                                    <td style={{ padding: '12px' }}>{order.deliveryAddress}</td>
                                    <td style={{ padding: '12px' }}>
                                        {/* NEW: Interactive Dropdown */}
                                        <select 
                                            value={order.status}
                                            onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                            style={{
                                                backgroundColor: colors.bg,
                                                color: colors.text,
                                                padding: '6px 10px',
                                                borderRadius: '6px',
                                                border: `1px solid ${colors.text}50`, // Slight transparency on border
                                                fontWeight: 'bold',
                                                fontSize: '12px',
                                                cursor: 'pointer',
                                                outline: 'none'
                                            }}
                                        >
                                            <option value="SCHEDULED" style={{ background: '#1E1E1E', color: '#00FF66' }}>SCHEDULED</option>
                                            <option value="PREPARING" style={{ background: '#1E1E1E', color: '#FFB800' }}>PREPARING</option>
                                            <option value="DELIVERED" style={{ background: '#1E1E1E', color: '#3B82F6' }}>DELIVERED</option>
                                        </select>
                                    </td>
                                </tr>
                            );
                        })
                    )}
                </tbody>
            </table>
        </div>
    );
}