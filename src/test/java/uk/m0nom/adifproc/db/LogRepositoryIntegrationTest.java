package uk.m0nom.adifproc.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.m0nom.adifproc.domain.Log;

@SpringBootTest
@ActiveProfiles("test")
public class LogRepositoryIntegrationTest {
    @Autowired
    private LogRepository logRepository;

    @Test
    public void testLogRepository() {
        Log log = new Log("msw");
        logRepository.save(log);

        log = logRepository.findByCallsign("msw");
        assert log != null;
        assert log.getTimestamp() != null;
    }
}
