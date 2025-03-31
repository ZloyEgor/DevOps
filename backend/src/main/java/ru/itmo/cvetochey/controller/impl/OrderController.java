package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.dto.OrderDto;
import ru.itmo.cvetochey.mapper.OrderMapper;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.Order;
import ru.itmo.cvetochey.model.Product;
import ru.itmo.cvetochey.repository.ClientRepository;
import ru.itmo.cvetochey.repository.OrderRepository;
import ru.itmo.cvetochey.repository.ProductRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cvet-ochey/api/v1/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @GetMapping
    public List<OrderDto> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOne(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrderDto create(@RequestBody OrderDto dto) {
        Order order = orderMapper.toEntity(dto);
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId()).orElse(null);
            order.setClient(client);
        }
        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            order.setProduct(product);
        }
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Long id, @RequestBody OrderDto dto) {
        return orderRepository.findById(id)
                .map(entity -> {
                    entity.setTotalPrice(dto.getTotalPrice());
                    if (dto.getClientId() != null) {
                        Client client = clientRepository.findById(dto.getClientId()).orElse(null);
                        entity.setClient(client);
                    } else {
                        entity.setClient(null);
                    }
                    if (dto.getProductId() != null) {
                        Product product = productRepository.findById(dto.getProductId()).orElse(null);
                        entity.setProduct(product);
                    } else {
                        entity.setProduct(null);
                    }
                    orderRepository.save(entity);
                    return orderMapper.toDto(entity);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
