//package com.example.aslapp_backend;
//
//
//import com.example.aslapp_backend.models.ERole;
//import com.example.aslapp_backend.models.Role;
//import com.example.aslapp_backend.repositories.UserRepository;
//import com.example.aslapp_backend.models.user;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.UserDetailsManager;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest
//public class UserTest {
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private UserDetailsManager userDetailsManager;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Test
//    public void testUtf8() {
//        String password = passwordEncoder.encode("password");
//        user user = new user();
//        System.out.println(user);
////        user.setUsername("Café_你好_नमस्ते");
////        user.setEmail("lijaba3036@hikuhu.com");
////        user.setPassword(password);
////        user.setEnabled(true);
////        user.setAge(22);
////        user.setReason("test reason");
////        user.getRoles(new Role(ERole.ROLE_ADMIN));
////        user userTest = userRepository.save(user);
////
////        user savedUser = userRepository.findById(user.getId()).orElseThrow();
////
////        assertThat(savedUser.getUsername()).isEqualTo("Café_你好_नमस्ते");
//    }
//}