package de.tum.users.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AnalysisTest {
    @Test
    void testAnalysisConstructorAndGetters() {
        User user = new User("ghid", "username", "token");
        Analysis analysis = new Analysis(user, "content", "repo");
        analysis.setId("anid");
        assertEquals("anid", analysis.getId());
        assertEquals("content", analysis.getContent());
        assertEquals("repo", analysis.getRepository());
        assertEquals(user, analysis.getUser());
    }

    @Test
    void testSetters() {
        Analysis analysis = new Analysis(null, "content", "repo");
        analysis.setId("anid2");
        analysis.setContent("newcontent");
        analysis.setRepository("newrepo");
        assertEquals("anid2", analysis.getId());
        assertEquals("newcontent", analysis.getContent());
        assertEquals("newrepo", analysis.getRepository());
    }
}
