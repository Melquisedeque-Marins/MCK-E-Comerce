package com.melck.userservice.service;

import com.melck.userservice.dto.Cart;
import com.melck.userservice.dto.UserResponse;
import com.melck.userservice.entity.User;
import com.melck.userservice.repository.UserRepository;
import com.melck.userservice.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final WebClient webClient;

    @Transactional
    public UserResponse registerUser(User user) {

        Cart cart = webClient.post()
                .uri("http://localhost:8081/api/v1/cart")
                .retrieve()
                .bodyToMono(Cart.class)
                .block();

        user.setCartId(cart.getId());

        return mapUserToUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found " + id));
        return mapUserToUserResponse(user);
    }


    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::mapUserToUserResponse).toList();
        return  users;
    }

    private UserResponse mapUserToUserResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}