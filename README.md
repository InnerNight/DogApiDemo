## Dog Breed Quiz App

一个基于Android的狗狗品种识别游戏应用，帮助用户学习和记忆不同的狗狗品种。

## 技术栈

### 核心技术
- **Kotlin** - 主要编程语言
- **Jetpack Compose** - 现代化UI框架
- **MVVM架构** - 清晰的架构模式，无依赖注入
- **Ktor** - 网络请求库
- **Coil** - 图片加载库
- **Kotlinx Serialization** - JSON序列化

### 项目结构
```
app/src/main/java/com/liye/dogapidemo/
├── data/
│   ├── model/          # 数据模型
│   │   └── DogImage.kt
│   ├── network/        # 网络层
│   │   └── DogApiService.kt
│   └── repository/     # 数据仓库层
│       └── DogRepository.kt
├── presentation/
│   ├── ui/            # UI组件
│   │   └── DogQuizScreen.kt
│   └── viewmodel/     # ViewModel层
│       └── DogQuizViewModel.kt
└── MainActivity.kt
```

## 功能特性

### 🎮 游戏机制
- 显示狗狗图片，用户选择正确品种
- 每轮游戏10道题目
- 实时计分和进度显示
- 答对后给予积极反馈

### 🎨 UI/UX设计
- **Material Design 3** - 现代化设计风格
- **动画效果** - 流畅的过渡和反馈动画
- **响应式布局** - 适配不同屏幕尺寸
- **直观交互** - 清晰的视觉提示和状态反馈

### 🏗️ 架构特点
- **MVVM架构** - 清晰的职责分离
- **Repository模式** - 统一的数据访问接口
- **状态管理** - 基于Flow的响应式状态管理
- **错误处理** - 完善的错误处理机制

## API集成

使用 [Dog CEO API](https://dog.ceo/dog-api/) 获取狗狗品种数据：
- `/breeds/list/all` - 获取所有品种列表
- `/breed/{breed}/images/random` - 获取指定品种的随机图片
- `/breed/{breed}/{sub-breed}/images/random` - 获取子品种图片

## 主要组件

### 1. 数据模型 (DogImage.kt)
```kotlin
data class DogBreed(
    val name: String,
    val subBreeds: List<String> = emptyList()
)

data class QuizQuestion(
    val imageUrl: String,
    val correctBreed: DogBreed,
    val options: List<DogBreed>,
    val correctAnswer: String
)
```

### 2. 网络服务 (DogApiService.kt)
- 基于Ktor的HTTP客户端
- 支持内容协商和日志记录
- 异步API调用

### 3. 数据仓库 (DogRepository.kt)
- 品种数据缓存
- 题目生成逻辑
- 错误处理封装

### 4. ViewModel (DogQuizViewModel.kt)
- 游戏状态管理
- 业务逻辑处理
- UI状态更新

### 5. UI组件 (DogQuizScreen.kt)
- 组合式UI构建
- 状态驱动的界面更新
- 丰富的交互反馈

## 测试策略

### 单元测试
- ViewModel逻辑测试
- Repository数据处理测试
- 模型类方法测试

### 测试工具
- JUnit 4 - 基础测试框架
- Mockito - Mock对象创建
- Kotlinx Coroutines Test - 协程测试

## 运行要求

- Android API 24+ (Android 7.0)
- 网络权限 (INTERNET)
- 编译目标 API 36

## 设计原则

### 1. 代码质量
- **可读性** - 清晰的命名和结构
- **可维护性** - 模块化设计
- **可测试性** - 依赖注入和Mock支持

### 2. 用户体验
- **响应性** - 快速的加载和反馈
- **直观性** - 简单易懂的界面
- **愉悦性** - 有趣的游戏体验

### 3. 架构设计
- **单一职责** - 每个类专注一个功能
- **开闭原则** - 易于扩展新功能
- **依赖倒置** - 面向接口编程

## 技术亮点

1. **现代化技术栈** - 使用最新的Android开发技术
2. **声明式UI** - Jetpack Compose提供的现代UI开发方式
3. **响应式编程** - 基于Flow的状态管理
4. **网络优化** - Ktor的高性能网络处理
5. **图片优化** - Coil的高效图片加载和缓存

## 构建和运行

1. 克隆项目到本地
2. 在Android Studio中打开项目
3. 等待Gradle同步完成
4. 运行应用到设备或模拟器

## 项目完成情况

✅ **架构设计** - 完整的MVVM架构实现
✅ **数据层** - API服务、Repository模式
✅ **表现层** - ViewModel状态管理
✅ **UI层** - Jetpack Compose现代UI
✅ **网络集成** - Ktor + Dog CEO API
✅ **图片加载** - Coil异步图片加载
✅ **错误处理** - 完善的异常处理机制
✅ **测试覆盖** - 单元测试和UI测试
✅ **用户体验** - 动画、反馈、进度显示
