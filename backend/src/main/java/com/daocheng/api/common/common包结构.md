common
├── constant
│   ├── UserRoleConstant.java        // 用户角色常量（如 "customer", "merchant"）
│   └── JwtConstant.java              // JWT 相关常量（密钥、过期时间等）
├── enums
│   └── ResultCodeEnum.java           // 统一返回码枚举（如 SUCCESS, ERROR, UNAUTHORIZED）
├── exception
│   ├── BusinessException.java        // 自定义业务异常
│   └── GlobalExceptionHandler.java   // 全局异常处理器（@ControllerAdvice）
├── utils
│   ├── JwtUtil.java                  // JWT 生成与解析工具
│   ├── BeanCopyUtil.java             // 对象属性拷贝工具（基于 Spring BeanUtils）
│   └── PasswordEncoderUtil.java       // 密码加密工具（可委托给 BCryptPasswordEncoder）
├── config
│   ├── CorsConfig.java               // 跨域配置
│   ├── MybatisPlusConfig.java         // MyBatis-Plus 配置（分页插件等）
│   └── SecurityConfig.java            // Spring Security 核心配置（将在其中配置 JWT 过滤器）
└── dto
└── Result.java                    // 统一 API 响应结果封装类（如 {code, message, data}）