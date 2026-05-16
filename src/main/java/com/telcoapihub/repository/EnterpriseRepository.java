package com.telcoapihub.repository;

import com.telcoapihub.domain.Enterprise;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Optional<Enterprise> findByApiKey(String apiKey);
}
