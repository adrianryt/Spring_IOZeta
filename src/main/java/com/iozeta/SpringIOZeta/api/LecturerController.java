package com.iozeta.SpringIOZeta.api;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Services.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LecturerController {
    private final LecturerService lecturerService;

    @GetMapping("/lecturers")
    public ResponseEntity<List<Lecturer>> getUsers(){
        return ResponseEntity.ok().body(lecturerService.getLecturers());
    }

    @PostMapping("/lecturer/save")
    public ResponseEntity<Lecturer> saveUser(@RequestBody Lecturer lecturer){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/lecturer/save").toUriString());
        return ResponseEntity.created(uri).body(lecturerService.saveLecturer(lecturer));
    }
}
