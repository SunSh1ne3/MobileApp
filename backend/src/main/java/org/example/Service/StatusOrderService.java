package org.example.Service;

import org.example.DTO.StatusEnum;
import org.example.Model.StatusOrder;
import org.example.Repository.StatusOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatusOrderService {
    @Autowired
    private StatusOrderRepository statusOrderRepository;

    public Optional<StatusOrder> getStatusByID(Integer ID) {
        Optional<StatusOrder> status = statusOrderRepository.findById(ID);
        if (status.isPresent()) {
            return status;
        } else {
            throw new RuntimeException("Status not found");
        }
    }

    public StatusOrder getStatusByName(String name) {
        StatusOrder Status = statusOrderRepository.findByName(name);
        if (Status != null) {
            return Status;
        } else {
            throw new RuntimeException("Status not found");
        }
    }

    public Integer getStatusByName(StatusEnum name) {
        StatusOrder Status = statusOrderRepository.findByName(name.getName());
        if (Status != null) {
            return Status.getId();
        } else {
            throw new RuntimeException("Status not found");
        }
    }
    public Integer getActiveStatus() {
        StatusOrder activeStatus = statusOrderRepository.findByName(StatusEnum.ACTIVE.getName());
        if (activeStatus != null) {
            return activeStatus.getId();
        } else {
            throw new RuntimeException("Active status not found");
        }
    }
}
