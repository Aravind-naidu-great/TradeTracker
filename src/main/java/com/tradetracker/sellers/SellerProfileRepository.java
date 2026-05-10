package com.tradetracker.sellers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    List<SellerProfile> findAllByOrderByBusinessNameAsc();
}
