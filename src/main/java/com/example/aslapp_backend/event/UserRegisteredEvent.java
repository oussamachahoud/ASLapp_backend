package com.example.aslapp_backend.event;

import com.example.aslapp_backend.models.user;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent  {
    final user usersave;
    final String Jwt;

    public UserRegisteredEvent(user usersave, String Jwt) {
        this.usersave = usersave;
        this.Jwt = Jwt;
    }


}
