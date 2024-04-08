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
| GET | `/api/event/upcoming`| `response:` <br><pre lang="json">[{&#13; "id": number,&#13; "title": String,&#13; "startTime": String,&#13; "description": String,&#13; "dateOfEvent": String,&#13; "durationInHours: number,&#13; "maxNumberOfStudents: number,&#13; "locationOfEvent": String,&#13; "instructor": String,&#13; "price: number,&#13; "category": String,&#13; "image": String,&#13; "status": enum,&#13; "createdAt": String,&#13; "updatedAt": String,&#13; "canceledAt": String,&#13; "users": [{  "email": String,&#13;  "name": String,&#13; "phoneNumber": number,&#13; }]&#13}]</pre>  | Retrive all upcoming events |
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

```JSON
"name": name
```