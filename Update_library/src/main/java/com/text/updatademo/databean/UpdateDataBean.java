package com.text.updatademo.databean;

import java.util.List;

/**
 * 类名：com.text.updatademo.databean
 * 时间：2017/12/6 10:06
 * 描述：更新实体类
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @author Liu_xg
 */

public class UpdateDataBean {
    /**
     * msg : 获取更新成功
     * code : 1
     * data : {"descs":[{"desc":"1.增加系统闪退后的错误日志收集"},{"desc":"2.将天气信息缓存起来，快速加载"},{"desc":"3.修改功能介绍里面每个功能模块的具体介绍"},{"desc":"4.修改区块查看里面的部分显示问题"},{"desc":null},{"desc":"4.修改区块查看里面的部分显示问题"},{"desc":"4.修改区块查看里面的部分显示问题"}],"create_time":1511489062292,"fileSize":18125327,"file_id":"000000005febc363015febc363eb0000","id":"000000005febc363015febc4c5920001","version":"2.0.1.1711240957"}
     */

    private String msg;
    private int code;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * descs : [{"desc":"1.增加系统闪退后的错误日志收集"},{"desc":"2.将天气信息缓存起来，快速加载"},{"desc":"3.修改功能介绍里面每个功能模块的具体介绍"},{"desc":"4.修改区块查看里面的部分显示问题"},{"desc":null},{"desc":"4.修改区块查看里面的部分显示问题"},{"desc":"4.修改区块查看里面的部分显示问题"}]
         * create_time : 1511489062292
         * fileSize : 18125327
         * file_id : 000000005febc363015febc363eb0000
         * id : 000000005febc363015febc4c5920001
         * version : 2.0.1.1711240957
         */

        private long create_time;
        private int fileSize;
        private String file_id;
        private String id;
        private String version;
        private List<DescsBean> descs;

        public long getCreate_time() {
            return create_time;
        }

        public void setCreate_time(long create_time) {
            this.create_time = create_time;
        }

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public String getFile_id() {
            return file_id;
        }

        public void setFile_id(String file_id) {
            this.file_id = file_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<DescsBean> getDescs() {
            return descs;
        }

        public void setDescs(List<DescsBean> descs) {
            this.descs = descs;
        }

        public static class DescsBean {
            /**
             * desc : 1.增加系统闪退后的错误日志收集
             */

            private String desc;

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }
        }
    }


}
