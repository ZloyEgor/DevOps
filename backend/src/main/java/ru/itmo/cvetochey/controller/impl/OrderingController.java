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
import ru.itmo.cvetochey.model.Ordering;
import ru.itmo.cvetochey.repository.OrderingRepository;

@RestController
@RequestMapping("cvet-ochey/api/v1/ordering")
public class OrderingController {

    private final OrderingRepository orderingRepository;

    public OrderingController(OrderingRepository orderingRepository) {
        this.orderingRepository = orderingRepository;
    }

    @GetMapping("/get-all")
    public List<Ordering> getAllOrders() {
        return orderingRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Ordering> getOrderById(@PathVariable Long id) {
        return orderingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/create")
    public Ordering createOrder(@RequestBody Ordering ordering) {
        return orderingRepository.save(ordering);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Ordering> updateOrder(@PathVariable Long id, @RequestBody Ordering updated) {
        return orderingRepository.findById(id)
                .map(o -> {
                    o.setQuantity(updated.getQuantity());
                    o.setClient(updated.getClient());
                    o.setProduct(updated.getProduct());
                    return ResponseEntity.ok(orderingRepository.save(o));
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (!orderingRepository.existsById(id)) {
            return ResponseEntity.noContent().build();
        }
        orderingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
