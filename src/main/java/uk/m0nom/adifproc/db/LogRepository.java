package uk.m0nom.adifproc.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.m0nom.adifproc.domain.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    Log findByCallsign(String callsign);
}
