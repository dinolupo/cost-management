package com.github.dinolupo.cm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import com.github.dinolupo.cm.business.entity.Project;
import com.github.dinolupo.cm.business.entity.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ProjectControllerSystemTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProjectRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void getShouldNotReturnArchivedProjects() throws Exception {
        var project1 = new Project();
        project1.setName("name 1");
        project1.setDescription("description 1");
        project1.setOwner("dino");
        project1.setBudget(15_000.0);
        project1.setArchived(false);
        var online = repository.save(project1);

        var project2 = new Project();
        project2.setName("name 2");
        project2.setDescription("description 2");
        project2.setOwner("dino 2");
        project2.setBudget(25_000.0);
        project2.setArchived(true);
        var offline = repository.save(project2);

        this.mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.projects.length()").value(1))
                .andExpect(jsonPath("$._embedded.projects[0].name").value("name 1"));
                //.andDo(document("arch"));
    }

    @Test
    public void shouldRaiseExceptionOnPutWithDifferentVersion() throws Exception {

        // first object saved (version 1)
        var project = new Project();
        project.setName("name");
        project.setDescription("description");
        project.setOwner("dino");
        project.setBudget(15_000.0);
        var saved = repository.save(project);

        // some other client save again (version 2)
        saved.setDescription("revisioned budget");
        saved.setBudget(10000.0);
        assertThat(saved.getVersion()).isEqualTo(0);
        var revisioned = repository.save(saved); // save again will change version
        assertThat(revisioned.getVersion()).isEqualTo(1);

        // PUT endpoint should raise exception if a client save again the first object
        this.mockMvc.perform(put("/projects/"+saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(saved)))
                .andDo(print())
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void archiveAndUnarchive() throws Exception {
        // can Archive id CANCELLED
        var project = new Project();
        project.setName("name");
        project.setBudget(200.0);
        project.setStatus(Project.Status.CANCELLED);
        project = repository.save(project);
        assertThat(project.getStatus()).isEqualTo(Project.Status.CANCELLED);
        this.mockMvc.perform(put("/projects/" + project.getId() + "/archive"))
                .andDo(print())
                .andExpect(status().isOk());

        // cannot ARCHIVE IF READY
        project = repository.findById(project.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(project.getArchived()).isEqualTo(true);
        project.setArchived(false);
        project.setStatus(Project.Status.READY);
        project = repository.save(project);
        this.mockMvc.perform(put("/projects/" + project.getId() + "/archive"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());

        // can ARCHIVE if COMPLETED
        project = repository.findById(project.getId()).orElseThrow(NoSuchElementException::new);
        project.setStatus(Project.Status.COMPLETED);
        project = repository.save(project);
        this.mockMvc.perform(put("/projects/" + project.getId() + "/archive"))
                .andDo(print())
                .andExpect(status().isOk());

        // test unarchive
        this.mockMvc.perform(put("/projects/" + project.getId() + "/unarchive"))
                .andDo(print())
                .andExpect(status().isOk());

        // cannot unarchive if already archived
        this.mockMvc.perform(put("/projects/" + project.getId() + "/unarchive"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(content().json("{\"title\":\"Method not allowed\",\"detail\":\"You can't unarchive a Project that is already online\"}"));

    }

    @Test
    public void getAll() throws Exception {
        this.mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").isNotEmpty());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
