package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.entity.Role;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            Optional<User> user = findByEmail(email);
            return user.orElse(null);
        }
        return null;
    }

    public List<User> getUnapprovedSellers() {
        return userRepository.findByRoleAndIsApproved(Role.SELLER, false);
    }

    public void approveSeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (seller.getRole() != Role.SELLER) {
            throw new RuntimeException("User is not a seller");
        }

        seller.setApproved(true);
        userRepository.save(seller);
    }

    public void declineSeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (seller.getRole() != Role.SELLER) {
            throw new RuntimeException("User is not a seller");
        }

        userRepository.delete(seller);
    }
}
