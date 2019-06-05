package com.shanjin.manager.log.controller;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.UserChannel;
import com.shanjin.manager.Bean.UserStartUp;
import com.shanjin.manager.Bean.UserVisit;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IUserTerminalService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.UserTerminalServiceImpl;

public class UserTerminalController extends Controller {
	IUserTerminalService userTerminalService = new UserTerminalServiceImpl();
	protected ExportService service = ExportService.service;
	// 用户版本统计页面
	public void userVersionIndex() {
		render("userVersionStatistic.jsp");
	}
	
	// 用户设备机型统计页面
	public void userDeviceIndex() {
		render("userDeviceStatistic.jsp");
	}
	
	// 用户设备启动次数统计页面
	public void userStartUpIndex() {
		render("userStartUpStatistic.jsp");
	}
	
	// 用户设备渠道统计页面
	public void userChannelIndex() {
		render("userChannelStatistic.jsp");
	}
	
	// 用户地域来源统计页面
	public void userAreaIndex() {
		render("userAreaStatistic.jsp");
	}
	
	/** 用户端地域来源详细列表 */
	public void userAreaList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = userTerminalService.userAreaList(param);
		if (versionList != null && versionList.size() > 0) {
			long total = versionList.get(0).get("total");
			this.renderJson(new NormalResponse(versionList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端地域来源饼状图统计 */
	public void userAreaPieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> areaList = userTerminalService.userAreaPieList(param);
		if (areaList != null && areaList.size() > 0) {
			this.renderJson(new NormalResponse(areaList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端地域来源柱状图统计 */
	public void userAreaColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> areaList = userTerminalService.userAreaColList(param);
		if (areaList != null && areaList.size() > 0) {
			this.renderJson(new NormalResponse(areaList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端版本详细列表 */
	public void userVersionList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = userTerminalService.userVersionList(param);
		if (versionList != null && versionList.size() > 0) {
			long total = versionList.get(0).get("total");
			this.renderJson(new NormalResponse(versionList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端版本饼状图统计 */
	public void userVersionPieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = userTerminalService.userVersionPieList(param);
		if (versionList != null && versionList.size() > 0) {
			this.renderJson(new NormalResponse(versionList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端版本柱状图统计 */
	public void userVersionColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = userTerminalService.userVersionColList(param);
		if (versionList != null && versionList.size() > 0) {
			this.renderJson(new NormalResponse(versionList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 用户端设备机型详细列表 */
	public void userDeviceList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> deviceList = userTerminalService.userDeviceList(param);
		if (deviceList != null && deviceList.size() > 0) {
			long total = deviceList.get(0).get("total");
			this.renderJson(new NormalResponse(deviceList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端设备机型饼状图统计（按安卓和苹果机型统计） */
	public void userDevicePieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> deviceList = userTerminalService.userDevicePieList(param);
		if (deviceList != null && deviceList.size() > 0) {
			this.renderJson(new NormalResponse(deviceList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端设备机型柱状图统计（按设备热门机型Top10统计） */
	public void userDeviceColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> deviceList = userTerminalService.userDeviceColList(param);
		if (deviceList != null && deviceList.size() > 0) {
			this.renderJson(new NormalResponse(deviceList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端启动次数日统计（按日统计用户端启动次数） */
	public void userStartUpList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = userTerminalService.userStartUpList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).get("total");
			this.renderJson(new NormalResponse(list,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端某日启动次数曲线图统计（统计用户端启动次数具体到每个小时） */
	public void userStartUpLineList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = userTerminalService.userStartUpLineList(param);
		if (list != null && list.size() > 0) {
			this.renderJson(new NormalResponse(list));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端渠道详细列表 */
	public void userChannelList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> channelList = userTerminalService.userChannelList(param);
		if (channelList != null && channelList.size() > 0) {
			long total = channelList.get(0).get("total");
			this.renderJson(new NormalResponse(channelList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端渠道饼状图统计 */
	public void userChannelPieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> channelList = userTerminalService.userChannelPieList(param);
		if (channelList != null && channelList.size() > 0) {
			this.renderJson(new NormalResponse(channelList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 用户端渠道柱状图统计 */
	public void userChannelColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> channelList = userTerminalService.userChannelColList(param);
		if (channelList != null && channelList.size() > 0) {
			this.renderJson(new NormalResponse(channelList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	// 导出用户地域记录
	public void exportUserAreaListExcel() {
		Map<String, String[]> param = this.getParaMap();
		List<UserVisit> list = userTerminalService.exportUserAreaListExcel(param); // 查询数据
		List<Pair> usetitles = userTerminalService.getExportUserAreaTitles(param);
		String fileName="用户地域记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
	
	// 导出用户设备机型记录
	public void exportUserDeviceListExcel() {
		Map<String, String[]> param = this.getParaMap();
		List<UserVisit> list = userTerminalService.exportUserDeviceListExcel(param); // 查询数据
		List<Pair> usetitles = userTerminalService.getExportUserDeviceTitles(param);
		String fileName="用户设备机型记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
		}
	// 导出用户版本记录
	public void exportUserVersionListExcel() {
		Map<String, String[]> param = this.getParaMap();
		List<UserVisit> list = userTerminalService.exportUserVersionListExcel(param); // 查询数据
		List<Pair> usetitles = userTerminalService.getExportUserVersionTitles(param);
		String fileName="用户版本记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
		}
	// 导出用户渠道记录
	public void exportUserChannelListExcel() {
		Map<String, String[]> param = this.getParaMap();
		List<UserChannel> list = userTerminalService.exportUserChannelListExcel(param); // 查询数据
		List<Pair> usetitles = userTerminalService.getExportUserChannelTitles(param);
		String fileName="用户渠道记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
		}
	// 导出设备启动次数记录
	public void exportUserStartUpListExcel() {
		Map<String, String[]> param = this.getParaMap();
		List<UserStartUp> list = userTerminalService.exportUserStartUpListExcel(param); // 查询数据
		List<Pair> usetitles = userTerminalService.getExportUserStartUpTitles(param);
		String fileName="设备启动次数记录";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
}
