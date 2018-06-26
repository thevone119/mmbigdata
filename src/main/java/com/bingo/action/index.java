package com.bingo.action;

import com.bingo.common.filter.AuthTarget;
import com.bingo.common.filter.AuthType;
import com.bingo.common.utility.PubClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2016/12/19.
 */
@Controller
@RequestMapping("/")
public class index {
    @Autowired
    HttpServletRequest request;

    @Resource
    private PubClass pubClass;

    @AuthTarget(AuthType.NONE)
    @RequestMapping(method = {RequestMethod.GET})
    public ModelAndView index() {
        pubClass.showLog("index");
        return new ModelAndView("view/index");
    }

    @AuthTarget(AuthType.USER)
    @RequestMapping("/main")
    public ModelAndView main() {
        // T_OS_User info = userService.getUserByToken(token);
        return new ModelAndView("view/main");
    }

    @AuthTarget(AuthType.USER)
    @RequestMapping(value = "/view/**", method = RequestMethod.GET)
    public ModelAndView osPage() {
        String route = request.getRequestURI();
        return new ModelAndView(route);
    }


//    @AuthTarget(AuthType.USER)
//    @RequestMapping(value = "/view/os/power/list", method = RequestMethod.GET)
//    public ModelAndView osPage() {
//        String route = request.getRequestURI();
//        return new ModelAndView(route);
//    }
}
