<!-- <template>
  <div class="product-grid">
    <el-row :gutter="20">
      <el-col
        v-for="product in productList"
        :key="product.id"
        :xs="24" :sm="12" :md="8" :lg="6" :xl="6"
      >
        <ProductCard :product="product" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ProductCard from './ProductCard.vue'

// 商品数据类型（增加 images 数组）
interface Product {
  id: number
  name: string
  price: number
  images: string[]   // 多张图片，用于轮播
}

// 模拟商品数据，每个商品提供三张图片（可替换为真实图片路径）
const productList = ref<Product[]>([
  {
    id: 1,
    name: '简约现代客厅地毯',
    price: 599.00,
    images: [
      '/images/product1_1.jpg',
      '/images/product1_2.jpg',
      '/images/product1_3.jpg'
    ]
  },
  {
    id: 2,
    name: '北欧风格卧室地毯',
    price: 399.00,
    images: [
      '/images/product2_1.jpg',
      '/images/product2_2.jpg',
      '/images/product2_3.jpg'
    ]
  },
  {
    id: 3,
    name: '手工编织门垫',
    price: 129.00,
    images: [
      '/images/product3_1.jpg',
      '/images/product3_2.jpg',
      '/images/product3_3.jpg'
    ]
  },
  {
    id: 4,
    name: '儿童房卡通地毯',
    price: 299.00,
    images: [
      '/images/product4_1.jpg',
      '/images/product4_2.jpg',
      '/images/product4_3.jpg'
    ]
  },
  {
    id: 5,
    name: '波斯风格地毯',
    price: 1299.00,
    images: [
      '/images/product5_1.jpg',
      '/images/product5_2.jpg',
      '/images/product5_3.jpg'
    ]
  },
  {
    id: 6,
    name: '防滑厨房地垫',
    price: 89.00,
    images: [
      '/images/product6_1.jpg',
      '/images/product6_2.jpg',
      '/images/product6_3.jpg'
    ]
  },
  {
    id: 7,
    name: '长毛绒地毯',
    price: 799.00,
    images: [
      '/images/product7_1.jpg',
      '/images/product7_2.jpg',
      '/images/product7_3.jpg'
    ]
  },
  {
    id: 8,
    name: '可定制地毯',
    price: 499.00,
    images: [
      '/images/product8_1.jpg',
      '/images/product8_2.jpg',
      '/images/product8_3.jpg'
    ]
  }
])
</script>

<style scoped>
.product-grid {
  margin-top: 30px;
}
</style> -->