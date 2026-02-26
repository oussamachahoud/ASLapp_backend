package com.example.aslapp_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
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
@Builder
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @PositiveOrZero
    private int age;

    @Column
    private String imageURL;


    @NotNull
    Boolean Enabled;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @BatchSize(size = 1000)
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orderList =new ArrayList<>();

    public User(Long id, String username, String password, String email, int age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    public User(String username, String password, String email, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }
    //@BatchSize(size = 1000)
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "user", cascade = CascadeType.ALL)
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
    
    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> {
                    String roleName = role.getName().toString();

                    // Ensure role name starts with ROLE_ (remove ERole )

                        roleName =  roleName.substring(6);

                    return new SimpleGrantedAuthority(roleName);
                })
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
}
