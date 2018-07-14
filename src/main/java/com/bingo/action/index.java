package com.bingo.action;

import com.bingo.common.utility.PubClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(method = {RequestMethod.GET})
    public ModelAndView index() {
        //pubClass.showLog("index");

        return new ModelAndView("/pay/index");
    }



    //@RequestMapping(value = "/view/**", method = RequestMethod.GET)
    public String viewPage() {
        String route = request.getRequestURI();
        return route;
        //return new ModelAndView(route);
    }


    @RequestMapping(value = "/{page1}.html", method = RequestMethod.GET)
    public String Page1(@PathVariable String page1) {
        return "/"+page1;
    }

    @RequestMapping(value = "/{page1}/{page2}.html", method = RequestMethod.GET)
    public String Page2(@PathVariable String page1,@PathVariable String page2) {
        return "/"+page1+"/"+page2;
        //return new ModelAndView(route);
    }

    @RequestMapping(value = "/{page1}/{page2}/{page3}.html", method = RequestMethod.GET)
    public String Page3(@PathVariable String page1,@PathVariable String page2,@PathVariable String page3) {
        return "/"+page1+"/"+page2+"/"+page3;
        //return new ModelAndView(route);
    }

    @RequestMapping(value = "/{page1}/{page2}/{page3}/{page4}.html", method = RequestMethod.GET)
    public String payPage4(@PathVariable String page1,@PathVariable String page2,@PathVariable String page3,@PathVariable String page4) {
        return "/"+page1+"/"+page2+"/"+page3+"/"+page4;
        //return new ModelAndView(route);
    }







//    @AuthTarget(AuthType.USER)
//    @RequestMapping(value = "/view/os/power/list", method = RequestMethod.GET)
//    public ModelAndView osPage() {
//        String route = request.getRequestURI();
//        return new ModelAndView(route);
//    }
}
