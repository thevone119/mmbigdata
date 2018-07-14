package com.bingo.business.sys.controller;

import com.bingo.business.sys.model.SysRole;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.service.SysRoleService;
import com.bingo.business.sys.service.SysUserService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.RandomImg;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-07-03.
 */
@RestController
@RequestMapping("/api/sys/login")
@ControllerFilter(LoginType = 0,UserType = 0)
public class LoginController {
    @Resource
    private PubClass pubClass;

    @Resource
    private SysUserService sysuserService;

    @Resource
    private RedisCacheService redis;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SessionCacheService sessionCache;

    /**
     * 获取随机验证码(有效期5分钟)
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/getRandomImg")
    public void getRandomImg(HttpServletResponse response, HttpServletRequest request) throws ServiceException, DaoException {
        RandomImg draw = new RandomImg();
        String rdCode = draw.getCode();
        BufferedImage image = draw.getImg();
        OutputStream os = null;
        //验证码放入缓存中5分钟有效，关联session放入
        String sessid = sessionCache.getCurrSessionId();
        redis.set("RandomImg:"+sessid,rdCode,5);
        try {
            os = response.getOutputStream();
            ImageIO.write(image, "PNG", os);
            image.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }


    /**
     * @description: <修改、保存>
     * @param:
     * @throws:
     */
    @ResponseBody
    @RequestMapping("/Login")
    public XJsonInfo login(HttpServletRequest request, String acc, String pwd, String imgcode) throws ServiceException, DaoException {
        XJsonInfo ret = new XJsonInfo();
        ret.setSuccess(false);
        if(acc==null || pwd==null || imgcode==null || acc.length()<3 || pwd.length()<3){
            ret.setMsg("参数错误，请重新输入1");
            return ret;
        }
        String sessid = sessionCache.getCurrSessionId();
        String code = (String)redis.get("RandomImg:"+sessid);
        if(!imgcode.toUpperCase().equals(code)){
            ret.setMsg("验证码错误，请重新输入");
            return ret;
        }
        pwd= SecurityClass.encryptMD5(pwd);
        SysUser user = sysuserService.queryByUserAndPwd(acc,pwd);
        if(user==null || user.getState()!=1){
            ret.setMsg("用户名或密码错误，请重试");
            return ret;
        }
        //查询用户的角色列表
        List<SysRole> listrole = sysRoleService.queryByUser(user.getUserid());
        List<Long> roleids = new ArrayList<Long>();
        List<String> rolecodes = new ArrayList<String>();
        for(SysRole role : listrole){
            roleids.add(role.getRoleid());
            rolecodes.add(role.getRolecode());
        }
        //构建sessionuser
        SessionUser suser = new SessionUser();
        suser.nikename = user.getNikename();
        suser.userid = user.getUserid();
        suser.useracc = user.getUseracc();
        suser.usertype = user.getUsertype();
        suser.rolecodes = rolecodes;
        suser.roleids = roleids;

        //登录成功，把用户放入session
        sessionCache.setloginUser(suser);
        ret.setData(suser);
        ret.setSuccess(true);
        return ret;
    }

    /**
     * @description: <修改、保存>
     * @param:
     * @throws:
     */
    @ResponseBody
    @RequestMapping("/loginOut")
    public XJsonInfo loginOut(HttpServletRequest request) throws ServiceException, DaoException {
        sessionCache.loginOut();
        return new XJsonInfo();
    }

}
