package com.iozeta.SpringIOZeta.Controllers;

import com.google.gson.JsonObject;
import com.iozeta.SpringIOZeta.Controllers.git.BranchesController;
import com.iozeta.SpringIOZeta.Controllers.git.ContributorsController;
import com.iozeta.SpringIOZeta.Controllers.git.RepositoriesController;
import com.iozeta.SpringIOZeta.Controllers.utilities.StudentSessionForm;
import com.iozeta.SpringIOZeta.Database.Entities.*;
import com.iozeta.SpringIOZeta.Database.Repositories.*;
import com.iozeta.SpringIOZeta.Database.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/student")
@RequiredArgsConstructor
public class StudentController {

    private final LabSessionRepository labSessionRepository;
    private final CheckpointRepository checkpointRepository;
    private final StudentRepository studentRepository;
    private final ProgressRepository progressRepository;
    private final TaskRepository taskRepository;

    @RequestMapping(value = "/add-to-session", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validateCodeAndAddStudentToSession(@Valid @RequestBody StudentSessionForm studentSessionForm) { // check if RequestBody is good
        JsonObject response = new JsonObject();
        Session session = this.getSessionByAccessCode(studentSessionForm.getCode());
        if(session == null) {
            response.addProperty("message", "Session with this code does not exist");
            return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
        }

        String studentUsername = studentSessionForm.getGithubUsername();


        String branchName = this.createBranchName(studentUsername, session);

        Student student;
        Optional<Student> optionalStudent = studentRepository.findByBranchName(branchName);
        if(optionalStudent.isPresent()){
            student = optionalStudent.get();
        } else{
            student = this.createStudent(studentUsername, branchName);
            this.createProgresses(student, session);

            var res = BranchesController.addBranch(
                    session.getLecturer(),
                    session.getRepoName(),
                    branchName,
                    taskRepository
            );
            //TODO error check

            var res2 = ContributorsController.addContributor(
                    session.getLecturer(), session.getRepoName(), studentUsername
            );
            //TODO error check
        }

        response.addProperty("message", "Student was added to the session");
        response.addProperty("session_id", session.getId());
        response.addProperty("student_id", student.getId());
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

    private String createBranchName(String studentNickname, Session session){
        return session.getName().replace(' ', '_') + "_" + session.getId() + "_" + studentNickname;
    }

}
