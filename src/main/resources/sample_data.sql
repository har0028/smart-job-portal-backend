-- ============================================================
-- Smart Job Portal - Sample Seed Data
-- Run AFTER schema.sql
-- Passwords are BCrypt hash of "Password@123"
-- ============================================================

USE smart_job_portal;

-- ── Skills ────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO skills (name, category) VALUES
('java', 'Backend'),
('spring boot', 'Backend'),
('hibernate', 'Backend'),
('python', 'Backend'),
('django', 'Backend'),
('node.js', 'Backend'),
('express', 'Backend'),
('react', 'Frontend'),
('angular', 'Frontend'),
('vue.js', 'Frontend'),
('javascript', 'Frontend'),
('typescript', 'Frontend'),
('html', 'Frontend'),
('css', 'Frontend'),
('tailwind css', 'Frontend'),
('mysql', 'Database'),
('postgresql', 'Database'),
('mongodb', 'Database'),
('redis', 'Database'),
('elasticsearch', 'Database'),
('docker', 'DevOps'),
('kubernetes', 'DevOps'),
('jenkins', 'DevOps'),
('git', 'DevOps'),
('github actions', 'DevOps'),
('amazon web services', 'Cloud'),
('google cloud platform', 'Cloud'),
('microsoft azure', 'Cloud'),
('machine learning', 'AI/ML'),
('tensorflow', 'AI/ML'),
('pytorch', 'AI/ML'),
('rest api', 'Architecture'),
('graphql', 'Architecture'),
('microservices', 'Architecture'),
('spring security', 'Security'),
('jwt', 'Security');

-- ── Users ─────────────────────────────────────────────────────────────────────
-- Admin (password: Admin@123  — seeded automatically by app startup)
-- These are extra test accounts:

-- Recruiter 1
INSERT IGNORE INTO users (email, password_hash, full_name, role, is_active, created_at)
VALUES ('recruiter1@techcorp.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewkzJHJY1e5yFl3m',
        'Alice Recruiter', 'RECRUITER', TRUE, NOW());

-- Recruiter 2
INSERT IGNORE INTO users (email, password_hash, full_name, role, is_active, created_at)
VALUES ('recruiter2@startupxyz.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewkzJHJY1e5yFl3m',
        'Bob Recruiter', 'RECRUITER', TRUE, NOW());

-- Job Seeker 1
INSERT IGNORE INTO users (email, password_hash, full_name, role, is_active, created_at)
VALUES ('john.doe@gmail.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewkzJHJY1e5yFl3m',
        'John Doe', 'JOB_SEEKER', TRUE, NOW());

-- Job Seeker 2
INSERT IGNORE INTO users (email, password_hash, full_name, role, is_active, created_at)
VALUES ('jane.smith@gmail.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewkzJHJY1e5yFl3m',
        'Jane Smith', 'JOB_SEEKER', TRUE, NOW());

-- ── Recruiter Profiles ────────────────────────────────────────────────────────
INSERT IGNORE INTO recruiter_profiles (user_id, company_name, company_website, designation, updated_at)
SELECT id, 'TechCorp Solutions', 'https://techcorp.com', 'Senior HR Manager', NOW()
FROM users WHERE email = 'recruiter1@techcorp.com';

INSERT IGNORE INTO recruiter_profiles (user_id, company_name, company_website, designation, updated_at)
SELECT id, 'StartupXYZ', 'https://startupxyz.io', 'Talent Acquisition Lead', NOW()
FROM users WHERE email = 'recruiter2@startupxyz.com';

-- ── Seeker Profiles ───────────────────────────────────────────────────────────
INSERT IGNORE INTO seeker_profiles (user_id, phone, location, bio, years_experience, updated_at)
SELECT id, '+91-9876543210', 'Bangalore', 'Experienced Java backend developer', 4, NOW()
FROM users WHERE email = 'john.doe@gmail.com';

INSERT IGNORE INTO seeker_profiles (user_id, phone, location, bio, years_experience, updated_at)
SELECT id, '+91-9123456780', 'Hyderabad', 'Full stack developer with React and Node.js', 2, NOW()
FROM users WHERE email = 'jane.smith@gmail.com';

-- ── User Skills ───────────────────────────────────────────────────────────────
-- John Doe: Java, Spring Boot, MySQL, Docker, Git, REST API
INSERT IGNORE INTO user_skills (seeker_profile_id, skill_id, proficiency_level)
SELECT sp.id, sk.id, 5
FROM seeker_profiles sp JOIN users u ON sp.user_id = u.id
JOIN skills sk ON sk.name IN ('java', 'spring boot', 'mysql', 'docker', 'git', 'rest api', 'hibernate', 'spring security')
WHERE u.email = 'john.doe@gmail.com';

