package com.iozeta.SpringIOZeta.Controllers;

import com.iozeta.SpringIOZeta.Controllers.git.BranchesController;
import com.iozeta.SpringIOZeta.Controllers.utilities.UpdateProgressForm;
import com.iozeta.SpringIOZeta.Database.Entities.Progress;
import com.iozeta.SpringIOZeta.Database.Entities.Session;
import com.iozeta.SpringIOZeta.Database.Entities.Student;
import com.iozeta.SpringIOZeta.Database.Repositories.LabSessionRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.ProgressRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.StudentRepository;
import com.iozeta.SpringIOZeta.Database.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final StudentRepository studentRepository;
    private final LabSessionRepository sessionRepository;
    private final ProgressRepository progressRepository;

    @PostMapping("/update")
    public ResponseEntity<?> updateProgress(@Valid @RequestBody UpdateProgressForm updateProgressForm) {
        Student student = studentRepository.findById(updateProgressForm.getStudent_id()).get();
        Session session = sessionRepository.findSessionById(updateProgressForm.getSession_id());

        List<Progress> progresses = progressRepository.findProgressesBySessionAndStudent(session, student);

        Progress progress = progresses.stream().filter(
                progress1 -> progress1.getCheckpoint().getNumber() == updateProgressForm.getCheckpoint_number())
                .findFirst().get();

        try {

            String commitHash = BranchesController.requestForCommitSha(
                    session.getLecturer(), session.getRepoName(), student.getBranchName(), progress.getCommitMessage());
            progress.setCommitHash(commitHash);
            progress.setStatus(Status.DONE);

            progressRepository.save(progress);
        }catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }
}
