package com.sample.security.model.mapper;

import com.sample.security.model.Roles;
import com.sample.security.model.User;
import io.jsonwebtoken.Claims;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserMapper {

    private UserMapper() { }

    public static User mapFromJWTClaims(Claims user) throws Exception {
        var listRoles = (LinkedHashMap) ((List) user.get("roles")).get(0);
        List<Roles> roles = new ArrayList<>();

        List<Map.Entry<String, Object>> listEntries = new ArrayList<>(listRoles.entrySet());

        var role = new Roles();
        for(Map.Entry<String, Object> entry : listEntries){
            if(entry.getKey().equals("id"))
                role.setId(Integer.parseInt(entry.getValue().toString()));

            if(entry.getKey().equals("name")) {
                role.setName(entry.getValue().toString());
                roles.add(role);
            }
        }

        return User.builder()
            .id(user.get("id").toString())
            .name(user.get("name").toString())
            .roles(roles)
            .build();
    }
}

