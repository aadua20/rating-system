package com.leverx.ratingsystem.repository;

import com.leverx.ratingsystem.entity.GameObject;
import com.leverx.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
    List<GameObject> findByUser(User user);
}
