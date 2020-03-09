package cn.com.vans.db;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 *
 *
 * @author vans
 * @date 2019/09/24
 */
public class CreateExcel {

    /**查询表字段的基本信息*/
    public static final String QUERY_TABLE_INFO_SQL="SELECT c.table_name, c.column_Name, t.table_comment , c.data_Type, c.is_Nullable, c.column_Type, c.column_Comment, c.column_key AS prikey " +
            "FROM information_schema.COLUMNS c ,information_schema.TABLES t WHERE c.TABLE_SCHEMA =t.TABLE_SCHEMA and c.TABLE_NAME=t.TABLE_NAME and t.TABLE_TYPE={1} and c.TABLE_SCHEMA={0}";


    /**查询部分的表的信息*/
    public static final String QUERY_TABLE_SQL_1="SELECT c.table_name, c.column_Name, t.table_comment , c.data_Type, c.is_Nullable, c.column_Type, c.column_Comment, c.column_key AS prikey " +
            "FROM information_schema.COLUMNS c ,information_schema.TABLES t WHERE c.TABLE_SCHEMA =t.TABLE_SCHEMA and c.TABLE_NAME=t.TABLE_NAME and t.TABLE_TYPE={1} and c.TABLE_SCHEMA={0} and t.table_Name in ({2})";

    public static final String QUERY_TABLE_INFO_SQL_ORCL="SELECT ut.table_name, us.column_Name, c.comments as table_comment, us.data_Type,  us.nullable as is_Nullable, '' as column_Type , '' as prikey ,"+
            "(select comments from  user_col_comments where column_name =us.column_name and table_name = us.table_name) as  column_Comment FROM  user_tab_comments c, " +
            " user_tables ut, user_tab_columns us WHERE   ut.table_name = c.table_name AND us.table_name =ut.table_name order by ut.table_name";




    public static void main(String[] args){
        Properties properties =AppConfig.getProperties("jdbc.properties");
        String dbType=properties.getProperty("dbType");
        String tableSchema=properties.getProperty("queryDbName");
        String  path =System.getProperty("user.dir");
        System.out.println("获取资源文件路径位置："+path +" ; tableSchema:"+tableSchema);
        String tableNames = properties.getProperty("queryTableNams");
        String format ;
        if(StringUtils.isNotEmpty(dbType) && "oracle".equals(dbType)){
            format=QUERY_TABLE_INFO_SQL_ORCL;
        }else{
            if(StringUtils.isEmpty(tableNames)){
                format=MessageFormat.format(QUERY_TABLE_INFO_SQL, new Object[]{"'"+tableSchema+"'","'BASE TABLE'"});
            }else{

                format=MessageFormat.format(QUERY_TABLE_SQL_1, new Object[]{"'"+tableSchema+"'","'BASE TABLE'",tableNames(tableNames)});
            }
        }

        System.out.println("执行SQL语句："+format);
        List<TableInfoModel> tableInfoModels = tableInfoModelList(format);
        System.out.println("SQL执行完毕，共查询出"+tableInfoModels.size()+"个字段");
        createExcel(tableInfoModels,tableSchema,System.getProperty("user.dir"));

    }




    public static String tableNames(String tableNames){
        String[] split = tableNames.split(",");
        int tableNums =split.length;
        String newTableNames="";
        for (int i = 0; i < tableNums; i++) {
            if(i==(tableNums-1)){
                newTableNames+="'"+split[i]+"'";
            }else{
                newTableNames+="'"+split[i]+"',";
            }
        }
        return newTableNames;
    }


    /**
     * 根据查询的List对象信息进行生产Excel
     * @param tableInfoModelList
     */
    private static  void createExcel( List <TableInfoModel> tableInfoModelList,String excelFileName,String path) {
        String []title = {"table_name","table_comment","column_Name","data_Type","is_Nullable","column_Type","column_Comment","prikey"};
        //创建HSSF工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建一个Sheet页
        HSSFSheet sheet = workbook.createSheet();
        //创建第一行（一般是表头）
        HSSFRow row0 = sheet.createRow(0);
        //创建列
        HSSFCell cell = null;
        //设置表头
        for (int i = 0; i <title.length ; i++) {
            cell=row0.createCell(i);
            cell.setCellValue(title[i]);
        }
        //填充20行数据
        for (int i = 1; i <tableInfoModelList.size(); i++) {
            TableInfoModel  tableInfoModel = tableInfoModelList.get(i);
            HSSFRow row =sheet.createRow(i);
            HSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(tableInfoModel.getTableName());
            HSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(tableInfoModel.getTableComment());
            HSSFCell cell3 = row.createCell(2);
            cell3.setCellValue(tableInfoModel.getColumnName());
            HSSFCell cell4 = row.createCell(3);
            cell4.setCellValue(tableInfoModel.getDataType());
            HSSFCell cell5 = row.createCell(4);
            cell5.setCellValue(tableInfoModel.getIsNullAble());
            HSSFCell cell6 = row.createCell(5);
            cell6.setCellValue(tableInfoModel.getColumnType());
            HSSFCell cell7 = row.createCell(6);
            cell7.setCellValue(tableInfoModel.getColumnComment());
            HSSFCell cell8 = row.createCell(7);
            if("PRI".equals(tableInfoModel.getPrikey())){
                cell8.setCellValue("TRUE");
            }else{
                cell8.setCellValue("FALSE");
            }
        }
        //保存到本地
        File file = new File(path+"/"+excelFileName+".xls");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream !=null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 获取所有的表的字段的基本信息
     * @return
     */
    public  static List <TableInfoModel> tableInfoModelList(String dbsql){
        Connection connection ;
        Statement statement;
        ResultSet rs;
        List<TableInfoModel> tableInfoModels = new ArrayList<>();
        try {
            connection= JdbcUtils.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(dbsql);
            while(rs.next()){
                String tableName =rs.getString("table_name");
                String columnName=rs.getString("column_Name");
                String dataType=rs.getString("data_Type");
                String isNullAble=rs.getString("is_Nullable");
                String columnType=rs.getString("column_Type");
                String columnComment=rs.getString("column_Comment");
                String prikey=rs.getString("prikey");
                String tableComment=rs.getString("table_comment");
                TableInfoModel tableInfoModel = new TableInfoModel(tableName,tableComment,columnName,dataType,isNullAble,columnType,columnComment,prikey);
                tableInfoModels.add(tableInfoModel);
            }
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableInfoModels;
    }




}
