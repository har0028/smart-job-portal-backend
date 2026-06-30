package com.smartjobportal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never",
    "app.admin.email=admin@test.com",
    "app.admin.password=Admin@123",
    "app.admin.name=Test Admin",
    "app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "app.jwt.expiration=900000",
    "app.upload.dir=uploads/test"
})
@DisplayName("Spring Application Context Integration Test")
class SmartJobPortalApplicationTests {

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // Passes if Spring context initializes without errors
    }
}
