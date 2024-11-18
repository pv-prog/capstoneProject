// src/components/CardTransactions.js
import React, { useCallback, useEffect, useState } from 'react';
import './CardTransactions.css';
import Transaction from './Transaction'; // Import the Transaction component
import TransactionFilter from './TransactionFilter'; // Import the TransactionFilter component

const CardTransactions = ({ transactions, cardNumber, cardType, cardStatus }) => {
    const [filteredTransactions, setFilteredTransactions] = useState(transactions);
    const [filterOption, setFilterOption] = useState('all'); // Default filter option
    const [customStartDate, setCustomStartDate] = useState('');
    const [customEndDate, setCustomEndDate] = useState('');

    const applyFilter = useCallback(() => {
        let filtered = [...transactions]; // Start with all transactions

        // Sort transactions by date in descending order
        filtered.sort((a, b) => new Date(b.transactionDate) - new Date(a.transactionDate));

        const today = new Date();
        const tenDaysAgo = new Date(today);
        tenDaysAgo.setDate(today.getDate() - 10); // Calculate the date for last 10 days

        if (filterOption === 'last10') {
            // Get the last 10 transactions
            filtered = filtered.slice(0, 10);
        } else if (filterOption === 'last3') {
            const threeMonthsAgo = new Date();
            threeMonthsAgo.setMonth(threeMonthsAgo.getMonth() - 3);
            filtered = filtered.filter(transaction => new Date(transaction.transactionDate) >= threeMonthsAgo);
        } else if (filterOption === 'last6') {
            const sixMonthsAgo = new Date();
            sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);
            filtered = filtered.filter(transaction => new Date(transaction.transactionDate) >= sixMonthsAgo);
        } else if (filterOption === 'last10') {
            // Filter for transactions in the last 10 days
            filtered = filtered.filter(transaction => {
                const transactionDate = new Date(transaction.transactionDate);
                return transactionDate >= tenDaysAgo;
            });
        } else if (filterOption === 'custom') {
            const startDate = new Date(customStartDate);
            const endDate = new Date(customEndDate);
            if (customStartDate && customEndDate) {
                filtered = filtered.filter(transaction => {
                    const transactionDate = new Date(transaction.transactionDate);
                    return transactionDate >= startDate && transactionDate <= endDate;
                });
            }
        }

        setFilteredTransactions(filtered); // Update the filtered transactions state
    }, [transactions, filterOption, customStartDate, customEndDate]);

    useEffect(() => {
        applyFilter();
    }, [filterOption, customStartDate, customEndDate, applyFilter]);

    const handleFilterChange = (option) => {
        setFilterOption(option); // Update the current filter option
        if (option !== 'custom') {
            // Reset custom dates if not filtering by custom
            setCustomStartDate('');
            setCustomEndDate('');
        }
    };

    const handleCustomDateChange = (startDate, endDate) => {
        if (startDate) {
            setCustomStartDate(startDate); // Update start date
        }
        if (endDate) {
            setCustomEndDate(endDate); // Update end date
        }
    };

    return (
        <div className="transactions-container">
            <h3>Transactions for {cardType} Card:</h3>
            <p><span style={{ fontWeight: 'bold' }}>Card Number:</span> {cardNumber}</p>
            <p>
                <span style={{ fontWeight: 'bold' }}>Status:</span> 
                <span style={{ color: cardStatus === 'active' ? 'green' : 'red', fontWeight: 'bold' }}>
                    {cardStatus}
                </span>
            </p>
            <TransactionFilter 
                onFilterChange={handleFilterChange} 
                onCustomDateChange={handleCustomDateChange} // Pass handler for custom date change
            /> 
            <div className="transactions">
                {filteredTransactions.map(transaction => (
                    <Transaction key={transaction.transactionId} transaction={transaction} />
                ))}
            </div>
        </div>
    );
};

export default CardTransactions;
