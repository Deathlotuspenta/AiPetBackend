# Stage 1: Build the code / 第一阶段：构建代码
FROM maven:3.8.5-openjdk-17 AS builder

# Set working folder / 设置工作文件夹
WORKDIR /app

# Copy all your code / 复制你所有的代码
COPY . .

# Run Maven to build the jar / 运行 Maven 打包 jar
RUN mvn clean package -DskipTests

# Stage 2: Run the code / 第二阶段：运行代码
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar from Stage 1 / 从第一阶段复制 jar 包
COPY --from=builder /app/target/*.jar app.jar

# Open the port / 打开端口
EXPOSE 8080

# Start the server / 启动服务器
ENTRYPOINT ["java", "-jar", "app.jar"]