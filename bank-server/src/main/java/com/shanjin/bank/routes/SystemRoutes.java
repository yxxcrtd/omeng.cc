package com.shanjin.bank.routes;

import com.jfinal.config.Routes;
import com.shanjin.bank.controller.*;


public class SystemRoutes extends Routes {

	@Override
	public void config() {
		add("/bank", BankController.class,"/view/bank");
	}

}
