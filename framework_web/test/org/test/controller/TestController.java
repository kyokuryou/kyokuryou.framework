package org.test.controller;

import org.smarty.web.http.BaseServlet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/test/*")
public class TestController extends BaseServlet {

    @RequestMapping(value = "/login")
    public String login() {
        request.setAttribute("userName", "qul");
        request.setAttribute("password", "quliang");
        return "";
    }

    @RequestMapping(value = "/test")
    public String test() {
        System.out.println("test()");
        return "test.ftl";
    }
}
