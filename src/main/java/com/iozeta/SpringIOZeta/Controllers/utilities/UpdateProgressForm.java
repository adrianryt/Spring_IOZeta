package com.iozeta.SpringIOZeta.Controllers.utilities;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateProgressForm {

    @NotNull
    private long studentId;

    @NotNull
    private long sessionId;

    @NotNull
    private long checkpointNumber;
}
