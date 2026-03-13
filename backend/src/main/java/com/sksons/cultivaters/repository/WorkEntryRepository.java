package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.WorkEntry;
import com.sksons.cultivaters.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkEntryRepository extends JpaRepository<WorkEntry, Long> {
    List<WorkEntry> findByClient(Client client);
    List<WorkEntry> findByClientId(Long clientId);

    @Query("SELECT COALESCE(SUM(w.totalCost), 0) FROM WorkEntry w")
    Double getTotalIncome();

    @Query("SELECT COUNT(w) FROM WorkEntry w")
    Long getTotalWorkCount();
}
