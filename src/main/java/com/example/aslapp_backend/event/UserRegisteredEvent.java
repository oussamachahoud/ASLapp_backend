package com.example.aslapp_backend.event;

import com.example.aslapp_backend.models.User;
import lombok.Getter;

/*about extends ApplicationEvent
Before Spring 4.2,
the event system was designed to only publish objects
 that were an instanceof ApplicationEvent.
Starting with Spring 4.2, the framework made the event system more flexible.
 It can now publish any arbitrary object (any POJO - Plain Old Java Object).
 When you call eventPublisher.publishEvent(myObject),
  Spring simply looks for an @EventListener method
   whose parameter type matches the class of myObject.*/
@Getter
public class UserRegisteredEvent  {
    final User usersave;
    final String Jwt;

    public UserRegisteredEvent(User usersave, String Jwt) {
        this.usersave = usersave;
        this.Jwt = Jwt;
    }


}
