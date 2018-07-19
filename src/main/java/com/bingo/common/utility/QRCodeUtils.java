package com.bingo.common.utility;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

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
        String filePath = "C:\\Users\\Administrator\\Desktop\\QQ图片20180621021539.jpg";
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
        /**
         * 如果用的jdk是1.9，需要配置下面这一行。
         */
        //System.setProperty("java.specification.version", "1.9");
        Result result = null;
        try {
            File file = new File(filePath);

            BufferedImage bufferedImage = ImageIO.read(file);
            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));

            HashMap hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, CHARTSET);
            result = new MultiFormatReader().decode(bitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
