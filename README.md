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

Double Function ( Key Generation + Logs )
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

Trigger 1# ( calculated duration on key creation (semi usefull) )

---
    CREATE OR REPLACE FUNCTION set_license_expiration_date()
    RETURNS TRIGGER AS $$
        BEGIN
      
        IF NEW.duration = 1 THEN
            NEW.expiration_date := CURRENT_DATE + INTERVAL '1 month';
        ELSIF NEW.duration = 6 THEN
            NEW.expiration_date := CURRENT_DATE + INTERVAL '6 months';
        ELSIF NEW.duration = 12 THEN
            NEW.expiration_date := CURRENT_DATE + INTERVAL '1 year';
        ELSE
            RAISE EXCEPTION 'Invalid duration for license key';
        END IF;
        
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;
    
    
    CREATE TRIGGER trigger_set_license_expiration_date
    BEFORE INSERT ON license_keys
    FOR EACH ROW
    EXECUTE FUNCTION set_license_expiration_date();
---

Trigger 2# ( Check for duplicates )

---

    CREATE OR REPLACE FUNCTION check_unique_license_key() 
    RETURNS TRIGGER AS $$
    BEGIN
        
        IF EXISTS (SELECT 1 FROM license_keys WHERE license_key = NEW.license_key) THEN
            RAISE EXCEPTION 'License key % already exists.', NEW.license_key; //DEBUG
        END IF;
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;
    
    
    CREATE TRIGGER trigger_check_unique_license_key
    BEFORE INSERT ON license_keys
    FOR EACH ROW
    EXECUTE FUNCTION check_unique_license_key();
---

Trigger 3# ( Everytime a key is created it also checks for other expired keys and deletes them  )

---
    CREATE OR REPLACE FUNCTION delete_expired_keys() 
    RETURNS TRIGGER AS $$
    BEGIN
        DELETE FROM license WHERE expiration_date <= NOW();
        RETURN NULL;
    END;
    $$ LANGUAGE plpgsql;
    
    CREATE TRIGGER trigger_delete_expired_keys
    AFTER INSERT OR UPDATE ON license
    FOR EACH ROW
    EXECUTE FUNCTION delete_expired_keys();
---

## 😒 Some boring random functions i included

---


---
        CREATE OR REPLACE FUNCTION get_license_keys()
        RETURNS TABLE (
            license_id INT,
            license_key VARCHAR,
            is_used BOOLEAN,
            username VARCHAR
        ) AS $$
        BEGIN
            RETURN QUERY 
            SELECT 
                l.id AS license_id, 
                l.license_key, 
                l.active AS is_used, 
                u.username
            FROM license l
            LEFT JOIN license_user lu ON l.id = lu.license_id
            LEFT JOIN users u ON lu.user_id = u.id;
        END;
$$ LANGUAGE plpgsql;

---
        CREATE OR REPLACE FUNCTION activate_license(p_license_id INT)
        RETURNS VOID AS
        $$
        BEGIN
            UPDATE license
            SET active = TRUE, updated_at = NOW()
            WHERE id = p_license_id;
        END;
        $$ LANGUAGE plpgsql;
---

---
        CREATE OR REPLACE FUNCTION check_license_key(p_license_key TEXT)
        RETURNS TABLE(license_id INT, is_valid BOOLEAN) AS
        $$
        BEGIN
            RETURN QUERY
            SELECT l.id, l.active
            FROM license l
            WHERE l.license_key = p_license_key
              AND l.active = FALSE
              AND l.expiration_date > NOW();
        END;
        $$ LANGUAGE plpgsql;

---

---
        CREATE OR replace FUNCTION FetchLocations() RETURNS TABLE (id INT, name TEXT) AS $$
        BEGIN
            RETURN QUERY SELECT id, name FROM location;
        END;
        $$ LANGUAGE plpgsql;
---

---
        CREATE FUNCTION IsUserAdmin(user_id INT) RETURNS BOOLEAN AS $$
        DECLARE
            admin_status BOOLEAN;
        BEGIN
            SELECT is_admin INTO admin_status FROM users WHERE id = IsUserAdmin.user_id;
            RETURN admin_status;
        END;
        $$ LANGUAGE plpgsql;

---



