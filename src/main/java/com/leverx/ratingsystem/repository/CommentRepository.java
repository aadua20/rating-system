package com.leverx.ratingsystem.repository;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySellerAndApproved(User seller, boolean approved);
    List<Comment> findByApproved(boolean approved);
}
