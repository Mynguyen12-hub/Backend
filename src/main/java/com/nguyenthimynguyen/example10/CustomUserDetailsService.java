package com.nguyenthimynguyen.example10;

import com.nguyenthimynguyen.example10.model.User;
import com.nguyenthimynguyen.example10.repository.UserRepository;
import com.nguyenthimynguyen.example10.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Trả về UserDetailsImpl, KHÔNG phải org.springframework.security.core.userdetails.User
        return UserDetailsImpl.build(user);
    }
}
