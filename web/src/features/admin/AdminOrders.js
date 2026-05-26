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

    const handleStatusChange = async (orderId, newStatus) => {
        try {
            await apiClient.put(`/admin/orders/${orderId}/status`, {
                status: newStatus
            });
            fetchOrders(); 
        } catch (error) {
            console.error("Failed to update status:", error);
            alert("Failed to update order status. Check console.");
        }
    };

    // 1. Added the DELIVERING color (Purple)
    const getStatusColor = (status) => {
        switch (status) {
            case 'SCHEDULED': return { bg: '#00FF6620', text: '#00FF66' };
            case 'PREPARING': return { bg: '#FFB80020', text: '#FFB800' }; 
            case 'DELIVERING': return { bg: '#A855F720', text: '#A855F7' }; // Purple
            case 'DELIVERED': return { bg: '#3B82F620', text: '#3B82F6' }; 
            default: return { bg: '#333333', text: '#FFFFFF' };
        }
    };

    // 2. THE STATE MACHINE: Determines what options are allowed in the dropdown
    const getAvailableOptions = (currentStatus) => {
        switch (currentStatus) {
            case 'SCHEDULED': 
                return ['SCHEDULED', 'PREPARING'];
            case 'PREPARING': 
                return ['PREPARING', 'DELIVERING'];
            case 'DELIVERING': 
                return ['PREPARING', 'DELIVERING', 'DELIVERED']; // Allows fallback to PREPARING
            case 'DELIVERED': 
                return ['DELIVERED']; // Locked forever
            default: 
                return [currentStatus];
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
                        <th style={{ padding: '12px' }}>Delivery Schedule</th>
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
                            const allowedOptions = getAvailableOptions(order.status);
                            const isDelivered = order.status === 'DELIVERED';
                            
                            return (
                                <tr key={order.id} style={{ borderBottom: '1px solid #2A2A2A' }}>
                                    <td style={{ padding: '12px' }}>#{order.id}</td>
                                    <td style={{ padding: '12px' }}>{order.user?.firstname} {order.user?.lastname}</td>
                                    <td style={{ padding: '12px', color: '#00FF66' }}>{order.meal?.name}</td>
                                    
                                    <td style={{ padding: '12px' }}>
                                        <div style={{ fontWeight: 'bold', color: '#FFF' }}>{order.deliveryDay}</div>
                                        <div style={{ fontSize: '12px', color: '#00FF66', marginTop: '2px' }}>
                                            {order.deliveryTime ? `⏰ ${order.deliveryTime}` : '⏰ Time not set'}
                                        </div>
                                    </td>
                                    
                                    <td style={{ padding: '12px' }}>{order.deliveryAddress}</td>
                                    <td style={{ padding: '12px' }}>
                                        {/* 3. DYNAMIC DROPDOWN: Maps only the allowed options and locks if delivered */}
                                        <select 
                                            value={order.status}
                                            onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                            disabled={isDelivered}
                                            style={{
                                                backgroundColor: colors.bg,
                                                color: colors.text,
                                                padding: '6px 10px',
                                                borderRadius: '6px',
                                                border: `1px solid ${colors.text}50`,
                                                fontWeight: 'bold',
                                                fontSize: '12px',
                                                cursor: isDelivered ? 'not-allowed' : 'pointer',
                                                outline: 'none',
                                                opacity: isDelivered ? 0.8 : 1
                                            }}
                                        >
                                            {allowedOptions.map(opt => (
                                                <option key={opt} value={opt} style={{ background: '#1E1E1E', color: getStatusColor(opt).text }}>
                                                    {opt}
                                                </option>
                                            ))}
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