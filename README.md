# Learning Management System (LMS)

A full-stack Learning Management System built with **Spring Boot** and **React**, featuring role-based access control, JWT authentication, email verification, and course enrollment management.

## Features

### Authentication & Security
- User registration with **mandatory email verification** via OTP (one-time password)
- Secure login with JWT-based authentication
- Passwords hashed with **BCrypt**
- Forgot password / reset password flow via OTP
- Role-based access control (RBAC) enforced at the API level with Spring Security

### Roles & Permissions

| Role | Capabilities |
|---|---|
| **Student** | Browse courses, enroll in courses, view enrolled courses on dashboard |
| **Instructor** | Create courses, edit/delete **only their own** courses, view enrolled students per course, remove students from their courses |
| **Admin** | Full access to all courses (edit/delete any), manage all registered users (promote/demote roles, delete accounts), platform-wide dashboard view |

### Core Functionality
- Full CRUD for courses (title, description, instructor name, duration)
- Student enrollment system with duplicate-enrollment prevention
- Cascade-safe course deletion (automatically removes related enrollments)
- Role-aware dashboard — shows different data depending on who's logged in
- Admin panel for user management

## Tech Stack

**Backend**
- Java 21, Spring Boot
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- MySQL
- BCrypt password hashing
- Resend API for transactional email (OTP delivery)
- Maven

**Frontend**
- React (Vite)
- React Router
- Tailwind CSS
- Axios

## Project Structure

```
learning_management_system/     # Spring Boot backend
├── src/main/java/com/lms/system/
│   ├── controller/              # REST endpoints
│   ├── service/                 # Business logic
│   ├── repository/               # Data access (Spring Data JPA)
│   ├── model/                    # JPA entities
│   ├── dto/                      # Request/response objects
│   ├── security/                 # JWT filter, user details service
│   └── config/                   # Security & CORS configuration
└── Dockerfile

lms-frontend/                    # React frontend
└── src/
    ├── pages/                   # Login, Register, Courses, Dashboard, Admin, etc.
    ├── components/               # Navbar and shared components
    ├── context/                  # Auth state (JWT, user info)
    └── api/                      # Centralized Axios instance
```

## API Overview

| Endpoint | Method | Access |
|---|---|---|
| `/api/auth/register` | POST | Public |
| `/api/auth/login` | POST | Public (blocks unverified accounts) |
| `/api/auth/verify-email` | POST | Public |
| `/api/auth/forgot-password` | POST | Public |
| `/api/auth/reset-password` | POST | Public |
| `/api/courses` | GET | Authenticated |
| `/api/courses` | POST/PUT/DELETE | Instructor (own courses) / Admin |
| `/api/enrollments` | POST/GET/DELETE | Student / Admin |
| `/api/enrollments/course/{id}` | GET/DELETE | Instructor (own courses) / Admin |
| `/api/dashboard` | GET | Authenticated (role-aware response) |
| `/api/admin/users` | GET/PUT/DELETE | Admin only |

## Getting Started

### Backend

1. Clone the repo and navigate to the backend folder.
2. Set the following environment variables (or use a local `application.properties`):

```
DB_URL=jdbc:mysql://localhost:3306/course_service_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=a_long_random_secret_string
RESEND_API_KEY=your_resend_api_key
RESEND_FROM_EMAIL=onboarding@resend.dev
```

3. Run with Maven:
```
./mvnw spring-boot:run
```

### Frontend

1. Navigate to the frontend folder.
2. Create a `.env` file:
```
VITE_API_URL=http://localhost:8081/api
```
3. Install and run:
```
npm install
npm run dev
```

## Deployment

- **Backend**: Deployed via Docker on Render
- **Database**: MySQL hosted on Aiven
- **Email**: Resend API (chosen over SMTP since many free-tier hosts block outbound SMTP ports)
- **Frontend**: Deployable to Vercel or Netlify with `VITE_API_URL` pointing to the live backend

## Notable Design Decisions

- **DTOs separate the API contract from database entities** — request/response shapes are never tied directly to JPA entities.
- **Enrollment is a dedicated entity** (not a raw many-to-many join) to allow tracking metadata like enrollment timestamps.
- **Ownership checks** ensure instructors can only manage courses they personally created, while admins can manage anything.
- **JWT is stateless** — no server-side session storage; every request is independently authenticated via its token.
- **Email verification is mandatory** — registration does not issue a usable session until the OTP is confirmed.

## License

This project was built as a personal learning/portfolio project.
