package com.gmail.merikbest2015.ecommerce.service.Impl;

import com.gmail.merikbest2015.ecommerce.controller.PaymentController;
import com.gmail.merikbest2015.ecommerce.domain.Order;
import com.gmail.merikbest2015.ecommerce.domain.OrderItem;
import com.gmail.merikbest2015.ecommerce.domain.Perfume;
import com.gmail.merikbest2015.ecommerce.dto.PaymentRestDTO;
import com.gmail.merikbest2015.ecommerce.dto.order.OrderResponse;
import com.gmail.merikbest2015.ecommerce.dto.order.OrderResponseWithPayURL;
import com.gmail.merikbest2015.ecommerce.exception.ApiRequestException;
import com.gmail.merikbest2015.ecommerce.repository.OrderItemRepository;
import com.gmail.merikbest2015.ecommerce.repository.OrderRepository;
import com.gmail.merikbest2015.ecommerce.repository.PerfumeRepository;
import com.gmail.merikbest2015.ecommerce.service.OrderService;
import com.gmail.merikbest2015.ecommerce.service.email.MailSender;

import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PerfumeRepository perfumeRepository;
    private final MailSender mailSender;

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiRequestException(ORDER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        Order order = getOrderById(orderId);
        return order.getOrderItems();
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByIdAsc(pageable);
    }

    @Override
    public Page<Order> getUserOrders(String email, Pageable pageable) {
        return orderRepository.findOrderByEmail(email, pageable);
    }

    @Override
    @Transactional
    public OrderResponseWithPayURL postOrder(Order order, Map<Long, Long> perfumesId) throws UnsupportedEncodingException {
        try {
            List<OrderItem> orderItemList = new ArrayList<>();

            for (Map.Entry<Long, Long> entry : perfumesId.entrySet()) {
                Perfume perfume = perfumeRepository.findById(entry.getKey()).get();
                OrderItem orderItem = new OrderItem();
                orderItem.setPerfume(perfume);
                orderItem.setAmount((perfume.getPrice() * entry.getValue()));
                orderItem.setQuantity(entry.getValue());
                orderItemList.add(orderItem);
                orderItemRepository.save(orderItem);
            }
            order.getOrderItems().addAll(orderItemList);
            orderRepository.save(order);


            String subject = "Order #" + order.getId();
            String template = "order-template";
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("order", order);
            mailSender.sendMessageHtml(order.getEmail(), subject, template, attributes);
            System.out.print("order" + order.getId());

            System.out.println("totalPrice: " + order.getTotalPrice());

//            double amount = order.getTotalPrice();
//            long totalPrice = amount


            PaymentController paymentController = new PaymentController();
//            System.out.println("totalPrice: " + order.getTotalPrice());

//
            ResponseEntity respone = paymentController.createPayment(order.getTotalPrice());
            PaymentRestDTO paymentRestDTO = (PaymentRestDTO) respone.getBody();
            String url = paymentRestDTO.getURL();
            OrderResponseWithPayURL orderResponseWithPayURL = new OrderResponseWithPayURL();
            orderResponseWithPayURL.setOrder(order);
            if (url != null) {
                orderResponseWithPayURL.setUrl(url);
            }

            return orderResponseWithPayURL;
            
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public String deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiRequestException(ORDER_NOT_FOUND, HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
        return "Order deleted successfully";
    }

    @Override
    public DataFetcher<List<Order>> getAllOrdersByQuery() {
        return dataFetchingEnvironment -> orderRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Order>> getUserOrdersByEmailQuery() {
        return dataFetchingEnvironment -> {
            String email = dataFetchingEnvironment.getArgument("email").toString();
            return orderRepository.findOrderByEmail(email);
        };
    }
}