-- Jane Smith: React, JavaScript, TypeScript, Node.js, MongoDB, CSS
INSERT IGNORE INTO user_skills (seeker_profile_id, skill_id, proficiency_level)
SELECT sp.id, sk.id, 4
FROM seeker_profiles sp JOIN users u ON sp.user_id = u.id
JOIN skills sk ON sk.name IN ('react', 'javascript', 'typescript', 'node.js', 'mongodb', 'css', 'html', 'tailwind css')
WHERE u.email = 'jane.smith@gmail.com';

-- ── Jobs ──────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO jobs (recruiter_profile_id, title, description, location, job_type,
                         salary_range, years_experience_required, status, posted_at, expires_at)
SELECT rp.id,
       'Senior Java Backend Developer',
       'We are looking for a Senior Java Developer with Spring Boot expertise. You will design and build scalable microservices and REST APIs.',
       'Bangalore', 'FULL_TIME', '15-25 LPA', 3, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM recruiter_profiles rp JOIN users u ON rp.user_id = u.id WHERE u.email = 'recruiter1@techcorp.com';

INSERT IGNORE INTO jobs (recruiter_profile_id, title, description, location, job_type,
                         salary_range, years_experience_required, status, posted_at, expires_at)
SELECT rp.id,
       'Full Stack React + Node Developer',
       'Join our fast-growing startup as a full stack engineer. Build modern web apps using React on the frontend and Node.js on the backend.',
       'Remote', 'REMOTE', '10-18 LPA', 2, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM recruiter_profiles rp JOIN users u ON rp.user_id = u.id WHERE u.email = 'recruiter2@startupxyz.com';

INSERT IGNORE INTO jobs (recruiter_profile_id, title, description, location, job_type,
                         salary_range, years_experience_required, status, posted_at, expires_at)
SELECT rp.id,
       'DevOps / Cloud Engineer',
       'Manage CI/CD pipelines, Kubernetes clusters, and AWS infrastructure for our growing platform.',
       'Hyderabad', 'FULL_TIME', '12-20 LPA', 3, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM recruiter_profiles rp JOIN users u ON rp.user_id = u.id WHERE u.email = 'recruiter1@techcorp.com';

INSERT IGNORE INTO jobs (recruiter_profile_id, title, description, location, job_type,
                         salary_range, years_experience_required, status, posted_at, expires_at)
SELECT rp.id,
       'Python ML Engineer',
       'Work on building and deploying machine learning models using Python, TensorFlow and PyTorch.',
       'Pune', 'HYBRID', '18-30 LPA', 4, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM recruiter_profiles rp JOIN users u ON rp.user_id = u.id WHERE u.email = 'recruiter2@startupxyz.com';

-- ── Job Skills ────────────────────────────────────────────────────────────────
-- Job 1 (Senior Java): Java, Spring Boot, Hibernate, MySQL, Docker, REST API (required) + Kubernetes (optional)
INSERT IGNORE INTO job_skills (job_id, skill_id, is_required)
SELECT j.id, sk.id, TRUE
FROM jobs j, skills sk
WHERE j.title = 'Senior Java Backend Developer'
AND sk.name IN ('java', 'spring boot', 'hibernate', 'mysql', 'docker', 'rest api', 'spring security');

INSERT IGNORE INTO job_skills (job_id, skill_id, is_required)
SELECT j.id, sk.id, FALSE
FROM jobs j, skills sk
WHERE j.title = 'Senior Java Backend Developer' AND sk.name IN ('kubernetes', 'microservices');

-- Job 2 (Full Stack): React, JavaScript, Node.js, MongoDB, TypeScript (required) + GraphQL (optional)
INSERT IGNORE INTO job_skills (job_id, skill_id, is_required)
SELECT j.id, sk.id, TRUE
FROM jobs j, skills sk
WHERE j.title = 'Full Stack React + Node Developer'
AND sk.name IN ('react', 'javascript', 'node.js', 'mongodb', 'typescript');

INSERT IGNORE INTO job_skills (job_id, skill_id, is_required)
SELECT j.id, sk.id, FALSE
FROM jobs j, skills sk
WHERE j.title = 'Full Stack React + Node Developer' AND sk.name IN ('graphql', 'redis');

-- Job 3 (DevOps): Docker, Kubernetes, Jenkins, Amazon Web Services, Git (required)
INSERT IGNORE INTO job_skills (job_id, skill_id, is_required)
SELECT j.id, sk.id, TRUE
FROM jobs j, skills sk
WHERE j.title = 'DevOps / Cloud Engineer'
AND sk.name IN ('docker', 'kubernetes', 'jenkins', 'amazon web services', 'git', 'github actions');

-- Job 4 (ML): Python, TensorFlow, PyTorch, Machine Learning (required)
INSERT IGNORE INTO job_skills (job_id, skill_id, is_required)
SELECT j.id, sk.id, TRUE
FROM jobs j, skills sk
WHERE j.title = 'Python ML Engineer'
AND sk.name IN ('python', 'tensorflow', 'pytorch', 'machine learning', 'postgresql');
