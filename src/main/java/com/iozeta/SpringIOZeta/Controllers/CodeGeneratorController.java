package com.iozeta.SpringIOZeta.Controllers;

import com.iozeta.SpringIOZeta.Controllers.utilities.EntranceCode;
import com.iozeta.SpringIOZeta.Controllers.utilities.EntranceCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CodeGeneratorController {

    @Autowired
    private EntranceCodeGenerator entranceCodeGenerator;

    @RequestMapping(value = "/entrance-code", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<EntranceCode> entranceCodeHandler() {
        String entranceCode = this.entranceCodeGenerator.generateCode();
        return ResponseEntity.ok().body(new EntranceCode(entranceCode));
    }
}