package com.example.demo.repository;

import com.example.demo.domain.DB.DietList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DietListRepository extends JpaRepository<DietList, Long> {


    Optional<List<DietList>> findByFoodNameContaining(String foodName);
}
