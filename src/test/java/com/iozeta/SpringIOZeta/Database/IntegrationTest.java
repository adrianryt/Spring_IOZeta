package com.iozeta.SpringIOZeta.Database;

import com.iozeta.SpringIOZeta.Database.Entities.*;
import com.iozeta.SpringIOZeta.Database.Repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Entire flow")
    public void entireFlow() {

        //create lecturer with some parameters
        Lecturer lecturer = new Lecturer();
        lecturer.setName("Lecturer");
        lecturer.setSurname("LSurname");
        lecturer.setGitNick("LSNick");
        lecturer.setGitToken("gitToken1");
        Lecturer tmpLecturer = lecturerRepository.save(lecturer);

        //get lecturer from database
        lecturer = lecturerRepository.getById(tmpLecturer.getId());

        //create subject
        Subject subject = new Subject();
        subject.setName("IO");
        subject.setLecturer(lecturer);
        Subject tmpSubject = subjectRepository.save(subject);

        //get subject from database
        subject = subjectRepository.getById(tmpSubject.getId());

        //create task
        Task task = new Task();
        task.setName("Task1");
        task.setSubject(subject);
        task.setRepoName("IORepo");
        Task tmpTask = taskRepository.save(task);

        //get task from database
        task = taskRepository.getById(tmpTask.getId());

        //create checkpoint
        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setNumber(1);
        checkpoint.setContent("Checkpoint1: content");
        checkpoint.setTask(task);
        Checkpoint tmpCheckpoint = checkpointRepository.save(checkpoint);

        //create session
        Session session = new Session();
        session.setName("Name");
        session.setAccess_code("Some code");
        session.setTask(task);
        session.setActive(true);
        Session tmpSession = sessionRepository.save(session);

        //create student
        Student student = new Student();
        student.setGitNick("Git nick");
        student.setBranchName("Branch name");
        Student tmpStudent = studentRepository.save(student);

        //get student, session and checkpoint from database
        student = studentRepository.getById(tmpStudent.getId());
        session = sessionRepository.getById(tmpSession.getId());
        checkpoint = checkpointRepository.getById(tmpCheckpoint.getId());

        //create progress
        Progress progress = new Progress();
        progress.setSession(session);
        progress.setStudent(student);
        progress.setCheckpoint(checkpoint);
        progress.setStatus(Status.NOT_DONE);
        Progress tmpProgress = progressRepository.save(progress);

        //get all from database
        Lecturer lecturer2 = lecturerRepository.getById(tmpLecturer.getId());
        Subject subject2 = subjectRepository.getById(tmpSubject.getId());
        Task task2 = taskRepository.getById(tmpCheckpoint.getId());
        Checkpoint checkpoint2 = checkpointRepository.getById(tmpCheckpoint.getId());
        Session session2 = sessionRepository.getById(tmpSession.getId());
        Progress progress2 = progressRepository.getById(tmpProgress.getId());
        Student student2 = studentRepository.getById(tmpStudent.getId());

        //check relation between lecturer and subject
        Assertions.assertEquals(subject2.getLecturer(), lecturer2);

        //check relation between subject and task
        Assertions.assertEquals(task2.getSubject(), subject2);

        //check relation between task and checkpoint
        Assertions.assertEquals(checkpoint2.getTask(), task2);

        //check relation between task and session
        Assertions.assertEquals(session2.getTask(), task2);

        //check relation between session and progress
        Assertions.assertEquals(progress2.getSession(), session2);

        //check relation between checkpoint and progress
        Assertions.assertEquals(progress2.getCheckpoint(), checkpoint2);

        //check relation between student and progress
        Assertions.assertEquals(progress2.getStudent(), student2);
    }

}
