---
날짜: 2026-01-23 10:59
tags:
  - error
  - hibernate
  - jackson
환경: hibernate7.2, JPA, Jackson3.0, springboot4.0
status: 해결
---

## 🛑 에러
> 스프링부트 4.0에서 JPA와 카프카가 들어간 프로젝트 진행 중
> Jsonb 타입의 엔티티를 생성하고 처리하려고 할 때 발생한 문제
> 분명 Jackson과 같은 경우 starter(jpa)쪽에 들어있는데 지속적으로 라이브러리가 존재하지 않는다고 뜸

#### 문제1: 엔티티 생성시 타입없음
- postgresql을 사용중인데 추후 추가될 수 있는 옵션이 있어, jsonb로 일시적 생성중, jackson이 없다는 오류
#### 문제2: kafka에서 시리얼라이져 타입없음오류
- kafka에서 요청을 받아 consume을 할 때, 시리얼라이저의 위치를 지정했음에도, 클래스 없음 오류 지속적으로 발생


## 🔍 원인
- spring boot 4.0부터 기본 Jackson 라이브러리의 위치가 기존 위치에서 변경(Jackson3.0버전으로 변경되면서)
- `Hibernate`의 경우 7.2버전까지는 `Jackson 2` 버전을 공식적으로 지원. 3.0은 지원하지 않음
- 최신 JPA 기준으로 `HIbernate`를 사용중이기 때문에 자동설정(`Auto configration`) Jackson을 읽어오지 못하는 이슈 발생
- 또한, `Jackson2`에서 3으로 변경되므로 시리얼라이저 위치도 3.0버전에 맞게 변경이 필요했음

## ✅ 해결방법

#### 문제 1: `HibernatePropertiesCustomizer`를 이용한 매퍼 수동등록
```java
package com.cvcvcx9.baseline.global.config;  
  
import org.hibernate.cfg.AvailableSettings;  
import org.hibernate.type.descriptor.WrapperOptions;  
import org.hibernate.type.descriptor.java.JavaType;  
import org.hibernate.type.format.FormatMapper;  
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
import tools.jackson.databind.json.JsonMapper;  
  
@Configuration  
public class JpaConfig {  
    @Bean  
    public HibernatePropertiesCustomizer jsonFormatMapperCustomizer(JsonMapper jsonMapper) {  
        return hibernateProperties -> {  
            hibernateProperties.put(AvailableSettings.JSON_FORMAT_MAPPER, new FormatMapper() {  
  
                @Override  
                @SuppressWarnings("unchecked")  
                public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {  
                    try {  
                        if (charSequence == null || charSequence.length() == 0) return null;  
  
                        // 핵심 수정: (Class<T>) 캐스팅을 제거하고 java.lang.reflect.Type을 그대로 사용합니다.  
                        // Jackson 3.0의 JsonMapper는 Type 객체를 직접 인식할 수 있습니다.                        java.lang.reflect.Type targetType = javaType.getJavaType();  
  
                        return jsonMapper.readValue(  
                                charSequence.toString(),  
                                jsonMapper.getTypeFactory().constructType(targetType)  
                        );  
                    } catch (Exception e) {  
                        throw new RuntimeException("Jackson 3.0 역직렬화 실패: " + e.getMessage(), e);  
                    }  
                }  
  
                @Override  
                public <T> String toString(T value, JavaType<T> javaType, WrapperOptions wrapperOptions) {  
                    try {  
                        if (value == null) return null;  
                        return jsonMapper.writeValueAsString(value);  
                    } catch (Exception e) {  
                        throw new RuntimeException("Jackson 3.0 직렬화 실패", e);  
                    }  
                }  
            });  
        };  
    }  
}
```
- 해당 코드의 경우 