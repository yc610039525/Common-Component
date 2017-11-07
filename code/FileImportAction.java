package com.boco.component.excel.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.boco.component.excel.bo.EltMainProcessService;
import com.boco.component.excel.pojo.FileDefine;
import com.boco.core.ibatis.vo.ServiceActionContext;

@Controller
public class FileImportAction {
	@Autowired
	private EltMainProcessService EltMainProcessService;
	
	@RequestMapping(value="import/index.do")
	public String execute(HttpServletRequest request,HttpServletResponse response) throws IOException  {
		String bmClassId = request.getParameter("bmClassId");
		String templateId = null;
		if(request.getParameter("templateId")!=null){
			templateId = URLDecoder.decode(request.getParameter("templateId"), "utf-8");
		}
		String sheetType = null;
		if(request.getParameter("sheetType")!=null){
			sheetType = URLDecoder.decode(request.getParameter("sheetType"), "utf-8");
		}
		// 转型为MultipartHttpRequest：   
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
		// 获得文件：   
		MultipartFile file = multipartRequest.getFile("file");
		// 获得文件名：   
		String filename = file.getOriginalFilename(); 
		Map<String,String> map = new HashMap<String, String>();
		map.put("bmClassId", bmClassId);
		map.put("templateId", templateId);
		map.put("fileName", filename);
		map.put("taskId", request.getParameter("taskId"));
		map.put("sheetType", sheetType);
		ServiceActionContext ac = new ServiceActionContext(request);
		FileDefine df = null;
		String msg = "导入完成!";
		try {
			df = EltMainProcessService.execute(ac,file.getInputStream(), map);
		} catch (Exception e) {
			msg = StringUtils.replace(e.getMessage(),"'","\"");
			if(StringUtils.isEmpty(msg)){
				msg="后台程序异常:"+e.toString();
			}else if(StringUtils.contains(msg, "java.sql.SQLException:")){
				msg = "数据库异常:"+StringUtils.substringAfterLast(msg, "java.sql.SQLException:");
			}else{
				msg="后台程序异常:"+msg;
			}
		}
		response.setContentType("text/html;charset=UTF-8");
		if(df!=null){
			response.getWriter().write("{success:true,msg:'"+msg+"',batchNo:'"+df.getBatchNo()+"',templateCuid:'"+df.getTemplateCuid()+"',total:"+df.getTotal()+",sucess:"+df.getSucess()+",error:"+df.getError()+" }");
		}else{
			response.getWriter().write("{success:true,msg:'"+msg+"'}");
		}
		
		return null;
	}
}
