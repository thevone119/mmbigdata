package com.bingo.common.utility;

import com.bingo.common.http.HttpReturn;
import com.bingo.common.http.MyOkHttp;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018-07-20.
 * 创建和读取二维码
 */
public class QRCodeUtils {
    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;
    public static final String FORMAT = "png";
    public static final String CHARTSET = "utf-8";

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Administrator\\Desktop\\wx\\QQ图片20180830180859.jpg";
        //createQRcode(filePath);
        testReadQRcode(filePath);
    }

    private static void testReadQRcode(String filePath) {
        Result result = getQRresult(filePath);
        if (result != null) {
            System.out.println("二维码内容：" + result.getText());
            System.out.println("二维码格式：" + result.getBarcodeFormat());
        }
    }

    /**
     *
     * @Title:createQRcode
     * @Description:创建二维码
     * @param contents
     * @author doubledumbao
     * @修改时间：2018年2月26日 上午9:44:45
     * @修改内容：创建
     */
    public static void createQRcode(String contents,OutputStream out) {
        /**
         * 如果用的jdk是1.9，需要配置下面这一行。
         */
        //System.setProperty("java.specification.version", "1.9");
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CHARTSET);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, FORMAT,out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @Title:getQRresult
     * @Description:读取二维码
     * @param filePath
     * @return
     * @author doubledumbao
     * @修改时间：2018年2月26日 上午9:45:19
     * @修改内容：创建
     */
    public static Result getQRresult(String filePath) {
        try {
            return  getQRresult(ImageIO.read(new File(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Result getQRresult(InputStream input) {
        try {
            return  getQRresult(ImageIO.read(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Result getQRresult(BufferedImage bufferedImage) {
        /**
         * 如果用的jdk是1.9，需要配置下面这一行。
         */
        //System.setProperty("java.specification.version", "1.9");
        Result result = null;
        try {
            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));

            HashMap hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, CHARTSET);
            result = new MultiFormatReader().decode(bitmap, hints);
        } catch (NotFoundException e) {
            result = getQRresultQRCode(bufferedImage);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 剪切图片后再处理
     * @return
     */
    public static Result getQRresultQRCode(BufferedImage bufferedImage) {
        Result result = null;
        try {
            //QRCode解码器
            QRCodeDecoder codeDecoder = new QRCodeDecoder();
            /**
             *codeDecoder.decode(new MyQRCodeImage())
             *这里需要实现QRCodeImage接口，移步最后一段代码
             */
            //通过解析二维码获得信息
            String _result = new String(codeDecoder.decode(new MyQRCodeImage(bufferedImage)), "utf-8");
            if(_result!=null){
                result = new Result(_result,null,null,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result getQRresultByWeb(byte[] imgbyte) {
        Result result = null;
        try {
            MyOkHttp req = new MyOkHttp();
            Map files = new HashMap<String,byte[]>();
            files.put("file",imgbyte);
            HttpReturn ret = req.PostImage("https://www.sojson.com/qrcode/deqrByImages.shtml",null,files);
            if(ret.code==200){
                JSONObject json = new JSONObject(ret.body);
                result = new Result(json.getString("txt"),null,null,null);
            }
            //System.out.println(retstr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    private static class MyQRCodeImage implements QRCodeImage {
        BufferedImage bufferedImage;

        public MyQRCodeImage(BufferedImage bufferedImage){
            this.bufferedImage=bufferedImage;
        }

        //宽
        @Override
        public int getWidth() {
            return bufferedImage.getWidth();
        }

        //高
        @Override
        public int getHeight() {
            return bufferedImage.getHeight();
        }

        //像素还是颜色
        @Override
        public int getPixel(int i, int j) {
            return bufferedImage.getRGB(i,j);
        }
    }

}
