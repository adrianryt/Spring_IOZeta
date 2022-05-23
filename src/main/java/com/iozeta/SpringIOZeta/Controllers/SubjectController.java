package com.iozeta.SpringIOZeta.Controllers;

import com.iozeta.SpringIOZeta.Controllers.utilities.SubjectJson;
import com.iozeta.SpringIOZeta.Controllers.utilities.TopicJson;
import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Entities.Subject;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.SubjectRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final LecturerRepository lecturerRepository;
    private final TaskRepository taskRepository;

    @GetMapping("/all")
    public ResponseEntity<List<SubjectJson>> getAllSubjects(@RequestParam("username") String username) {
        Lecturer lecturer = lecturerRepository.findByGitNick(username);

         var subjects = subjectRepository.findSubjectsByLecturer(lecturer).stream().map(subject -> {
            var tasks = taskRepository.findTasksBySubject(subject).stream().
                    map(TopicJson::taskToTopicJson).toList();

            return new SubjectJson(subject.getId(), subject.getName(), subject.getLecturer(), tasks);

        }).toList();

        return ResponseEntity.ok().body(subjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok().body(subjectRepository.getSubjectById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<Subject> saveSubject(@RequestBody Subject subject) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("subject/save").toUriString());

        Long lecturerId = subject.getLecturer().getId();
        Lecturer lecturer = lecturerRepository.findLecturerById(lecturerId);
        subject.setLecturer(lecturer);

        return ResponseEntity.created(uri).body(subjectRepository.save(subject));
    }
}
