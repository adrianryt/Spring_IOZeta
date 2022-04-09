package com.iozeta.SpringIOZeta.Database;

import com.iozeta.SpringIOZeta.Database.Entities.Checkpoint;
import com.iozeta.SpringIOZeta.Database.Repositories.CheckpointRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckpointTest {

    @Autowired
    CheckpointRepository checkpointRepository;

    Checkpoint checkpoint1 = new Checkpoint();


}
