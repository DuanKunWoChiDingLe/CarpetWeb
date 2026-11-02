# CarpetWeb

## 技术栈

- 前端：Vue 3 + TypeScript + Vite + Element Plus
- 后端：Spring Boot 3 + MyBatis-Plus + MySQL
- 鉴权：JWT + Spring Security

## 本地启动

### 1. 后端

后端敏感配置通过环境变量提供，示例见 `backend/.env.example`。

```powershell
cd backend
$env:DB_PASSWORD='你的数据库密码'
$env:JWT_SECRET='至少32位的随机字符串'
$env:ORDER_PAY_PASSWORD='你的支付校验密码'
$env:FILE_UPLOAD_PATH='F:/uploads'
.\mvnw.cmd spring-boot:run
```

后端地址：`http://localhost:8080`

### 2. 前端

```powershell
cd frontend
npm install
npm run dev
```

本机地址：`http://localhost:5173`

同一局域网内的手机可使用 Vite 输出的 Network 地址访问。Vite 已代理 `/api`、`/uploads` 和 `/ws` 到后端。

## 常用检查

```powershell
cd frontend
npm run type-check
npm run build-only
```

```powershell
cd backend
.\mvnw.cmd test
```

## 权限规则

- 普通用户只能访问自己的购物车、订单和个人资料。
- 商品、品牌、系列、轮播图、销量统计和订单管理接口仅允许商家角色访问。
- 普通登录用户只能上传个人头像，不能修改商品或商家数据。
- 公开注册只能创建普通用户，商家账号不能通过公开注册接口自行获得。

## Render 部署

仓库根目录提供 `render.yaml`，可在 Render Dashboard 中选择 **New → Blueprint** 并连接本仓库部署。

Blueprint 会创建：

- `carpetweb-web`：Vue 静态站点，包含 SPA 路由回退。
- `carpetweb-api`：Spring Boot Docker Web Service。
- `carpetweb-mysql`：MySQL 8 私有服务，挂载 10 GB 持久化磁盘。
- `carpetweb-api` 的上传目录挂载 5 GB 持久化磁盘。

首次创建 Blueprint 时，Render 会要求填写：

- `ORDER_PAY_PASSWORD`：请输入 6 位数字支付密码，需与前端支付弹窗格式一致。

Render 会自动生成并连接以下密钥：

- MySQL 用户密码和 root 密码
- JWT 签名密钥

部署完成后：

1. 打开 `carpetweb-web` 的 `onrender.com` 地址访问前端。
2. 数据库首次启动时会自动导入 `daocheng_dp.sql`。
3. `backend/seed-uploads` 中的图片会在首次启动时复制到上传磁盘。
4. 如绑定自定义域名，请修改 `carpetweb-api` 的 `CORS_ALLOWED_ORIGIN_PATTERNS`，加入前端域名，例如：
   `https://www.example.com,https://*.onrender.com`

注意：

- Render 私有服务和持久化磁盘需要付费实例。
- Render 没有托管 MySQL，因此这里使用官方 MySQL 8 Docker 镜像作为私有服务。
- 生产环境建议使用 `mysqldump` 做数据库备份，不要仅依赖磁盘快照。
- 当前 Blueprint 默认使用新加坡区域和 `0.5c-512mb` 实例，可按流量和预算在 Render 控制台升级。


