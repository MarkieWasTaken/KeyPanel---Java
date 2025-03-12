👀 License Key Management System
A simple Java application with a graphical user interface (GUI) for managing users and license keys. This application includes features such as generating license keys, assigning roles to users, and managing license expiration. It also integrates with a PostgreSQL database for persistent storage of user and license data, ensuring seamless operations for managing licenses at scale.

✨ Features
🔑 License Key Generation: Automatically generate unique license keys for different durations (1 month, 6 months, 1 year). These keys can be assigned to users and tracked.
👥 User Management: Admin users have the ability to view and manage users, including assigning licenses to users and managing their access to different functionalities.
🗝️ License Key Management: Admin users can view a list of all license keys, including details such as whether they are active, expired, or assigned to users. License keys can also be deleted manually or automatically when expired.
🗄️ Database Integration: The system uses PostgreSQL for storing user data, license keys, expiration dates, and more. This ensures persistent storage and data integrity, while enabling quick access to data through SQL queries.
⏳ Automatic Expiration Handling: The system automatically handles license expiration by deleting expired keys using PostgreSQL triggers. No manual intervention is required, which helps in maintaining a clean database without expired keys cluttering the system.
🔒 Role Management: The application allows for role-based access, where admins have full control over the system, while regular users have restricted access to their assigned licenses. Admin users can toggle roles and manage permissions as needed.
📗 Excel Extract: An admin feature is available that allows the export of all active users and their assigned license keys to an Excel document. This feature utilizes Apache POI to generate the Excel file with user details and license information, making reporting and analysis easy.
📅 Key Duration & Expiration: The license keys are designed to have an expiration date. The system tracks these dates and automatically manages the status of each license, marking it as expired when the date has passed.
🔧 Easy-to-Use GUI: The user interface is built with Java Swing, making it lightweight and user-friendly. Admins can perform all necessary tasks such as viewing license details, managing keys, and exporting data via a simple interface.
🚀 Fast and Scalable: The system can handle a large number of users and license keys, providing fast query responses and efficient database operations.
🤖 Technologies Used
Java: The primary programming language used for this application. Java provides the platform-independent nature, ensuring the system works across various operating systems.
Swing (GUI): Swing is used to build the graphical user interface for managing license keys, users, and other application functionalities. Swing offers a flexible way to create desktop applications in Java.
PostgreSQL: The relational database used for storing users, license keys, and other relevant data. PostgreSQL ensures data integrity, and the system leverages SQL triggers and functions for automatic expiration handling and key management.
Apache POI: A powerful library used to generate Excel files. Apache POI allows the extraction of user and license data into Excel files for reporting and analysis purposes.
JDBC: Java Database Connectivity (JDBC) is used to connect the Java application with the PostgreSQL database. It ensures smooth communication between the Java application and the database.
Maven/Gradle: For dependency management, you can use Maven or Gradle based on your setup. These tools simplify the process of managing project dependencies and building the project.
🛠️ Setup Instructions
Prerequisites
Before setting up the project, ensure that you have the following installed on your machine:

JDK 8 or higher: The project requires Java Development Kit (JDK) version 8 or higher. You can download and install JDK from Oracle.
PostgreSQL: The project uses PostgreSQL as its database backend. You can download and install PostgreSQL from here. You will also need to configure PostgreSQL on your machine and create a database for the application to connect to.
IDE (Integrated Development Environment): Any Java IDE such as IntelliJ IDEA, Eclipse, or NetBeans can be used for developing and running the project. Make sure your IDE is set up to handle Java projects and can import Maven/Gradle dependencies.
🧑‍💻 Clone the Repository
To get started, clone the repository to your local machine using the following Git command:

bash
Copy
Edit
git clone https://github.com/yourusername/license-key-management.git
cd license-key-management
📦 Install Dependencies
The project uses Maven for dependency management. To ensure all necessary libraries are included in your project, you can use Maven's clean and install commands:

bash
Copy
Edit
mvn clean install
This will download and install any required dependencies specified in the pom.xml file, such as the PostgreSQL JDBC driver, Apache POI, and other necessary libraries.

🔗 Set Up PostgreSQL Database
Create the Database: You need to create a PostgreSQL database for storing the application data (users, licenses, etc.). Log in to PostgreSQL and run the following command to create a new database:
bash
Copy
Edit
psql -U postgres
CREATE DATABASE license_key_management;
Run the SQL Script: The project includes an SQL script (schema.sql) that creates the necessary tables and functions in your database. Run the script to set up the database structure:
bash
Copy
Edit
psql -U postgres -d license_key_management -f schema.sql
Configure Database Connection: Edit the DatabaseConnection.java file to include your PostgreSQL database credentials:
java
Copy
Edit
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/license_key_management", "yourusername", "yourpassword");
}
🚀 Running the Application
Run the Application: After the dependencies are set up and the database is configured, you can run the application. You can do this through your IDE or by using Maven:
bash
Copy
Edit
mvn exec:java
Admin Login: Once the application is running, you can log in as an admin user. From there, you can:

View, generate, and delete license keys
View and manage users
Export data to Excel
Ensure that expired license keys are automatically deleted from the system.
User Interaction: Users assigned licenses can see the keys they have access to, and admins can toggle user roles and assign/unassign keys as needed.

📄 Excel Export Feature
One of the key features of this application is the ability to export user and license data to an Excel file. The export is done via Apache POI, and the file includes information about the username, email, license key, duration, and expiration date. Admins can click on the "Export" button in the application to generate an Excel file.

🌐 Future Enhancements
Web Interface: The application could be expanded to support a web-based interface using frameworks like Spring Boot and Thymeleaf for better scalability.
API Integration: APIs could be created to allow other systems to interact with the license management system, such as issuing keys through a web interface or a third-party service.
License Notifications: Implement a system for notifying users and admins when a license is about to expire.
License
This project is licensed under the MIT License - see the LICENSE file for details.
