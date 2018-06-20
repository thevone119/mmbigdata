package com.bingo.common.utility;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @Copyright:广州市品高软件股份有限公司
 * @Author:李丽全
 * @Email:15119575223@139.com
 * @Telephone:15119575223
 * @Date:2017年11月14日 17:32:43
 * @Description:导出（下载）文件工具
 */
public class DownloadUtils {
	public final static int BUFFER = 8192;
	
	/**
	 * @description <下载文件 >
	 * @param response
	 * @param downloadFileName  下载显示的文件名
	 * @param file  被下载的服务器的文件
	 * @return
	 */
	public static boolean writeFileToPage(HttpServletResponse response,
			String downloadFileName, File file) {
		OutputStream out = null;
		FileInputStream in = null;
		
		try {
			//清空response
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			//通知浏览器下载文件而不是打开网页
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downloadFileName, "UTF-8"));
			
			out = response.getOutputStream();
			in = new FileInputStream(file);
			byte[] buffer = new byte[BUFFER];
			int length = 0;
			while((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) {
					out.close();
				}
				if(in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
}