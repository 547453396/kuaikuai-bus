package cn.kuaikuai.trip.model.bean.path;

import java.util.List;

public class LineListBean {


    /**
     * msg : ok
     * code : 200
     * data : {"total":1,"pageSize":10,"value":[{"routes":null,"line":{"srcGeoCode":"wtm7z","srcName":"江陵路地铁站","dstName":"阿里、网易、西可","dstAdcode":"330108","destination":"{'id':'B0FFG083SL','name':'阿里巴巴滨江园区','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.190371,30.189602','address':'网商路699号','typecode':'120100','city':'123'}","srcDistrict":"浙江省杭州市滨江区","dstAddress":"网商路699号","updateTime":null,"source":"{'id':'BX10013591','name':'江陵路地铁站A口','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.216794,30.209829','address':'1号线','typecode':'150501','city':'123'}","srcAddress":"1号线","srcAdcode":"330108","srcLocationLongitude":120.216794,"createTime":"2019-05-06 12:15:17","price":0,"dstLocationLongitude":120.190371,"dstDistrict":"浙江省杭州市滨江区","dstLocationLatitude":30.189602,"id":1,"srcLocationLatitude":30.209829,"dstGeoCode":"wtm7w","status":"open"}}],"pageNum":1}
     */
    private String msg;
    private String code;
    private DataEntity data;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public DataEntity getData() {
        return data;
    }

    public class DataEntity {
        /**
         * total : 1
         * pageSize : 10
         * value : [{"routes":null,"line":{"srcGeoCode":"wtm7z","srcName":"江陵路地铁站","dstName":"阿里、网易、西可","dstAdcode":"330108","destination":"{'id':'B0FFG083SL','name':'阿里巴巴滨江园区','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.190371,30.189602','address':'网商路699号','typecode':'120100','city':'123'}","srcDistrict":"浙江省杭州市滨江区","dstAddress":"网商路699号","updateTime":null,"source":"{'id':'BX10013591','name':'江陵路地铁站A口','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.216794,30.209829','address':'1号线','typecode':'150501','city':'123'}","srcAddress":"1号线","srcAdcode":"330108","srcLocationLongitude":120.216794,"createTime":"2019-05-06 12:15:17","price":0,"dstLocationLongitude":120.190371,"dstDistrict":"浙江省杭州市滨江区","dstLocationLatitude":30.189602,"id":1,"srcLocationLatitude":30.209829,"dstGeoCode":"wtm7w","status":"open"}}]
         * pageNum : 1
         */
        private int total;
        private int pageSize;
        private List<ValueEntity> value;
        private int pageNum;

