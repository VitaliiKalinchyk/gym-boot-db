package epam.task.gymbootdb.controller.impl;

import org.springframework.security.core.context.SecurityContextHolder;

public abstract class AbstractController {
    protected String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
