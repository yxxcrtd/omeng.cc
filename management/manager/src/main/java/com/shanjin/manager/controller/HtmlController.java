package com.shanjin.manager.controller;

import static com.shanjin.manager.constant.Constant.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.shanjin.sso.bean.UserSession;
import com.shanjin.manager.service.IConfigGenerateService;
import com.shanjin.manager.service.impl.ConfigGenerateServiceImpl;
import com.shanjin.manager.utils.FileUtil;
import com.shanjin.manager.utils.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 运营发布html 页面
 * @author Huang yulai
 *
 */
public class HtmlController extends Controller {

	 /** 系统维护页面输出路径 */
    private static final String PATH_MAINTAIN = StringUtil.null2Str(config.get("path.maintain"));
    private static final String PATH_UPLOAD_IMAGES = StringUtil.null2Str(config.get("path.upload.images"));

    private IConfigGenerateService configGenerateService = new ConfigGenerateServiceImpl();

    /**
     * Index
     */
    public void index() {
        setAttr("request", getRequest());
        List<Record> list = configGenerateService.getConfigGenerate("maintain");
        if (null != list) {
            setAttr("obj", list.get(0));
        }
        renderFreeMarker("Maintain.ftl");
    }

    /**
     * Save
     */
    public void save() {
        getFile();
        Map<String, Object> map = new HashMap<String, Object>();
        Long contId = StringUtil.nullToLong(getPara("id"));
        map.put("id", getPara("id"));
        map.put("title", getPara("title"));
        map.put("content", getPara("maintain"));
        HttpSession session = getSession();
        String username = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
        boolean flag = configGenerateService.saveOrUpdate(map, username);
        try {
        	String path = PATH_MAINTAIN;
        	path = path + FileUtil.CONT + FileUtil.SEPARATOR  + FileUtil.SEPARATOR + FileUtil.idToPath(contId) + FileUtil.SEPARATOR;
        	String fileName = FileUtil.idToShortName(contId);
			generateHTML("view/maintain", "MaintainTemplate.ftl", fileName+".html", map, getRequest().getSession().getServletContext(), path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        renderJson(true);
    }
    
    public void publish() {
        Map<String, Object> map = new HashMap<String, Object>();
        Long contId = StringUtil.nullToLong(getPara("id"));
        map.put("id", getPara("id"));
        map.put("title", getPara("title"));
        map.put("content", getPara("maintain"));
        try {
        	String path = PATH_MAINTAIN;
        	path = path + FileUtil.CONT + FileUtil.SEPARATOR  + FileUtil.SEPARATOR + FileUtil.idToPath(contId) + FileUtil.SEPARATOR;
        	String fileName = FileUtil.idToShortName(contId);
			generateHTML("view/maintain", "MaintainTemplate.ftl", fileName+".html", map, getRequest().getSession().getServletContext(), path);
		} catch (Exception e) {
			e.printStackTrace();
		}
        renderJson(true);
    }

    /**
     * Upload Image
     */
    public void image() {
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

    /**
     * Generate Maintain HTML File
     *
     * @param map
     * @param request
     */
    private void generateMaintain(Map<String, Object> map, HttpServletRequest request) throws Exception {
        generateHTML("view/maintain", "MaintainTemplate.ftl", "maintain.html", map, request.getSession().getServletContext(), PATH_MAINTAIN);
    }
    
	@SuppressWarnings("deprecation")
	public static void generateHTML(String folderName, String ftl, String htmlName, Map<String, Object> map, ServletContext servletContext, String path) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(servletContext, File.separator + folderName);
		cfg.setEncoding(Locale.getDefault(), "UTF-8");
		Template template = cfg.getTemplate(ftl);
		template.setEncoding("UTF-8");
		File pathFile = new File(path);
		if (!pathFile.exists()) {
			pathFile.mkdir();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + htmlName));
		File htmlFile = new File(path + htmlName);
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), "UTF-8"));
		template.process(map, out);
		bufferedWriter.close();
		out.flush();
		out.close();
	}
	
}
