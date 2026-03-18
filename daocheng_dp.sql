/*
 Navicat Premium Dump SQL

 Source Server         : DK
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : daocheng_dp

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 18/03/2026 22:01:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `link_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '链接类型：product 或 url',
  `link_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '链接值（商品ID或完整URL）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序（升序）',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_enabled_sort`(`enabled` ASC, `sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (2, '/uploads/20260315/2433de243457407aa8b9cda36e311e36.png', 'product', '9', 1, 1, '2026-03-15 18:28:36', '2026-03-15 18:28:36');
INSERT INTO `banner` VALUES (3, '/uploads/20260315/622be37a39ac48f9a74eb11edce6e748.png', 'product', '11', 2, 1, '2026-03-15 18:29:32', '2026-03-15 18:29:32');
INSERT INTO `banner` VALUES (4, '/uploads/20260315/1d054dc9938c4404982f89ecd7f1247b.jpg', 'product', '7', 3, 1, '2026-03-15 18:32:32', '2026-03-15 18:32:32');
INSERT INTO `banner` VALUES (5, '/uploads/20260315/54e64b7db9544cb8b7868fb9f15b7ddf.jpg', 'product', '8', 4, 1, '2026-03-15 18:32:45', '2026-03-15 18:32:45');
INSERT INTO `banner` VALUES (6, '/uploads/20260316/e8d19687fb7747e680d54784fec9203e.jpg', 'product', '27', 5, 1, '2026-03-16 22:00:33', '2026-03-16 22:00:33');

-- ----------------------------
-- Table structure for brand
-- ----------------------------
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '品牌名称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌logo',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '品牌表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of brand
-- ----------------------------
INSERT INTO `brand` VALUES (2, '飞湃', '', 2, '2026-03-13 20:22:36', '2026-03-13 20:22:36');
INSERT INTO `brand` VALUES (3, '红塬', '', 3, '2026-03-13 20:23:08', '2026-03-13 20:23:08');
INSERT INTO `brand` VALUES (4, '道成', '', 3, '2026-03-13 20:28:55', '2026-03-13 20:28:55');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, 5, '2026-03-15 17:04:47', '2026-03-15 17:04:47');
INSERT INTO `cart` VALUES (2, 2, '2026-03-15 17:14:56', '2026-03-15 17:14:56');
INSERT INTO `cart` VALUES (3, 1, '2026-03-15 17:30:12', '2026-03-15 17:30:12');

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL COMMENT '购物车ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '数量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cart_product`(`cart_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_cart_id`(`cart_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (29, 3, 31, 6, '2026-03-17 19:08:30', '2026-03-17 19:08:30');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号（唯一）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（冗余）',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '订单状态：pending待付款, paid已付款, shipped已发货, completed已完成, cancelled已取消',
  `consignee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '买家备注',
  `paid_at` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, '202603142107315867', 2, 'qwe123', 41.70, 'completed', '特朗普', '14785245841', '美国华盛顿白宫', '', '2026-03-14 21:14:49', '2026-03-14 21:07:32', '2026-03-14 21:07:32');
INSERT INTO `order` VALUES (2, '202603142152090639', 4, '203139976', 159.90, 'shipped', '敌法师', '15974201139', 'DOTA2国服敌法师家', '', '2026-03-14 21:52:17', '2026-03-14 21:52:10', '2026-03-14 21:52:10');
INSERT INTO `order` VALUES (3, '202603151726034283', 2, 'qwe123', 9939.60, 'cancelled', '华盛顿', '14785245215', '阿斯顿', '', NULL, '2026-03-15 17:26:04', '2026-03-15 17:26:04');
INSERT INTO `order` VALUES (4, '202603151728006503', 2, 'qwe123', 39.60, 'completed', '阿斯顿', '13421514923', '阿斯顿', '', '2026-03-15 17:28:10', '2026-03-15 17:28:01', '2026-03-15 17:28:01');
INSERT INTO `order` VALUES (5, '202603151728455926', 2, 'qwe123', 10078.20, 'completed', 'asd', '13214521485', 'asd.', '', '2026-03-15 17:28:50', '2026-03-15 17:28:45', '2026-03-15 17:28:45');
INSERT INTO `order` VALUES (6, '202603151729508845', 2, 'qwe123', 1380.00, 'completed', '阿斯顿', '13952148246', '阿萨', '', '2026-03-15 17:29:56', '2026-03-15 17:29:51', '2026-03-15 17:29:51');
INSERT INTO `order` VALUES (7, '202603151951161148', 2, 'qwe123', 2870.00, 'completed', '华盛顿', '13814751245', '白宫', '', '2026-03-15 19:55:06', '2026-03-15 19:51:17', '2026-03-15 19:51:17');
INSERT INTO `order` VALUES (8, '202603152136355094', 5, '20010629', 14900.00, 'completed', '🦌😓草', '13252181234', '广东理工学院47栋41513宿舍1号床', '铺满床位，谢谢', '2026-03-15 21:36:46', '2026-03-15 21:36:35', '2026-03-15 21:36:35');
INSERT INTO `order` VALUES (9, '202603162127347069', 5, '20010629', 9900.00, 'completed', '特朗普', '13254862469', '美国华盛顿白宫', '', '2026-03-16 21:28:44', '2026-03-16 21:27:34', '2026-03-16 21:27:34');
INSERT INTO `order` VALUES (10, '202603171823561692', 5, '20010629', 39.60, 'completed', '领导证', '18675920319', '美国白宫', '', '2026-03-17 18:24:16', '2026-03-17 18:23:56', '2026-03-17 18:23:56');
INSERT INTO `order` VALUES (11, '202603171831061794', 5, '20010629', 1380.00, 'completed', '领导证', '15486312154', '白宫', '', '2026-03-17 18:31:28', '2026-03-17 18:31:06', '2026-03-17 18:31:06');
INSERT INTO `order` VALUES (12, '202603171834444563', 5, '20010629', 9.90, 'completed', '请问', '14325434537', '但是', '', '2026-03-17 18:34:51', '2026-03-17 18:34:45', '2026-03-17 18:34:45');
INSERT INTO `order` VALUES (13, '202603171838362500', 5, '20010629', 990.00, 'completed', 'asd', '13165432165', '1321', '', '2026-03-17 18:38:46', '2026-03-17 18:38:37', '2026-03-17 18:38:37');
INSERT INTO `order` VALUES (14, '202603171842588709', 5, '20010629', 9.90, 'completed', 'dc', '13464248976', '231', '', '2026-03-17 18:43:13', '2026-03-17 18:42:59', '2026-03-17 18:42:59');
INSERT INTO `order` VALUES (15, '202603171907373971', 5, '20010629', 9.90, 'completed', 'asd', '13532876321', '阿斯顿', '', '2026-03-17 19:07:45', '2026-03-17 19:07:38', '2026-03-17 19:07:38');
INSERT INTO `order` VALUES (16, '202603171912545427', 5, '20010629', 9.90, 'paid', 'asd', '13215642315', 'asd', '', '2026-03-17 19:13:02', '2026-03-17 19:12:54', '2026-03-17 19:12:54');
INSERT INTO `order` VALUES (17, '202603171919295607', 5, '20010629', 2178.00, 'paid', 'sad', '13532154321', 'as', '', '2026-03-17 19:19:37', '2026-03-17 19:19:30', '2026-03-17 19:19:30');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称（快照）',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片（快照）',
  `price` decimal(10, 2) NOT NULL COMMENT '下单时的单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单商品明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 7, '幸运12', '/uploads/20260314/72d1ba4db1b9447e800ca50662556b08.jpg', 13.90, 3, '2026-03-14 21:07:31');
INSERT INTO `order_item` VALUES (2, 2, 7, '幸运12', '/uploads/20260314/72d1ba4db1b9447e800ca50662556b08.jpg', 13.90, 4, '2026-03-14 21:52:09');
INSERT INTO `order_item` VALUES (3, 2, 6, '幸运T12', '/uploads/20260314/2c9f3ac48ab34421bbae44d885d8da54.jpg', 14.90, 7, '2026-03-14 21:52:09');
INSERT INTO `order_item` VALUES (4, 3, 18, '江J17', '/uploads/20260315/0e04b71bf9bb4b118f343e994e66274b.jpg', 19.80, 500, '2026-03-15 17:26:03');
INSERT INTO `order_item` VALUES (5, 3, 31, '实07', '/uploads/20260315/3bfdad2e39cb4292ad69f52af18b85d6.jpg', 9.90, 1, '2026-03-15 17:26:03');
INSERT INTO `order_item` VALUES (6, 3, 32, '实09', '/uploads/20260315/18234ec0594d452c956c2ea53823bb0a.jpg', 9.90, 3, '2026-03-15 17:26:03');
INSERT INTO `order_item` VALUES (7, 4, 31, '实07', '/uploads/20260315/3bfdad2e39cb4292ad69f52af18b85d6.jpg', 9.90, 4, '2026-03-15 17:28:00');
INSERT INTO `order_item` VALUES (8, 5, 17, '江J05', '/uploads/20260315/c7ffcd67e3ba4e31ad397920bd37f56f.jpg', 19.80, 509, '2026-03-15 17:28:45');
INSERT INTO `order_item` VALUES (9, 6, 12, 'M1系列05', '/uploads/20260315/2e85dc462fa14e11bf70a1e4b7e772da.png', 13.80, 100, '2026-03-15 17:29:50');
INSERT INTO `order_item` VALUES (10, 7, 6, '幸运T12', '/uploads/20260314/2c9f3ac48ab34421bbae44d885d8da54.jpg', 14.90, 100, '2026-03-15 19:51:16');
INSERT INTO `order_item` VALUES (11, 7, 12, 'M1系列05', '/uploads/20260315/2e85dc462fa14e11bf70a1e4b7e772da.png', 13.80, 100, '2026-03-15 19:51:16');
INSERT INTO `order_item` VALUES (12, 8, 6, '幸运T12', '/uploads/20260314/2c9f3ac48ab34421bbae44d885d8da54.jpg', 14.90, 1000, '2026-03-15 21:36:35');
INSERT INTO `order_item` VALUES (13, 9, 14, '江J01', '/uploads/20260315/6062449baad24de08609c242dd70db00.jpg', 19.80, 500, '2026-03-16 21:27:34');
INSERT INTO `order_item` VALUES (14, 10, 31, '实07', '/uploads/20260315/3bfdad2e39cb4292ad69f52af18b85d6.jpg', 9.90, 1, '2026-03-17 18:23:56');
INSERT INTO `order_item` VALUES (15, 10, 32, '实09', '/uploads/20260315/18234ec0594d452c956c2ea53823bb0a.jpg', 9.90, 3, '2026-03-17 18:23:56');
INSERT INTO `order_item` VALUES (16, 11, 9, 'M1系列01', '/uploads/20260315/c11ef9736ece4dd4b52eb2bc0b751049.png', 13.80, 100, '2026-03-17 18:31:06');
INSERT INTO `order_item` VALUES (17, 12, 32, '实09', '/uploads/20260315/18234ec0594d452c956c2ea53823bb0a.jpg', 9.90, 1, '2026-03-17 18:34:44');
INSERT INTO `order_item` VALUES (18, 13, 31, '实07', '/uploads/20260315/3bfdad2e39cb4292ad69f52af18b85d6.jpg', 9.90, 100, '2026-03-17 18:38:36');
INSERT INTO `order_item` VALUES (19, 14, 32, '实09', '/uploads/20260315/18234ec0594d452c956c2ea53823bb0a.jpg', 9.90, 1, '2026-03-17 18:42:58');
INSERT INTO `order_item` VALUES (20, 15, 31, '实07', '/uploads/20260315/3bfdad2e39cb4292ad69f52af18b85d6.jpg', 9.90, 1, '2026-03-17 19:07:37');
INSERT INTO `order_item` VALUES (21, 16, 31, '实07', '/uploads/20260315/3bfdad2e39cb4292ad69f52af18b85d6.jpg', 9.90, 1, '2026-03-17 19:12:54');
INSERT INTO `order_item` VALUES (22, 17, 17, '江J05', '/uploads/20260315/c7ffcd67e3ba4e31ad397920bd37f56f.jpg', 19.80, 110, '2026-03-17 19:19:29');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `series_id` bigint NOT NULL COMMENT '所属系列ID',
  `color_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '色号',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称（系列名+色号）',
  `price_per_sqm` decimal(10, 2) NOT NULL COMMENT '每平方米价格',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `images` json NULL COMMENT '图片URL数组',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0下架 1上架',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sales` int NOT NULL DEFAULT 0 COMMENT '累计销量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_series_id`(`series_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表（色号）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (6, 3, 'T12', '幸运T12', 14.90, 4000, '[\"/uploads/20260314/2c9f3ac48ab34421bbae44d885d8da54.jpg\"]', '', 1, '2026-03-14 01:04:01', '2026-03-14 01:04:01', 0);
INSERT INTO `product` VALUES (7, 3, '12', '幸运12', 13.90, 93, '[\"/uploads/20260314/72d1ba4db1b9447e800ca50662556b08.jpg\"]', '', 1, '2026-03-14 01:07:49', '2026-03-14 01:07:49', 0);
INSERT INTO `product` VALUES (8, 3, '13', '幸运13', 13.90, 500, '[\"/uploads/20260314/57d2b5859ca645fca95a5ab4b2035293.jpg\"]', '', 1, '2026-03-14 01:08:06', '2026-03-14 01:08:06', 0);
INSERT INTO `product` VALUES (9, 4, '01', 'M1系列01', 13.80, 500, '[\"/uploads/20260315/c11ef9736ece4dd4b52eb2bc0b751049.png\"]', '', 1, '2026-03-15 15:45:18', '2026-03-15 15:45:18', 0);
INSERT INTO `product` VALUES (10, 4, '02', 'M1系列02', 13.80, 1000, '[\"/uploads/20260315/deef5a17c73548e486bb462a996c0f29.png\"]', '', 1, '2026-03-15 15:45:32', '2026-03-15 15:45:32', 0);
INSERT INTO `product` VALUES (11, 4, '03', 'M1系列03', 13.80, 1000, '[\"/uploads/20260315/83dae286825947489cfbef88eab40801.png\"]', '', 1, '2026-03-15 15:45:51', '2026-03-15 15:45:51', 0);
INSERT INTO `product` VALUES (12, 4, '05', 'M1系列05', 13.80, 200, '[\"/uploads/20260315/2e85dc462fa14e11bf70a1e4b7e772da.png\"]', '', 1, '2026-03-15 15:46:04', '2026-03-15 15:46:04', 0);
INSERT INTO `product` VALUES (13, 4, '06', 'M1系列06', 13.80, 50, '[\"/uploads/20260315/f962aba62bb14fd1b01d420c866b48f9.png\"]', '', 1, '2026-03-15 15:46:20', '2026-03-15 15:46:20', 0);
INSERT INTO `product` VALUES (14, 5, '01', '江J01', 19.80, 1000, '[\"/uploads/20260315/6062449baad24de08609c242dd70db00.jpg\"]', '', 1, '2026-03-15 15:50:22', '2026-03-15 15:50:22', 0);
INSERT INTO `product` VALUES (15, 5, '03', '江J03', 19.80, 200, '[\"/uploads/20260315/9359896a8d5f42a5af85d1ebe16a7366.jpg\"]', '', 1, '2026-03-15 15:50:42', '2026-03-15 15:50:42', 0);
INSERT INTO `product` VALUES (16, 5, '04', '江J04', 19.80, 500, '[\"/uploads/20260315/92380b241fe64184bafc931a22548e53.jpg\"]', '', 1, '2026-03-15 15:51:00', '2026-03-15 15:51:00', 0);
INSERT INTO `product` VALUES (17, 5, '05', '江J05', 19.80, 181, '[\"/uploads/20260315/c7ffcd67e3ba4e31ad397920bd37f56f.jpg\"]', '', 1, '2026-03-15 15:51:17', '2026-03-15 15:51:17', 0);
INSERT INTO `product` VALUES (18, 5, '17', '江J17', 19.80, 2000, '[\"/uploads/20260315/0e04b71bf9bb4b118f343e994e66274b.jpg\"]', '', 1, '2026-03-15 15:51:42', '2026-03-15 15:51:42', 0);
INSERT INTO `product` VALUES (19, 6, '01', '初01', 8.80, 2000, '[\"/uploads/20260315/1ec8309c3b6a4fe3ab648f992608a101.jpg\"]', '', 1, '2026-03-15 16:02:19', '2026-03-15 16:02:19', 0);
INSERT INTO `product` VALUES (20, 6, '03', '初03', 8.80, 2000, '[\"/uploads/20260315/0f755e2e158d4d91a5b0e176336dd2c1.jpg\"]', '', 1, '2026-03-15 16:02:32', '2026-03-15 16:02:32', 0);
INSERT INTO `product` VALUES (21, 6, '04', '初04', 8.80, 2000, '[\"/uploads/20260315/04cff284ec714d74be0e51a9df61e291.jpg\"]', '', 1, '2026-03-15 16:02:43', '2026-03-15 16:02:43', 0);
INSERT INTO `product` VALUES (22, 6, '06', '初06', 8.80, 2000, '[\"/uploads/20260315/a66612119bb944fca474900f34a256b5.jpg\"]', '', 1, '2026-03-15 16:02:57', '2026-03-15 16:02:57', 0);
INSERT INTO `product` VALUES (23, 6, '12', '初12', 8.80, 2000, '[\"/uploads/20260315/bf3574f418ad4679a61b012e0d22a8a7.jpg\"]', '', 1, '2026-03-15 16:03:11', '2026-03-15 16:03:11', 0);
INSERT INTO `product` VALUES (24, 6, '11', '初11', 8.80, 2000, '[\"/uploads/20260315/3eaa24ebf77d4d8583304241d9234b61.jpg\"]', '', 1, '2026-03-15 16:03:24', '2026-03-15 16:03:24', 0);
INSERT INTO `product` VALUES (25, 6, '07', '初07', 8.80, 2000, '[\"/uploads/20260315/7c482c45a6c64213a79125c9bbc3b8f9.jpg\"]', '', 1, '2026-03-15 16:03:38', '2026-03-15 16:03:38', 0);
INSERT INTO `product` VALUES (26, 6, '09', '初09', 8.80, 2000, '[\"/uploads/20260315/744e4e0d0d1743388d77a50253fa8507.jpg\"]', '', 1, '2026-03-15 16:03:49', '2026-03-15 16:03:49', 0);
INSERT INTO `product` VALUES (27, 7, '01', '实01', 9.90, 1000, '[\"/uploads/20260315/a91f5d43019b4ee0b757e9242a266631.jpg\"]', '', 1, '2026-03-15 16:05:25', '2026-03-15 16:05:25', 0);
INSERT INTO `product` VALUES (28, 7, '02', '实02', 9.90, 1000, '[\"/uploads/20260315/2f860edb8dc143ef8ad343befc1e5bd2.jpg\"]', '', 1, '2026-03-15 16:05:37', '2026-03-15 16:05:37', 0);
INSERT INTO `product` VALUES (29, 7, '03', '实03', 9.90, 1000, '[\"/uploads/20260315/0be68690cc834c57abc5818fe5f0e298.jpg\"]', '', 1, '2026-03-15 16:05:51', '2026-03-15 16:05:51', 0);
INSERT INTO `product` VALUES (30, 7, '05', '实05', 10.90, 1500, '[\"/uploads/20260315/ca7549d4c68e45eeb173a3d77c80dd62.jpg\"]', '', 1, '2026-03-15 16:06:05', '2026-03-15 16:06:05', 0);
INSERT INTO `product` VALUES (31, 7, '07', '实07', 9.90, 893, '[\"/uploads/20260315/3bfdad2e39cb4292ad69f52af18b85d6.jpg\"]', '', 1, '2026-03-15 16:06:19', '2026-03-15 16:06:19', 0);
INSERT INTO `product` VALUES (32, 7, '09', '实09', 9.90, 995, '[\"/uploads/20260315/18234ec0594d452c956c2ea53823bb0a.jpg\"]', '', 1, '2026-03-15 16:06:36', '2026-03-15 16:06:36', 0);
INSERT INTO `product` VALUES (33, 7, '12', '实12', 9.90, 1000, '[\"/uploads/20260315/6d3ad6f5253c438baf2632b7eeb6102c.jpg\"]', '', 1, '2026-03-15 16:06:47', '2026-03-15 16:06:47', 0);

-- ----------------------------
-- Table structure for series
-- ----------------------------
DROP TABLE IF EXISTS `series`;
CREATE TABLE `series`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `brand_id` bigint NOT NULL COMMENT '品牌ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列名称',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '系列封面图',
  `lay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '铺设方式: full/modular',
  `material` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '材质: polypropylene/polyester/nylon',
  `spec` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规格: 4m宽 / 50x50 / 100x25',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '系列描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_brand_id`(`brand_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of series
-- ----------------------------
INSERT INTO `series` VALUES (3, 2, '幸运', '/uploads/20260313/0d575a845af94486b60d4e5b1f05652c.jpg', 'modular', 'polypropylene', '50*50', '品牌：飞湃型号：幸运系列\n尺寸：50*50cm\n材质：丙纶\n底背：环保无纺底', '2026-03-13 22:38:06', '2026-03-13 22:38:06');
INSERT INTO `series` VALUES (4, 2, 'M1系列', '/uploads/20260313/5799e3e414f64dd7ba292ba555d44fa8.png', 'modular', 'polypropylene', '50*50', '品牌：飞湃型号：幸运系列\n尺寸：50*50cm\n材质：丙纶\n底背：环保无纺底', '2026-03-13 22:40:18', '2026-03-13 22:40:18');
INSERT INTO `series` VALUES (5, 3, '江J', '/uploads/20260315/bcb76f01b43e49e98f7ac31d82da4295.jpg', 'modular', 'polypropylene', '50*50', '品牌：霖坤红塬型号：江J尺寸：50*50CM材质：丙纶底背：沥青底风格：现代简约适用场景：客厅、卧室、书房、走廊、大厅、会议室备注：深圳现货', '2026-03-15 15:49:34', '2026-03-15 15:49:34');
INSERT INTO `series` VALUES (6, 2, '初', '/uploads/20260315/202db09d44504028bfeb158b437642f0.jpg', 'full', 'polypropylene', '4M', '', '2026-03-15 16:01:57', '2026-03-15 16:01:57');
INSERT INTO `series` VALUES (7, 2, '实', '/uploads/20260315/a430b19764fa4749859ea7223ed78d33.jpg', 'full', 'polypropylene', '4M', '', '2026-03-15 16:05:07', '2026-03-15 16:05:07');
INSERT INTO `series` VALUES (8, 2, '界系列', '/uploads/20260316/b73d9ddb18b1455fa31642ede1a7ae1d.jpg', 'modular', 'polyester', '50*50', '品牌：飞湃\n型号：素界/序界/影界系列尺寸：50*50/100*33.33底背： PVC消防等级：B1', '2026-03-16 21:43:07', '2026-03-16 21:43:07');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'customer',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'root', '管理员', '$2a$10$wRETnIiOWGoROpKMuw2C2eijOqZ18dVSy.uRc/c1SpOJHlBOi//Py', 'merchant', '/uploads/20260314/e84201093d9a4ad5b8972b3c32db0b44.png', '2026-03-12 17:02:17', '2026-03-12 17:02:17');
INSERT INTO `user` VALUES (2, 'qwe123', '测试用户', '$2a$10$BhgN1pbRIAeGZBADcA2Ure3pVTtSEXi8pjzwItTqlixGmnIgzpe6K', 'customer', '', '2026-03-12 20:24:06', '2026-03-12 20:24:06');
INSERT INTO `user` VALUES (3, 'user123', 'user123', '$2a$10$eIDFQmg7HnzImiigiRdHouoT7hFyecdre4tEbZnK/mX0B/9AD3u9i', 'customer', NULL, '2026-03-12 21:18:59', '2026-03-12 21:18:59');
INSERT INTO `user` VALUES (4, '203139976', '203139976', '$2a$10$uqAU7A4TBlLM7JeyxICxVunMNkq3zby7McxJGhEswfWvbILQLtoCG', 'customer', NULL, '2026-03-14 21:51:22', '2026-03-14 21:51:22');
INSERT INTO `user` VALUES (5, '20010629', '20010629', '$2a$10$NQ2Fv6SvchZEfa6BuPTeI.Gz0xxPyAh2lWGL4SJu3hT0VvG7zACtW', 'customer', NULL, '2026-03-15 15:42:48', '2026-03-15 15:42:48');

SET FOREIGN_KEY_CHECKS = 1;
