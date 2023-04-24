package com.ecommerce.core.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("database-obj")
@Slf4j
public class DatabaseController extends BaseController {

    private final String START_LOG = "Custom Database {}";

    private final String END_LOG = "Custom Database {} finished in: {}";

}
