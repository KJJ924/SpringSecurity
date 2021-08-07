package com.example.security.common;

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
}
