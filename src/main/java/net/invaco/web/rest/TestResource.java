package net.invaco.web.rest;

import net.invaco.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/")
public class TestResource {

    private final Logger log = LoggerFactory.getLogger(TestResource.class);

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public void activateAccount() {
        testService.test();
    }
}
