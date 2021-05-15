# SpringSecurity Test 코드 작성하기

---

## 이번글에서 소개하고자 하는 것은?

이번 글에서는 컨트롤러에서 @AuthenticationPrincipal 을 사용하여 Principal 객체를 바인딩 받아 사용하는 경우를

테스트 코드를 작성하기위한 글입니다.

(테스트를 작성하는 방법은 여러가지 있음으로 해당 글이 무조건 옳은 방법의 테스트라고 주장하는 글이 아닙니다.)



## 서론

컨트롤러 예제는 다음과 같습니다.

```java
@GetMapping("/hi")
public ResponseEntity<Object> hi(@AuthenticationPrincipal AuthUser user) {
    return ResponseEntity.ok("Hi !" +user.getEmail());
}
```

해당 컨트롤러를 테스트하기위해서는 AuthUser가 SercurityContextHolder 에 담겨 있어야 하는데요.



물론 테스트 전에 다음 예제 처럼 셋팅 하면 됩니다.

```java
@BeforeEach
void setAuthUser() {
    //given
    LocalDateTime time = LocalDateTime.now();
    JwtUser jwtUser = JwtUser.of("kjj", "dkansk924@naver.com", time, time.plusHours(1));
    JwtAuthToken authToken = new JwtAuthToken(jwtUser);
    SecurityContextHolder.getContext().setAuthentication(authToken);
}
```

하지만 다음과 같은경우에는 모든 테스트 전에 AuthUser 를 셋팅함으로 별로 좋지않은 테스트 입니다.
(물론 @Nested 를 이용하여 중첩된 구조의 테스트를 만들면 됩니다.)

## 그럼 어떻게 테스트를 짜야 하는가?

시큐리티는 테스트를 작성을 도와주기위해 여러가지 애노테이션을 지원 합니다. 

3가지 애노테이션중 이번 본문에서는 [공식문서](https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-method.html)에서 소개하는  <u>3가지 방법 중 한 가지만 알아볼 것입니다.</u> 



## @WithSecurityContext

해당 방식은 커스텀 애노테이션을 만들어서 Authentication 객체를 바인딩하는 과정을 가지는데요.

이번 글에서는 왜 해당 방법을 사용하는가? 라는 질문에는 서론에서 살펴본 것처럼 컨트롤러에서 바인딩 받고있는 

Authentication 이 제가 만든 Authentication(AuthUser) 타입이기 때문에 해당 방법을 선택하게 되었습니다.



그래서 <u>최종적으로 다음과 같은 테스트코드를 작성하는 것이 목표입니다</u>.

```java
@Test
@WithAuthUser(email = "dkansk924@naver.com",role = "ROLE_USER") // 여기가 핵심이랍니다~
void exam() throws Exception {
    MvcResult resultActions = mockMvc.perform(get("/hi"))
        .andDo(print())
        .andExpect(status().isOk()).andReturn();
  
    MockHttpServletResponse response = resultActions.getResponse();
    String content = response.getContentAsString();
  
    Assertions.assertThat(content.contains("dkansk924@naver.com")).isTrue();
}
```

그럼  @WithAuthUser를 만들기위해서 어떻게 해야하는지 하나씩 알아보죠.



## 과정

먼저 @WithSecurityContext 을 적용하기위해서는 애노테이션을 만들어야 하는데요  그 이유는

![image-20210515191655908](https://tva1.sinaimg.cn/large/008i3skNgy1gqjaj1lfwrj30i80593z2.jpg)

해당 인터페이스를 구현해야하는데 제네릭 바운디드 타입이 Annotation 이기 때문입니다!



따라서 다음과 같이 커스텀 애노테이션을 만들어주시면 됩니다.

```java
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAuthUserSecurityContextFactory.class)
public @interface WithAuthUser {
    String email();
    String role();
}
```

해당 애노테이션은 런타임까지 유지해야하기 때문에 **<u>Retention 은 RetentionPolicy.RUNTIME 을 주셔야 합니다.</u>**

애노테이션이 들고있는 값은  각각의 서비스마다 Authentication 객체를 생성함에 있어 필요한 속성을 채워주시면 됩니다.

마지막으로  앞서 소개한 인터페이스를 구현하고 있는 클래스를 명시해주면 됩니다.



이제 WithSecurityContextFactory 를 구현하고 있는 클래스를 만들어보죠.

```java
public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {
  
    @Override
    public SecurityContext createSecurityContext(WithAuthUser annotation) {
        String email = annotation.id();
        String role = annotation.role();

        AuthUser authUser = new AuthUser("testUserName", email);
        UsernamePasswordAuthenticationToken token = 
          new UsernamePasswordAuthenticationToken(authUser, "password", List.of(new SimpleGrantedAuthority(role)));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
```

복잡해 보이지만 매우 간단한 로직입니다. 

각각의 서비스에서 사용하고 있는 인증객체를 만들어서 (저는 AuthUser 라는 타입을 사용하고 있습니다.)  

SecurityContext에 셋팅해주면 끝납니다. 



코드의 부연설명을 더하자면

SecurityContext 에 Authentication 을  set 하기위해선 Authentication 타입이 필요하기때문에 

스프링 시큐리티가 가지고있는 UsernamePasswordAuthenticationToken 을 이용하여 어댑터 역할로 사용했습니다.

따라서 각자의 서비스에  어댑터 역할을하는 클래스가 있다면 해당하는 클래스를 사용하시면 됩니다.



이제 테스트에 인증 객체가 필요한 경우 이번 글에서 만든

```java
@WithAuthUser(email = "email", role = "role")
```

애노테이션을 테스트위에 붙여주시면 됩니다.



## 결론

테스트 작성에는 사람마다 여러가지 방법이 있습니다.

따라서 테스트에서 나타내고자하는 목적을 가장 뚜렷하게 표현할 수 있는 방법을 찾아서 선택하는 것이 중요합니다.

해당 글은 수많은 방법중 하나이며 공식문서를 참고하여 작성했습니다.

본문에 잘못된 내용이나 지적할만한 부분이 존재한다면 댓글에 남겨주시면 반영 하도록 하겠습니다.

긴글 읽어주셔서 감사합니다. 

