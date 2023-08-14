#  Online Examination Web Application for Medical University
## Project Title: BloodCount

**Table of Contents**
1. Introduction
2. Features
3. Requirements

**Introduction**
  
  BloodCount is a game in test format to establish the patience disease. The primary goal of this project is to provide an efficient and user-friendly platform for students, supervisors, and admins/root to conduct and manage exams seamlessly.
  
**Features**
- User roles: Student, Supervisor, Administrator and Root
- Secure login and authentication system
- Adding of cases (disease information with collection of blood count abnormalities) via form
- Dymanic generation of patient with attached blood count resulted by selected case for each conducted exam session
- Server side exam session for each user
- Auto save of selected answers during exam session when user logs off (Logged off user - when websocket disconnects)
- Automated grading and results generation
- Export users info into CSV

**Requirements**
- Server with Java 19, Spring Boot 2.7.10, Maven 3.8.6
- Client with Angular 15 or higher
- PostgreSQL
- Modern web browser (Chrome, Firefox, Safari, Edge)

*Deployed in Heroku* -> https://morphology-app-ceefa3648a59.herokuapp.com/login
*Client side application's repository* -> https://github.com/syio27/blood-count-frontend
