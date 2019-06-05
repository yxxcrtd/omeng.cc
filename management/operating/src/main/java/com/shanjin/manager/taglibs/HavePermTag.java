package com.shanjin.manager.taglibs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.Bean.UserSession;

public class HavePermTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	private String url;

	public int doStartTag() throws JspException {
		setDefaultVar("havePerm");
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		boolean haveperm = false;
		HttpSession session = (HttpSession) request.getSession();
		String userName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		if (Constant.ADMIN.equals(userName)){
			haveperm = true;
		}else{
			String perms = StringUtil.null2Str(session.getAttribute(UserSession._USER_RESOURCES));
			haveperm = BusinessUtil.havePerm(url, perms);
		}
		if (haveperm)
		return EVAL_BODY_INCLUDE;
	    else
		return SKIP_BODY;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
