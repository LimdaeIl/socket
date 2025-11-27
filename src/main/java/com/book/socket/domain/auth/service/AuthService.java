package com.book.socket.domain.auth.service;

import com.book.socket.common.exception.CustomException;
import com.book.socket.common.exception.ErrorCode;
import com.book.socket.domain.auth.model.request.CreateUserRequest;
import com.book.socket.domain.auth.model.request.LoginRequest;
import com.book.socket.domain.auth.model.response.CreateUserResponse;
import com.book.socket.domain.auth.model.response.LoginResponse;
import com.book.socket.domain.repository.UserRepository;
import com.book.socket.domain.repository.entity.User;
import com.book.socket.domain.repository.entity.UserCredentials;
import com.book.socket.security.Hasher;
import com.book.socket.security.JWTProvider;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Hasher hasher;


    @Transactional(transactionManager = "createUserTransactionManager")
    public CreateUserResponse createUser(CreateUserRequest request) {
        Optional<User> user = userRepository.findByName(request.name());

        if (user.isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        try {
            User newUser = this.newUser(request.name());
            UserCredentials newCredentials = this.newUserCredentials(request.password(), newUser);
            newUser.setCredentials(newCredentials);

            User save = userRepository.save(newUser);

            if (save == null) {
                throw new CustomException(ErrorCode.USER_SAVED_FAILED);
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.USER_SAVED_FAILED);
        }

        return new CreateUserResponse(request.name());
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByName(request.name());
        if (!user.isPresent()) {
            log.error("NOT_EXISTS_USER: {}", request.name());
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }

        user.map(u -> {
            String hashedValue = hasher.getHashingValue(request.password());

            if (!u.getCredentials().getHashedPassword().equals(hashedValue)) {
                throw new CustomException(ErrorCode.MISS_MATCH_PASSWORD);
            }

            return hashedValue;
        }).orElseThrow(() -> {
            throw new CustomException(ErrorCode.MISS_MATCH_PASSWORD);
        });

//        String token = JWTProvider.createRefreshToken(request.name());
//        log.info("token: {}", token);
//        return new LoginResponse(ErrorCode.SUCCESS, token);
        // ✅ 여기서 액세스 토큰 + 리프레시 토큰 발급
        String accessToken  = JWTProvider.createToken(request.name());
        String refreshToken = JWTProvider.createRefreshToken(request.name());

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        // LoginResponse를 확장해서 둘 다 내려주거나, 별 DTO를 쓰는 게 좋습니다.
        return new LoginResponse(ErrorCode.SUCCESS, accessToken /*, refreshToken */);
    }

    public String getUserFromToken(String token) {
        return JWTProvider.getUserFromToken(token);
    }


    private User newUser(String name) {
        return User.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private UserCredentials newUserCredentials(String password, User user) {
        String hashedValue = hasher.getHashingValue(password);

        return UserCredentials.builder()
                .user(user)
                .hashedPassword(hashedValue)
                .build();
    }
}
