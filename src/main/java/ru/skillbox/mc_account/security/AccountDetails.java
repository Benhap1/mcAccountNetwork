package ru.skillbox.mc_account.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skillbox.mc_account.entity.Role;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor
public class AccountDetails implements UserDetails {
    @Getter
    private final UUID id;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;

    @Getter
    @Setter
    private String token;

    private final String email;
    private final String password;
    private final Role role;
    private final boolean blocked;
    private final boolean deleted;




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role::name);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !blocked;
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }
}

