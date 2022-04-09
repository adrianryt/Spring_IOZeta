package com.iozeta.SpringIOZeta.Database;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LecturerTest {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Test
    public void givenLecturer_whenSave_thenGetOk() {
        Lecturer lecturer = new Lecturer();
        lecturer.setName("Lecturer");
        lecturer.setSurname("LSurname");
        lecturer.setGitNick("LSNick");
        lecturer.setGitToken("gitToken1");

        lecturerRepository.save(lecturer);

        Lecturer lecturer2 = lecturerRepository.getById(1L);

        Assertions.assertEquals("Lecturer", lecturer2.getName());
        Assertions.assertEquals("LSurname", lecturer2.getSurname());
        Assertions.assertEquals("LSNick", lecturer2.getGitNick());
        Assertions.assertEquals("gitToken1", lecturer2.getGitToken());

    }
}
