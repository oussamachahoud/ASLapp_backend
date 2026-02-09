package com.example.aslapp_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@Getter
@Setter
@NoArgsConstructor
public class user implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String username;

    @NotBlank
    @Size(max = 255)
    @Getter
    @Setter
    private String password;
    @NotBlank
    @Size(max = 50)
    @Email
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    @PositiveOrZero
    private int age;

    @Column
    private String imageURL;


    @NotNull
    Boolean Enabled;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Cart cart;

    @BatchSize(size = 1000)
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "user", cascade = CascadeType.ALL)
    @Getter @Setter
    private List<Order> orderList =new ArrayList<>();

    public user(Long id, String username, String password, String email, int age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    public user(String username, String password, String email, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }
    //@BatchSize(size = 1000)
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "user", cascade = CascadeType.ALL)
     @Getter @Setter
    private List<Address> address = new ArrayList<>();
     public void addAdress(Address address){
         this.address.add(address);address.setUser(this);

     }
    public void removeAdress(Address address){
        this.address.remove(address);
        address.setUser(null);
    }
    @BatchSize(size = 10)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles= new HashSet<>();
    public void setRoles(Role roles) {
        this.roles.add(roles);
    }


    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());
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
