package com.example.security.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * @author dkansk924@naver.com
 * @since 2021/05/14
 */

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithAuthUser(id = "dkansk924@naver.com",role = "ROLE_USER")
    void account() throws Exception {

        //given
        MvcResult resultActions = mockMvc.perform(get("/hi"))
            .andDo(print())
            .andExpect(status().isOk()).andReturn();
        //when
        MockHttpServletResponse response = resultActions.getResponse();
        String content = response.getContentAsString();
        //then
        Assertions.assertThat(content.contains("dkansk924@naver.com")).isTrue();
    }
}