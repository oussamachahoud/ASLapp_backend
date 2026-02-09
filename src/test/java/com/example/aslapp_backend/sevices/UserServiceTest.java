package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.DTOs.UpdateUserDTO;
import com.example.aslapp_backend.DTOs.userDTO;
import com.example.aslapp_backend.DTOs.userWithAddressResponseDTO;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.Address;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//AAA: Arrange / Act / Assert
//Unit Test
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveUser() {
        user u = new user();
        when(userRepository.save(u)).thenReturn(u);
        
        user result = userService.saveUser(u);
        
        assertEquals(u, result);
        verify(userRepository).save(u);
    }

    @Test
    void shouldGetUserWithAddressById() {
        Long id = 1L;
        user u = new user();
        u.setId(id);
        
        when(userRepository.findByIdWithAddresses(id)).thenReturn(Optional.of(u));
        
        user result = userService.getUserWithAdressById(id);
        
        assertEquals(u, result);
    }

    @Test
    void shouldUpdateImage() throws IOException {
        Long id = 1L;
        MultipartFile file = mock(MultipartFile.class);
        user u = new user();
        String url = "http://image.url";
        
        when(userRepository.findById(id)).thenReturn(Optional.of(u));
        when(storageService.uploadFile(file)).thenReturn(url);
        when(userRepository.save(u)).thenReturn(u);
        
        user result = userService.updateImage(file, id);
        
        assertEquals(url, result.getImageURL());
        verify(userRepository).save(u);

    }

    @Test
    void shouldPatchUser() {
        Long id = 1L;
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername("newUsername");
        dto.setEmail("new@email.com");
        dto.setAge(30);
        
        user u = new user();
        u.setId(id);
        u.setUsername("oldUsername");
        
        when(userRepository.findById(id)).thenReturn(Optional.of(u));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(u)).thenReturn(u);
        
        user result = userService.patchMe(id, dto);
        
        assertEquals("newUsername", result.getUsername());
        assertEquals("new@email.com", result.getEmail());
        assertEquals(30, result.getAge());
    }

    @Test
    void shouldDeleteUser() {
        Long id = 1L;
        user u = new user();
        when(userRepository.findById(id)).thenReturn(Optional.of(u));
        
        userService.deleteUser(id);
        
        verify(userRepository).delete(u);
    }
    
    @Test
    void shouldGetAllUsers() {
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "ASC";
        
        user u = new user();
        u.setId(1L);
        Page<user> userPage = new PageImpl<>(Collections.singletonList(u));
        
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        
        Page<userDTO> result = userService.getAlluser(page, size, sortBy, direction);
        
        assertEquals(1, result.getTotalElements());
        assertEquals(u.getId(), result.getContent().get(0).getId());
    }

    @Test
    void shouldFindUser() {
        Long id = 1L;
        user u = new user();
        u.setId(id);
        
        when(userRepository.findById(id)).thenReturn(Optional.of(u));
        
        userDTO result = userService.findUser(id);
        
        assertNotNull(result);
        assertEquals(id, result.getId());
    }
}
