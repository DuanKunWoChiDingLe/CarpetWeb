product
├── controller
│   ├── BrandController.java
│   ├── SeriesController.java
│   └── ProductController.java
├── service
│   ├── BrandService.java
│   ├── SeriesService.java
│   ├── ProductService.java
│   └── impl
│       ├── BrandServiceImpl.java
│       ├── SeriesServiceImpl.java
│       └── ProductServiceImpl.java
├── mapper
│   ├── BrandMapper.java
│   ├── SeriesMapper.java
│   └── ProductMapper.java
├── entity
│   ├── Brand.java
│   ├── Series.java
│   └── Product.java
├── dto
│   ├── request
│   │   ├── BrandRequest.java
│   │   ├── SeriesRequest.java
│   │   └── ProductRequest.java
│   └── response
│       ├── BrandResponse.java
│       ├── SeriesResponse.java
│       ├── ProductResponse.java
│       └── PageResult.java (可复用 common 中的分页)
└── enums
├── LayTypeEnum.java
└── MaterialEnum.java