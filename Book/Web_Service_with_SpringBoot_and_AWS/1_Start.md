## https://jojoldu.tistory.com/539?category=717427

> ***완료된 나의 세팅***
```gradle
plugins {
    id 'org.springframework.boot' version '2.4.1'
    id 'io.spring.dependency-management' version '1.0.10.RELEASE'
    id 'java'
}

group 'com.jojoldu.book'
version '1.0.4-SNAPSHOT-'+new Date().format("yyyyMMddHHmmss")
sourceCompatibility = 1.8

repositories {
    mavenCentral()
    jcenter()
}

test {
    useJUnitPlatform()
}

dependencies {
    implementation('org.springframework.boot:spring-boot-starter-web')
    implementation('org.springframework.boot:spring-boot-starter-mustache')

    implementation('org.projectlombok:lombok')
    annotationProcessor('org.projectlombok:lombok')
    testImplementation('org.projectlombok:lombok')
    testAnnotationProcessor('org.projectlombok:lombok')

    implementation('org.springframework.boot:spring-boot-starter-data-jpa')
    implementation("org.mariadb.jdbc:mariadb-java-client")
    implementation('com.h2database:h2')

    testImplementation('org.springframework.boot:spring-boot-starter-test')
}
```

## # mavenCentral과 jcenter
둘 다 **의존성(라이브러리)를 받을 원격 저장소**인데  
mavenCentral이 많이 사용되었지만 라이브러리 업로드가 쉽지않아서  
최근에 나온 jcenter를 사용한다고 한다.  

---
책이 몇년지났다보니 버전이 바뀌면서 설정도 바뀌었다
위 저자 블로그를 참조해서 설정했다.