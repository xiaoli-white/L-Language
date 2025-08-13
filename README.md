# L-Lang

L-Lang 是编程语言 L 的工具链项目，包含编译器、虚拟机、IR 生成器等组件。

## 项目概述

L 语言是一种现代化的编程语言，该项目提供了一整套工具链来支持 L 语言的开发、编译和执行。整个工具链由多个模块组成，每个模块负责不同的功能。

## 模块介绍

### lc (L Compiler)
L 语言的编译器模块，负责将 L 语言源代码编译为中间表示(IR)。

主要功能：
- 词法分析
- 语法分析
- 抽象语法树(AST)构建
- 语义分析
- IR 生成

### lg (L Generator)
代码生成模块，负责将中间表示(IR)转换为目标代码。

支持的目标平台：
- LVM (L Virtual Machine)
- LLVM IR

### lvm (L Virtual Machine)
L 语言虚拟机，用于执行生成的字节码。

特点：
- 基于寄存器的虚拟机架构
- 支持多线程执行
- 内存管理机制

### llvm-ir-generator
LLVM IR 生成器，将 L 语言的 IR 转换为 LLVM IR。

### lvm-bytecode-generator
LVM 字节码生成器，将 L 语言的 IR 转换为 LVM 可执行的字节码。

### lpm (L Package Manager)
L 语言包管理器，用于管理项目依赖和插件。

功能：
- 安装/卸载包
- 包列表管理
- 插件系统支持

### lutil (L Utilities)
工具模块，提供通用工具类和基础功能。

## 技术架构

### 核心组件交互
```
Source Code (.l)
     ↓
    lc (编译器)
     ↓
 Intermediate Representation (IR)
     ↓
    lg (代码生成器)
     ↓
Target Code (LLVM IR / LVM Bytecode)
     ↓
  执行环境 (LLVM / LVM)
```

### IR 设计
IR (Intermediate Representation) 是编译器前端和后端之间的接口层，包含：
- 模块定义 (IRModule)
- 类型系统 (IRType)
- 函数定义 (IRFunction)
- 指令集 (IRInstruction)
- 操作数 (IROperand)

### 插件系统
项目采用插件化架构，支持通过插件扩展功能：
- 代码生成插件 (lg-plugin)
- 优化插件 (优化 passes)
- 语言扩展插件

## 构建和运行

### 环境要求
- JDK 11 或更高版本
- Gradle 7.x+

### 构建项目
```bash
./gradlew build
./gradlew shadowJar
```

### 运行编译器
```bash
# 编译 L 源代码
java -jar lc/build/libs/lc.jar source.l
```

### 运行虚拟机
```bash
# 执行 LVM 字节码
java -jar lvm/build/libs/lvm.jar program.lvme
```

### 包管理
```bash
# 安装本地包
java -jar lpm/build/libs/lpm.jar install --local package.zip

# 列出已安装的包
java -jar lpm/build/libs/lpm.jar list
```

## 许可证

本项目采用 MIT 许可证，详情请见 [LICENSE](LICENSE) 文件。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。