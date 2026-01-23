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
> 

## 🔍 원인
- spring boot 4.0부터 기본 Jackson 라이브러리의 위치가 기존 위치에서 변경(Jackson3.0버전으로 변경되면서)
- Hibernate의 경우 7.2버전까지는 Jackson 2 버전을 공식적으로 지원. 3.0은 지원하지 않음
- 최신 JPA 기준으로 HIbernate를 사용중이기 때문에 Jackson을 읽어오지 못하는 이슈 발생

## ✅ 해결방법
``` 여기에 해결 코드나 설정법을 적으세요.