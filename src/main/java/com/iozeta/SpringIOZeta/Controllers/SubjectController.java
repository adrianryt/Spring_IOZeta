package com.iozeta.SpringIOZeta.Controllers;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Entities.Subject;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import com.iozeta.SpringIOZeta.Database.Repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final LecturerRepository lecturerRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok().body(subjectRepository.findAllBy());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok().body(subjectRepository.getSubjectById(id));
    }

    @PostMapping
    public ResponseEntity<Subject> saveSubject(@RequestBody Subject subject) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("subject/save").toUriString());

        Long lecturerId = subject.getLecturer().getId();
        Lecturer lecturer = lecturerRepository.getLecturerById(lecturerId);
        subject.setLecturer(lecturer);
        subjectRepository.save(subject);

        return ResponseEntity.created(uri).build();
    }
}
