package com.example.aslapp_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })

public class user implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public user(String username,  String password, String reason, String email, int age) {
        this.username = username;
        this.password = password;
        this.reason = reason;
        this.email = email;
        this.age = age;
    }

    public user() {

    }

    public Long getId() {
        return id;
    }

    public @NotBlank @Size(max = 50) @Email String getEmail() {
        return email;
    }

    public @NotBlank String getReason() {
        return reason;
    }

    public   int getAge() {
        return age;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(@NotBlank @Size(max = 20) String username) {
        this.username = username;
    }

    public void setPassword(@NotBlank @Size(max = 255) String password) {
        this.password = password;
    }

    public void setEmail(@NotBlank @Size(max = 50) @Email String email) {
        this.email = email;
    }

    public void setReason(@NotBlank String reason) {
        this.reason = reason;
    }

    public void setAge(  int age) {
        this.age = age;
    }

    public void setRoles(Role roles) {
        this.roles.add(roles);
    }

    @NotBlank
    @Size(max = 20)
    private String username;
    @NotBlank
    @Size(max = 255)
    private String password;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @NotBlank
    private String reason;


    private int age;

    @NotNull
    Boolean Enabled;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles= new HashSet<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Enabled;
    }
    public void setEnabled(Boolean Enabled){
        this.Enabled = Enabled;
    }
}
