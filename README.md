# API contract between the server and front-end devices
Authorization Roles/Permissions: Must be logged in. To complete this operation successfully.
----------------
Customer Object
```
{
    "id": "6739e4e77544f601a61c7129",
    "username": "achilleyb",
    "password": "$2a$12$x/G8rsZs/cHMEL0U6Ist2.jNmlTZRzXNOUtOSb/vSTeImqA0bOKLG",
    "name": {
        "first": "Maurita",
        "last": "Baden"
    },
    "dob": "10/16/2007",
    "sex": "Female",
    "email": "mbaden19@guardian.co.uk",
    "customerId": 76795327,
    "address": {
        "street": "3781 Clemons Park",
        "city": "Oklahoma City",
        "state": "Oklahoma",
        "zip": 73129,
        "country": "United States"
    },
    "active": true,
    "createdAt": "2024-10-26T18:30:00.000+00:00"
}
```
CreditCard Object
```
{
    "id": "6739ee447544f601a61c7ce8",
    "username": "achilleyb",
    "nameOnTheCard": "Baldwin Mewhirter",
    "creditcards": [
        {
            "creditCardId": 1,
            "creditCardNumber": "5108758767108221",
            "expiryMonth": 11,
            "expiryYear": 25,
            "cvv": 542,
            "wireTransactionVendor": "rupay",
            "status": "enabled"
        }]
}
```
Transaction Object
```
{
    "id": "6739f28c7544f601a61c80ab",
    "username": "achilleyb",
    "creditcards": [
        {
            "creditCardId": 1,
            "transactions": [
                {
                    "transactionId": "7.884806127E9",
                    "transactionDate": "2024-06-13T18:30:00.000+00:00",
                    "transactionTime": "8:15 AM",
                    "transactionType": "cr",
                    "transactionAmount": 240082.03
                    "transactionDesc": "Clothing"
                }]
}]
}
```
## HTTP Method
### 1. When customer wants to view last month maximum expenses of all the cards
### GET /api/customer/creditcards/lastmonth/max
### Url
```
http://{hostname}/api/customer/transactions/maxExpenses/lastMonth/{username}
```
**URL Params:** None
**Request Params:** username 
**Success Response:**
Code: 200
Content: 
```
[
  {
    "credit_card": "5108-7587-6710-8221",
    "month": "NOV",
    "amount": 222204.6
  },
  {
    "credit_card": "5048-3787-4861-6649",
    "month": "NOV",
    "amount": 1235.92
  },
  {
    "credit_card": "5108-7594-1855-6222",
    "month": "NOV",
    "amount": 125.92
  }
```
