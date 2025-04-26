# Web-spring-vue 项目

一个基于Spring Boot后端和Vue.js前端的全栈Web应用程序。该项目包含用户认证、文章管理、关注/订阅功能等模块。

## 项目结构

```
Web-spring-vue/
├── exp2_back/         # Spring Boot 后端
│   ├── src/           # 源代码
│   ├── build.gradle   # Gradle构建配置
│   └── ...
├── exp2_front/        # Vue.js 前端
│   ├── src/           # 源代码
│   ├── public/        # 静态资源
│   └── ...
└── README.md          # 项目说明文档
```

## 技术栈

### 后端
- Java 8+
- Spring Boot 2.x
- Spring Security
- MyBatis
- H2数据库 (可配置为其他如MySQL等)
- Gradle构建工具

### 前端
- Vue 3
- Element Plus UI组件库
- Vue Router
- Axios
- V-MD-Editor (Markdown编辑器)

## 功能特性

- 用户注册与登录
- 个人信息管理
- 文章创建、编辑、删除
- Markdown支持
- 用户关注/订阅系统
- 文章动态流
- 主题切换功能

## 开发环境设置

### 后端

1. 克隆项目
```
git clone <repository-url>
cd Web-spring-vue/exp2_back
```

2. 构建项目
```
./gradlew build
```

3. 运行后端
```
./gradlew bootRun
```
后端将在 http://localhost:8081 启动

### 前端

1. 进入前端目录
```
cd ../exp2_front
```

2. 安装依赖
```
npm install
```

3. 运行开发服务器
```
npm run serve
```
前端将在 http://localhost:8080 启动

## 部署

### 后端部署
1. 构建生产版本
```
./gradlew build
```

2. 运行JAR文件
```
java -jar build/libs/exp2-0.0.1-SNAPSHOT.jar
```

### 前端部署
1. 构建生产版本
```
npm run build
```

2. 部署`dist`目录中的文件到Web服务器

## 接口文档

### 用户API
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/{username}` - 获取用户信息
- `PUT /api/user/{username}` - 更新用户信息

### 文章API
- `GET /api/articles` - 获取文章列表
- `POST /api/articles` - 创建文章
- `GET /api/articles/{id}` - 获取文章详情
- `PUT /api/articles/{id}` - 更新文章
- `DELETE /api/articles/{id}` - 删除文章
- `GET /api/articles/feed/{username}` - 获取关注用户的文章

### 订阅API
- `POST /api/subscriptions/follow` - 关注用户
- `DELETE /api/subscriptions/unfollow` - 取消关注用户
- `GET /api/subscriptions/my/{username}` - 获取用户的关注列表
- `GET /api/subscriptions/check` - 检查是否已关注
- `GET /api/subscriptions/search` - 搜索用户

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull请求

## 许可证

此项目采用 MIT 许可证 - 详见 LICENSE 文件