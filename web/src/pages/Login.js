import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';

const Login = () => {
    const navigate = useNavigate();
    
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            // Send exact payload to /auth/login using your apiClient
            const response = await apiClient.post('/auth/login', formData);
            
            setMessage(response.data.message);
            setIsError(false);

            // The backend now returns data inside response.data.data
            const payload = response.data.data;

            // --- NEW: SAVE THE TOKEN AND DATA TO LOCAL STORAGE ---
            localStorage.setItem('accessToken', payload.accessToken);
            localStorage.setItem('userRole', payload.user.role);
            localStorage.setItem('userFirstName', payload.user.firstname);

            // Log it to verify
            console.log("Logged in payload:", payload);

            // Route the user based on their role after a short success message delay
            setTimeout(() => {
                if (payload.user.role === 'ADMIN') {
                    navigate('/admin/dashboard');
                } else {
                    navigate('/dashboard');
                }
            }, 1000);

        } catch (error) {
            setIsError(true);
            setMessage(error.response?.data?.message || 'Invalid email or password');
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    

    // --- STANDARD INLINE STYLES MATCHING YOUR SDD ---
    const styles = {
        container: { minHeight: '100vh', backgroundColor: '#121212', color: '#FFFFFF', display: 'flex', justifyContent: 'center', alignItems: 'center', fontFamily: 'Inter, Roboto, sans-serif' },
        card: { backgroundColor: '#1E1E1E', padding: '30px', borderRadius: '8px', width: '100%', maxWidth: '400px', border: '1px solid #333' },
        input: { width: '100%', padding: '10px', marginBottom: '15px', backgroundColor: '#121212', color: '#FFFFFF', border: '1px solid #333', borderRadius: '4px', boxSizing: 'border-box' },
        button: { width: '100%', padding: '12px', backgroundColor: '#10B981', color: '#121212', border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer', fontSize: '16px' },
        message: { padding: '10px', marginBottom: '15px', borderRadius: '4px', textAlign: 'center', backgroundColor: isError ? '#4A0000' : '#004A20', color: isError ? '#FF6666' : '#00FF66' }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={{ textAlign: 'center', color: '#00FF66' }}>Goated Meals!</h2>
                <p style={{ textAlign: 'center', color: '#A0AEC0', marginBottom: '20px' }}>Welcome back</p>
                
                {message && <div style={styles.message}>{message}</div>}

                <form onSubmit={handleSubmit}>
                    <input type="email" name="email" placeholder="Email Address" value={formData.email} onChange={handleChange} required style={styles.input} />
                    <input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} required style={styles.input} />
                    
                    <button type="submit" style={styles.button}>Login</button>
                </form>
                <p style={{ textAlign: 'center', marginTop: '15px', fontSize: '14px', color: '#A0AEC0' }}>
                    Don't have an account? <span style={{ color: '#00FF66', cursor: 'pointer' }} onClick={() => navigate('/register')}>Register</span>
                </p>
            </div>
        </div>
    );
};

export default Login;
