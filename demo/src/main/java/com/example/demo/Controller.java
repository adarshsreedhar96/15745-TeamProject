package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    Map<String, String> data = new HashMap<String, String>();
    {
        data.put("a", "customer 1");
        data.put("b", "customer 2");
        data.put("c", "customer 3");
        data.put("d", "customer 4");
        data.put("e", "customer 5");
    }

    @RequestMapping("/info/{id}")
    public String getInfoById(@PathVariable("id") String id) {

        return data.get(id);

    }
}
