// src/components/UserProfile.js
import React from 'react';
import UserCreditCards from './UserCreditCards'; // Import UserCreditCards component

const UserProfile = ({ user, onLogout }) => {
    return (
        <div>
            <h1>Welcome, {user.nameOnTheCard}</h1>
            <UserCreditCards user={user} /> {/* Render user's credit cards */}
        </div>
    );
};

export default UserProfile;
