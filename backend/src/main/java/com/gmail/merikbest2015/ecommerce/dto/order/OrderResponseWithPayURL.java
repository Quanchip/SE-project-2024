package com.gmail.merikbest2015.ecommerce.dto.order;

import com.gmail.merikbest2015.ecommerce.domain.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseWithPayURL {
    private Order order;
    private String url;
}
