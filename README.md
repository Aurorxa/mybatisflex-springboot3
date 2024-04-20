# 运行 TestContainer 来进行 Mock 测试

> Testcontainers 是一个开源框架，用于提供数据库、消息代理、Web 浏览器或任何可以在 Docker 容器中运行的东西的一次性轻量级实例。
>
> 以代码形式测试依赖关系，不再需要模拟或复杂的环境配置。将测试依赖项定义为代码，然后只需运行测试，容器就会被创建然后删除。
>
> Docker 支持多种语言和测试框架，我们所需要的只是 Docker。
>
> Testcontainers 主要被用于 testing time（单元测试、CI 集成测试等），在测试时使用容器来创建一个轻量级的中间件实例（CI 集成测试环境一般无法连 Real DB）
>
> estcontainers 也可以赋能 development time，用于为服务注入一些“不方便在本地启动的中间件依赖”，而 Spring Boot 已提供了这样的[支持](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testcontainers)
> 
> 


> 注意：本项目中，支持单元测试和本地开发测试（重启容器，并不会丢失数据）