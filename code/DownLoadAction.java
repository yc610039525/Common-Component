package com.boco.component.download;

import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.boco.core.bean.SpringContextUtil;

@Controller
public class DownLoadAction {
	private static Logger logger = Logger.getLogger(DownLoadAction.class);
	@RequestMapping(value="download.do")
	public void execute(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String file = request.getParameter("file");
	    String fileName = request.getParameter("fileName");
	    file = URLDecoder.decode(URLDecoder.decode(file, "UTF-8"), "UTF-8");
	    file = "file:" + StringUtils.replace(file, "//", "/");
	    fileName = URLDecoder.decode(URLDecoder.decode(fileName, "UTF-8"), "UTF-8");
	    file = file.replaceAll("\\.\\.\\/", "");
	    fileName = fileName.replaceAll("\\.\\.\\/", "");
	    logger.info("file:" + file);
	    logger.info("fileName:" + fileName);
	    ServletOutputStream os = response.getOutputStream();
	    Resource res = SpringContextUtil.getApplicationContext().getResource(file);
		response.setContentType("application/octet-stream;charset=utf-8");
		if(StringUtils.isEmpty(fileName)){
			response.setHeader("Content-disposition","attachment;filename="+ new String(res.getFilename().getBytes("gb2312"), "iso8859-1"));
		}else{
			response.setHeader("Content-disposition","attachment;filename="+ new String(fileName.getBytes("gb2312"), "iso8859-1"));
		}
		InputStream in = res.getInputStream();
		byte[] b = new byte[4 * 1024];//缓冲区调小
		int i = 0;
		while ((i = in.read(b)) != -1) {
			os.write(b, 0, i);
		}
		os.flush();
		response.flushBuffer();	
		os.close();
		in.close();
	}
}
