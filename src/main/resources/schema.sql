-- ============================================================
-- Smart Job Portal - Database Schema
-- Engine: MySQL 8.x
-- Run before starting the application (or let JPA auto-create)
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_job_portal
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_job_portal;

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('ADMIN','RECRUITER','JOB_SEEKER') NOT NULL,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6),
    INDEX idx_user_email (email),
    INDEX idx_user_role  (role)
) ENGINE=InnoDB;

-- ── Seeker Profiles ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS seeker_profiles (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT NOT NULL UNIQUE,
    phone             VARCHAR(20),
    location          VARCHAR(100),
    bio               TEXT,
    resume_url        VARCHAR(500),
    years_experience  INT DEFAULT 0,
    updated_at        DATETIME(6),
    CONSTRAINT fk_seeker_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ── Recruiter Profiles ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS recruiter_profiles (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL UNIQUE,
    company_name     VARCHAR(150) NOT NULL,
    company_website  VARCHAR(200),
    designation      VARCHAR(100),
    updated_at       DATETIME(6),
    CONSTRAINT fk_recruiter_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ── Skills ────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS skills (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL UNIQUE,
    category  VARCHAR(50),
    INDEX idx_skill_name (name)
) ENGINE=InnoDB;

-- ── User Skills (Seeker ↔ Skill) ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_skills (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    seeker_profile_id   BIGINT NOT NULL,
    skill_id            BIGINT NOT NULL,
    proficiency_level   INT DEFAULT 1,
    UNIQUE KEY uq_user_skill (seeker_profile_id, skill_id),
    CONSTRAINT fk_us_seeker FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_us_skill  FOREIGN KEY (skill_id)          REFERENCES skills(id)           ON DELETE CASCADE
) ENGINE=InnoDB;

-- ── Jobs ──────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS jobs (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_profile_id      BIGINT NOT NULL,
    title                     VARCHAR(150) NOT NULL,
    description               TEXT NOT NULL,
    location                  VARCHAR(100),
    job_type                  ENUM('FULL_TIME','PART_TIME','CONTRACT','INTERNSHIP','REMOTE','HYBRID') DEFAULT 'FULL_TIME',
    salary_range              VARCHAR(50),
    years_experience_required INT DEFAULT 0,
    status                    ENUM('DRAFT','ACTIVE','CLOSED') NOT NULL DEFAULT 'ACTIVE',
    posted_at                 DATETIME(6) NOT NULL,
    updated_at                DATETIME(6),
    expires_at                DATETIME(6),
    INDEX idx_job_status    (status),
    INDEX idx_job_recruiter (recruiter_profile_id),
    CONSTRAINT fk_job_recruiter FOREIGN KEY (recruiter_profile_id) REFERENCES recruiter_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ── Job Skills (Job ↔ Skill) ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS job_skills (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id      BIGINT NOT NULL,
    skill_id    BIGINT NOT NULL,
    is_required BOOLEAN DEFAULT TRUE,
    UNIQUE KEY uq_job_skill (job_id, skill_id),
    CONSTRAINT fk_js_job   FOREIGN KEY (job_id)   REFERENCES jobs(id)   ON DELETE CASCADE,
    CONSTRAINT fk_js_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ── Applications ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS applications (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id            BIGINT NOT NULL,
    seeker_profile_id BIGINT NOT NULL,
    status            ENUM('PENDING','REVIEWING','SHORTLISTED','REJECTED','HIRED') NOT NULL DEFAULT 'PENDING',
    cover_letter      TEXT,
    match_score       DOUBLE DEFAULT 0.0,
    applied_at        DATETIME(6) NOT NULL,
    updated_at        DATETIME(6),
    UNIQUE KEY uq_application (job_id, seeker_profile_id),
    INDEX idx_app_seeker (seeker_profile_id),
    INDEX idx_app_job    (job_id),
    CONSTRAINT fk_app_job    FOREIGN KEY (job_id)            REFERENCES jobs(id)            ON DELETE CASCADE,
    CONSTRAINT fk_app_seeker FOREIGN KEY (seeker_profile_id) REFERENCES seeker_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ── Saved Jobs ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS saved_jobs (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT NOT NULL,
    job_id   BIGINT NOT NULL,
    saved_at DATETIME(6) NOT NULL,
    UNIQUE KEY uq_saved_job (user_id, job_id),
    CONSTRAINT fk_sj_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sj_job  FOREIGN KEY (job_id)  REFERENCES jobs(id)  ON DELETE CASCADE
) ENGINE=InnoDB;
