package org.wickedsource.coderadar.security.domain;

import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;


public class UserResource extends ResourceSupport {

    @NotNull
    private String username;

    public UserResource() {
    }

    public UserResource(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
