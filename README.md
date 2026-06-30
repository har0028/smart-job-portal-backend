# Smart Job Portal — Backend

**Stack:** Java 21 · Spring Boot 3.2.5 · MySQL · Spring Security · JWT · Hibernate · Maven

---

## Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 21+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---

## 1. Database Setup

```sql
-- In MySQL shell:
CREATE DATABASE smart_job_portal
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Optionally run the schema manually:
```bash
mysql -u root -p smart_job_portal < src/main/resources/schema.sql
mysql -u root -p smart_job_portal < src/main/resources/sample_data.sql
```

---

## 2. Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_job_portal?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USER
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

---

## 3. Run the Application

```bash
# From project root
mvn clean spring-boot:run
```

Or build the jar:
```bash
mvn clean package -DskipTests
java -jar target/smart-job-portal-backend-1.0.0.jar
```

The server starts on **http://localhost:8080**

---

## 4. Admin Credentials (Auto-Seeded)

| Field | Value |
|---|---|
| Email | admin@smartjobportal.com |
| Password | Admin@123 |

> Change these in `application.properties` before production.

---

## 5. Sample Test User Credentials

All sample users use password: **Password@123**

| Role | Email |
|---|---|
| Recruiter | recruiter1@techcorp.com |
| Recruiter | recruiter2@startupxyz.com |
| Job Seeker | john.doe@gmail.com |
| Job Seeker | jane.smith@gmail.com |

---

## 6. API Usage Flow

### Step 1 — Register or Login
```bash
POST /api/auth/login
{ "email": "admin@smartjobportal.com", "password": "Admin@123" }
```
Copy the `accessToken` from the response.

### Step 2 — Use token in all protected requests
```
Authorization: Bearer <your-token>
```

### Step 3 — Seeker Recommendation Flow
1. Login as job seeker
2. `PUT /api/seeker/profile` — set location and experience
3. `POST /api/seeker/skills` — add skill IDs (get from `/api/skills`)
4. `GET /api/recommendations` — get ranked job recommendations

---

## 7. Key Endpoints Quick Reference

| Endpoint | Method | Auth | Description |
|---|---|---|---|
| `/api/auth/register` | POST | Public | Register new user |
| `/api/auth/login` | POST | Public | Login, get JWT |
| `/api/jobs` | GET | Public | Search/filter jobs |
| `/api/jobs/{id}` | GET | Public | Job detail |
| `/api/skills` | GET | Public | List all skills |
| `/api/admin/dashboard` | GET | ADMIN | Platform stats |
| `/api/admin/users` | GET | ADMIN | All users |
| `/api/admin/users/{id}/block` | PATCH | ADMIN | Block/unblock user |
| `/api/recruiter/jobs` | POST | RECRUITER | Create job |
| `/api/recruiter/jobs/{id}/applicants` | GET | RECRUITER | View applicants |
| `/api/recruiter/applications/{id}/status` | PATCH | RECRUITER | Update status |
| `/api/seeker/profile` | GET/PUT | JOB_SEEKER | Profile management |
| `/api/seeker/skills` | POST | JOB_SEEKER | Add skill |
| `/api/seeker/resume` | POST | JOB_SEEKER | Upload resume (PDF) |
| `/api/seeker/jobs/{id}/apply` | POST | JOB_SEEKER | Apply for job |
| `/api/seeker/applications` | GET | JOB_SEEKER | My applications |
| `/api/seeker/saved-jobs` | GET | JOB_SEEKER | Saved jobs |
| `/api/recommendations` | GET | JOB_SEEKER | AI recommendations |
| `/api/recommendations/{id}/score` | GET | JOB_SEEKER | Score for one job |

---

## 8. Project Structure

```
src/main/java/com/smartjobportal/
├── SmartJobPortalApplication.java   ← Entry point + admin seeder
├── ai/                              ← Recommendation Engine
│   ├── SkillNormalizer.java
│   ├── JaccardScorer.java
│   ├── MatchResult.java
│   └── RecommendationExplainer.java
├── config/                          ← Security, CORS, JPA config
├── controller/                      ← REST controllers
├── dto/request/                     ← Validated input DTOs
├── dto/response/                    ← Output DTOs (no entity leakage)
├── entity/                          ← JPA entities
├── enums/                           ← Role, JobStatus, ApplicationStatus, JobType
├── exception/                       ← Custom exceptions + global handler
├── repository/                      ← Spring Data JPA repositories
├── security/                        ← JWT filter, service, UserDetails
├── service/                         ← Service interfaces
├── service/impl/                    ← Service implementations
└── util/                            ← SecurityUtils, FileStorageUtil
```

---

## 9. AI Recommendation Engine

The engine in `RecommendationServiceImpl` runs a three-factor weighted score for every active job:

```
finalScore = (skillScore × 0.70)
           + (experienceScore × 0.20)
           + (recencyScore × 0.10)
```

**Skill score** uses Jaccard similarity over required skills only:
```
skillScore = |matched_skills| / |required_skills| × 100
```

**Experience score** penalises under-experience linearly:
```
experienceScore = max(0, 100 - (gap_years × 15))
```

**Recency score** decays from 100 (≤7 days old) to 0 (≥30 days old).

Results are sorted descending by `finalScore`. Each result carries matched skills, missing skills, and a human-readable explanation string.

---

## 10. Postman Collection

Import `SmartJobPortal.postman_collection.json` into Postman.

The collection auto-saves tokens to collection variables after login requests. Run in this order:
1. **Login Admin** → saves `adminToken`
2. **Login Recruiter** → saves `recruiterToken`
3. **Login Job Seeker** → saves `seekerToken`

---

## 11. Production Checklist

- [ ] Change JWT secret key in `application.properties`
- [ ] Change admin credentials
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure production database credentials
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Configure S3/cloud storage instead of local disk for resumes
- [ ] Add HTTPS (TLS certificate)
- [ ] Whitelist only production frontend origin in `CorsConfig.java`
- [ ] Enable structured logging (Logback JSON appender)
