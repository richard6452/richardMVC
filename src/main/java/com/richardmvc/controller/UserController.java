package com.richardmvc.controller;

import com.richardmvc.annotations.MyGetMapping;
import com.richardmvc.entity.User;
import com.richardmvc.framework.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class UserController {
    @MyGetMapping("/userInfo")
    public ModelAndView queryUserInfo(HttpServletResponse resp, HttpSession session) {
        User user = new User();
        user.setUserName("richard");
        user.setAge(22);
        return new ModelAndView("/userInfo.html", Map.of("user", user));
    }

    @MyGetMapping("/user/profile")
    public ModelAndView profile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/signin");
        }
        return new ModelAndView("/profile.html", "user", user);
    }

}
