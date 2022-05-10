package com.iozeta.SpringIOZeta.Controllers;

import com.google.gson.JsonObject;
import com.iozeta.SpringIOZeta.Controllers.utilities.StudentSessionForm;
import com.iozeta.SpringIOZeta.Database.Entities.*;
import com.iozeta.SpringIOZeta.Database.Repositories.CheckpointRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.LabSessionRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.ProgressRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.StudentRepository;
import com.iozeta.SpringIOZeta.Database.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/student")
@RequiredArgsConstructor
public class StudentController {

    private final LabSessionRepository labSessionRepository;
    private final CheckpointRepository checkpointRepository;
    private final StudentRepository studentRepository;
    private final ProgressRepository progressRepository;

    @RequestMapping(value = "/add-to-session", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validateCodeAndAddStudentToSession(@Valid @RequestBody StudentSessionForm studentSessionForm) { // check if RequestBody is good
        JsonObject response = new JsonObject();
        Session session = this.getSessionByAccessCode(studentSessionForm.getCode());
        if(session == null) {
            response.addProperty("message", "Session with this code does not exist");
            return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
        }

        //TODO check if githubusername exist

        //TODO setBranchName
        Student student = this.createStudent(studentSessionForm.getGithubUsername(), "BranchName");
        this.createProgresses(student, session);

        response.addProperty("message", "Student was added to the session");
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    private Student createStudent(String githubName, String branchName) {
        Student student = new Student();
        student.setGitNick(githubName);
        student.setBranchName(branchName);
        return this.studentRepository.save(student);
    }

    private Session getSessionByAccessCode(String code) {
        return labSessionRepository.findSessionByAccessCode(code);
    }

    private void createProgresses(Student student, Session session) {
        List<Checkpoint> checkpointList = this.checkpointRepository.findAllByTask(session.getTask());
        for(Checkpoint checkpoint: checkpointList) {
            Progress progress = new Progress();
            progress.setStudent(student);
            progress.setSession(session);
            progress.setCheckpoint(checkpoint);
            progress.setStatus(Status.NOT_DONE);
            progress.setCommitHash(null);
            this.progressRepository.save(progress);
        }
    }
}
