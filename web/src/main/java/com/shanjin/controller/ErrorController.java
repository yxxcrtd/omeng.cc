package com.shanjin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;

@Controller
@RequestMapping("/error")
public class ErrorController {
    @RequestMapping("/outOfFileSize")
    @SystemControllerLog(description = "文件太大,请处理后重新上传")
    public @ResponseBody Object tobig() {
        JSONObject jsonObject = new ResultJSONObject("01", "文件太大,请处理后重新上传");
        return jsonObject;
    }

    @RequestMapping("/error")
    @SystemControllerLog(description = "系统发生错误,请于管理员联系")
    public @ResponseBody Object error() {
        JSONObject jsonObject = new ResultJSONObject("01", "系统发生错误,请于管理员联系");
        return jsonObject;
    }

    @RequestMapping("/notfound")
    @SystemControllerLog(description = "无法找到资源文件")
    public @ResponseBody Object notfound() {
        JSONObject jsonObject = new ResultJSONObject("01", "无法找到资源文件");
        return jsonObject;
    }

    @RequestMapping("/repeatSubmit")
    @SystemControllerLog(description = "请勿重复提交，上一个请求已经处理")
    public @ResponseBody Object repeatSubmit() {
        JSONObject jsonObject = new ResultJSONObject("01", "请勿重复提交，上一个请求已经处理");
        return jsonObject;
    }

    @RequestMapping("/vaildateFailed")
    @SystemControllerLog(description = "验证失败，请检查token")
    public @ResponseBody Object vaildateFailed() {
        JSONObject jsonObject = new ResultJSONObject("01", "验证失败，请检查token");
        return jsonObject;
    }

}
