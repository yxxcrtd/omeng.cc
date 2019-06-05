package com.shanjin.manager.log.controller;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.service.IMerchantTerminalService;
import com.shanjin.manager.service.impl.MerchantTerminalServiceImpl;

public class MerchantTerminalController extends Controller {
	
	IMerchantTerminalService merchantTerminalService = new MerchantTerminalServiceImpl();

	// 商户版本统计页面
	public void merchantVersionIndex() {
		render("merchantVersionStatistic.jsp");
	}
	
	// 商户设备机型统计页面
	public void merchantDeviceIndex() {
		render("merchantDeviceStatistic.jsp");
	}
	
	// 商户设备启动次数统计页面
	public void merchantStartUpIndex() {
		render("merchantStartUpStatistic.jsp");
	}
	
	// 商户设备渠道统计页面
	public void merchantChannelIndex() {
		render("merchantChannelStatistic.jsp");
	}
	
	// 商户地域来源统计页面
	public void merchantAreaIndex() {
		render("merchantAreaStatistic.jsp");
	}
	
	/** 商户端地域来源详细列表 */
	public void merchantAreaList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = merchantTerminalService.merchantAreaList(param);
		if (versionList != null && versionList.size() > 0) {
			long total = versionList.get(0).get("total");
			this.renderJson(new NormalResponse(versionList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端地域来源饼状图统计 */
	public void merchantAreaPieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> areaList = merchantTerminalService.merchantAreaPieList(param);
		if (areaList != null && areaList.size() > 0) {
			this.renderJson(new NormalResponse(areaList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端地域来源柱状图统计 */
	public void merchantAreaColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> areaList = merchantTerminalService.merchantAreaColList(param);
		if (areaList != null && areaList.size() > 0) {
			this.renderJson(new NormalResponse(areaList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端版本详细列表 */
	public void merchantVersionList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = merchantTerminalService.merchantVersionList(param);
		if (versionList != null && versionList.size() > 0) {
			long total = versionList.get(0).get("total");
			this.renderJson(new NormalResponse(versionList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端版本饼状图统计 */
	public void merchantVersionPieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = merchantTerminalService.merchantVersionPieList(param);
		if (versionList != null && versionList.size() > 0) {
			this.renderJson(new NormalResponse(versionList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端版本柱状图统计 */
	public void merchantVersionColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> versionList = merchantTerminalService.merchantVersionColList(param);
		if (versionList != null && versionList.size() > 0) {
			this.renderJson(new NormalResponse(versionList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 商户端设备机型详细列表 */
	public void merchantDeviceList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> deviceList = merchantTerminalService.merchantDeviceList(param);
		if (deviceList != null && deviceList.size() > 0) {
			long total = deviceList.get(0).get("total");
			this.renderJson(new NormalResponse(deviceList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端设备机型饼状图统计（按安卓和苹果机型统计） */
	public void merchantDevicePieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> deviceList = merchantTerminalService.merchantDevicePieList(param);
		if (deviceList != null && deviceList.size() > 0) {
			this.renderJson(new NormalResponse(deviceList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端设备机型柱状图统计（按设备热门机型Top10统计） */
	public void merchantDeviceColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> deviceList = merchantTerminalService.merchantDeviceColList(param);
		if (deviceList != null && deviceList.size() > 0) {
			this.renderJson(new NormalResponse(deviceList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端启动次数日统计（按日统计商户端启动次数） */
	public void merchantStartUpList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = merchantTerminalService.merchantStartUpList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).get("total");
			this.renderJson(new NormalResponse(list,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端某日启动次数曲线图统计（统计商户端启动次数具体到每个小时） */
	public void merchantStartUpLineList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> list = merchantTerminalService.merchantStartUpLineList(param);
		if (list != null && list.size() > 0) {
			this.renderJson(new NormalResponse(list));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端渠道详细列表 */
	public void merchantChannelList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> channelList = merchantTerminalService.merchantChannelList(param);
		if (channelList != null && channelList.size() > 0) {
			long total = channelList.get(0).get("total");
			this.renderJson(new NormalResponse(channelList,total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端渠道饼状图统计 */
	public void merchantChannelPieList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> channelList = merchantTerminalService.merchantChannelPieList(param);
		if (channelList != null && channelList.size() > 0) {
			this.renderJson(new NormalResponse(channelList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/** 商户端渠道柱状图统计 */
	public void merchantChannelColList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> channelList = merchantTerminalService.merchantChannelColList(param);
		if (channelList != null && channelList.size() > 0) {
			this.renderJson(new NormalResponse(channelList));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

}
