package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkRepository extends JpaRepository<Park, Long> {

    // Bounding Box 안에 있는 공원들을 먼저 필터링 (빠름)
    @Query("SELECT p FROM Park p WHERE " +
            "p.latitude BETWEEN :minLat AND :maxLat AND " +
            "p.longitude BETWEEN :minLng AND :maxLng")
    List<Park> findParksInBoundingBox(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng
    );

    Optional<Park> findByName(String name);
}
