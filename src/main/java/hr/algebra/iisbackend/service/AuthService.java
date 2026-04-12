package hr.algebra.iisbackend.service;

import hr.algebra.iisbackend.dto.AuthDTO;
import hr.algebra.iisbackend.entity.AppUser;
import hr.algebra.iisbackend.repository.AppUserRepository;
import hr.algebra.iisbackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Pogrešna lozinka");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), List.of(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new AuthDTO.TokenResponse(accessToken, refreshToken, user.getRole());
    }

    public AuthDTO.TokenResponse register(AuthDTO.LoginRequest request) {
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Korisnik već postoji");
        }
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
        appUserRepository.save(user);
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), List.of(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new AuthDTO.TokenResponse(accessToken, refreshToken, user.getRole());
    }

    public AuthDTO.TokenResponse refresh(AuthDTO.RefreshRequest request) {
        if (!jwtUtil.isTokenValid(request.getRefreshToken())) {
            throw new RuntimeException("Refresh token nije validan");
        }

        String username = jwtUtil.extractUsername(request.getRefreshToken());
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), List.of(user.getRole()));
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new AuthDTO.TokenResponse(newAccessToken, newRefreshToken, user.getRole());
    }
}
