package com.shanjin.manager.controller;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.Voucher;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IVourchersService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.VourchersServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;

public class VouchersController extends Controller {

	private IVourchersService vourchersService = new VourchersServiceImpl();
	protected ExportService service = ExportService.service;
	public void showVouchers() {
		this.render("vouchersAdd.jsp");
	}

	public void showVouchersUse() {
		this.render("vouchersUse.jsp");
	}

	public void showVouchersIssue() {
		this.render("vouchersIssue.jsp");
	}
	/**获取所有代金券*/
	public void getVouchers() {
		Map<String, String[]> param = this.getParaMap();
		List<Voucher> vouchersList = vourchersService.getVouchers(param);
		if (vouchersList != null && vouchersList.size() > 0) {
			long total = vouchersList.get(0).getTotal();
			this.renderJson(new NormalResponse(vouchersList,total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/**增加代金券*/
	public void addVouchers() {
		UploadFile file = this.getFile("upload");
		Map<String, String[]> param = this.getParaMap();
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.VoucherPath();
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		
		int flag = vourchersService.addVouchers(param,resultPath);
		if(flag==1){
			this.renderJson(true);
		}else if(flag==2){
			this.renderText("此类型代金券已存在");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}

	public void editVouchers(){
		UploadFile file = this.getFile("upload");
		Map<String, String[]> param = this.getParaMap();
		String resultPath = "";
		if (file != null) {
			String filePath = BusinessUtil.VoucherPath();
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		
		int flag = vourchersService.editVouchers(param,resultPath);
		if(flag==1){
			this.renderJson(true);
		}else if(flag==2){
			this.renderText("此类型代金券已存在");
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	
	/** 代金券启用或暂停 */
	public void startOrstopVouchers() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = vourchersService.startOrstopVouchers(param);
		this.renderJson(new NormalResponse(flag));
	}
	/**删除代金券*/
	public void deleteVouchers() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = vourchersService.deleteVouchers(param);
		this.renderJson(new NormalResponse(flag));
	}
	
	/**获取已使用的代金券*/
	public void getUseVouchers() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> uservouchersList = vourchersService.getUseVouchers(param);
		if (uservouchersList != null && uservouchersList.size() > 0) {
			long total = uservouchersList.get(0).getLong("total");
			this.renderJson(new NormalResponse(uservouchersList,total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**根据商户ID查看启用的代金券*/
	public void getVouchersByMerId(){
		Map<String, String[]> param = this.getParaMap();
		List<Record> vouchers=vourchersService.getVouchersByMerId(param);
		if (vouchers != null && vouchers.size() > 0) {
			long total = vouchers.get(0).getLong("total");
			this.renderJson(new NormalResponse(vouchers,total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**获取所有发放代金券记录*/
	public void getIssueVouchers() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> uservouchersList = vourchersService.getIssueVouchers(param);
		if (uservouchersList != null && uservouchersList.size() > 0) {
			long total = uservouchersList.get(0).getLong("total");
			this.renderJson(new NormalResponse(uservouchersList,total));

		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/**发放代金券*/
	public void IssueVouchers(){
		Map<String, String[]> param = this.getParaMap();
		boolean flag = vourchersService.IssueVouchers(param);
		this.renderJson(new NormalResponse(flag));
	}
	/**删除发放代金券记录*/
	public void deleteVoucherIssue(){
		Map<String, String[]> param = this.getParaMap();
		boolean flag = vourchersService.deleteVoucherIssue(param);
		this.renderJson(new NormalResponse(flag));
	}
	/**查看不同服务代金券的价格种类*/
	public void getPriceByVoucherType(){
		String app_type=this.getPara("app_type");
		String coupons_type=this.getPara("coupons_type");
		List<Voucher> price=vourchersService.getPriceByVoucherType(app_type,coupons_type);
		if(price!=null&&price.size()>0){
			this.renderJson(new NormalResponse(price));
		}else{
			this.renderJson(new EmptyResponse());
		}
	}
	/**查看不同价格代金券剩余数量*/
	public void getCountByPrice(){
		String app_type=this.getPara("app_type");
		String coupons_type=this.getPara("coupons_type");
		String price=this.getPara("price");
		List<Voucher> count=vourchersService.getCountByPrice(app_type,coupons_type,price);
		if(count!=null&&count.size()>0){
			this.renderJson(new NormalResponse(count));
		}else{
			this.renderJson(new EmptyResponse());
		}
	}
	/**导出代金券*/
		public void exportExcel() {
			Map<String, String[]> param = this.getParaMap();
			List<Voucher> list = vourchersService.getVoucherExportList(param); // 查询数据
	        List<Pair> usetitles=vourchersService.getExportTitles();
	        String fileName="代金券";
			// 导出
			service.export(getResponse(), getRequest(), list,usetitles,fileName,0);
			renderNull();
		}
		
		/**获取所有代金券*/
		public void getCanIsuseVouchers() {
			Map<String, String[]> param = this.getParaMap();
			List<Voucher> vouchersList = vourchersService.getCanIsuseVouchers(param);
			if (vouchersList != null && vouchersList.size() > 0) {
				long total = vouchersList.get(0).getTotal();
				this.renderJson(new NormalResponse(vouchersList,total));

			} else {
				this.renderJson(new EmptyResponse());
			}
		}
}
