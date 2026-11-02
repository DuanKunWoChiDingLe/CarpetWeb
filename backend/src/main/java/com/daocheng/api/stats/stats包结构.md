stats
├── controller
│   └── StatsController.java               # 提供销量统计相关接口（概览、趋势、系列占比等）
├── service
│   ├── StatsService.java                  # 销量统计服务接口
│   └── impl
│       └── StatsServiceImpl.java          # 销量统计服务实现类（使用 OrderMapper、ProductMapper 等）
├── dto
│   ├── SalesOverviewDTO.java               # 概览数据 DTO（今日、本月、累计）
│   ├── SalesTrendDTO.java                  # 趋势数据 DTO（日期、金额、订单数、销量）
│   ├── SeriesSalesDTO.java                 # 系列销量占比 DTO
│   ├── BrandSalesDTO.java                   # 品牌销量占比 DTO
│   └── TopProductDTO.java                   # 热销商品 DTO
└── mapper                                   # 无需新建 Mapper，复用其他模块的 Mapper
（依赖 order.mapper.OrderMapper、order.mapper.OrderItemMapper、
product.mapper.ProductMapper、product.mapper.SeriesMapper、
product.mapper.BrandMapper）