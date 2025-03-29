package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.itmo.cvetochey.model.Order;
import ru.itmo.cvetochey.repository.OrderRepository;

@RestController
@RequestMapping("cvet-ochey/api/v1/order")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/get-all")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/create")
    public Order createOrder(@RequestBody Order ordering) {
        return orderRepository.save(ordering);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updated) {
        return orderRepository.findById(id)
                .map(o -> {
                    o.setClient(updated.getClient());
                    o.setProduct(updated.getProduct());
                    return ResponseEntity.ok(orderRepository.save(o));
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.noContent().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
