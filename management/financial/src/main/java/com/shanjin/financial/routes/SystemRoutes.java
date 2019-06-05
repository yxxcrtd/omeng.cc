package com.shanjin.financial.routes;

import com.jfinal.config.Routes;
import com.shanjin.financial.controller.*;


public class SystemRoutes extends Routes {

	@Override
	public void config() {
		add("/welcome", WelcomeController.class,"/view/welcome");
		add("/system", SystemController.class,"/view/system");
		add("/financial", FinancialController.class, "/view/finacial");
		add("/report", ReportController.class, "/view/report");
		add("/common", CommonController.class, "/view/common");
	}

}
