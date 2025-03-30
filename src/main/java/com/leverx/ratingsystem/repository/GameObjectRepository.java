package com.leverx.ratingsystem.repository;

import com.leverx.ratingsystem.entity.GameObject;
import com.leverx.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Long> {

}
