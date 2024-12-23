package ru.skillbox.mc_account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.mc_account.entity.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByEmail(String email);

    @Query(value = """
    SELECT * 
    FROM accounts a
    WHERE EXTRACT(MONTH FROM (a.birth_date AT TIME ZONE 'UTC')) = EXTRACT(MONTH FROM now() AT TIME ZONE 'UTC')
      AND EXTRACT(DAY FROM (a.birth_date AT TIME ZONE 'UTC')) = EXTRACT(DAY FROM now() AT TIME ZONE 'UTC')
    """, nativeQuery = true)
    List<Account> findByTodayBirthday();

    Optional<Account> findById(UUID id);
}

