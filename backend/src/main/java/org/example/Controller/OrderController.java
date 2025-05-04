package org.example.Controller;

import org.example.DTO.Response.ErrorResponse;
import org.example.Model.Order;
import org.example.Model.StatusOrder;
import org.example.Service.BicycleService;
import org.example.Service.OrderService;
import org.example.Service.StatusOrderService;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private StatusOrderService statusOrderService;
    @Autowired
    private UserService userService;

    @Autowired
    private BicycleService bicycleService;
    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrder() {
        List<Order> orders = orderService.findAllOrder();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{statusName}")
    public ResponseEntity<StatusOrder> getStatusByName(@PathVariable String statusName) {
        StatusOrder status = statusOrderService.getStatusByName(statusName);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/user/{id_user}/orders")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Integer id_user) {
        if (userService.getUser(id_user).isEmpty()) {
              return ResponseEntity.notFound().build();
        }
        List<Order> orders = orderService.findOrderByUserId(id_user);
        return ResponseEntity.ok(orders);
    }

    @PostMapping()
    public ResponseEntity<Object> addOrder(@RequestBody Order orderData) {
        try {
            if (userService.getUser(orderData.getUserId()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User with ID: '" + orderData.getUserId() + "' not found"));
            }
            if (bicycleService.getBicycleByID(orderData.getBicycleId()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Bicycle with ID: '" + orderData.getBicycleId() + "'not found"));
            }
            if (orderData.getCountHours() == 0 && orderData.getCountDays() == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Number of days and hours cannot be null"));
            }
            Order order = orderService.addOrder(orderData);
            return ResponseEntity.ok(order);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Integer id, @RequestBody StatusOrder newStatus) {
        try {
            if (orderService.getOrderByID(id).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Order with ID: '" + id + "' not found"));
            }
            if (statusOrderService.getStatusByID(newStatus.getId()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Status with ID: '" + newStatus.getId() + "'not found"));
            }
            Order order = orderService.updateOderStatus(id, newStatus);
            return ResponseEntity.ok(order);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Updated status occurred: " + e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrderByID(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllOrder() {
        orderService.deleteAllOrder();
        return ResponseEntity.ok().build();
    }

}
