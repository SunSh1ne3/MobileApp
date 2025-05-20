package org.example.Config;

import org.example.DTO.StatusEnum;
import org.example.DTO.TypeBicycleEnum;
import org.example.DTO.TypeBrakesEnum;
import org.example.Model.StatusOrder;
import org.example.Model.TypeBicycle;
import org.example.Model.TypeBrakes;
import org.example.DTO.UserRoleEnum;
import org.example.Model.UserRole;
import org.example.Repository.StatusOrderRepository;
import org.example.Repository.TypeBicyclesRepository;
import org.example.Repository.TypeBrakesRepository;
import org.example.Repository.UserRoleRepository;
import org.example.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private StatusOrderRepository statusOrderRepository;
    @Autowired
    private TypeBicyclesRepository typeBicyclesRepository;
    @Autowired
    private TypeBrakesRepository typeBrakesRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        initializeStatus();
        initializeTypeBicycle();
        initializeTypeBrakes();
        initializeUserRoles();
    }

    public void initializeStatus() {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            String statusValue  = statusEnum.toString();
            if (statusOrderRepository.findByName(statusValue ) == null) {
                statusOrderRepository.save(new StatusOrder(statusValue));
            }
        }
    }

    public void initializeTypeBicycle() {
        for (TypeBicycleEnum typeBicycle : TypeBicycleEnum.values()) {
            if (typeBicyclesRepository.findByName(typeBicycle.getName()) == null) {
                typeBicyclesRepository.save(new TypeBicycle(typeBicycle));
            }
        }
    }

    public void initializeTypeBrakes() {
        for (TypeBrakesEnum typeBrakes : TypeBrakesEnum.values()) {
            if (typeBrakesRepository.findByName(typeBrakes.getName()) == null) {
                typeBrakesRepository.save(new TypeBrakes(typeBrakes));
            }
        }
    }

    public void initializeUserRoles() {
        for (UserRoleEnum userRole : UserRoleEnum.values()) {
            if (userRoleRepository.findByName(userRole.getName()) == null) {
                userRoleRepository.save(new UserRole(userRole));
            }
        }
    }

}
