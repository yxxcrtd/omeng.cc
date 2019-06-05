package com.shanjin.manager.routes;

import com.jfinal.config.Routes;
import com.shanjin.manager.controller.*;
import com.shanjin.manager.log.controller.MerchantTerminalController;
import com.shanjin.manager.log.controller.StatisticController;
import com.shanjin.manager.log.controller.UserTerminalController;
import com.shanjin.manager.log.controller.serviceStatisticController;

public class SystemRoutes extends Routes {

	@Override
	public void config() {
		add("/user", UserController.class,"/view/user");
		add("/order", OrderController.class,"/view/order");
		add("/welcome", WelcomeController.class,"/view/welcome");
		add("/merchants", MerchantsController.class,"/view/merchants");
		add("/common", CommonController.class,"/view/common");
		add("/slider", SliderController.class,"/view/slider");
		add("/vouchers", VouchersController.class,"/view/vouchers");
		add("/agent", AgentController.class,"/view/agent");	
		add("/systemManager", SystemManagerController.class,"/view/systemManager");
		add("/terminal", TerminalController.class,"/view/terminal");
		add("/valueAdded", ValueAddedController.class,"/view/valueAdded");
		add("/loading", LoadingController.class,"/view/loading");
		add("/message", MessageController.class,"/view/message");
		add("/operate", OperateController.class,"/view/operate");
		add("/serviceTag", ServiceTagController.class,"/view/serviceTag");
		add("/statistic", StatisticController.class,"/view/statistic");
		add("/merchantTerminal", MerchantTerminalController.class,"/view/statistic");
		add("/userTerminal", UserTerminalController.class,"/view/statistic");
		add("/defined", DefinedController.class,"/view/defined");
		add("/activity", ActivityController.class,"/view/activity");
		add("/analysis", serviceStatisticController.class,"/view/statistic");
        add("/maintain", MaintainController.class,"/view/maintain");  
        add("/editHtml", EditHtmlController.class,"/view/editRelease");
    	   
	}

}
