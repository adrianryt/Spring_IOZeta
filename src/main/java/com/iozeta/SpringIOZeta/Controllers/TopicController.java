package com.iozeta.SpringIOZeta.Controllers;

import com.iozeta.SpringIOZeta.Controllers.utilities.TopicJson;
import com.iozeta.SpringIOZeta.Database.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topics")
public class TopicController {

    private final TaskRepository taskRepository;

    @GetMapping
    public ResponseEntity<List<TopicJson>> getListOfTopics(){

        return new ResponseEntity<>(
                taskRepository.findAll().stream().map(TopicJson::taskToTopicJson).collect(Collectors.toList()),
                HttpStatus.OK);

    }

}
