package com.text.updatademo.databean;

/**
 * 类名：com.text.updatademo.databean
 * 时间：2017/12/6 20:07
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @author Liu_xg
 */

public class ErrorDataBean {

    /**
     * msg : null
     * code : 200
     * data : 1 最大值
     */

    private Object msg;
    private int code;
    private Object data;

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
