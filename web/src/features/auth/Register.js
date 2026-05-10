import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../core/api/axiosConfig';

const Register = () => {
    const navigate = useNavigate();
    
    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        email: '',
        contactNumber: '',
        password: '',
        confirmPassword: ''
    });
    
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // 1. Frontend Validation
        if (formData.password !== formData.confirmPassword) {
            setIsError(true);
            setMessage("Passwords do not match!");
            return;
        }

        try {
            // 2. Remove confirmPassword before sending to Spring Boot
            const { confirmPassword, ...apiData } = formData;
            
            // 3. Send exact payload to /auth/register
            const response = await apiClient.post('/auth/register', apiData);
            
            setMessage(response.data.message);
            setIsError(false);
            
            // 4. Redirect to login after 2 seconds
            setTimeout(() => navigate('/login'), 2000);

        } catch (error) {
            setIsError(true);
            setMessage(error.response?.data?.message || 'Registration failed');
        }
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
                <p style={{ textAlign: 'center', color: '#A0AEC0', marginBottom: '20px' }}>Create your account</p>
                
                {message && <div style={styles.message}>{message}</div>}

                <form onSubmit={handleSubmit}>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <input type="text" name="firstname" placeholder="First Name" value={formData.firstname} onChange={handleChange} required style={styles.input} />
                        <input type="text" name="lastname" placeholder="Last Name" value={formData.lastname} onChange={handleChange} required style={styles.input} />
                    </div>
                    <input type="email" name="email" placeholder="Email Address" value={formData.email} onChange={handleChange} required style={styles.input} />
                    <input type="text" name="contactNumber" placeholder="Contact Number (+639...)" value={formData.contactNumber} onChange={handleChange} required style={styles.input} />
                    <input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} required style={styles.input} />
                    <input type="password" name="confirmPassword" placeholder="Confirm Password" value={formData.confirmPassword} onChange={handleChange} required style={styles.input} />
                    
                    <button type="submit" style={styles.button}>Register</button>
                </form>
                <p style={{ textAlign: 'center', marginTop: '15px', fontSize: '14px', color: '#A0AEC0' }}>
                    Already have an account? <span style={{ color: '#00FF66', cursor: 'pointer' }} onClick={() => navigate('/login')}>Log in</span>
                </p>
            </div>
        </div>
    );
};

export default Register;
