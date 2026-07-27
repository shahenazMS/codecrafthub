CodeCraftHub - Simple JSON-file REST CRUD for Courses

CodeCraftHub is a lightweight learning platform where developers can track courses they want to learn. It uses Spring Boot with a JSON file as storage, exposing a REST API for CRUD operations.

Overview

Tech: Java 17+ + Spring Boot
Storage: JSON file (no database)
Data model per course:
id (String)
name (String)
description (String)
targetDate (ISO date, e.g. 2026-12-31)
status (NOT_STARTED, IN_PROGRESS, COMPLETED)
API endpoints: CRUD (Create, Read, Update, Delete)
No authentication or user management
Project structure

The project is organized under the codecrafthub folder. All code, data, and configs live here.

codecrafthub/
pom.xml
data/
courses.json (managed by the app; created automatically if missing)
src/
main/
java/
com/
codecrafthub/
CodeCraftHubApplication.java
controller/
CourseController.java
model/
Course.java
CourseStatus.java
service/
CourseStorageService.java
resources/
application.properties (optional; you can customize port here)
Note: The application reads/writes data to a local data/courses.json file relative to the working directory. If you run from the codecrafthub folder, the path will be codecrafthub/data/courses.json.

How to run

Prerequisites:

JDK 17+ (LTS)
Maven
Steps (from the repository root):

Move into the project directory
cd codecrafthub
Build the project
mvn clean package
Run the application
java -jar target/codecrafthub-0.0.1-SNAPSHOT.jar
Or, for development speed, you can run with Maven:
mvn spring-boot:run
First run will create the data directory and an empty courses.json if needed:
data/courses.json will be created inside codecrafthub/data
Then you can interact with the API at:

http://localhost:8080/courses
Endpoints

Create a course

POST /courses
Request body (example): { "name": "Java Basics", "description": "Intro to Java", "targetDate": "2026-12-31", "status": "NOT_STARTED" }
Response: 201 Created with the created course (including generated id)
Get all courses

GET /courses
Response: 200 OK with a JSON array of courses
Get a course by ID

GET /courses/{id}
Response: 200 OK with the course, or 404 Not Found if not found
Update a course by ID

PUT /courses/{id}
Request body (partial updates are supported by leaving fields out): { "name": "Java Basics - Updated", "description": "Updated description", "targetDate": "2027-01-15", "status": "IN_PROGRESS" }
Response: 200 OK with the updated course, or 404 Not Found if not found
Delete a course by ID

DELETE /courses/{id}
Response: 204 No Content if deleted, or 404 Not Found if not found
Notes:

Target date uses ISO format (YYYY-MM-DD).
Status values: NOT_STARTED, IN_PROGRESS, COMPLETED.
Data storage format

The app stores data in a JSON array inside data/courses.json.
Example content: [ { "id": "a1b2c3", "name": "Java Basics", "description": "Intro to Java", "targetDate": "2026-12-31", "status": "NOT_STARTED" } ]
On startup, if data/courses.json is missing, the app creates the file with an empty array [].
Troubleshooting

Common issues and fixes:

No data directory or file missing

Ensure you run the app from the codecrafthub folder (where the data/ directory should live).
If data/courses.json isn’t created automatically, manually create codecrafthub/data and an empty courses.json, or let the app initialize it.
Port already in use

Default port is 8080. If in use, configure a different port in application.properties or via command line:
Add to application.properties: server.port=8081
Or run with: java -jar target/codecrafthub-0.0.1-SNAPSHOT.jar --server.port=8081
Invalid date or JSON payload

Ensure targetDate is in YYYY-MM-DD format.
Ensure JSON payload follows the field names and types described above.
If you get Jackson or parsing errors, check that data/courses.json contains valid JSON (or delete it to reset).
Updating a non-existent course

PUT /courses/{id} or DELETE /courses/{id} will return 404 Not Found if the id does not exist.
Concurrent writes or data corruption

This simple implementation uses in-memory storage synchronized for basic safety. For concurrent clients, consider restarting the app or inspecting data/courses.json; the app will recreate the file if missing.
No authentication

This is an intentionally simple REST API without authentication as requested.
Next steps (optional)

Add validation for required fields (e.g., name) and more robust error messages.
Extend the model with additional fields or relationships.
Add unit tests for storage and controller logic.
Create a small README or Postman collection for testing endpoints.
If you’d like, I can tailor this README to a Gradle setup or add additional sections (e.g., sample Postman collection, CI steps).