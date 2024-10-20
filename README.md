# GYM CRM Application for EPAM Lab

## Overview

This Gym CRM application is designed to help gym trainees and trainers manage their profiles, track activities, and manage interactions. It allows trainees to select trainers and enables both parties to view, log, and update activity records. Additional features include profile activation/deactivation and the ability to modify profile details.

### Key Features

- **User Profiles**: Trainees and trainers can register, log in, and manage their profiles.
- **Trainer Selection**: Trainees can choose one or more trainers to work with.
- **Activity Logging**: Both trainees and trainers can log and view activities.
- **Profile Management**: Users can update their information and activate/deactivate their accounts.
- **Weekly Summary**: Trainers receive a weekly overview of their training hours.
- **Authentication**: A login is required for all pages, except the registration and profile creation pages.

### Profiles and Environment Setup

The application supports three profiles — **test**, **development (dev)**, and **production (prod)** — each with its own database:

- **Test**: Uses H2 in-memory database.
- **Development**: Uses MySQL database.
- **Production**: Uses PostgreSQL database.

Containers for the **test** and **production** environments are provided via Docker.

#### Profiles
- **test**: Uses H2 in-memory database, for both unit and integration testings.
- **dev**: Uses MySQL database, suitable for development work.
- **prod**: Uses PostgreSQL database for the production environment.

### Running the Application with Docker

To start the application using Docker, you need to specify the profile you'd like to run (either `test` or `prod`). Use the following command:

```bash
docker-compose --profile {profile} up --build -d
```
Replace `{profile}` with either `test` or `prod`.

For example, to start the production container:
```bash
docker-compose --profile prod up --build -d
```

**Stopping the Docker Containers**<br>
To stop the Docker containers, run:
```bash
docker-compose down
```

### Accessing the Application
Once the application is running, it will be available at:

* **Test**: http://localhost:8011/api
* **Development**: http://localhost:8080/api
* **Production**: http://localhost:9000/api

For API documentation and available endpoints, visit:

`http://localhost:{port}/api/swagger-ui/index.html#/`<br>
Replace `{port}` with the appropriate port for your environment:

* 8011 for **test** profile
* 8080 for **development** profile
* 9000 for **production** profile

