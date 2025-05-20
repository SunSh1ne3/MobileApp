package org.example.DTO.Response;

import org.example.Model.User;
import lombok.Getter;
@Getter
public class UserResponse {
    private Integer id;
    private String username;
    private String numberPhone;
    private String userRole ;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.numberPhone = user.getNumberPhone();
        this.userRole = user.getRoleName();
    }
}
