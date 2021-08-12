package com.github.dinolupo.cm;

import com.github.dinolupo.cm.business.project.entity.Project;
import com.github.dinolupo.cm.business.project.entity.ProjectRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ProjectRepositoryTests {

    final int MAX_ELEM = 10;
    final String NAME_PREFIX = "name ";
    final String DESC_PREFIX = "description ";
    Long[] ids = new Long[MAX_ELEM];

    @Autowired
    ProjectRepository repository;

    @BeforeEach
    public void beforeEach() {
        for (int i = 0; i < MAX_ELEM; i++) {
            var project = new Project();
            project.setName(NAME_PREFIX+ i);
            project.setDescription(DESC_PREFIX+ i);
            project.setBudget(Double.valueOf(100 * i + 1000));
            project.setStartDate(LocalDate.now().plusMonths(i));
            project.setEndDate(LocalDate.now().plusMonths(i + 3));
            project.setOwner("dino");
            var res = repository.save(project);
            ids[i] = res.getId();
        }
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
        List<Project> all = repository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    public void versionIsZeroForNewElements() {
        for (int i = 0; i < MAX_ELEM; i++) {
            Optional<Project> found = repository.findById(ids[i]);
            assertThat(found).isNotEmpty();
            var elem = found.get();
            assertThat(elem.getVersion()).isEqualTo(0);
        }
    }

    @Test
    public void optimisticLockException() {
        var p1 = repository.findById(ids[0]).get();
        var p2 = repository.findById(ids[0]).get();

        p1.setName("modified 1");
        Project saved = repository.save(p1);
        assertThat(saved.getVersion() == p1.getVersion() + 1);

        p2.setName("modified 2");
        assertThatThrownBy(() -> {
            repository.save(p2);
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

}
