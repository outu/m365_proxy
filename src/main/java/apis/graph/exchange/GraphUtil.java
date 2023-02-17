package apis.graph.exchange;

public class GraphUtil {
    public static String ewsIdConvertToGraphId(String ewsId){
        String graphId = "";

        graphId = ewsId.replace("-", "/");
        graphId = graphId.replace("_", "+");

        return graphId;
    }


    public static String graphIdConvertToEwsId(String ewsId){
        String graphId = "";

        graphId = ewsId.replace("/", "-");
        graphId = graphId.replace("+", "_");

        return graphId;
    }
}
