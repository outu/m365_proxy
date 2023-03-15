package com.vinchin.m365proxy.apis;

public class BaseUtil {
    public enum ApiTypeEnum {
        EWSAPI(0),
        GRAPHAPI(1);
        private int apiType = 0;

        private ApiTypeEnum(int value) {
            apiType = value;
        }


        private int getCode() {
            return apiType;
        }

        public static ApiTypeEnum getApiEnumByApiType(int apiType) {
            for (ApiTypeEnum apiEnum : ApiTypeEnum.values()) {
                if (apiEnum.getCode() == apiType) {
                    return apiEnum;
                }
            }

            return null;
        }

    }


    public enum RegionEnum{
        GLOBALCLOUD(0),
        CHINACLOUD(1),
        LOCAL(100);

        private int region = 0;
        private RegionEnum(int value){
            region = value;
        }

        public int getCode(){
            return region;
        }

        public static RegionEnum getRegionEnumByRegion(int region){
            for(RegionEnum regionEnum : RegionEnum.values()){
                if(regionEnum.getCode() == region){
                    return regionEnum;
                }
            }

            return null;
        }
    }


    public enum ExchDataType{
        MESSAGE(0),
        CALENDAR(1),
        CONTACT(2),
        TASK(3);

        private int type = 0;

        private ExchDataType(int value){
            type = value;
        }

        public int getCode(){
            return type;
        }

        public static ExchDataType getExchDataType(int type){
            for (ExchDataType exchDataType : ExchDataType.values()){
                if (exchDataType.getCode() == type){
                    return exchDataType;
                }
            }

            return null;
        }
    }

    public enum ExchFolderNum{
        INBOX(0),
        DRAFTS(1),
        SENTITEMS(2),
        DELETEDITEMS(3),
        JUNKEMAIL(4),
        ARCHIVE(5),
        CONVERSATIONHISTORY(6),
        CALENDAR(100),
        CONTACTS(200),
        TASKS(300);

        private int num = 0;

        private ExchFolderNum(int value){
            num = value;
        }

        public int getNum(){
            return num;
        }

        public static ExchFolderNum getExchFolderNum(int num){
            for (ExchFolderNum exchFolderNum : ExchFolderNum.values()){
                if (exchFolderNum.getNum() == num){
                    return exchFolderNum;
                }
            }

            return null;
        }
    }
}