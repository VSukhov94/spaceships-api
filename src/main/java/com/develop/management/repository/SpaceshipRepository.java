package com.develop.management.repository;

import com.develop.management.model.Spaceship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceshipRepository extends JpaRepository<Spaceship, Long> {

    @Query(value = "SELECT * FROM spaceships WHERE is_deleted = false AND name LIKE CONCAT('%', :name, '%')", nativeQuery = true)
    List<Spaceship> findByNameContaining(String name);

    @Query(value = "SELECT * FROM spaceships WHERE id = :id AND is_deleted = false", nativeQuery = true)
    Optional<Spaceship> findById(Long id);

    @Modifying
    @Query(value = "UPDATE spaceships SET is_deleted = true WHERE id = :id", nativeQuery = true)
    void deleteById(Long id);

    @Query(value = "SELECT * FROM spaceships WHERE is_deleted = false", nativeQuery = true)
    Page<Spaceship> getAllSpaceships(Pageable pageable);

}
