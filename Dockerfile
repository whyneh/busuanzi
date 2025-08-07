FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="yww@yww52.com"

# 设置时区
ENV TZ=Asia/Shanghai
RUN apk add tzdata \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone \
    && apk del tzdata \
    && rm -rf /var/cache/apk/*

# 定义应用相关环境变量
ENV JAVA_OPTS="-Xms512m -Xmx2048m -Xmn614m -Xss256k -XX:SurvivorRatio=6 -XX:+UseG1GC -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"
ENV APP_HOME=/app

# 创建非root用户并设置权限
RUN adduser -D appuser && \
    mkdir -p $APP_HOME && \
    chown -R appuser:appuser $APP_HOME
USER appuser

# 设置工作目录和应用目录
WORKDIR $APP_HOME

# 复制已打包的 JAR 文件
COPY busuanzi-1.0.jar $APP_HOME/app.jar

# 暴露应用端口
EXPOSE 10010

# 启动命令（使用环境变量配置 JVM 参数）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]