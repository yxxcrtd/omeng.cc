package com.shanjin.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/17
 * @desc cplan 交互 需要的 json串对象
 * @see
 */
public class ResultVo<T> implements Serializable {

    private static final long serialVersionUID = -222285227025872397L;

    private String retcode;
    private String retmsg;
    private T retdata;

    public ResultVo(){}

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public T getRetdata() {
        return retdata;
    }

    public void setRetdata(T retdata) {
        this.retdata = retdata;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