        public void setTotal(int total) {
            this.total = total;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public void setValue(List<ValueEntity> value) {
            this.value = value;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getTotal() {
            return total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public List<ValueEntity> getValue() {
            return value;
        }

        public int getPageNum() {
            return pageNum;
        }

        public class ValueEntity {
            /**
             * routes : null
             * line : {"srcGeoCode":"wtm7z","srcName":"江陵路地铁站","dstName":"阿里、网易、西可","dstAdcode":"330108","destination":"{'id':'B0FFG083SL','name':'阿里巴巴滨江园区','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.190371,30.189602','address':'网商路699号','typecode':'120100','city':'123'}","srcDistrict":"浙江省杭州市滨江区","dstAddress":"网商路699号","updateTime":null,"source":"{'id':'BX10013591','name':'江陵路地铁站A口','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.216794,30.209829','address':'1号线','typecode':'150501','city':'123'}","srcAddress":"1号线","srcAdcode":"330108","srcLocationLongitude":120.216794,"createTime":"2019-05-06 12:15:17","price":0,"dstLocationLongitude":120.190371,"dstDistrict":"浙江省杭州市滨江区","dstLocationLatitude":30.189602,"id":1,"srcLocationLatitude":30.209829,"dstGeoCode":"wtm7w","status":"open"}
             */
            private String routes;
            private LineEntity line;

            public void setRoutes(String routes) {
                this.routes = routes;
            }

            public void setLine(LineEntity line) {
                this.line = line;
            }

            public String getRoutes() {
                return routes;
            }

            public LineEntity getLine() {
                return line;
            }

            public class LineEntity {
                /**
                 * srcGeoCode : wtm7z
                 * srcName : 江陵路地铁站
                 * dstName : 阿里、网易、西可
                 * dstAdcode : 330108
                 * destination : {'id':'B0FFG083SL','name':'阿里巴巴滨江园区','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.190371,30.189602','address':'网商路699号','typecode':'120100','city':'123'}
                 * srcDistrict : 浙江省杭州市滨江区
                 * dstAddress : 网商路699号
                 * updateTime : null
                 * source : {'id':'BX10013591','name':'江陵路地铁站A口','district':'浙江省杭州市滨江区','adcode':'330108','location':'120.216794,30.209829','address':'1号线','typecode':'150501','city':'123'}
                 * srcAddress : 1号线
                 * srcAdcode : 330108
                 * srcLocationLongitude : 120.216794
                 * createTime : 2019-05-06 12:15:17
                 * price : 0
                 * dstLocationLongitude : 120.190371
                 * dstDistrict : 浙江省杭州市滨江区
                 * dstLocationLatitude : 30.189602
                 * id : 1
                 * srcLocationLatitude : 30.209829
                 * dstGeoCode : wtm7w
                 * status : open
                 */
                private String srcGeoCode;
                private String srcName;
                private String dstName;
                private String dstAdcode;
                private String destination;
                private String srcDistrict;
                private String dstAddress;
                private String updateTime;
                private String source;
                private String srcAddress;
                private String srcAdcode;
                private double srcLocationLongitude;
                private String createTime;
                private int price;
                private double dstLocationLongitude;
                private String dstDistrict;
                private double dstLocationLatitude;
                private int id;
                private double srcLocationLatitude;
                private String dstGeoCode;
                private String status;

                public void setSrcGeoCode(String srcGeoCode) {
                    this.srcGeoCode = srcGeoCode;
                }

                public void setSrcName(String srcName) {
                    this.srcName = srcName;
                }

                public void setDstName(String dstName) {
                    this.dstName = dstName;
                }

                public void setDstAdcode(String dstAdcode) {
                    this.dstAdcode = dstAdcode;
                }

                public void setDestination(String destination) {
                    this.destination = destination;
                }

                public void setSrcDistrict(String srcDistrict) {
                    this.srcDistrict = srcDistrict;
                }

                public void setDstAddress(String dstAddress) {
                    this.dstAddress = dstAddress;
                }

                public void setUpdateTime(String updateTime) {
                    this.updateTime = updateTime;
                }

                public void setSource(String source) {
                    this.source = source;
                }

                public void setSrcAddress(String srcAddress) {
                    this.srcAddress = srcAddress;
                }

                public void setSrcAdcode(String srcAdcode) {
                    this.srcAdcode = srcAdcode;
                }

                public void setSrcLocationLongitude(double srcLocationLongitude) {
                    this.srcLocationLongitude = srcLocationLongitude;
                }

                public void setCreateTime(String createTime) {
                    this.createTime = createTime;
                }

                public void setPrice(int price) {
                    this.price = price;
                }

                public void setDstLocationLongitude(double dstLocationLongitude) {
                    this.dstLocationLongitude = dstLocationLongitude;
                }

                public void setDstDistrict(String dstDistrict) {
                    this.dstDistrict = dstDistrict;
                }

                public void setDstLocationLatitude(double dstLocationLatitude) {
                    this.dstLocationLatitude = dstLocationLatitude;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public void setSrcLocationLatitude(double srcLocationLatitude) {
                    this.srcLocationLatitude = srcLocationLatitude;
                }

                public void setDstGeoCode(String dstGeoCode) {
                    this.dstGeoCode = dstGeoCode;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getSrcGeoCode() {
                    return srcGeoCode;
                }

                public String getSrcName() {
                    return srcName;
                }

                public String getDstName() {
                    return dstName;
                }

                public String getDstAdcode() {
                    return dstAdcode;
                }

                public String getDestination() {
                    return destination;
                }

                public String getSrcDistrict() {
                    return srcDistrict;
                }

                public String getDstAddress() {
                    return dstAddress;
                }

                public String getUpdateTime() {
                    return updateTime;
                }

                public String getSource() {
                    return source;
                }

                public String getSrcAddress() {
                    return srcAddress;
                }

                public String getSrcAdcode() {
                    return srcAdcode;
                }

                public double getSrcLocationLongitude() {
                    return srcLocationLongitude;
                }

                public String getCreateTime() {
                    return createTime;
                }

                public int getPrice() {
                    return price;
                }

                public double getDstLocationLongitude() {
                    return dstLocationLongitude;
                }

                public String getDstDistrict() {
                    return dstDistrict;
                }

                public double getDstLocationLatitude() {
                    return dstLocationLatitude;
                }

                public int getId() {
                    return id;
                }

                public double getSrcLocationLatitude() {
                    return srcLocationLatitude;
                }

                public String getDstGeoCode() {
                    return dstGeoCode;
                }

                public String getStatus() {
                    return status;
                }
            }
        }
    }
}
