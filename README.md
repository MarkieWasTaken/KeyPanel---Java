# License Key Management System

A simple Java application with a graphical user interface (GUI) for managing users and license keys. This application includes features such as generating license keys, assigning roles to users, and managing license expiration. It also integrates with a PostgreSQL database for persistent storage of user and license data, ensuring seamless operations for managing licenses at scale.

---

## ✨ Features

- **🔑 License Key Generation**: Automatically generate unique license keys for different durations (1 month, 6 months, 1 year). These keys can be assigned to users and tracked.
- **👥 User Management**: Admin users have the ability to view and manage users, including assigning licenses to users and managing their access to different functionalities.
- **🗝️ License Key Management**: Admin users can view a list of all license keys, including details such as whether they are active, expired, or assigned to users. License keys can also be deleted manually or automatically when expired.
- **🗄️ Database Integration**: The system uses PostgreSQL for storing user data, license keys, expiration dates, and more. This ensures persistent storage and data integrity, while enabling quick access to data through SQL queries.
- **⏳ Automatic Expiration Handling**: The system automatically handles license expiration by deleting expired keys using PostgreSQL triggers. No manual intervention is required, which helps in maintaining a clean database without expired keys cluttering the system.
- **🔒 Role Management**: The application allows for role-based access, where admins have full control over the system, while regular users have restricted access to their assigned licenses. Admin users can toggle roles and manage permissions as needed.
- **📗 Excel Extract**: An admin feature is available that allows the export of all active users and their assigned license keys to an Excel document. This feature utilizes Apache POI to generate the Excel file with user details and license information, making reporting and analysis easy.
- **📅 Key Duration & Expiration**: The license keys are designed to have an expiration date. The system tracks these dates and automatically manages the status of each license, marking it as expired when the date has passed.
- **🔧 Easy-to-Use GUI**: The user interface is built with Java Swing, making it lightweight and user-friendly. Admins can perform all necessary tasks such as viewing license details, managing keys, and exporting data via a simple interface.
- **🚀 Fast and Scalable**: The system can handle a large number of users and license keys, providing fast query responses and efficient database operations.

---

## 🤖 Technologies Used

- **Java**: The primary programming language used for this application. Java provides the platform-independent nature, ensuring the system works across various operating systems.
- **Swing (GUI)**: Swing is used to build the graphical user interface for managing license keys, users, and other application functionalities. Swing offers a flexible way to create desktop applications in Java.
- **PostgreSQL**: The relational database used for storing users, license keys, and other relevant data. PostgreSQL ensures data integrity, and the system leverages SQL triggers and functions for automatic expiration handling and key management.
- **Apache POI**: A powerful library used to generate Excel files. Apache POI allows the extraction of user and license data into Excel files for reporting and analysis purposes.
- **JDBC**: Java Database Connectivity (JDBC) is used to connect the Java application with the PostgreSQL database. It ensures smooth communication between the Java application and the database.
- **Maven/Gradle**: For dependency management, you can use Maven or Gradle based on your setup. These tools simplify the process of managing project dependencies and building the project.

---

## 🛠️ Setup Instructions

### Prerequisites

Before setting up the project, ensure that you have the following installed on your machine:

- **JDK 8 or higher**: The project requires Java Development Kit (JDK) version 8 or higher. You can download and install JDK from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html).
- **PostgreSQL**: The project uses PostgreSQL as its database backend. You can download and install PostgreSQL from [here](https://www.postgresql.org/download/). You will also need to configure PostgreSQL on your machine and create a database for the application to connect to.
- **IDE (Integrated Development Environment)**: Any Java IDE such as IntelliJ IDEA, Eclipse, or NetBeans can be used for developing and running the project. Make sure your IDE is set up to handle Java projects and can import Maven/Gradle dependencies.

---

## 🤷‍♂️ My SQL Functions

---
  CREATE OR REPLACE FUNCTION generate_license_key_new(p_license_key TEXT, p_duration INT)
  RETURNS TEXT AS
  $$
  DECLARE
      v_expiration_date TIMESTAMP;
      v_admin_id INT := 1;  
  BEGIN
     
      v_expiration_date := NOW() + (p_duration || ' months')::interval;
  
      
      INSERT INTO license(license_key, duration, active, expiration_date, created_at, updated_at)
      VALUES (p_license_key, p_duration, FALSE, v_expiration_date, NOW(), NOW());
  
      
      INSERT INTO log(table_name, action, executed_by, executed_at)
      VALUES ('license', 'License key generated: ' || p_license_key, v_admin_id, NOW());
  
      
      RETURN p_license_key;
  END;
  $$ LANGUAGE plpgsql;
---

