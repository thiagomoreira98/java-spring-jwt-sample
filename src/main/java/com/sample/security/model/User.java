package com.sample.security.model;

import com.sample.security.annotation.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String name;
    private List<Roles> roles;

    public boolean hasAnyAccess(UserRoles baapRoles) {
        return this.hasAnyAccess(Arrays.asList(baapRoles.value()));
    }

    public boolean hasAnyAccess(List<String> roles) {
        return (this.roles != null && roles != null) &&
                this.roles.stream().anyMatch(g -> roles.contains(g.getName()));
    }

}
