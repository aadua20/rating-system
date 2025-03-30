# 🎮 Rating System for In-Game Item Sellers

An independent, community-driven rating system for sellers of in-game items across popular games like CS:GO, FIFA, Dota, Team Fortress, and more. The platform ensures authenticity by verifying comments through trusted administrators, helping users make informed decisions based on transparent seller ratings.

---

## ✅ Project Description

This platform allows users to rate and review sellers of virtual in-game items. Reviews are verified before being factored into a seller's overall rating. Administrators manage the approval process for both sellers and user-submitted comments.

---

## 🧑‍💻 Roles on the Platform

- **Administrator**: Verifies seller registrations and user comments.
- **Seller**: A registered and approved user offering in-game items.
- **Anonymous User**: Can browse and submit comments on sellers.

---

## 🚀 User Scenarios

1. **Seller Registration**  
   Sellers submit a registration form. The admin reviews and either approves or rejects the request.

2. **Submitting a Comment**  
   Anonymous users can leave comments and ratings for sellers. These are reviewed by admins before being published.

---

## ⚙️ Core Functionality

- 📄 **Seller Profile Management**
- ✍️ **User Comment Submission**
- ⭐ **Rating Calculation for Sellers**
- 🏆 **Top Sellers Leaderboard**
- 🎯 **Filtering by Rating Ranges**

---

## 🔐 Registration & Authorization Flow

- Users provide registration info (email, password, role).
- System sends an email with a confirmation link (stored temporarily in **Redis** with a 24-hour expiration).
- Users must confirm their email before logging in.
- Login attempts with unconfirmed emails are blocked.

---

## 🛠️ Tech Stack

- **Java** with **Spring Boot**
- **Spring Security** for authentication & role-based access
- **JWT** for token-based authorization
- **Redis** for temporary confirmation code storage
- **PostgreSQL** for persistent data
- **JUnit & Mockito** for testing
- **Lombok** for boilerplate reduction, and **SLF4J** for logging

---

## 📦 Project Structure

```
└── ratingsystem/
   ├── config/        # Application config
   ├── controller/    # REST API endpoints
   ├── dto/           # Data transfer objects
   ├── entity/        # JPA entities (User, Comment, etc.)
   ├── exception/     # Exception classes
   ├── mapper/        # Entity-DTO mapping logic
   ├── repository/    # Spring Data JPA repositories
   ├── security/      # JWT, filters, auth providers
   ├── service/       # Business logic
   └── util/          # Helpers like Redis/email utilities
```

---

## 📬 Getting Started

1. **Clone the repo:**

   ```bash
   git clone https://github.com/aadua20/rating-system.git
   cd rating-system
   ```

2. **Set up your environment:**

   - cd `docker`
   - Update environment variables in your `docker-compose.yml` and `Dockerfile` if needed.
   - Ensure you have Docker installed and running.

3. **Build and run the containers:**

   ```bash
   docker compose up --build
   ```

4. **Test endpoints:**

   Use Postman, Swagger UI, or your frontend client to interact with the backend (usually on http://localhost:8080/rating-system/swagger-ui/index.html).

---
