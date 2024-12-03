import React, { useEffect, useState } from 'react';
import './App.css';
import Login from './components/Login';
import UserCards from './components/UserCards';
import axios from 'axios'; // Import axios for making API requests

function App() {
    const [loggedInUser, setLoggedInUser] = useState(null);
    const [loading, setLoading] = useState(false); // Loading state for API request
    const [error, setError] = useState(null); // Error state for handling errors

    // Check if there's a stored user in localStorage when the component mounts
    useEffect(() => {
        const storedUser = localStorage.getItem('loggedInUser');
        if (storedUser) {
            const user = JSON.parse(storedUser);
            setLoggedInUser(user);
        }
    }, []);  // Runs only once when the component mounts

    // Handle user login, making an API call to fetch user data based on username
    const handleLogin = async (username) => {
        setLoading(true); // Set loading state to true when API call starts
        setError(null); // Reset error state

        try {
            const response = await axios.get(`/api/customer/${username}`);  // API call to get user data
            if (response.data) {
                const user = response.data;  // Assuming the response contains the user data
                setLoggedInUser(user); // Set the logged-in user
                localStorage.setItem('loggedInUser', JSON.stringify(user));  // Store the user in localStorage
            } else {
                alert("User not found!");  // Alert if no user data is found
            }
        } catch (error) {
            console.error('Error fetching user data:', error);
            setError('Error fetching user data');  // Set error state if API call fails
        } finally {
            setLoading(false);  // Set loading state to false once the API call is completed
        }
    };

    // Handle logout by clearing loggedInUser state and removing it from localStorage
    const handleLogout = () => {
        setLoggedInUser(null);
        localStorage.removeItem('loggedInUser');
    };

    return (
        <div className="App">
            <header className="App-header">
                <h1 className="header-title">Credit Card Manager</h1>
                {loggedInUser && (
                    <div className="user-info">
                        <span className="username">{loggedInUser.username.toUpperCase()}</span>
                        <button onClick={handleLogout} className="logout-button">Logout</button>
                    </div>
                )}
            </header>
            <main>
            {loggedInUser ? (
    <UserCards username={loggedInUser.username} /> // Pass the username directly
) : (
    <Login onLogin={handleLogin} />
)}

            </main>
        </div>
    );
}

export default App;
