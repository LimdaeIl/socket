package com.book.socket.domain.user.service;

import com.book.socket.common.exception.ErrorCode;
import com.book.socket.domain.repository.UserRepository;
import com.book.socket.domain.user.model.response.UserSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceV1 {

    private final UserRepository userRepository;

    public final UserSearchResponse searchUser(String name, String user) {
        List<String> names = userRepository.findNameByNameMatch(name,user);
        return new UserSearchResponse(ErrorCode.SUCCESS, names);
    }
}
