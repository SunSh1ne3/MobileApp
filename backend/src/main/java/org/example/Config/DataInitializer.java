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



    @Override
    public void run(String... args) throws Exception {
        runStatus();
        runTypeBicycle();
        runTypeBrakes();
        runUserRole();
    }

    public void runStatus(String... args) throws Exception {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusOrderRepository.findByName(statusEnum.getName()) == null) {
                statusOrderRepository.save(new StatusOrder(statusEnum.getName()));
            }
        }
    }

    public void runTypeBicycle(String... args) throws Exception {
        for (TypeBicycleEnum typeBicycle : TypeBicycleEnum.values()) {
            if (typeBicyclesRepository.findByName(typeBicycle.getName()) == null) {
                typeBicyclesRepository.save(new TypeBicycle(typeBicycle.getName()));
            }
        }
    }

    public void runTypeBrakes(String... args) throws Exception {
        for (TypeBrakesEnum typeBrakes : TypeBrakesEnum.values()) {
            if (typeBrakesRepository.findByName(typeBrakes.getName()) == null) {
                typeBrakesRepository.save(new TypeBrakes(typeBrakes.getName()));
            }
        }
    }

    public void runUserRole(String... args) throws Exception {
        for (UserRoleEnum userRole : UserRoleEnum.values()) {
            if (userRoleRepository.findByName(userRole.getName()) == null) {
                userRoleRepository.save(new UserRole(userRole.getName()));
            }
        }
    }

}
