package cn.com.vans.db;

/**
 * TableInfoModel class
 *
 * @author vans
 * @date 2019/09/23
 */
public class TableInfoModel {
    private String tableName;
    private String columnName;
    private String dataType;
    private String isNullAble;
    private String columnType;
    private String columnComment;
    private String prikey;
    private String tableComment;

    public TableInfoModel() {
    }

    public TableInfoModel(String tableName,String tableComment, String columnName, String dataType, String isNullAble, String columnType, String columnComment, String prikey) {
        this.tableName = tableName;
        this.tableComment=tableComment;
        this.columnName = columnName;
        this.dataType = dataType;
        this.isNullAble = isNullAble;
        this.columnType = columnType;
        this.columnComment = columnComment;
        this.prikey = prikey;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getIsNullAble() {
        return isNullAble;
    }

    public void setIsNullAble(String isNullAble) {
        this.isNullAble = isNullAble;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public String getPrikey() {
        return prikey;
    }

    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }
}
