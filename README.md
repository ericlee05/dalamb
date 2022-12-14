# dalamb
> Java로 작성된 FaaS 엔진

## 🐿️ dalamb이란?
dalamb(다람)은 Java로 FaaS를 손쉽게 구축하도록 도와주는 엔진 소프트웨어입니다.

본 엔진은 `서비스(Service)`, `주제(Topic)`, `액션(Action)` 구성요소로 나뉘게 됩니다.
 * **Service:** 
   * -> 모놀리식에서 Project 역할
   * jar 형태로 배포
 * **Topic:** 특정 주제와 관련된 비즈니스 로직들의 모임 (ex. 사용자 Topic, 거래 Topic)
   * -> 모놀리식에서 Service Class 역할
   * Class 형태
 * **Action:** 비즈니스 로직 (ex. 사용자 조회하기 Action, 거래내역 추가 Action)
   * -> 모놀리식에서 하나의 함수 역할

## 🔎 예제
우선, 첫번째 FaaS 서비스를 만들기 위해 Topic과 Action을 구현해야 합니다.
```java
// HelloWorld.java

package com.example.dalamb;

@Topic("helloWorld") // 주제명 정의
public class HelloWorld {
   @Action("hello") // 액션명 정의
   public String hello(@QueryParam("name") String name) {
      return String.format("To.%s : Hello, World!", request.getName());
   }
}
```
`HelloWorld.java`를 작성한 뒤, Maven이나 Gradle과 같은 빌드 도구를 이용하여 jar 파일을 생성해 줍니다. jar 파일을 생성하여 서비스를 배포할 준비를 끝냈다면, 이제 yaml을 작성할 차례입니다.
```yaml
# config.yaml

services:
   - package: com.example.dalamb
     name: example
     jar: # jar 파일의 위치

bindings:
   - path: /
     methods: [ GET ]
     action: example.helloWorld.hello # 형식: <서비스명>.<주제명>.<액션명>
```
작성이 끝나면, 아래 명령어를 입력하여 dalamb을 실행합니다.
```shell
java -jar dalamb.jar config.yml 8080
```

이후, 웹 브라우저에서 `http://localhost:8080?name=fox`에 접속하면 `To.Fox: Hello, World!`라는 문자를 확인할 수 있습니다.

## ⚙️ 기술
### 작동 원리
1. dalamb은 구동 시점에 `설정 파일(yaml)`을 읽어들입니다.
2. `JarClassLoader`를 이용하여 설정 파일에 정의된 `Service`들을 메모리에 업로드합니다.
   * 리플렉션 API에서 제공되는 `Class` 타입으로 메모리 상에 존재합니다.
3. 설정 파일에서 정의된 HTTP 위치(`bindings` 항목)으로 요청이 들어올 경우, handler는 정의된 `Action`을 찾아 수행합니다.
   * `(2)`번 과정을 통해 메모리 상에 업로드된 Class들에게서 Action 정보(`Method` 타입 객체)를 찾게 됩니다.
   * Action에 대한 정보는 Map 자료구조에 캐시됩니다.
   * 메모리 낭비를 막기 위해, 실행 시점이 되어서야 Action을 찾도록 구현하였습니다.
   * 이 과정에서, 클래스의 인스턴스화 또한 진행됩니다. (`Class` 또한 캐시 적용)
4. Jackson을 통해 실행 결과를 json으로 변환하여 Client에게 반환합니다.

### 최적화 기술
* **클래스 레벨 지연로딩 (TODO)**
  * 현재의 로딩 방식은 클래스는 즉시 로딩, 액션은 지연 로딩을 사용합니다.
  * 액션의 지연 로딩은 메모리 절약에 큰 영향을 끼치지 못한다는 단점이 존재합니다.
  * 따라서, 추후 클래스 단에서의 지연로딩을 추가로 적용할 예정입니다.
  * 페이징 기법도 추가로 지원할 예정입니다.

# 📜 Service API
> Service 작성 시 사용할 수 있는 API의 목록입니다.
## Annotations
### [For Class] `@Topic(String name)`
해당 클래스를 Topic 클래스로 정의합니다.
 * `name`: Topic을 식별할 수 있는 이름

### [For Method] `@Action(String name)`
해당 메서드를 Action으로 정의합니다.
 * `name`: Action을 식별할 수 있는 이름

### [For Param] `@PathVariable(String name)`
시멘틱 URL의 값을 가지고 옵니다.
 * `name`: 시멘틱 URL의 식별자

### [For Param] `@QueryParameter(String name)`
쿼리스트링의 값을 가지고 옵니다.
* `name`: 쿼리스트링의 식별자
* 값이 없는 쿼리스트링 경우에는 공백 문자열이 반환됩니다.

### [For Param] `@Body`
HTTP 요청의 바디를 가지고 옵니다. `application/json` 형태의 요청만 해석 가능합니다.

## Storage (TODO)
> 추후 구현 예정

## 🗺️ dalamb을 사용하는 곳
* **CNS (대구소프트웨어마이스터고 동아리)**
  * 신입생 입학전형시스템의 일부 서비스를 dalamb을 통해 개발하고 있습니다.