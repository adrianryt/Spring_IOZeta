package com.iozeta.SpringIOZeta.Database.Services;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;

import java.util.List;

public interface LecturerService {
    Lecturer saveLecturer(Lecturer Lecturer);

    Lecturer getUser(String gitNick);

    List<Lecturer> getLecturers();
}
