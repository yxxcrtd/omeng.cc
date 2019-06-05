package com.shanjin.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.shanjin.sso.bean.UserSession;
import com.shanjin.manager.service.IConfigGenerateService;
import com.shanjin.manager.service.impl.ConfigGenerateServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.FileUtil;
import com.shanjin.manager.utils.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.shanjin.manager.constant.Constant.config;

/**
 * 生成系统维护页面
 */
public class MaintainController extends Controller {

    /** 系统维护页面输出路径 */
    private static final String PATH_MAINTAIN = StringUtil.null2Str(config.get("path.maintain"));
    private static final String PATH_UPLOAD_IMAGES = StringUtil.null2Str(config.get("path.upload.images"));

    private IConfigGenerateService configGenerateService = new ConfigGenerateServiceImpl();

    /**
     * Index
     */
    public void index() {
    	String id = this.getPara("id");
        setAttr("request", getRequest());
        Record obj = Db.findById("html_content", id);
        setAttr("obj", obj);
//        List<Record> list = configGenerateService.getConfigGenerate("maintain");
//        if (null != list) {
//            setAttr("obj", list.get(0));
//        }
        renderFreeMarker("Maintain.ftl");
    }

    /**
     * Save
     */
    public void save1() {
        getFile();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", getPara("id"));
        map.put("title", getPara("title"));
        map.put("content", getPara("maintain"));
        HttpSession session = getSession();
        String username = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
        boolean flag = configGenerateService.saveOrUpdate(map, username);
        if (flag) {
            try {
                generateMaintain(map, getRequest());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        renderJson(flag);
    }

    /**
     * Upload Image
     */
    public void image() {
        UploadFile uf = getFile();
        String resultPath = "";
        if (null != uf) {
    		String filePath = FileUtil.SEPARATOR + FileUtil.IMAGE + FileUtil.SEPARATOR;
    		resultPath = BusinessUtil.fileUpload(uf, filePath);
        }
        JSONObject obj = new JSONObject();
        obj.put("error", 0);
        String url = BusinessUtil.getFileUrl(resultPath);
        obj.put("url", url);
        renderJson(obj);
    }
    
    /**
     * Upload Image
     */
    public void image1() {
        UploadFile uf = getFile();
        String imageName = "";
        if (null != uf) {
            try {
                imageName = FileUtil.uploadFile(uf.getFile(), PATH_UPLOAD_IMAGES, uf.getOriginalFileName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("error", 0);
        obj.put("url", new StringBuffer().append("/upload/").append(imageName).toString());
        renderJson(obj);
    }

    
    public void save() {
        getFile();
        Map<String, Object> map = new HashMap<String, Object>();
        Long contId = StringUtil.nullToLong(getPara("id"));
        String content = getPara("maintain");
        map.put("id", getPara("id"));
        map.put("title", getPara("title"));
        map.put("content", content);
        HttpSession session = getSession();
        String username = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
        Record r = Db.findById("html_content", contId);
        String url = "";
        try {
        	String path = PATH_MAINTAIN;
        	path = path + FileUtil.CONT + FileUtil.SEPARATOR  + FileUtil.idToPath(contId) + FileUtil.SEPARATOR;
        	String fileName = FileUtil.idToShortName(contId)+".html";
			generateHTML("view/maintain", "MaintainTemplate.ftl", fileName, map, getRequest().getSession().getServletContext(), path);
			File htmlFile = new File(path + fileName);
			String abPath = FileUtil.SEPARATOR  + FileUtil.CONT + FileUtil.SEPARATOR + FileUtil.idToPath(contId) + FileUtil.SEPARATOR ;
			BusinessUtil.fileUpload(htmlFile, abPath, fileName);
			
		    url = BusinessUtil.getFileUrl(abPath+fileName);
		    
		 
		    r.set("url", url).set("content", content).set("is_pub", 1).set("pub_time", new Date()).set("pub_user", username);
		    Db.update("html_content", r);
		    
		    System.out.println("url =="+url);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        redirect(url);
        //renderJson(url);
    }
    
    public void onlySave() {
        getFile();
        Map<String, Object> map = new HashMap<String, Object>();
        Long contId = StringUtil.nullToLong(getPara("id"));
        String content = getPara("maintain");
        map.put("id", getPara("id"));
        map.put("title", getPara("title"));
        map.put("content", content);
        
        Record r = Db.findById("html_content", contId);
        try {
		    r.set("content", content);
		    Db.update("html_content", r);
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.renderHtml("保存成功");
      
      
    } 
    
    /**
     * Generate Maintain HTML File
     *
     * @param map
     * @param request
     */
    private void generateMaintain(Map<String, Object> map, HttpServletRequest request) throws Exception {
        FileUtil.generateHTML("view/maintain", "MaintainTemplate.ftl", "maintain.html", map, request.getSession().getServletContext(), PATH_MAINTAIN);
    }
    
	@SuppressWarnings("deprecation")
	public static String generateHTML(String folderName, String ftl, String htmlName, Map<String, Object> map, ServletContext servletContext, String path) throws Exception {
		String url = "";
		Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(servletContext, File.separator + folderName);
		cfg.setEncoding(Locale.getDefault(), "UTF-8");
		Template template = cfg.getTemplate(ftl);
		template.setEncoding("UTF-8");
		File pathFile = new File(path);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + htmlName));
		File htmlFile = new File(path + htmlName);
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), "UTF-8"));
		template.process(map, out);
		bufferedWriter.close();
		out.flush();
		out.close();
		

		
		
		return url;
	}

}
