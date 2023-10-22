#  Online Examination Web Application for Medical University
## Project Title: BloodCount

<div align="center">

![alt text](https://github.com/syio27/blood-count-backend/blob/main/BClogo.png)

</div>

**Table of Contents**
1. Introduction
2. Features
3. Requirements

**Introduction**
  
  BloodCount is a game in test format to establish the patient's disease. The primary goal of this project is to provide an efficient and user-friendly platform for students, supervisors, and admins/root to conduct and manage exams seamlessly.
  
**Features**
- User roles: Student, Supervisor, Administrator and Root
- User profile, password change, and page to view history of conducted exams (games)
- User invitation via the form with the granted role, invited user receives an email
- Secure login and authentication system (login, register, forgot password flow)
- Adding cases (disease-patient information with a collection of blood count abnormalities) via a user-friendly form available for Admin/Root users
- Dynamic generation of a patient with attached blood count resulted from the selected case for each conducted exam(game) session
- Server-side exam session for each user
- Autosave selected answers during the exam session
- Automated grading and results generation
- Export user's info into CSV
- Localization - English and Polish languages
- Fully responsive web application -> mobile (portrait/landscape modes), desktop, tablet

*UI screenshots* -> https://github.com/syio27/blood-count-backend/tree/main/ui-screenshots

**Requirements**
- Server with Java 17, Spring Boot 2.7.10, Maven 3.8.6
- Client with Angular 16 or higher
- PostgreSQL
- Modern web browser (Chrome, Firefox, Safari, Edge)

*Deployed in Heroku* -> https://morphology-app-ceefa3648a59.herokuapp.com/login

*Client side application's repository* -> https://github.com/syio27/blood-count-frontend

