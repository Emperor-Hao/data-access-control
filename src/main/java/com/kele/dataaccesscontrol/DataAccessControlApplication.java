package com.kele.dataaccesscontrol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kele.dataaccesscontrol.mapper")
public class DataAccessControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataAccessControlApplication.class, args);
    }

}
