user
├── controller
│   └── AuthController.java            // 认证控制器（处理登录、注册、获取当前用户）
├── service
│   ├── UserService.java               // 用户服务接口
│   └── impl
│       └── UserServiceImpl.java       // 用户服务实现类
├── mapper
│   └── UserMapper.java                 // MyBatis-Plus 的 Mapper 接口
├── entity
│   └── User.java                       // 用户实体类（对应数据库表）
├── dto
│   ├── request
│   │   ├── UpdateProfileRequest.java   // 更新个人资料请求 DTO
│   │   ├── LoginRequest.java           // 登录请求 DTO（username, password）
│   │   └── RegisterRequest.java        // 注册请求 DTO（username, password, nickname, role）
│   └── response
│       ├── LoginResponse.java          // 登录响应 DTO（id, username, nickname, role, avatar, token）
│       └── UserInfoResponse.java       // 用户信息响应 DTO（id, username, nickname, role, avatar）
└── vo (可选)
└── UserInfoVO.java                  // 视图对象，若与 response 重复可省略