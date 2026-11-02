order
├── controller
│   ├── OrderController.java          // 用户端订单接口
│   └── MerchantOrderController.java   // 商家端订单接口
├── service
│   ├── OrderService.java              // 订单服务接口
│   └── impl
│       └── OrderServiceImpl.java       // 订单服务实现
├── mapper
│   ├── OrderMapper.java                // 订单Mapper
│   └── OrderItemMapper.java             // 订单项Mapper
├── entity
│   ├── Order.java                       // 订单实体
│   └── OrderItem.java                    // 订单项实体
└── dto
├── request
│   ├── OrderCreateRequest.java
│   ├── CartItem.java (可内嵌)
│   └── PayRequest.java
└── response
├── OrderResponse.java
└── OrderItemResponse.java