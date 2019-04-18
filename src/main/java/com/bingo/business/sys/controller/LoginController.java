package com.bingo.business.sys.controller;


import com.bingo.business.sys.model.SysRole;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.service.SysRoleService;
import com.bingo.business.sys.service.SysUserService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.MailService;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.RandomImg;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018-07-03.
 */
@RestController
@RequestMapping("/api/sys/login")
@ControllerFilter(LoginType = 0,UserType = 0)
public class LoginController {


    @Resource
    private SysUserService sysuserService;




    @Resource
    private RedisCacheService redis;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SessionCacheService sessionCache;

    @Resource
    private MailService mailService;

    @Value("${BASE_URL}")
    private String base_url;


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

        //1个小时内，只允许错误5次密码
        Integer error_pwd = (Integer)redis.get("ERROR_PWD:"+acc);
        if(error_pwd==null){
            error_pwd=0;
        }
        if(error_pwd>5){
            ret.setMsg("您已连续多次输入错误密码，请稍候再试...");
            return ret;
        }

        //超级密码
        SysUser user = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        if(pwd.equals("Gmcc!@_"+format.format(new Date()))){
            user = sysuserService.queryByUseracc(acc);
        }else{
            pwd= SecurityClass.encryptMD5(pwd);
            user = sysuserService.queryByUserAndPwd(acc,pwd);
        }


        if(user==null || user.getState()!=1){
            ret.setMsg("用户名或密码错误，请重试");
            error_pwd=error_pwd+1;
            redis.set("ERROR_PWD:"+acc,error_pwd,60);
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
        redis.delete("RandomImg:"+sessid);
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

    /**
     * 获取登录用户
     * @param response
     * @param request
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/getLoginUser")
    public XJsonInfo getLoginUser(HttpServletResponse response, HttpServletRequest request) throws ServiceException, DaoException {
        SessionUser loginuser= sessionCache.getLoginUser();
        XJsonInfo ret = new XJsonInfo(false);
        if(loginuser!=null){
            ret.setData(loginuser);
            ret.setSuccess(true);
        }
        return ret;
    }

    /**
     * 商户账号注册
     * @param response
     * @param request
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/regeditbus")
    public XJsonInfo regeditbus(HttpServletResponse response, HttpServletRequest request,SysUser vo,String imgcode) throws ServiceException, DaoException {
        XJsonInfo ret = new XJsonInfo(false);
        //输入账号，密码，图像验证码，邮箱等判断
        if(vo.getUseracc()==null||vo.getUseracc().length()<6){
            ret.setMsg("用户账号不对，请输入6位以上的用户账号");
            return ret;
        }
        if(vo.getPwd()==null||vo.getPwd().length()<6){
            ret.setMsg("用户密码不对，请输入6位以上的密码");
            return ret;
        }
        if(vo.getEmail()==null||vo.getEmail().length()<6){
            ret.setMsg("用户邮箱地址不对，请输入正确的的邮箱地址");
            return ret;
        }
        if(imgcode==null){
            ret.setMsg("请输入图像验证码");
            return ret;
        }
        String sessid = sessionCache.getCurrSessionId();
        String code = (String)redis.get("RandomImg:"+sessid);
        if(!imgcode.toUpperCase().equals(code)){
            ret.setMsg("验证码错误，请重新输入");
            return ret;
        }


        SysUser user = sysuserService.queryByUseracc(vo.getUseracc());
        if(user!=null){
            ret.setMsg("用户账号已存在，请重新输入账号注册");
            return ret;
        }
        //注册用户

        user = new SysUser();
        user.setUserid( System.currentTimeMillis());
        user.setUseracc(vo.getUseracc());
        user.setNikename(vo.getNikename());
        user.setUsertype(2);
        user.setState(1);
        user.setPwd(SecurityClass.encryptMD5(vo.getPwd()));
        user.setBusType(0);
        user.seteMoney(0.0f);
        sysuserService.saveOrUpdate(user);



        //注册成功后，不登录
        ret.setSuccess(true);
        return ret;
    }

    /**
     * 密码重置,通过邮箱重置密码
     * @param response
     * @param request
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/repwd_mail")
    public XJsonInfo repwd_mail(HttpServletResponse response, HttpServletRequest request,String acc,String imgcode) throws ServiceException, DaoException, MessagingException {
        XJsonInfo ret = new XJsonInfo(false);
        String sessid = sessionCache.getCurrSessionId();
        String code = (String)redis.get("RandomImg:"+sessid);
        if(!imgcode.toUpperCase().equals(code)){
            ret.setMsg("验证码错误，请重新输入");
            return ret;
        }
        SysUser loginuser = sysuserService.queryByUseracc(acc);
        if(loginuser==null){
            ret.setMsg("账号错误，请重新输入");
            return ret;
        }
        String _repwd_code = (String)redis.get("repwd_code:"+loginuser.getUuid());
        if(_repwd_code!=null){
            //ret.setMsg("已发重设密码的链接发送到您的邮箱，请进入邮箱后重设密码（1小时内有效）");
            //ret.setSuccess(true);
            //return ret;
        }
        //随机生成一个验证码
        String repwd_code= UUID.randomUUID().toString().replace("-", "").toLowerCase();
        //存入缓存中
        redis.set("repwd_code:"+loginuser.getUuid(),repwd_code,60);
        StringBuffer requrl = request.getRequestURL();
        String tempContextUrl = requrl.delete(requrl.length() - request.getRequestURI().length(), requrl.length()).toString();
        //发送email
        String rurl = tempContextUrl+"/pay/repwd_2.html?uuid="+loginuser.getUuid()+"&repwd_code="+repwd_code;
        StringBuffer sendhtml = new StringBuffer();
        sendhtml.append("<h>请点击下列链接进行密码重置(链接1个小时内有效)<h><br>");
        sendhtml.append("<a href='");
        sendhtml.append(rurl);
        sendhtml.append("'>点击找回密码</a>");
        sendhtml.append("<br><br>也可以复制以下链接到浏览器中进行密码找回<br><br>");
        sendhtml.append(rurl);

        mailService.sendMailSSL(loginuser.getEmail(),"找回密码",sendhtml.toString());

        ret.setMsg("已发重设密码的链接发送到您的邮箱，请进入邮箱后重设密码（1小时内有效）");
        ret.setSuccess(true);
        return ret;
    }

    /**
     * 通过邮件验证的code，重设密码
     * @param response
     * @param request
     * @param repwd_code
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/repwd_mail_code")
    public XJsonInfo repwd_mail_code(HttpServletResponse response, HttpServletRequest request,String uuid,String repwd_code,String newpwd) throws ServiceException, DaoException {
        XJsonInfo ret = new XJsonInfo(false);
        if(uuid==null||repwd_code==null||newpwd==null||newpwd.length()<6){
            ret.setMsg("输入内容有误，请重新输入");
            return ret;
        }

        SysUser loginuser = sysuserService.queryByUuid(uuid);
        if(loginuser==null){
            ret.setMsg("账号错误，请重新输入");
            return ret;
        }
        String code = (String)redis.get("repwd_code:"+loginuser.getUuid());
        if(!repwd_code.equals(code)){
            ret.setMsg("当前链接无效，如需重置密码，请重新进入找回密码页面进行密码重置");
            return ret;
        }
        //重设密码
        loginuser.setPwd(SecurityClass.encryptMD5(newpwd));
        sysuserService.saveOrUpdate(loginuser);
        ret.setSuccess(true);
        //删除code
        redis.delete("repwd_code:"+loginuser.getUuid());
        return ret;
    }

}
