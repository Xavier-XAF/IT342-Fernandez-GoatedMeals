import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../core/api/axiosConfig';
import { GoogleLogin } from '@react-oauth/google';

const Login = () => {
    const navigate = useNavigate();
    
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);

    // --- GOOGLE LOGIN LOGIC ---
    const handleGoogleSuccess = async (credentialResponse) => {
        try {
            const payload = {
                loginType: "GOOGLE",
                googleIdToken: credentialResponse.credential
            };

            const response = await apiClient.post('/auth/login', payload);
            console.log("Login Successful!", response.data);
            
            const payloadData = response.data.data;

            // Save the Goated Meals access token to local storage
            localStorage.setItem('accessToken', payloadData.accessToken);
            localStorage.setItem('userRole', payloadData.user.role);
            localStorage.setItem('userFirstName', payloadData.user.firstname);
            
            // Redirect using React Router for a seamless transition
            navigate('/dashboard'); 

        } catch (error) {
            console.error("Error logging in with Google: ", error.response?.data || error.message);
            setIsError(true);
            setMessage(error.response?.data?.message || 'Failed to login with Google.');
        }
    };

    const handleGoogleFailure = () => {
        console.error("Google Login was closed or failed.");
        setIsError(true);
        setMessage("Google Login was closed or failed.");
    };

    // --- STANDARD LOGIN LOGIC ---
    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            // Because we are sending 'formData', the backend will default to "STANDARD" loginType
            const response = await apiClient.post('/auth/login', formData);
            
            setMessage(response.data.message);
            setIsError(false);

            const payloadData = response.data.data;

            localStorage.setItem('accessToken', payloadData.accessToken);
            localStorage.setItem('userRole', payloadData.user.role);
            localStorage.setItem('userFirstName', payloadData.user.firstname);

            setTimeout(() => {
                if (payloadData.user.role === 'ADMIN') {
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

    // --- STYLES ---
    const styles = {
        container: { minHeight: '100vh', backgroundColor: '#121212', color: '#FFFFFF', display: 'flex', justifyContent: 'center', alignItems: 'center', fontFamily: 'Inter, Roboto, sans-serif' },
        card: { backgroundColor: '#1E1E1E', padding: '30px', borderRadius: '8px', width: '100%', maxWidth: '400px', border: '1px solid #333' },
        input: { width: '100%', padding: '10px', marginBottom: '15px', backgroundColor: '#121212', color: '#FFFFFF', border: '1px solid #333', borderRadius: '4px', boxSizing: 'border-box' },
        button: { width: '100%', padding: '12px', backgroundColor: '#10B981', color: '#121212', border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer', fontSize: '16px' },
        message: { padding: '10px', marginBottom: '15px', borderRadius: '4px', textAlign: 'center', backgroundColor: isError ? '#4A0000' : '#004A20', color: isError ? '#FF6666' : '#00FF66' }
    };

    // --- SINGLE UNIFIED RENDER RETURN ---
    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={{ textAlign: 'center', color: '#00FF66' }}>Goated Meals!</h2>
                <p style={{ textAlign: 'center', color: '#A0AEC0', marginBottom: '20px' }}>Welcome back</p>
                
                {message && <div style={styles.message}>{message}</div>}

                {/* Standard Email/Password Form */}
                <form onSubmit={handleSubmit}>
                    <input type="email" name="email" placeholder="Email Address" value={formData.email} onChange={handleChange} required style={styles.input} />
                    <input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} required style={styles.input} />
                    
                    <button type="submit" style={styles.button}>Login</button>
                </form>

                {/* --- GOOGLE LOGIN SECTION MERGED HERE --- */}
                <hr style={{ borderColor: '#333', margin: '25px 0' }} />
                <p style={{ textAlign: 'center', color: '#A0AEC0', marginBottom: '15px', fontSize: '14px' }}>Or login with</p>
                
                <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <GoogleLogin
                        onSuccess={handleGoogleSuccess}
                        onError={handleGoogleFailure}
                        theme="filled_black" // Forces the Google button to match your dark theme
                        shape="pill"
                    />
                </div>
                {/* -------------------------------------- */}

                <p style={{ textAlign: 'center', marginTop: '25px', fontSize: '14px', color: '#A0AEC0' }}>
                    Don't have an account? <span style={{ color: '#00FF66', cursor: 'pointer' }} onClick={() => navigate('/register')}>Register</span>
                </p>
            </div>
        </div>
    );
};

export default Login;