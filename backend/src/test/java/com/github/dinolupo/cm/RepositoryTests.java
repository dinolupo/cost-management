package com.github.dinolupo.cm;

import com.github.dinolupo.cm.business.entity.*;
import com.github.dinolupo.cm.security.entity.RoleRepository;
import com.github.dinolupo.cm.security.entity.User;
import com.github.dinolupo.cm.security.entity.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class RepositoryTests {

    final int MAX_ELEM = 10;
    final String NAME_PREFIX = "name ";
    final String DESC_PREFIX = "description ";
    Long[] ids = new Long[MAX_ELEM];

    @Autowired
    ProjectRepository projectRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

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
            var res = projectRepo.save(project);
            ids[i] = res.getId();
        }
    }

    @AfterEach
    public void afterEach() {
        projectRepo.deleteAll();
        List<Project> all = projectRepo.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    public void versionIsZeroForNewElements() {
        for (int i = 0; i < MAX_ELEM; i++) {
            Optional<Project> found = projectRepo.findById(ids[i]);
            assertThat(found).isNotEmpty();
            var elem = found.get();
            assertThat(elem.getVersion()).isEqualTo(0);
        }
    }

    @Test
    public void optimisticLockException() {
        var p1 = projectRepo.findById(ids[0]).get();
        var p2 = projectRepo.findById(ids[0]).get();

        p1.setName("modified 1");
        Project saved = projectRepo.save(p1);
        assertThat(saved.getVersion()).isEqualTo(p1.getVersion() + 1);

        p2.setName("modified 2");
        assertThatThrownBy(() -> {
            projectRepo.save(p2);
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void verifyDisabled() {
        userRepo.deleteAll();
        var userA = new User();
        userA.setUsername("user1");
        userA.setPassword("password1");
        userA.setName("User Name 1");
        userA.setDisabled(false);
        userRepo.save(userA);
        var userB = new User();
        userB.setUsername("user2");
        userB.setPassword("password2");
        userB.setName("User Name 2");
        userB.setDisabled(true);
        userRepo.save(userB);

        var allActiveUsers = userRepo.findByDisabled(false, Pageable.unpaged());
        assertThat(allActiveUsers.getContent().size()).isEqualTo(1);
        assertThat(allActiveUsers.getContent().get(0).getUsername()).isEqualTo("user1");

        var userByUsername = userRepo.findByUsernameAndDisabled("user1", false);
        assertThat(userByUsername).isNotEmpty();
        assertThat(userByUsername.get().getUsername()).isEqualTo("user1");
    }

}
