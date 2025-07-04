# 文件外发控制

请使用 Maven 构建。JDK 版本为 1.8。

构建与运行前，请填充 `/src/main/resources/application.yml` 中的配置。包括如下内容：
- 数据库连接信息
- Jpa 策略
- `base-url`：后端实际运行的地址，VBA 会使用此地址进行 API 调用。
- `file-storage`
  - `type`：文件存储类型，目前仅支持 `local`（本地存储）。
  - `local-temp-path`：本地临时文件存储路径。
