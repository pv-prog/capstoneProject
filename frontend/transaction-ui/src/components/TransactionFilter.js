// src/components/TransactionFilter.js
import React, { useState } from 'react';
import './TransactionFilter.css';

const TransactionFilter = ({ onFilterChange, onCustomDateChange }) => {
    const [filterOption, setFilterOption] = useState('all'); // Default filter option

    const handleFilterChange = (event) => {
        const option = event.target.value;
        setFilterOption(option); // Update the filter option
        onFilterChange(option); // Notify parent about the filter change
    };

    const handleStartDateChange = (event) => {
        const startDate = event.target.value;
        onCustomDateChange(startDate, null); // Notify parent about the start date change
    };

    const handleEndDateChange = (event) => {
        const endDate = event.target.value;
        onCustomDateChange(null, endDate); // Notify parent about the end date change
    };

    return (
        <div className="transaction-filter">
            <label htmlFor="transaction-filter" style={{ marginRight: '10px' }}>Filter Transactions:</label>
            <select id="transaction-filter" value={filterOption} onChange={handleFilterChange}>
                <option value="all">All Transactions</option>
                <option value="last10">Last 10</option>
                <option value="last3">Last 3 Months</option>
                <option value="last6">Last 6 Months</option>
                <option value="custom">Custom Date</option>
            </select>
            {filterOption === 'custom' && (
                <div className="custom-date-picker">
                    <input 
                        type="date" 
                        onChange={handleStartDateChange} 
                        placeholder="Start Date" 
                    />
                    <input 
                        type="date" 
                        onChange={handleEndDateChange} 
                        placeholder="End Date" 
                    />
                </div>
            )}
        </div>
    );
};

export default TransactionFilter;
