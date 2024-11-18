import { Card, CardContent, MenuItem, TextField, Typography } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import React, { useState } from 'react';

const useStyles = makeStyles((theme) => ({
  root: {
    maxWidth: 600,
    margin: 'auto',
    marginTop: theme.spacing(4),
    padding: theme.spacing(2),
  },
  card: {
    margin: theme.spacing(2),
  },
}));

const TransactionsPage = () => {
  const classes = useStyles();
  const [selectedCard, setSelectedCard] = useState('');

  const handleCardChange = (event) => {
    setSelectedCard(event.target.value);
  };

  return (
    <div className={classes.root}>
      <Typography variant="h4" gutterBottom>
        Transactions
      </Typography>
      <Card className={classes.card}>
        <CardContent>
          {/* Render transaction details here */}
        </CardContent>
      </Card>
      <Typography variant="h4" gutterBottom>
        Cards
      </Typography>
      <Card className={classes.card}>
        <CardContent>
          <TextField
            select
            label="Select Card"
            value={selectedCard}
            onChange={handleCardChange}
            variant="outlined"
          >
            <MenuItem value="">None</MenuItem>
            <MenuItem value="card1">Card 1</MenuItem>
            <MenuItem value="card2">Card 2</MenuItem>
            <MenuItem value="card3">Card 3</MenuItem>
          </TextField>
          {/* Render card details based on selected card */}
          {selectedCard && (
            <Typography variant="body1" gutterBottom>
              Card Info: {selectedCard}
            </Typography>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default TransactionsPage;