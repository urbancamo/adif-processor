package uk.m0nom.adifproc.activity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ActivityLuceneIndexTest {

    @Autowired
    private ActivityDatabaseService activityDatabaseService;

    @Test
    public void testSearchGLDSotaReferences() throws Exception {
        // Ensure activity databases are populated
        activityDatabaseService.loadData();
        
        // Get SOTA activities
        ActivityDatabase sotaDatabase = activityDatabaseService.getDatabase(ActivityType.SOTA);
        List<Activity> sotaActivities = new ArrayList<>(sotaDatabase.getValues());
        
        // Create Lucene index with all SOTA activities
        ActivityLuceneIndex luceneIndex = new ActivityLuceneIndex(sotaActivities);
        
        // Search for Lake District SOTA references using 'G/LD' substring
        List<String> results = luceneIndex.searchByRefSubstring("G/LD");
        
        // Verify we get results containing G/LD (may include other references that contain this substring)
        assertTrue(results.size() >= 55, 
            String.format("Expected at least 55 Lake District SOTA references, but found %d", results.size()));
        
        // Verify some specific known Lake District references are included
        assertTrue(results.contains("G/LD-001"), "Expected to find G/LD-001 (Scafell Pike)");
        assertTrue(results.contains("G/LD-003"), "Expected to find G/LD-003 (Helvellyn)");
        assertTrue(results.contains("G/LD-007"), "Expected to find G/LD-007 (Fairfield)");
        assertTrue(results.contains("G/LD-050"), "Expected to find G/LD-050 (test reference)");
        
        // Count actual G/LD references (vs references that just contain G/LD as a substring)
        long actualGLDReferences = results.stream()
            .filter(ref -> ref.startsWith("G/LD-"))
            .count();
        
        assertTrue(actualGLDReferences >= 55, 
            String.format("Expected at least 55 actual G/LD references, but found %d", actualGLDReferences));
    }

    @Test
    public void testSearchByNameSubstring() throws Exception {
        // Ensure activity databases are populated
        activityDatabaseService.loadData();
        
        // Get SOTA activities
        ActivityDatabase sotaDatabase = activityDatabaseService.getDatabase(ActivityType.SOTA);
        List<Activity> sotaActivities = new ArrayList<>(sotaDatabase.getValues());
        
        // Create Lucene index
        ActivityLuceneIndex luceneIndex = new ActivityLuceneIndex(sotaActivities);
        
        // Search for activities by name (looking for something that should match multiple summits)
        List<String> results = luceneIndex.searchByNameSubstring("fell");
        
        // Should return some results (many Lake District summits have "fell" in their name)
        assertFalse(results.isEmpty(), "Expected some results for 'fell' name search");
        assertTrue(results.size() <= 10, "Expected at most 10 results due to name search limit");
    }

    @Test
    public void testSearchLimitConstraints() throws Exception {
        // Ensure activity databases are populated
        activityDatabaseService.loadData();
        
        // Get SOTA activities
        ActivityDatabase sotaDatabase = activityDatabaseService.getDatabase(ActivityType.SOTA);
        List<Activity> sotaActivities = new ArrayList<>(sotaDatabase.getValues());
        
        // Create Lucene index
        ActivityLuceneIndex luceneIndex = new ActivityLuceneIndex(sotaActivities);
        
        // Search for activities by reference with a pattern that should return many results
        List<String> results = luceneIndex.searchByRefSubstring("001");
        
        // Should be limited to 100 results as per the searchByRefSubstring method implementation
        assertTrue(results.size() <= 100, 
            String.format("Expected at most 100 results due to search limit, but got %d", results.size()));
    }
}