package com.bingo.common.utility;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by Administrator on 2017/8/4.
 */
public class Captcha {

    private Random random = new Random();
    private String g_randString = "0123456789";// 随机字符串种子

    private int width = 80;// 图片宽
    private int height = 26;// 图片高
    private int lineSize = 40;// 干扰线数量

    public int stringNum = 4;// 随机产生字符数量
    public String captchaString; // 最终生成的验证码

    /*
     * 获得字体
     */
    private Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
    }

    /*
     * 获得颜色
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /*
     * 绘制字符串
     */
    private void drowString(Graphics g) {
        char[] c=this.captchaString.toCharArray();
        for (int i = 1; i <= c.length; i++) {
            g.setFont(getFont());
            g.setColor(new Color(random.nextInt(101), random.nextInt(111), random.nextInt(121)));
            g.translate(random.nextInt(3), random.nextInt(3));
            g.drawString(String.valueOf(c[i-1]), 13 * i, 16);
        }
    }

    /*
   * 绘制干扰线
   */
    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /*
     * 获取随机的字符
     */
    private String getRandomString(int num) {
        return String.valueOf(g_randString.charAt(num));
    }

    /**
     * 生成随机图片
     */

    public BufferedImage getRandcode() {
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
        g.setColor(getRandColor(110, 133));
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
        // 绘制随机字符
        if (StringUtils.isBlank(this.captchaString)) {
            this.captchaString = StringClass.randomStr(stringNum);
        }

        drowString(g);


        g.dispose();
        return image;

    }
}
