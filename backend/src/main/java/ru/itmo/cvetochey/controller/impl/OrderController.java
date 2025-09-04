package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/cvet-ochey/api/v1/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderController(OrderRepository orderRepository, 
                          ClientRepository clientRepository,
                          ProductRepository productRepository,
                          OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

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
    public ResponseEntity<OrderDto> create(@RequestBody OrderDto dto) {
        Order order = orderMapper.toEntity(dto);
        
        // Validate client exists
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElse(null);
            if (client == null) {
                return ResponseEntity.badRequest().build();
            }
            order.setClient(client);
        }
        
        // Validate product exists
        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().build();
            }
            order.setProduct(product);
        }
        
        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(orderMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Long id, @RequestBody OrderDto dto) {
        return orderRepository.findById(id)
                .map(entity -> {
                    entity.setTotalPrice(dto.getTotalPrice());
                    
                    // Validate client if provided
                    if (dto.getClientId() != null) {
                        Client client = clientRepository.findById(dto.getClientId()).orElse(null);
                        if (client == null) {
                            return ResponseEntity.badRequest().<OrderDto>build();
                        }
                        entity.setClient(client);
                    } else {
                        entity.setClient(null);
                    }
                    
                    // Validate product if provided
                    if (dto.getProductId() != null) {
                        Product product = productRepository.findById(dto.getProductId()).orElse(null);
                        if (product == null) {
                            return ResponseEntity.badRequest().<OrderDto>build();
                        }
                        entity.setProduct(product);
                    } else {
                        entity.setProduct(null);
                    }
                    
                    orderRepository.save(entity);
                    return ResponseEntity.ok(orderMapper.toDto(entity));
                })
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

    @GetMapping("/client/{clientId}")
    public List<OrderDto> getByClientId(@PathVariable Long clientId) {
        return orderRepository.findByClientId(clientId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/product/{productId}")
    public List<OrderDto> getByProductId(@PathVariable Long productId) {
        return orderRepository.findByProductId(productId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/price-range")
    public List<OrderDto> getByPriceRange(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        return orderRepository.findByTotalPriceBetween(minPrice, maxPrice).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

}
