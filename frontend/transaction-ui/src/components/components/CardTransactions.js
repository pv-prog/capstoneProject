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

    // Function to convert transactions to CSV and trigger download
    const downloadCSV = () => {
        const csvData = [
            ['Transaction ID', 'Date','TransactionTime', 'TransactionType','Amount',], // CSV header
            ...filteredTransactions.map(transaction => [
                transaction.transactionId,
                transaction.transactionDate,
                transaction.transactionTime,
                transaction.transactionType,
                transaction.transactionAmount,
              
               
                
            ]),
        ];

        // Convert array of arrays into a CSV string
        const csvContent = 'data:text/csv;charset=utf-8,' + 
            csvData.map(e => e.join(',')).join('\n');

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement('a');
        link.setAttribute('href', encodedUri);
        link.setAttribute('download', `transactions_${cardNumber}.csv`);
        document.body.appendChild(link); // Required for FF

        link.click(); // This will download the data file
        document.body.removeChild(link); // Clean up
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
            <button onClick={downloadCSV} className="download-button">Download Statement</button> {/* Download button */}
            <div className="transactions">
                {filteredTransactions.map(transaction => (
                    <Transaction key={transaction.transactionId} transaction={transaction} />
                ))}
            </div>
        </div>
    );
};

export default CardTransactions;
