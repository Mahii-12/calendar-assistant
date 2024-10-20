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
  > cd calendar-assistant
 * Build the project using Maven:
  > mvn clean install

## Business Logic Overview
* Booking a Meeting:

> Check if the employee already has another meeting during the requested time slot.
> If not, save the meeting in the database.

* Finding Free Slots:
> Compare the schedules of two employees.
> Find time slots where both employees are free.
> Return available slots for a meeting of a given duration (e.g., 30 minutes).

* Conflict Checking:

> For a new meeting request, check each participant’s schedule.
> Identify participants who have conflicting meetings.
