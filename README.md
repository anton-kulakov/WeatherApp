# Weather App

Weather App is a web application that allows users to track current weather conditions.
This project is based on the technical specification provided in the Java Backend Roadmap by Sergey Zhukov (link [here](https://zhukovsd.github.io/java-backend-learning-course/projects/weather-viewer/)).

- [Built with](#built-with)
- [Functionality](#functionality)
- [Description of the application pages](#description-of-the-application-pages)
- [How to run locally using IntelliJ IDEA](#how-to-run-locally-using-intellij-idea)

## Built with

- Java
- Spring MVC
- Hibernate
- MySql database
- Flyway
- Apache Maven
- Apache Tomcat
- Lombok
- HTML/CSS
- Thymeleaf

## Functionality

The application enables users to:

- Sign up for an account
- Sign in using their credentials
- Search for locations by entering a query in the search bar
- Save locations to track weather conditions
- Remove locations from their saved list
- Log out of their account

## Description of the application pages

The project includes 5 pages:

- Sign up page: user registration interface
- Sign in page: authentication page for existing users
- Home page: displays saved locations and weather data
- Search results page: shows locations found based on user queries
- Error page: displays error messages for invalid routes or API failures

## How to run locally using IntelliJ IDEA
To run the application, make sure that you have Java (JDK), GIT, Apache Maven, Apache Tomcat and MySql database installed on your computer.
1. Download or clone the project

You can get the project by either downloading it as a ZIP archive or by cloning it using Git.

**Option 1: Download as ZIP Archive**
- Go to the project’s GitHub page
- Click the "Code" button
- Select "Download ZIP"
- Extract the ZIP file to a directory on your local machine.

**Option 2: Clone the Repository using Git**

Open your terminal or Git Bash and run the following command:

  ```bash
  git clone https://github.com/anton-kulakov/TennisScoreboard.git
  ```

2. In IntelliJ IDEA open the Database menu

![1  In IntelliJ IDEA open the Database menu](https://github.com/user-attachments/assets/d4195d1c-4e53-4134-8dc9-5005243462bc)

3. Click the plus button

![2  Click the plus button](https://github.com/user-attachments/assets/495c4521-bcb6-4abd-bd61-bfd56fed0cbb)

4. Choose MySql

![3  Choose MySql](https://github.com/user-attachments/assets/698ece07-aa78-48c7-aac7-b6c2f02aa46d)

5. Enter the username and password for MySQL

![4  Enter the username and password for MySQL](https://github.com/user-attachments/assets/c1a0d2b8-9cac-40a5-b944-b61cf847598d)

6. Download missing driver files if needed

7. Click "Test Connection"

![6  Click Test Connection](https://github.com/user-attachments/assets/701318d2-d22c-4930-b43f-25241d1b291d)

8. A successful connection message should be displayed

![8  A successful connection message should be displayed](https://github.com/user-attachments/assets/44be4dc1-ecef-4b44-bd2e-db7c4f6ab9e5)

Then click "Apply" and "OK".

9. Then right click. Chose New - Schema

![10  Then right click  Chose New - Schema](https://github.com/user-attachments/assets/be42d2dd-1097-4b0c-a46b-151873834bb7)

And create two schemas: 

- Main schema with name "database"
- And schema for testing with name "test_database"

10. Go to the configurations menu

![1  Go to the configurations menu](https://github.com/user-attachments/assets/e59d28ef-c385-4551-b63d-b897c69839f4)

11. Select "Edit configurations"

![2  Select Edit configurations](https://github.com/user-attachments/assets/49cf4038-bf76-465c-a1dd-5c0fcb97a29b)

12. Click the plus sign

![3  Click the plus sign](https://github.com/user-attachments/assets/c668675d-2168-4be2-ba29-932bf7c30178)

13. Select Tomcat Server -> Local

14. Select Tomcat as an Application server

![4  Select Tomcat as a Application server](https://github.com/user-attachments/assets/68211fa6-a9e1-4e43-bc15-99b77678182e)

15. Then in VM options type required environment variables

![16  Then in VM options type required environment variables](https://github.com/user-attachments/assets/d4495701-41ca-424c-9cfd-1a6e566a8b02)

Enter your values instead of [YOUR_API_KEY], [YOUR_DATABASE_USERNAME] and [YOUR_DATABASE_PASSWORD].

To make requests to the API, you need a key. To obtain it, you need to:

- Register at https://openweathermap.org/
- Create a free key with a limit of 60 requests per minute
- Wait for the key to be activated

16. Click "Fix"

![5  Click Fix](https://github.com/user-attachments/assets/3886edbe-a2af-477e-96ab-b0061b86d248)

17. Select "WeatherApp:war exploded"

18. Remove "WeatherApp_war_exploded" from the "Application context" field

19. Then click "Apply" and "OK"

20. Click "Run"

![21  Click Run](https://github.com/user-attachments/assets/465edfae-2b9b-4988-b3e7-e15555e05a99)


