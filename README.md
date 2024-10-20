## calendar-assistant

* I've developed a backend solution to help employees book meetings, find free slots between two employees, and check meeting conflicts between participants. It's built using Spring Boot, Hibernate, and PostgreSQL, ensuring efficient meeting management.

# Technology Stack:

## Java, Maven, Spring Boot, Hibernate: 
* I chose Java as the programming language due to its robustness and widespread usage. Spring Boot simplifies the setup of Spring-based applications, while Hibernate facilitates object-relational mapping (ORM), streamlining database interactions.

## PostgreSQL Database Integration:
 * Utilized PostgreSQL as the database to store user, calendar, and meeting details. PostgreSQL provides robust support for concurrent access, reliability, and data integrity, making it ideal for handling complex queries, large datasets, and ensuring persistence in a production-grade environment.

## System Features
* Meeting Booking
 > Employees can book meetings using a RESTful API with time slots.
* Find Free Slots
 > The system can find common free time slots between two employees based on their schedules, allowing meetings of a fixed duration (like 30 minutes) to be scheduled.
* Conflict Detection
 > The system identifies participants with meeting conflicts when trying to schedule a new meeting.

## Application Setup Instructions
 * Clone the repository to your local machine:
  > git clone https://github.com/Mahii-12/calendar-assistant.git
 * Navigate to the Project Directory: 
  > cd CalendarAssistant
 * Build the project using Maven:
  > mvn clean install


## API Endpoints
* Book Meeting
> Endpoint: POST /api/meetings/book

 > Book a meeting by specifying the employee, start time, end time, and meeting details.

 ```json
{
  "employeeId": 1,
  "meetingTitle": "Project Discussion",
  "startTime": "2024-10-21T10:00:00",
  "endTime": "2024-10-21T11:00:00"
}
  ```
* Find Free Slots Between Two Employees
> Endpoint: GET /api/meetings/freeSlots

> Find free slots of a given duration between two employees' schedules.

> GET /api/meetings/freeSlots?employeeId1=1&employeeId2=2&duration=30
* Check for Meeting Conflicts
> Endpoint: POST /api/meetings/checkConflicts

> Given a new meeting, check if any of the participants have a conflict.

 ```json
{
  "employeeIds": [1, 2, 3],
  "startTime": "2024-10-21T10:00:00",
  "endTime": "2024-10-21T11:00:00"
}
 ```
