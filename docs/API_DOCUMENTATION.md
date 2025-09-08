# Camp Sarafrika USSD API Documentation

## Overview
This document outlines the API endpoints and data structures for the Camp Sarafrika USSD Service.

## Endpoints

### USSD Webhook
**Endpoint:** `POST /ussd`  
**Content-Type:** `application/x-www-form-urlencoded`  
**Description:** Main webhook endpoint for handling USSD requests from Africa's Talking

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sessionId | String | Yes | Unique session identifier from AT |
| phoneNumber | String | Yes | User's phone number |
| networkCode | String | Yes | Network operator code |
| serviceCode | String | Yes | USSD service code (e.g., *123#) |
| text | String | No | User input text (empty for initial request) |

#### Response Format
```
CON [message] - Continue session
END [message] - End session
```

### Health Check
**Endpoint:** `GET /ussd/health`  
**Content-Type:** `application/json`  
**Description:** Service health check

#### Response
```json
{
  "status": "UP",
  "service": "sarafrika-camp-ussd"
}
```

## Data Models

### UserSession
Stored in Redis with 5-minute TTL

```json
{
  "session_id": "string",
  "phone_number": "string",
  "state_history": ["main_menu", "select_category"],
  "data": {
    "selectedCategory": "Adventure",
    "selectedCampId": "1",
    "participantName": "John Doe"
  },
  "pagination_offset": 0,
  "current_menu_items": ["1", "2", "3"]
}
```

### Camp Entity
```json
{
  "id": 1,
  "uuid": "uuid-string",
  "name": "Adventure Seekers Camp",
  "category": "Adventure",
  "location": "Naivasha",
  "dates": "Dec 15-20, 2024",
  "fee": 15000.00,
  "activities": ["Rock Climbing", "Zip Lining"],
  "created_date": "2024-09-08T15:41:00",
  "is_deleted": false
}
```

### Registration Entity
```json
{
  "id": 1,
  "uuid": "uuid-string",
  "participant_name": "John Doe",
  "participant_age": 16,
  "participant_phone": "+254712345678",
  "guardian_phone": "+254712345679",
  "camp_id": 1,
  "fee_paid": 15000.00,
  "reference_code": "CS-A4T9B1C7",
  "status": "PENDING",
  "registration_date": "2024-09-08T15:41:00",
  "created_date": "2024-09-08T15:41:00",
  "is_deleted": false
}
```

## USSD Flow States

| State | Description | User Input | Next State |
|-------|-------------|------------|------------|
| main_menu | Welcome screen | 1-4 | select_category, my_bookings, help, END |
| select_category | Camp category selection | 1-N | select_camp |
| select_camp | Camp selection with pagination | 1-3, 99 | enter_full_name, pagination |
| enter_full_name | Participant name input | Text | enter_age |
| enter_age | Age input | Number | enter_guardian_phone/enter_participant_phone |
| enter_guardian_phone | Guardian phone (minors) | Phone | enter_participant_phone |
| enter_participant_phone | Participant phone | Phone | confirm_registration |
| confirm_registration | Registration summary | 1-2 | END |
| my_bookings | User's bookings | Any | main_menu |
| help | Help information | Any | main_menu |

## Validation Rules

### Phone Number Validation
- Format: `^(\\+254|254|0)[17][0-9]{8}$`
- Accepts: +254712345678, 254712345678, 0712345678
- Networks: Safaricom (7xx), Airtel (1xx)

### Age Validation
- Range: 5-25 years
- Guardian phone required if age < 18

### Name Validation
- Minimum length: 2 characters
- No special validation beyond length

## Error Handling

### Common Error Responses
```
CON Invalid option. Please try again.
[Previous menu]

CON Invalid input. Please enter a number.
[Previous menu]

END Sorry, we're experiencing technical difficulties. Please try again later.
```

### Session Timeout
- Sessions expire after 5 minutes of inactivity
- Automatic cleanup via Redis TTL
- User must restart USSD session

## Reference Code Generation
- Format: `CS-XXXXXXXX`
- Characters: A-Z, 0-9 (36 characters)
- Length: 8 characters after prefix
- Uniqueness: Database constraint enforced

## Pagination
- Page size: 3 items
- Navigation: "99. More >>" option
- State: Tracked in session pagination_offset

## Configuration

### Environment Variables
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sarafrika_camp
DB_USERNAME=postgres
DB_PASSWORD=postgres

REDIS_HOST=localhost
REDIS_PORT=6379

AFRICAS_TALKING_API_KEY=your-api-key
AFRICAS_TALKING_USERNAME=your-username
AFRICAS_TALKING_ENVIRONMENT=sandbox

USSD_SERVICE_CODE=*123#
```

## Integration with Africa's Talking

### Webhook Configuration
1. Set webhook URL in Africa's Talking dashboard
2. Configure callback URL: `https://your-domain.com/ussd`
3. Ensure HTTPS for production

### Testing
1. Use sandbox environment for development
2. Test with provided simulator
3. Validate with real USSD codes in production

---
**Last Updated:** September 8, 2025