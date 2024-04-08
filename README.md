# SP-2

## ERD
![Image of ERD](doc/ERD.png)
 
 ## Endpoints

| HTTP method | REST Resource             |                              | Comment                     |
|-------------|---------------------------|------------------------------|-----------------------------|
| POST | `/api/auth/login`         | `response:` status code  | Login |
| POST | `/api/auth/logout`        | `response:` satus code  | Logout |
| POST | `/api/auth/register`      | `response:` {response body}  | Register |
| POST | `/api/auth/addRoleToUser` | `response:` {response body}  | Add a role to a user |
| PUT | `/api/event/registerUser` | `response:` none  | Adds a user to an event |
| PUT | `/api/event/cancelRegistration`| `response:` none | Cancels a registration |
| GET | `/api/event/upcoming`| `response:` [{```JSON
"id": number,
"title": String,
"startTime": String,
"description": String,
"dateOfEvent": String,
"durationInHours: number,
"maxNumberOfStudents: number,
"locationOfEvent": String,
"instructor": String,
"price: number,
"category": String,
"image": String,
"status": enum,
"createdAt": String,
"updatedAt": String,
"canceledAt": String,
"users": [{
 "email": String,
 "name": String,
 "phoneNumber": number,
 }]
}]```  | Retrive all upcoming events |
| GET | `/api/users`| `response:` {response body}  | Retrive all users |
| PUT | `/api/users/update`| `response:` {response body}  | update a user |
| GET | `/api/users/delete/{id}`| `response:` {response body}  | Delete a specific user |
| PUT | `/api/event/cancelEvent/{id}`| `response:` status code  | Cancels a spesific event |
| GET | `/api/events` | `response:` [{response body}]  | Retrieve all events |
| PUT | `/api/events/{id}` | `response:` {response body}  | Updates an event |
| GET | `/api/events/{id}` | `response:` {response body}  | Retrieves a spesific event |
| GET | `/api/events/category/{category}`             | `response:` [{response body}]  | Retrieves the subset of all events that have a spcific category |
| GET | `/api/events/status/{status}`             | `response:` [{response body}]  | Retrieves the subset of all events that have a spcific status |
| GET | `/api/registrations/{id}`         | `response:` [{response body}]  | Retrieves all registrations to a spesific event |
| GET | `/api/registration/{userid}/{eventid}` | `response:` status code | Tells if the user is registed to a spesific event |

