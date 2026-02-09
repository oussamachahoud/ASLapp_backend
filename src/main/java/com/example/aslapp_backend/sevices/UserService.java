package com.example.aslapp_backend.sevices;


import com.example.aslapp_backend.DTOs.AddressResponseDTO;
import com.example.aslapp_backend.DTOs.UpdateUserDTO;
import com.example.aslapp_backend.DTOs.userDTO;
import com.example.aslapp_backend.DTOs.userWithAddressResponseDTO;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private  final StorageService storageService;

    public UserService(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
    }



    public user saveUser(user user) {
        return    userRepository.save(user);  // Save the user to the database
    }

    @Transactional(readOnly = true)
    public user getUserWithAdressById(Long id) {
       // user user = userRepository.findById(id)
         //       .orElseThrow(() -> new RuntimeException("User not found"));

        // Force Hibernate to fetch the lazy collection while session is open
      //  user.getAddress().size(); // or just iterate over it
        user user = userRepository.findByIdWithAddresses(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        return user;
    }
    @Transactional
    public user updateImage(MultipartFile file, long id) throws RuntimeException, IOException {
        user u =  userRepository.findById(id).
                orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        String url = storageService.uploadFile(file);
        u.setImageURL(url);
        return  userRepository.save(u) ;
    }

    @Transactional
    public user patchMe(Long userId, UpdateUserDTO dto) {

        user u = userRepository.findById(userId)
                .orElseThrow(() ->  new BusinessException(HttpStatus.NOT_FOUND, "User not found"));

        // username
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            if (userRepository.existsByUsername(dto.getUsername())
                    && !dto.getUsername().equals(u.getUsername())) {
                throw new BusinessException(HttpStatus.CONFLICT, "Username already exists");
            }
            u.setUsername(dto.getUsername());
        }

        // email
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (userRepository.existsByEmail(dto.getEmail())
                    && !dto.getEmail().equals(u.getEmail())) {
                throw new BusinessException(HttpStatus.CONFLICT, "Email already exists");
            }
            u.setEmail(dto.getEmail());

        }

        // age
        if (dto.getAge() != null) {
            u.setAge(dto.getAge());
        }

        return userRepository.save(u);
    }
    public void deleteUser(long id){
        user u = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(u);


    }

    public Page<userDTO> getAlluser( int page, int size, String sortBy, String direction){
        Sort sort =direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<userDTO> userDTOPage= userRepository.findAll(pageable)
                .map(this::toUserDTO);
        return userDTOPage;
    }
    private userDTO toUserDTO(user user){
        return userDTO.builder()
                .id(user.getId())
                .age(user.getAge())
                .email(user.getEmail())
                .imageURL(user.getImageURL())
                .username(user.getUsername())
                .build();
    }

    public Page<userWithAddressResponseDTO> getAlluserWithAdress( int page, int size, String sortBy, String direction){
        Sort sort =direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<userWithAddressResponseDTO> userDTOPage= userRepository.findAllWithAddresses(pageable)
                .map(this::toUserDTOWithAdress);
        return userDTOPage;
    }

    private userWithAddressResponseDTO toUserDTOWithAdress(user user){
        List<AddressResponseDTO> addresses= user.getAddress().stream().map(a -> new AddressResponseDTO(a.getId(), a.getStreet(), a.getWilaya(), a.getCommune(), a.getCodePostal()))
                .collect(Collectors.toList());
        return userWithAddressResponseDTO.builder()
                .id(user.getId())
                .age(user.getAge())
                .email(user.getEmail())
                .imageURL(user.getImageURL())
                .username(user.getUsername())
                .addresses(addresses)
                .build();
    }
     public userDTO findUser(long id){
        user user =userRepository.findById(id).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        return toUserDTO(user);
     }
}