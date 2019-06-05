package com.shanjin.financial.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/*
 * 
 * 提请注意:
 * 1) 返回值的问题
 *  doStartTag中使用
 *      SKIP_BODY = 0：跳过了开始和结束标签之间的代码。TagSupport default : SKIP_BODY
 *	    EVAL_BODY_INCLUDE = 1：将body的内容输出到存在的输出流中    
 * 
 *  doEndTag()中使用
 *	     SKIP_PAGE= 5  ：  忽略剩下的页面。    
 *	     EVAL_PAGE = 6：继续执行下面的页   TagSupport default : EVAL_PAGE
 *
 *  2) BODY的处理
 *   TagSupport与BodyTagSupport的区别主要是标签处理类是否需要与标签体交互，如果不需要交互的就用TagSupport，否则如果不需要交互就用BodyTagSupport。
 *   交互就是标签处理类是否要读取标签体的内容和改变标签体返回的内容。
 *   
 *   
 */
public class BaseTag extends TagSupport {
	private static final long serialVersionUID = -6288399036748705934L;
	private String var;

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	private String scope;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public int doStartTag() throws JspException {
		return TagSupport.EVAL_BODY_INCLUDE;
	}

	/**
	 * 设置默认变量名
	 * 
	 * @param varName
	 */
	protected void setDefaultVar(String varName) {
		if (var == null || "".equals(var.trim()))
			var = varName;
	}

	/**
	 * 将对象保存在对应的范围内
	 * 
	 * @param obj
	 * @throws JspException
	 */
	protected void saveScope(Object obj) throws JspException {
		if (var == null || "".equals(var))
			return;

		if (scope != null) {
			if (scope.equals("page")) {
				pageContext.setAttribute(var, obj);
			} else if (scope.equals("request")) {
				pageContext.getRequest().setAttribute(var, obj);
			} else if (scope.equals("session")) {
				pageContext.getSession().setAttribute(var, obj);
			} else if (scope.equals("application")) {
				pageContext.getServletContext().setAttribute(var, obj);
			} else {
				throw new JspException(
						"Attribute 'scope' must be: page, request, session or application");
			}
		} else {
			pageContext.setAttribute(var, obj);
			// 设置为page快速释放
		}
	}

	protected void write(String str) {
		try {
			pageContext.getOut().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void write(long count) {
		write(String.valueOf(count));
	}
}