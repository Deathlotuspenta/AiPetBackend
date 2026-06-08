# 1. Base environment / 基础环境
FROM openjdk:17-jdk-slim

# 2. Set working folder / 设置工作文件夹
WORKDIR /app

# 3. Copy the jar file / 复制 jar 文件
# This copies any jar in the target folder and renames it to app.jar
# 这会复制 target 文件夹中的任何 jar 包，并将其重命名为 app.jar
COPY target/*.jar app.jar

# 4. Open the port / 打开端口
EXPOSE 80

# 5. Start the server / 启动服务器
ENTRYPOINT ["java", "-jar", "app.jar"]