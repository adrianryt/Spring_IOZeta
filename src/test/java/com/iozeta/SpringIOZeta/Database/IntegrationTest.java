package com.iozeta.SpringIOZeta.Database;

import com.iozeta.SpringIOZeta.Database.Entities.*;
import com.iozeta.SpringIOZeta.Database.Repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IntegrationTest {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CheckpointRepository checkpointRepository;

    @Autowired
    private LabSessionRepository sessionRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired StudentRepository studentRepository;

    @Test
    public void entireFlow() {
        Lecturer lecturer = new Lecturer();
        lecturer.setName("Lecturer");
        lecturer.setSurname("LSurname");
        lecturer.setGitNick("LSNick");
        lecturer.setGitToken("gitToken1");
        lecturerRepository.save(lecturer);

        lecturer = lecturerRepository.getById(1L);

        Subject subject = new Subject();
        subject.setName("IO");
        subject.setLecturer(lecturer);
        subject.setRepoName("IORepo");
        subjectRepository.save(subject);

        lecturer.addSubject(subject);
        lecturerRepository.save(lecturer);

        subject = subjectRepository.getById(1L);

        Task task = new Task();
        task.setName("Task1");
        task.setSubject(subject);
        task.setBranchName("IO_Task1");
        taskRepository.save(task);

        subject.addTask(task);
        subjectRepository.save(subject);

        task = taskRepository.getById(1L);

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setNumber(1);
        checkpoint.setContent("Checkpoint1: content");
        checkpoint.setTask(task);
        checkpointRepository.save(checkpoint);

        Session session = new Session();
        session.setName("Name");
        session.setAccess_code("Some code");
        session.setTask(task);
        session.setActive(true);
        sessionRepository.save(session);

        task.addCheckpoint(checkpoint);
        task.addSession(session);

        Student student = new Student();
        student.setGitNick("Git nick");
        student.setBranchName("Branch name");
        studentRepository.save(student);

        student = studentRepository.getById(1L);
        session = sessionRepository.getById(1L);
        checkpoint = checkpointRepository.getById(1L);

        Progress progress = new Progress();
        progress.setSession(session);
        progress.setStudent(student);
        progress.setCheckpoint(checkpoint);
        progress.setDone(false);
        progressRepository.save(progress);

        student.addProgress(progress);
        session.addProgress(progress);
        checkpoint.addProgress(progress);


        Lecturer lecturer2 = lecturerRepository.getById(1L);
        Subject subject2 = subjectRepository.getById(1L);
        Task task2 = taskRepository.getById(1L);
        Checkpoint checkpoint2 = checkpointRepository.getById(1L);
        Session session2 = sessionRepository.getById(1L);
        Progress progress2 = progressRepository.getById(1L);
        Student student2 = studentRepository.getById(1L);


        Assertions.assertTrue(lecturer2.getSubjects().contains(subject2));
        Assertions.assertEquals(subject2.getLecturer(), lecturer2);

        Assertions.assertTrue(subject2.getTasks().contains(task2));
        Assertions.assertEquals(task2.getSubject(), subject2);

        Assertions.assertTrue(task2.getCheckpoints().contains(checkpoint2));
        Assertions.assertEquals(checkpoint2.getTask(), task2);

        Assertions.assertTrue(task2.getSessions().contains(session2));
        Assertions.assertEquals(session2.getTask(), task2);

        Assertions.assertTrue(session2.getProgresses().contains(progress2));
        Assertions.assertEquals(progress2.getSession(), session2);

        Assertions.assertTrue(checkpoint2.getProgresses().contains(progress2));
        Assertions.assertEquals(progress2.getCheckpoint(), checkpoint2);

        Assertions.assertTrue(student2.getProgresses().contains(progress2));
        Assertions.assertEquals(progress2.getStudent(), student2);
    }

}
