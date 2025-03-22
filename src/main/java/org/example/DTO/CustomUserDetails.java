package org.example.DTO;

import org.example.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String numberPhone;
    private Long typeOrder;

    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.numberPhone = user.getNumberPhone();
        this.password = user.getPassword();
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() { return UserDetails.super.isAccountNonExpired(); }
    @Override
    public boolean isAccountNonLocked() { return UserDetails.super.isAccountNonLocked(); }
    @Override
    public boolean isCredentialsNonExpired() { return UserDetails.super.isCredentialsNonExpired(); }
    @Override
    public boolean isEnabled() { return UserDetails.super.isEnabled(); }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumberPhone() {
        return numberPhone;
    }
    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public Long getTypeOrder() { return typeOrder; }
    public void setTypeOrder(Long typeOrder) { this.typeOrder = typeOrder; }
}
