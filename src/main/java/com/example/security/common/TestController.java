package com.example.security.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dkansk924@naver.com
 * @since 2021/08/07
 */

@RestController
public class TestController {

    //테스트 대상
    //HttpSession 을 설정하였을때 사용자에게 넘겨지는 Cookie(JSESSIONID) 는 언제 만들어지는가?
    @GetMapping("/session")
    public String setSession(HttpSession httpSession){
        httpSession.setAttribute("user","kjj");
        return "ok";
    }


    // Cookie 의 age 값을 설정하지않고 사용하게되면 브라우저가 종료될때까지만 유지하게된다.
    // Session 으로 생성되는 Cookie 같은경우가 그렇다.
    // 또한 HttpOnly 속성은 ture 로 설정된다.
    @GetMapping("/cookieAge")
    public String checkCookieLifeCycle(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("testCookie","TestValue");
        cookie.setMaxAge(60*30); // 단위는 seconds 임
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return "ok";
    }
}
