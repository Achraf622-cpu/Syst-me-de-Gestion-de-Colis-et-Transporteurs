package org.example.colis.service;

import org.example.colis.dto.LoginRequest;
import org.example.colis.dto.LoginResponse;
import org.example.colis.exception.UnauthorizedException;
import org.example.colis.model.User;
import org.example.colis.repository.UserRepository;
import org.example.colis.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginAndActiveTrue(request.getLogin())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials or account disabled"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user);
        
        return new LoginResponse(token, user.getLogin(), user.getRole().name());
    }
}
