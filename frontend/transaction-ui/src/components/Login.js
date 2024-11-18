// src/components/Login.js
import React, { useState } from 'react';
import './Login.css'; // Import the CSS file for styling






const Login = ({ onLogin }) => {
    const [username, setUsername] = useState('');

    const handleLogin = () => {
        onLogin(username); // Call the onLogin prop function with the username
    };

       // Function to handle key press events
       const handleKeyPress = (event) => {
        if (event.key === 'Enter') { // Check if the pressed key is Enter
            handleLogin(); // Call the login function
        }
    };
    return (
        <div className="login">
            <h2>Login</h2>
            <input 
                type="text" 
                value={username} 
                onChange={(e) => setUsername(e.target.value)} 
                onKeyDown ={handleKeyPress}
                placeholder="Enter username" 
                required 
            />
            <button onClick={handleLogin}>Login</button>
        </div>
    );
};

export default Login;
