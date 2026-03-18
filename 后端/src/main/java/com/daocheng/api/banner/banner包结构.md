banner
├── controller
│   ├── BannerController.java           // 商家端轮播图管理接口（需商家权限）
│   └── PublicBannerController.java      // 公共轮播图接口（供首页调用，无需登录）
├── service
│   ├── BannerService.java               // 业务接口
│   └── impl
│       └── BannerServiceImpl.java       // 业务实现类
├── mapper
│   └── BannerMapper.java                 // MyBatis-Plus Mapper接口
├── entity
│   └── Banner.java                       // 轮播图实体类（对应数据库表）
├── dto
│   ├── request
│   │   ├── BannerRequest.java            // 新增/更新轮播图请求体
│   │   └── BannerStatusRequest.java      // 更新状态请求体
│   └── response
│       └── BannerResponse.java           // 轮播图响应体（用于返回给前端）
└── enums (可选)
└── LinkTypeEnum.java                  // 如果链接类型需要枚举，可定义，目前用字符串即可