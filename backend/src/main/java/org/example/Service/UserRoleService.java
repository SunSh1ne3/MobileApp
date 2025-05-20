package org.example.Service;

import org.example.DTO.UserRoleEnum;
import org.example.Model.UserRole;
import org.example.Repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserRole getUserRole()  {
        return userRoleRepository.findByName(UserRoleEnum.USER.getName());
    }
    public UserRole getManagerRole() {
        return userRoleRepository.findByName(UserRoleEnum.MANAGER.getName());
    }
    public UserRole getAdminRole() {
        return userRoleRepository.findByName(UserRoleEnum.ADMIN.getName());
    }
}
