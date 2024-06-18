package uk.m0nom.adifproc.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.m0nom.adifproc.FileProcessorApplicationConfig;
import uk.m0nom.adifproc.domain.Log;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FileProcessorApplicationConfig.class)
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
