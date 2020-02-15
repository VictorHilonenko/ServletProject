<div align="center">
	<a href="https://github.com/VictorHilonenko/testgit/blob/11_add_readme/README.md"><code>English</code></a>
	<a href="https://github.com/VictorHilonenko/testgit/blob/11_add_readme/README_uk.md"><code>Українська</code></a>
</div>
<hr>
<div align="center">
	<img src="src/main/webapp/images/logo_en.png">
</div>

# Beauty Salon Scheduler system
A web page should be created, which allows to view the work schedule of salon employees,
so a user could make an appointment.
Customer **Appointments** should only be seen by **Administrator** and **Master**, other users see the active appointment placeholder.
When the service is provided, the **Customer** leaves a feedback.
A suggestion to leave a feedback comes **by email**.
The **administrator** can read feedbacks about colleagues.

## Technologies
1. DB - MySql
1. Java 8
1. Maven

## Installation  

1. Copy (clone) project
1. Install MySQL if necessary 
1. Run: schema.sql from folder DUMP_DB
1. Run: populate.sql from folder DUMP_DB
1. Configure `/src/main/webapp/META-INF/context.xml` according to your DB settings
1. Run in terminal: ```mvn clean tomcat7:run``` or add configuration and run in IDE  
1. Go to: [```localhost:8989```](http://localhost:8989/)


## **_Clarification_** of conditions, **_extension_** of the task.
Before development the following conditions where specified:

1. There can be **multiple** Masters and Administrators.
1. Masters have **specialties**.
1. We consider that **working hours** of masters = working hours of salon (it is set **from 8 till 20**, with no days off).
1. Time for each service = **1 hour**.
1. If all the masters are **already engaged** for the time selected, the customer will not be able to enroll in it.
1. Customers and Masters see their Name on the active appointment placeholder if it is their "Record". 
Administrators see **detailed** information.
1. A Master can put a **"Service Provided"** mark in the "records" he is assigned to. At this point the email is sent to the Customer and he gets a possibility to write a feedback.
1. All registered users can read feedbacks, but only **Administrator** can see **all the feedbacks**.
Masters and Customer **can see** the feedbacks they **are related** to.