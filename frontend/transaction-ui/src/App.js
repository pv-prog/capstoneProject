// src/App.js
import React, { useEffect, useState } from 'react';
import './App.css';
import Login from './components/Login';
import UserCards from './components/UserCards';
import { users } from './data/users';

function App() {
    const [loggedInUser, setLoggedInUser] = useState(null);

    useEffect(() => {
        const storedUser = localStorage.getItem('loggedInUser');
        if (storedUser) {
            const user = JSON.parse(storedUser);
            setLoggedInUser(user);
        }
    }, []);

    const handleLogin = (username) => {
        const user = users.find((u) => u.username === username);
        if (user) {
            setLoggedInUser(user);
            localStorage.setItem('loggedInUser', JSON.stringify(user));
        } else {
            alert("User not found!");
        }
    };

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
                    <UserCards userId={loggedInUser._id} /> // Pass user ID to UserCards
                ) : (
                    <Login onLogin={handleLogin} />
                )}
            </main>
        </div>
    );
}

export default App;
