package com.bingo.common.utility;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * 生成随机验证码与随机验证码图片
 * Created by Administrator on 2018-06-25.
 */
public class RandomImg {
    final static String[] key = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private String code;
    private int width = 0;
    private int height = 50;
    private Random random = new Random();

    public RandomImg(){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            buffer.append(key[random.nextInt(key.length)]);
        }
        code=buffer.toString();
    }

    public RandomImg(String code){
        this.code=code;
    }

    public BufferedImage getImg(){
        BufferedImage image = null;
        this.width = 38 * code.length();
        image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g = image.createGraphics();
// 绘制背景
// g.setColor(getRandomColor(200, 250));

//透明度设置
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

//混淆线
// drawRandomLines(g, 120);
//混淆文字
        drawRandomText(g);
//干扰线
        drawCenterLine(g);


        StringBuffer strbuf = new StringBuffer();
// 定义字体样式
        Font myFont = new Font("Consolas", Font.BOLD, 38);
        boolean b = random.nextBoolean();
        for (int i = 0; i < code.length(); i++) {
            String temp = String.valueOf(code.charAt(i));
            Color color = new Color(40 + random.nextInt(80), 40 + random.nextInt(80), 40 + random.nextInt(80));
            g.setColor(color);
//旋转一定的角度
            AffineTransform trans = new AffineTransform();
            int rad = random.nextInt(30);
            if (b = !b) {
                trans.rotate(Math.toRadians(rad), 50 * (i) + 8, 30);
            } else {
                trans.rotate(Math.toRadians(-rad), 50 * (i) + 8, 40);
            }
// 缩放文字
            float scaleSize = random.nextFloat() + 0.8f;
            if (scaleSize > 1f) {
                scaleSize = 1f;
            }
            trans.scale(1f, 1f);
            g.setTransform(trans);
//写文字
// 设置字体
            g.setFont(myFont);
            g.drawString(temp, 35 * i + (i == 0 ? 10 : 15), 40);
            strbuf.append(temp);
        }
        g.dispose();
        return image;

    }

    /**
     * 功能描述：随机颜色
     *
     * @param fc
     * @param bc
     * @return <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     * @author 彭志(pengzhistar@sina.com.cn)
     * <p>创建日期 ：2015年2月3日 下午4:08:37</p>
     */
    private Color getRandomColor(int fc, int bc) {
        if (fc > 255)
            fc = 200;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 功能描述：混淆背景线
     *
     * @param g
     * @param nums <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     * @author 彭志(pengzhistar@sina.com.cn)
     * <p>创建日期 ：2015年2月3日 下午4:10:08</p>
     */
    private void drawRandomLines(Graphics2D g, int nums) {
        int x1 = 0, y1 = 0;
        for (int i = 0; i < nums; i++) {
            g.setColor(this.getRandomColor(190, 230));
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }
    }

    /**
     * 功能描述：混淆文字
     *
     * @param g <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     * @author 彭志(pengzhistar@sina.com.cn)
     * <p>创建日期 ：2015年2月3日 下午4:09:59</p>
     */
    private void drawRandomText(Graphics2D g) {
        Font myFont = new Font("Consolas", Font.HANGING_BASELINE, 30);
// 设置字体
        g.setFont(myFont);
        int max = 8 + random.nextInt(4);
        for (int i = 0; i < max; i++) {
            g.setColor(this.getRandomColor(170, 220));
            String temp = key[random.nextInt(key.length)];
            g.drawString(temp, i * 24, random.nextInt(height));
        }
    }

    /**
     * 功能描述：中间混淆线
     *
     * @param g <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     * @author 彭志(pengzhistar@sina.com.cn)
     * <p>创建日期 ：2015年2月3日 下午4:09:42</p>
     */
    private void drawCenterLine(Graphics2D g) {
        Random random = new Random();
        int py = 3 + random.nextInt(6);
        int x1 = 0, y1 = height / 2, y2 = height / 2 - 1;
        boolean minus = true;
        for (int i = 0; i < width; i++) {
            g.setColor(this.getRandomColor(60, 80));
            g.drawLine(x1, y1, x1, y2);
            x1++;
            if (minus) {
                y1--;
                y2--;
            } else {
                y1++;
                y2++;
            }
            if (Math.abs(y1 - height / 2) > py) {
                minus = !minus;
                py = 3 + random.nextInt(6);
            }
        }
    }

    public String getCode(){
        return this.code;
    }

    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            RandomImg idCode = new RandomImg();
            System.out.println(UUID.randomUUID().toString());
            System.out.println(idCode.getCode());
        }
    }

}
