drop database probono_db;
create database probono_db;
use probono_db;
CREATE TABLE users (
                          loginId VARCHAR(20) NOT NULL,
                          loginPw VARCHAR(255) NOT NULL,
                          name VARCHAR(40) NOT NULL,
                          serialNumber INT NULL UNIQUE,
                          sex ENUM('FEMALE', 'MALE'),
                          grade ENUM('LOWER_KG', 'NURSERY', 'PLAYGROUP', 'UPPER_KG', 'CLASS1', 'CLASS2', 'CLASS3', 'CLASS4', 'CLASS5', 'CLASS6', 'CLASS7', 'CLASS8', 'CLASS9', 'CLASS10', 'GRADUATED') ,
                          phoneNum VARCHAR(20),
                          pwAnswer VARCHAR(50),
                          birth DATE,
                          fatherPhoneNum VARCHAR(20),
                          motherPhoneNum VARCHAR(20),
                          guardiansPhoneNum VARCHAR(20),
                          role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL,
                          status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                          createdAt TIMESTAMP NOT NULL,
                          updatedAt TIMESTAMP NOT NULL,
                          createdChargeId BIGINT NOT NULL,
                          updatedChargeId BIGINT,
                          classId BIGINT,
                          imageId BIGINT,
                          PRIMARY KEY (loginId)
);
CREATE TABLE notice (
                        noticeId BIGINT NOT NULL AUTO_INCREMENT,
                        title VARCHAR(100) NOT NULL,
                        content TEXT,
                        status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                        createdAt TIMESTAMP NOT NULL,
                        updatedAt TIMESTAMP NOT NULL,
                        createdChargeId BIGINT NOT NULL,
                        updatedChargeId BIGINT,
                        views INT NOT NULL DEFAULT 0,
                        type ENUM('CLASS', 'SCHOOL','COURSE') NOT NULL,
                        classId BIGINT,
                        loginId VARCHAR(20),
                        courseId BIGINT,
                        PRIMARY KEY (noticeId)
);
CREATE TABLE classes (
                       classId BIGINT NOT NULL AUTO_INCREMENT,
                       year INT NOT NULL,
                       grade ENUM('LOWER_KG', 'NURSERY', 'PLAYGROUP', 'UPPER_KG', 'CLASS1', 'CLASS2', 'CLASS3', 'CLASS4', 'CLASS5', 'CLASS6', 'CLASS7', 'CLASS8', 'CLASS9', 'CLASS10', 'GRADUATED') NOT NULL,
                       section ENUM('A','B') NOT NULL,
                       status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                       createdAt TIMESTAMP NOT NULL,
                       updatedAt TIMESTAMP NOT NULL,
                       createdChargeId BIGINT NOT NULL,
                       updatedChargeId BIGINT,
                       PRIMARY KEY (classId)
);
CREATE TABLE subject (
                         subjectId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(20) NOT NULL UNIQUE,
                         isElective BOOLEAN NOT NULL,
                         status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                         createdAt TIMESTAMP NOT NULL,
                         updatedAt TIMESTAMP NOT NULL,
                         createdChargeId BIGINT NOT NULL,
                         updatedChargeId BIGINT NULL
);
CREATE TABLE course (
                        courseId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                        createdAt TIMESTAMP NOT NULL,
                        updatedAt TIMESTAMP NOT NULL,
                        createdChargeId BIGINT NOT NULL,
                        updatedChargeId BIGINT NULL,
                        subjectId BIGINT,
                        classId BIGINT
);
CREATE TABLE course_user (
                             cuId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                             createdAt TIMESTAMP NOT NULL,
                             updatedAt TIMESTAMP NOT NULL,
                             createdChargeId BIGINT NOT NULL,
                             updatedChargeId BIGINT NULL,
                             courseId BIGINT,
                             loginId VARCHAR(20)
);
CREATE TABLE image (
                             imageId BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                             createdAt TIMESTAMP NOT NULL,
                             updatedAt TIMESTAMP NOT NULL,
                             createdChargeId BIGINT NOT NULL,
                             updatedChargeId BIGINT NULL,
                             noticeId BIGINT,
                             imagePath VARCHAR(2048) NOT NULL
);

ALTER TABLE users
ADD FOREIGN KEY(classId) REFERENCES classes(classId);

ALTER TABLE users
ADD FOREIGN KEY(imageId) REFERENCES image(imageId);

ALTER TABLE notice
ADD FOREIGN KEY(loginId) REFERENCES users(loginId);

ALTER TABLE notice
    ADD FOREIGN KEY(classId) REFERENCES classes(classId);

ALTER TABLE notice
    ADD FOREIGN KEY(courseId) REFERENCES course(courseId);

ALTER TABLE course
    ADD FOREIGN KEY(subjectId) REFERENCES subject(subjectId);

ALTER TABLE course
    ADD FOREIGN KEY(classId) REFERENCES classes(classId);

ALTER TABLE course_user
    ADD FOREIGN KEY(courseId) REFERENCES course(courseId);

ALTER TABLE course_user
    ADD FOREIGN KEY(loginId) REFERENCES users(loginId);

ALTER TABLE image
    ADD FOREIGN KEY(noticeId) REFERENCES notice(noticeId);

