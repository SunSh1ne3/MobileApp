package org.example.Service;

import jakarta.persistence.EntityNotFoundException;
import org.example.DTO.Response.ErrorResponse;
import org.example.DTO.StatusEnum;
import org.example.Model.Order;
import org.example.Model.StatusOrder;
import org.example.Model.User;
import org.example.Repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StatusOrderService statusOrderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public List<Order> findAllOrder() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderByID(Integer id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error during order found: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Order> findOrderByUserId(Integer id_user) {
        return orderRepository.findByUserId(id_user);
    }

    public Order addOrder(Order orderData) {
        try {
            Order newOrder = new Order();
            newOrder.setBicycleId(orderData.getBicycleId());
            newOrder.setUserId(orderData.getUserId());
            newOrder.setStartDate(orderData.getStartDate());

            newOrder.setCountHours(orderData.getCountHours());
            newOrder.setCountDays(orderData.getCountDays());

            newOrder.setEndDate(calculateEndDate(orderData.getStartDate(), newOrder.getCountHours(), newOrder.getCountDays()));
            newOrder.setStatus(statusOrderService.getStatusByName(StatusEnum.NEW));
            newOrder.setPrice(orderData.getPrice());

            return orderRepository.save(newOrder);
        } catch (Exception e) {
            logger.error("Error during order added: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Order updateOderStatus(Integer orderId, StatusOrder newStatus) {
        try {
                Optional<Order> orderOptional  = getOrderByID(orderId);
                Order order = orderOptional.orElseThrow(() ->
                    new EntityNotFoundException("Order with ID " + orderId + " not found")
                );
                order.setStatus(newStatus.getId());
                return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error during order added: {}", e.getMessage(), e);
            throw e;
        }
    }



    public OffsetDateTime calculateEndDate(OffsetDateTime  startDate, Integer countHours, Integer countDays) {
        OffsetDateTime endDate = startDate;

        if (countHours != null) {
            endDate = endDate.plusHours(countHours);
        }

        if (countDays != null) {
            endDate = endDate.plusDays(countDays);
        }

        return endDate;
    }

    public void deleteOrderByID(Integer ID) {
        orderRepository.deleteById(ID);
    }

    public void deleteAllOrder() {
        orderRepository.deleteAll();
    }

}
