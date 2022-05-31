package com.iozeta.SpringIOZeta.Controllers.utilities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
public class UpdateProgressForm {

    @NotNull
    private long student_id;

    @NotNull
    private long session_id;

    @NotNull
    private long checkpoint_number;
}
