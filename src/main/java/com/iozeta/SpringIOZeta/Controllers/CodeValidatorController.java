package com.iozeta.SpringIOZeta.Controllers;

import com.google.gson.JsonObject;
import com.iozeta.SpringIOZeta.Controllers.utilities.StudentSessionForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class CodeValidatorController {

    @RequestMapping(value = "/validate-code", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validateCode(@Valid @RequestBody StudentSessionForm studentSessionForm) { // sprawdzenie czy RequestBody jest dobre
        // return error if student is in any session right now
        // return error if code is wrong
        // check if githubusername exist
        // set session on student and return OK
        JsonObject response = new JsonObject();
        response.addProperty("message", "Oke jest!");
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
//        return ResponseEntity.ok().body(response.toString());
//        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
