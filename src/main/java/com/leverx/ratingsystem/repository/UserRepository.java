package com.leverx.ratingsystem.repository;

import com.leverx.ratingsystem.entity.Role;
import com.leverx.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndIsApproved(Long id, boolean isApproved);

    List<User> findByRoleAndIsApproved(Role role, boolean isApproved);

}
