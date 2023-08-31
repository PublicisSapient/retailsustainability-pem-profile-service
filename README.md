# Profile Service

The Profile Service is a Spring Boot application that provides user details functionalities. It utilizes MongoDB for data storage, Image Service for handling product images and integrates with Swagger for API documentation.


## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Error Messages](#error-messages)
- [Contributing](#contributing)
- [License](#license)



## Features

- Creating User Account and saving to MongoDB database
- Updating User Profile
- Change User Password
- Get User Details
- Delete User Account with deleting all Products associated with the user
- API documentation using Swagger
- Error messages for common scenarios



## Technologies Used

- Java 17
- Spring Boot 3.0.5
- MongoDB
- Swagger (OpenAPI)
- Actuator
- Feign Client
- Jacoco



## Getting Started

To get started with the Product Service, follow these steps:

1. Clone the repository: `git clone https://github.com/PublicisSapient/retailsustainability-pem-profile-service.git`
2. Clone the repository: `git clone https://github.com/PublicisSapient/retailsustainability-pem-common-framework.git`
3. Navigate to the common framework directory: `cd common-framework`
4. Build the common-framework: `mvn clean build`
5. Configure the environment variables to your environment (see [Configuration](#configuration))
6. Build the project: `mvn clean build`
7. Navigate to the target directory: `cd target`
8. Run the application: `java -jar profile-service.jar`

The service will start running on the configured port (default: 9004). You can access the APIs using the base URL `http://localhost:9004/api/v1/profile-service` and Swagger UI: `http://localhost:9004/api/v1/profile-service/swagger-ui`



## Configuration

The application can be configured using the following properties:

- **mongodb-root-password**: The Password for MongoDB database
- **MONGODB_SERVICE_HOST**: The host of MongoDB (e.g. localhost)
- **MONGODB_SERVICE_PORT**: The port of the MongoDB database (e.g. 27017)
- **SVC_IMG**: The url to Image Service (e.g. http://localhost:9002/api/v1, https://p2pmarket.dev/api/v1)
- **SVC_PDT**: The url to Profile Service (e.g. http://localhost:9000/api/v1, https://p2pmarket.dev/api/v1)

Ensure that you have MongoDB installed and running before starting the service.



## API Documentation

The Profile Service provides API documentation using Swagger(Open API). You can access the Swagger UI by navigating to `http://localhost:9004/api/v1/profile-service/swagger-ui` in your web browser. This UI provides detailed information about the available API endpoints, request/response schemas



## Error Messages

The application defines a set of error messages for common scenarios. These messages are configurable and can be found in the `application.yml` file. You can customize the error messages according to your needs.



## Contributing

Contributions to the Product Service are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

1. Fork the repository.
2. Create your feature branch: `git checkout -b feature/my-new-feature`.
3. Commit your changes: `git commit -m 'Add some feature'`.
4. Push to the branch: `git push origin feature/my-new-feature`.
5. Submit a pull request.



## License

The Product Service is open-source and available under the [MIT License](https://opensource.org/licenses/MIT).

Feel free to modify and adapt the code to suit your needs.

