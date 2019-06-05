package com.shanjin.financial.taglibs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import com.shanjin.financial.constant.Constants;
import com.shanjin.financial.util.BusinessUtil;
import com.shanjin.financial.util.StringUtil;
import com.shanjin.sso.bean.UserSession;


public class HavePermTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	private String url;

	public int doStartTag() throws JspException {
		setDefaultVar("havePerm");
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		boolean haveperm = true;
		HttpSession session = (HttpSession) request.getSession();
		String userName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		if (Constants.ADMIN.equals(userName)){
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
