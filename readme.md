# 开发文档

## 1. 项目结构

项目采用前后端分离的架构，后端使用 Spring Boot，前端使用 Vue.js。

```
protein/
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── FileUpload.vue
│   │   │   ├── DataTable.vue
│   │   │   └── SearchBar.vue
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vue.config.js
│
backend/
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/
    │       │       └── protein/
    │       │           ├── controller/
    │       │           │   └── SequenceController.java
    │       │           ├── data/
    │       │           │   ├── Sequence.java
    │       │           │   └── SequenceRepository.java
    │       │           ├── parser/
    │       │           │   └── SequenceParser.java
    │       │           └── ProteinSequenceManagementApplication.java
    │       └── resources/
    │           └── application.properties
    ├── pom.xml
    └── sequences.db
```

## 2. 已实现功能

这是一个 Java 课设，实现了以下目标：

1. 将用户提供的文件解析后存进数据库

   - 将用户提供的文件解析后存进数据库
   - 批量处理数个包含复数以上文件解析后存进数据库

2. 按页浏览数据库收录的序列信息

   - 每一页以表格的形式输出
   - 使用 SQLite 保存数据

3. 按关键字检索数据库收录的序列

   - 检索结果可以按页进行浏览
   - 检索结果可以保存为文件（txt 或 tsv）

4. 借助 vue 建立了一个前端操作界面

## 3. 编译和运行步骤

### 3.1 后端

#### 编译

1. 确保您的系统中安装了 Java 11 和 Maven。

2. 打开命令行，进入项目的 backend 目录：

   ```
   cd path/to/protein-sequence-management/backend
   ```

3. 执行 Maven 命令编译项目：

   ```
   mvn clean package
   ```

4. 编译成功后，在`target`目录下会生成一个 JAR 文件，名为`protein-sequence-management-0.0.1-SNAPSHOT.jar`。

#### 运行

1. 确保您已经完成了后端的编译步骤。

2. 打开命令行，进入 backend 目录下的 target 文件夹：

   ```
   cd path/to/protein-sequence-management/backend/target
   ```

3. 运行 JAR 文件：

   ```
   java -jar protein-sequence-management-0.0.1-SNAPSHOT.jar
   ```

4. 后端将在默认端口 8080 上启动。

### 3.2 前端

#### 编译和运行

1. 确保您的系统中安装了 Node.js 和 npm。

2. 打开命令行，进入项目的 frontend 目录：

   ```
   cd path/to/protein-sequence-management/frontend
   ```

3. 安装依赖：

   ```
   npm install
   ```

4. 启动前端开发服务器：

   ```
   npm run serve
   ```

5. 前端开发服务器将启动。由于 http://localhost:8080 被后端占用，前端通常在 http://localhost:8081 上运行（如果该端口被占用，可能会使用其他端口）。

## 4. 测试已实现功能的步骤

1. 确保后端已经启动并在运行。

2. 打开浏览器，访问 `http://localhost:8081`。

3. 测试文件上传功能：

   - 点击"选择文件"按钮，选择一个包含蛋白质序列的文件（如 FASTA 格式）。
   - 点击"上传"按钮，观察是否成功上传并显示在数据表中。

4. 测试搜索功能：

   - 在搜索栏中输入蛋白质的相关信息。
   - 点击搜索按钮，观察数据表是否显示匹配的结果。

5. 测试数据表显示：

   - 检查数据表是否正确显示蛋白质序列的信息。

6. 测试分页功能：

   - 尝试切换不同的页面，检查是否正确加载数据。

7. 测试导出功能：
   - 尝试在搜索栏中输入蛋白质序列的部分序列，再导出数据。

## 5. 使用的额外依赖包

除了 Spring Boot 的标准库外，本项目还使用了以下额外的依赖：

1. SQLite JDBC Driver
2. SQLite Dialect
3. Hibernate Community Dialects

这些依赖已经在 pom.xml 文件中配置，无需额外安装。

## 6. 注意事项

- 确保在运行应用程序之前，SQLite 数据库文件 (sequences.db) 已经存在于正确的位置。
- 如果遇到端口冲突，可以在`application.properties`文件中修改`server.port`属性来更改后端服务的端口。
- 前端开发服务器默认运行在 8081 端口，如果与后端端口冲突，可以在`vue.config.js`中配置不同的端口。
