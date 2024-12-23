package ru.skillbox.mc_account.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.skillbox.mc_account.entity.Account;
import ru.skillbox.mc_account.entity.Role;
import ru.skillbox.mc_account.repository.AccountRepository;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() {
        createAdminUser();
        createRegularUser();
    }

    private void createAdminUser() {
        if (!accountRepository.findByEmail("admin@example.com").isPresent()) {
            Account admin = new Account();
            admin.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")); // Постоянный UUID для админа
            admin.setFirstName("Admin");
            admin.setLastName("Adminov");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("adminpassword"));
            admin.setPhone("+1234567890");
            admin.setPhoto("admin_photo_url");
            admin.setProfileCover("admin_cover_url");
            admin.setAbout("Administrator of the system");
            admin.setCity("AdminCity");
            admin.setCountry("AdminLand");
            admin.setStatusCode("ACTIVE");
            admin.setRegDate(Instant.now());
            admin.setBirthDate(Instant.parse("2000-01-01T00:00:00.000Z"));  // Пример даты
            admin.setMessagePermission("ALL");
            admin.setLastOnlineTime(Instant.now());
            admin.setEmojiStatus("😊");
            admin.setCreatedOn(Instant.now());
            admin.setUpdatedOn(Instant.now());
            admin.setDeletionTimestamp(null);
            admin.setDeleted(false);
            admin.setOnline(true);
            admin.setBlocked(false);
            admin.setRole(Role.ADMIN);

            accountRepository.save(admin);
        }
    }

    private void createRegularUser() {
        if (!accountRepository.findByEmail("user@example.com").isPresent()) {
            Account user = new Account();
            user.setId(UUID.fromString("987e6543-e21c-34b2-c567-527819174abc")); // Постоянный UUID для пользователя
            user.setFirstName("User");
            user.setLastName("Userov");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("userpassword"));
            user.setPhone("+0987654321");
            user.setPhoto("user_photo_url");
            user.setProfileCover("user_cover_url");
            user.setAbout("Regular user");
            user.setCity("UserCity");
            user.setCountry("UserLand");
            user.setStatusCode("ACTIVE");
            user.setRegDate(Instant.now());
            user.setBirthDate(Instant.parse("2000-01-01T00:00:00.000Z"));  // Пример даты
            user.setMessagePermission("FRIENDS_ONLY");
            user.setLastOnlineTime(Instant.now());
            user.setEmojiStatus("🙂");
            user.setCreatedOn(Instant.now());
            user.setUpdatedOn(Instant.now());
            user.setDeletionTimestamp(null);
            user.setDeleted(false);
            user.setOnline(true);
            user.setBlocked(false);
            user.setRole(Role.USER);

            accountRepository.save(user);
        }
    }
}


