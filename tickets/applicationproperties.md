server.port: 8080

spring.application.name=tickets
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3300/tickets?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=*Your password*

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=*Your email*
spring.mail.password=*Your email password*
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

logging.group.tomcat=org.apache.catalina, org.apache.coyote, org.apache.tomcat, o.s.b.w.embedded.tomcat.TomcatWebServer

logging.level.root=WARN
logging.level.org.springframework=ERROR
logging.level.com.nrs=INFO
logging.level.tomcat=INFO

logging.file.name="app.log"
logging.file.path=.

logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} %p  [%t] %m%n

logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
