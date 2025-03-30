package com.leverx.ratingsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.dto.PasswordResetRequest;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.exception.UserAuthException;
import com.leverx.ratingsystem.security.JwtUtil;
import com.leverx.ratingsystem.util.EmailService;
import com.leverx.ratingsystem.util.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldSendConfirmationEmail_andStoreInRedis() {
        User user = new User();
        user.setEmail("ana@gmail.com");
        user.setPassword("123456");

        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        String response = authService.register(user);

        assertTrue(response.contains("Registration successful"));

        verify(emailService).sendConfirmationEmail(eq(user.getEmail()), anyString());
        verify(redisService).storeUserPendingConfirmation(eq(user.getEmail()), anyString(), anyString());
    }

    @Test
    void confirmEmail_shouldSaveUser_andRemoveCode() throws Exception {
        String email = "ana@gmail.com";
        String code = "123456";

        User user = new User();
        user.setEmail(email);
        user.setPassword("123456");

        String userJson = objectMapper.writeValueAsString(user);

        when(redisService.validateAndRetrievePendingUser(email, code)).thenReturn(userJson);

        String response = authService.confirmEmail(email, code);

        assertEquals("Email confirmed successfully! You can now log in.", response);

        verify(userService).save(any(User.class));
        verify(redisService).removeConfirmationCode(email);
    }

    @Test
    void login_shouldThrowException_whenEmailNotConfirmed() {
        String email = "ana@gmail.com";
        LoginRequest loginRequest = new LoginRequest(email, "123456");

        when(redisService.isEmailUnconfirmed(email)).thenReturn(true);

        UserAuthException exception = assertThrows(
                UserAuthException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Email is not confirmed. Please check your email.", exception.getMessage());
        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    void checkResetCode_shouldReturnFalse_whenCodeIsInvalid() {
        String email = "ana@gmail.com";
        String code = "123456";

        when(redisService.validateResetCode(email, code)).thenReturn(false);

        boolean result = authService.checkResetCode(email, code);

        assertFalse(result);
        verify(redisService).validateResetCode(email, code);
    }

    @Test
    void resetPassword_shouldUpdatePassword_whenResetCodeIsValid() {
        String email = "ana@gmail.com";
        String code = "123456";
        String newPassword = "newPass";

        User user = new User();
        user.setEmail(email);
        user.setPassword("oldEncodedPass");

        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail(email);
        request.setCode(code);
        request.setNewPassword(newPassword);

        when(redisService.validateResetCode(email, code)).thenReturn(true);
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");

        String response = authService.resetPassword(request);

        assertEquals("Password has been successfully reset.", response);
        assertEquals("encodedNewPass", user.getPassword());

        verify(userService).save(user);
        verify(redisService).removeResetCode(email);
    }

    @Test
    void register_shouldThrow_whenUserAlreadyExists() {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");

        when(userService.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        UserAuthException exception = assertThrows(
                UserAuthException.class,
                () -> authService.register(existingUser)
        );

        assertEquals("An account with this email already exists.", exception.getMessage());
    }

    @Test
    void forgotPassword_shouldSendResetCode_ifUserExists() {
        String email = "ana@gmail.com";
        User user = new User();
        user.setEmail(email);

        when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        String response = authService.forgotPassword(email);

        assertEquals("Password reset code sent to your email.", response);
        verify(redisService).storeResetCode(eq(email), anyString());
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void login_shouldReturnToken_ifCredentialsAreValid() {
        String email = "ana@gmail.com";
        String password = "123456";
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(redisService.isEmailUnconfirmed(email)).thenReturn(false);
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("mocked-jwt-token");

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        AuthResponse response = authService.login(loginRequest);

        assertEquals(email, response.getEmail());
        assertEquals("mocked-jwt-token", response.getToken());
    }


}
