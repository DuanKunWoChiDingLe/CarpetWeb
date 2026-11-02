src/
├── stores/
│   └── product.ts          # 产品 store（管理产品列表、筛选、排序、分页）
├── views/
│   └── ProductsView.vue    # 产品中心页面
├── components/
│   ├── layout/
│   │   └── NavBar.vue      # 修改后的导航栏（含自动提示搜索）
│   └── product/
│       └── ProductCard.vue # 商品卡片（需适配展示品牌、类型、每平米价格）
└── types/
    └── product.ts          # 类型定义（可选，也可直接在 store 中定义）