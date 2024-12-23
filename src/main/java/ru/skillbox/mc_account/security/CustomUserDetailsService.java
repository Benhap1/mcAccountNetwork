package ru.skillbox.mc_account.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.mc_account.entity.Account;
import ru.skillbox.mc_account.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new AccountDetails(
                account.getId(),
                account.getFirstName(),
                account.getLastName(),
                null,
                account.getEmail(),
                account.getPassword(),
                account.getRole(),
                account.isBlocked(),
                account.isDeleted()
        );
    }
}
