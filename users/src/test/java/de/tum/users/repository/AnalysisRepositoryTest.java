package de.tum.users.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.tum.users.model.Analysis;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AnalysisRepositoryTest {
    @Test
    void testSaveAndFind() {
        AnalysisRepository repo = mock(AnalysisRepository.class);
        Analysis analysis = new Analysis(null, "content", "repo");
        when(repo.saveAndFlush(analysis)).thenReturn(analysis);
        when(repo.findById("anid")).thenReturn(Optional.of(analysis));
        Analysis saved = repo.saveAndFlush(analysis);
        Optional<Analysis> found = repo.findById("anid");
        assertEquals(analysis, saved);
        assertTrue(found.isPresent());
        assertEquals(analysis, found.get());
    }
}
