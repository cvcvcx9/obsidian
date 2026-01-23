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
import tools.jackson.databind.json.JsonMapper; // Jackson 3.x 버전의 매퍼 패키지

@Configuration // 이 클래스가 스프링의 설정 정보임을 나타냄
public class JpaConfig {

    @Bean // Hibernate 설정을 커스터마이징하는 빈을 등록
    public HibernatePropertiesCustomizer jsonFormatMapperCustomizer(JsonMapper jsonMapper) {
        // 스프링 컨텍스트에 등록된 Jackson 3.0의 JsonMapper를 주입받음
        
        return hibernateProperties -> {
            // Hibernate의 설정 맵(Properties)에 직접 접근하여 설정을 추가
            hibernateProperties.put(AvailableSettings.JSON_FORMAT_MAPPER, new FormatMapper() {
                // 'JSON_FORMAT_MAPPER' 설정에 우리가 정의한 새로운 FormatMapper를 할당함

                @Override
                @SuppressWarnings("unchecked")
                // DB의 JSON 문자열을 자바 객체로 변환하는 메서드 (역직렬화)
                public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {
                    try {
                        // 입력 데이터가 비어있으면 null 반환
                        if (charSequence == null || charSequence.length() == 0) return null;

                        // Hibernate가 전달해준 타입 정보(javaType)에서 실제 자바의 리플렉션 Type 정보를 추출
                        java.lang.reflect.Type targetType = javaType.getJavaType();

                        // Jackson 3.0의 엔진을 사용하여 문자열을 해당 타입의 객체로 읽어들임
                        return jsonMapper.readValue(
                                charSequence.toString(),
                                jsonMapper.getTypeFactory().constructType(targetType)
                        );
                    } catch (Exception e) {
                        // 변환 실패 시 런타임 예외로 래핑하여 던짐
                        throw new RuntimeException("Jackson 3.0 역직렬화 실패: " + e.getMessage(), e);
                    }
                }

                @Override
                // 자바 객체를 DB에 저장할 JSON 문자열로 변환하는 메서드 (직렬화)
                public <T> String toString(T value, JavaType<T> javaType, WrapperOptions wrapperOptions) {
                    try {
                        // 객체가 null이면 null 반환
                        if (value == null) return null;
                        
                        // Jackson 3.0을 사용하여 객체를 JSON 문자열로 변환
                        return jsonMapper.writeValueAsString(value);
                    } catch (Exception e) {
                        // 변환 실패 시 에러 메시지와 함께 예외 발생
                        throw new RuntimeException("Jackson 3.0 직렬화 실패", e);
                    }
                }
            });
        };
    }
}
```
- 해당 코드는 Gemni