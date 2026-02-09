package com.example.aslapp_backend;

import com.example.aslapp_backend.Seasaons;

public class test {

   public static void main (String[] args) {
      //  System.out.println(ERole.ROLE_USER.toString());
       Seasaons s = Seasaons.Spring;

       System.out.println("The " + s.getName() + " is season number "+ s );
       System.out.println("3 is season number "+s.Seasaons(2));
// Output: The SPRING is season number 1
    }
}
