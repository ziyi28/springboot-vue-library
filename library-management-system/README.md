# 图书管理系统

基于Spring Boot + MyBatis-Plus + MySQL的Web图书管理系统，支持用户管理、图书管理、借阅管理等功能。

## 项目结构

```
library-management-system/
├── src/main/java/com/library/
│   ├── config/                # 配置类
│   │   ├── SecurityConfig.java
│   │   └── MyBatisPlusConfig.java
│   ├── controller/            # 控制器层
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── BookController.java
│   │   ├── BookCategoryController.java
│   │   └── BorrowRecordController.java
│   ├── entity/               # 实体类
│   │   ├── User.java
│   │   ├── Admin.java
│   │   ├── Book.java
│   │   ├── BookCategory.java
│   │   ├── BookRecord.java
│   │   ├── BorrowRecord.java
│   │   └── Favorites.java
│   ├── mapper/               # MyBatis Mapper接口
│   │   ├── UserMapper.java
│   │   ├── AdminMapper.java
│   │   ├── BookMapper.java
│   │   ├── BookCategoryMapper.java
│   │   ├── BookRecordMapper.java
│   │   ├── BorrowRecordMapper.java
│   │   └── FavoritesMapper.java
│   ├── service/              # 服务层接口和实现
│   │   ├── UserService.java
│   │   ├── AdminService.java
│   │   ├── BookService.java
│   │   ├── BookCategoryService.java
│   │   ├── BorrowRecordService.java
│   │   └── impl/
│   ├── security/             # Spring Security相关
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── JwtAuthenticationFilter.java
│   ├── utils/                # 工具类
│   │   └── JwtUtils.java
│   ├── common/               # 通用类
│   │   ├── BaseEntity.java
│   │   └── Result.java
│   └── LibraryApplication.java
├── src/main/resources/
│   ├── mapper/               # MyBatis XML映射文件
│   │   ├── BookMapper.xml
│   │   └── BorrowRecordMapper.xml
│   └── application.yml
├── database/
│   └── init.sql             # 数据库初始化脚本
└── pom.xml
```

## 技术栈

- **后端框架**: Spring Boot 2.7.14
- **数据库**: MySQL 5.7/8.0
- **ORM框架**: MyBatis-Plus 3.5.3.1
- **安全框架**: Spring Security + JWT
- **其他**: Lombok, FastJSON, Validation

## 数据库设计

系统包含7张表：

1. **users** - 普通用户表
2. **admins** - 管理员表
3. **book_categories** - 图书分类表
4. **books** - 图书信息表
5. **book_records** - 图书记录表（副本）
6. **borrow_records** - 借阅记录表
7. **favorites** - 用户收藏表

## 快速开始

### 1. 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+ 或 8.0+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE `library_management_system` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库初始化脚本：
```bash
mysql -u root -p library_management_system < database/init.sql
```

3. 修改配置文件 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_management_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
    username: root
    password: 你的密码
```

### 3. 启动项目

1. 进入项目根目录：
```bash
cd library-management-system
```

2. 编译项目：
```bash
mvn clean compile
```

3. 启动项目：
```bash
mvn spring-boot:run
```

### 4. 默认账户

- **管理员账户**: admin / admin123
- **图书管理员**: librarian / admin123

## API接口

### 认证接口

- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 用户注册

### 用户管理

- `GET /api/users/list` - 获取用户列表（管理员）
- `GET /api/users/{id}` - 获取用户详情
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户（管理员）

### 图书管理

- `GET /api/books/list` - 获取图书列表
- `GET /api/books/{id}` - 获取图书详情
- `GET /api/books/search` - 搜索图书
- `POST /api/books` - 添加图书（管理员）
- `PUT /api/books/{id}` - 更新图书（管理员）
- `DELETE /api/books/{id}` - 删除图书（管理员）

### 分类管理

- `GET /api/categories/list` - 获取分类列表
- `GET /api/categories/{id}` - 获取分类详情
- `POST /api/categories` - 添加分类（管理员）
- `PUT /api/categories/{id}` - 更新分类（管理员）
- `DELETE /api/categories/{id}` - 删除分类（管理员）

### 借阅管理

- `GET /api/borrow-records/my-records` - 获取我的借阅记录
- `GET /api/borrow-records/list` - 获取所有借阅记录（管理员）
- `POST /api/borrow-records/borrow/{bookId}` - 借阅图书
- `POST /api/borrow-records/return/{recordId}` - 归还图书
- `POST /api/borrow-records/renew/{recordId}` - 续借图书

## 项目特点

1. **完整的CRUD操作** - 所有表都实现了增删改查
2. **权限控制** - 基于Spring Security和JWT的认证授权
3. **密码加密** - 使用BCrypt加密存储密码
4. **分页查询** - MyBatis-Plus分页插件支持
5. **逻辑删除** - 保护历史数据不丢失
6. **RESTful API** - 标准的REST接口设计
7. **统一响应** - 统一的Result响应格式
8. **参数校验** - 数据验证和错误处理

## Git提交规范

```bash
feat: 添加用户注册和登录功能
feat: 实现JWT认证机制
feat: 完成图书信息CRUD接口
feat: 添加图书分类管理功能
feat: 实现借阅记录管理
fix: 修复密码加密逻辑错误
fix: 优化分页查询性能
refactor: 重构响应结果统一封装
docs: 更新API文档
test: 添加用户服务单元测试
```

## 许可证

本项目仅供学习和研究使用。