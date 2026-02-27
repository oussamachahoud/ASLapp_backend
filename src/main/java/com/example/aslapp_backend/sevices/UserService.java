package com.example.aslapp_backend.sevices;


import com.example.aslapp_backend.DTOs.responseDTOs.AddressResponseDTO;
import com.example.aslapp_backend.DTOs.requestDTOs.UpdateUserDTO;
import com.example.aslapp_backend.DTOs.requestDTOs.AddressRequestDTO;
import com.example.aslapp_backend.DTOs.modelDTOs.userDTO;
import com.example.aslapp_backend.DTOs.responseDTOs.userWithAddressResponseDTO;
import com.example.aslapp_backend.Exeption.BusinessException;
import com.example.aslapp_backend.models.Enum.ERole;
import com.example.aslapp_backend.models.Role;
import com.example.aslapp_backend.models.User;
import com.example.aslapp_backend.models.Address;
import com.example.aslapp_backend.repositories.RoleRepository;
import com.example.aslapp_backend.repositories.UserRepository;
import com.example.aslapp_backend.repositories.AddressRepository;
import lombok.AllArgsConstructor;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private  final StorageService storageService;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;





    public User saveUser(User user) {
        return    userRepository.save(user);  // Save the User to the database
    }

    @Transactional(readOnly = true)
    public User getUserWithAdressById(Long id) {
       // User User = userRepository.findById(id)
         //       .orElseThrow(() -> new RuntimeException("User not found"));

        // Force Hibernate to fetch the lazy collection while session is open
      //  User.getAddress().size(); // or just iterate over it
        User user = userRepository.findByIdWithAddresses(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        return user;
    }
    @Transactional
    public User updateImage(MultipartFile file, long id) throws RuntimeException, IOException {
        User u =  userRepository.findById(id).
                orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        String url = storageService.uploadFile(file);
        u.setImageURL(url);
        return  userRepository.save(u) ;
    }

    @Transactional
    public userDTO patchMe(Long userId, UpdateUserDTO dto) {

        User u = userRepository.findById(userId)
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

        return toUserDTO(userRepository.save(u));
    }
    public void deleteUser(long id){
        User u = userRepository.findById(id)
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
    private userDTO toUserDTO(User user){
        return userDTO.builder()
                .id(user.getId())
                .age(user.getAge())
                .email(user.getEmail())
                .imageURL(user.getImageURL())
                .username(user.getUsername())
                .role(user.getRoles().stream().filter(i -> i.getName() != null).map(i->i.getName().name()).collect(Collectors.toSet()))
                .build();
    }
 @Transactional(readOnly = true)
    public Page<userWithAddressResponseDTO> getAlluserWithAdress( int page, int size, String sortBy, String direction){
        Sort sort =direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<User> userPage= userRepository.findAllUsers(pageable);
        return userPage.map(this::toUserDTOWithAdress);
    }

    public userWithAddressResponseDTO toUserDTOWithAdress(User user){
        Set<AddressResponseDTO> addresses= user.getAddress().stream().map(a -> new AddressResponseDTO(a.getId(), a.getStreet(), a.getWilaya(), a.getCommune(), a.getCodePostal()))
                .collect(Collectors.toSet());
        return userWithAddressResponseDTO.builder()
                .id(user.getId())
                .age(user.getAge())
                .email(user.getEmail())
                .imageURL(user.getImageURL())
                .username(user.getUsername())
                .addresses(addresses)
                .role(user.getRoles().stream().map(i->i.getName().name()).collect(Collectors.toSet()))
                .build();
    }

     public userDTO findUser(long id){
        User user =userRepository.findById(id).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        return toUserDTO(user);
     }

     public userDTO findUserByUsernameOrEmail(String query){
        User user = userRepository.findByEmail(query)
                .or(() -> userRepository.findByUsername(query))
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        return toUserDTO(user);
     }

    @Transactional
    public AddressResponseDTO addAddressForUser(Long userId, AddressRequestDTO addressRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));

        Address address = new Address();
        address.setStreet(addressRequestDTO.getStreet());
        address.setWilaya(addressRequestDTO.getWilaya());
        address.setCommune(addressRequestDTO.getCommune());
        address.setCodePostal(addressRequestDTO.getCodePostal());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        user.addAdress(savedAddress);
        userRepository.save(user);

        return new AddressResponseDTO(
                savedAddress.getId(),
                savedAddress.getStreet(),
                savedAddress.getWilaya(),
                savedAddress.getCommune(),
                savedAddress.getCodePostal()
        );
    }

    public userDTO updateUserRole(long id, ERole  eRole) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        ERole finalRole = ERole.findByNumber(eRole);
        Role role = roleRepository.findByName(finalRole).orElseGet(
                () -> roleRepository.save(new Role(finalRole)));

        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getName() == finalRole);
        if (hasRole) throw new BusinessException(HttpStatus.NOT_FOUND, "User has the role already");
        user.addRole(role);
        user = userRepository.save(user);
        return toUserDTO(user);
    }
    public userDTO removeUserRole(long id, ERole  eRole) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        ERole finalRole = ERole.findByNumber(eRole);
        Role role = roleRepository.findByName(finalRole).orElseGet(
                () -> roleRepository.save(new Role(finalRole)));

        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getName() == finalRole);
        if (!hasRole) throw new BusinessException(HttpStatus.NOT_FOUND, "User don't has the role already");
        user.removeRole(role);
        user = userRepository.save(user);
        return toUserDTO(user);
    }

    public void removeAddressForUser(User user, Long addressId) {
        addressRepository.deleteAllByIdAndUser(addressId,user);
    }

}