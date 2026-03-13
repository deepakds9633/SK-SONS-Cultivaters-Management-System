package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCase(String name);
    List<Client> findByVillageContainingIgnoreCase(String village);
}
