package simpleorm.core;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @Description: 公用常量
 * @Date: 2021/7/14 14:41
 * @Pacakge: simpleorm.core
 * @ClassName: SimpleDbPipe
 * @Version: v1.0.0
 * @Author: ccc
 */
public class SimpleDbPipe<E> {
    //添加对象到数据库
    public int add(E element){
        if(element == null){
            throw new IllegalArgumentException("插入的元素为空");
        }
        Class clazz = element.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = clazz.getDeclaredFields();
        if(fields == null || fields.length == 0) {
            throw new RuntimeException(element + "没有属性");
        }
        String sql = getInsertSql(tableName, fields.length);
        Object[] params = getSqlParams(element, fields);
        System.out.println("InsertSql = " +sql);
        System.out.println(Arrays.toString(params));
        return JdbcUtils.excuteUpdate(sql, params);
    }

    //根据对象获取sql语句的参数
    private Object[] getSqlParams(E element, Field[] fields){
        Object[] params = new Object[fields.length];
        for(int i = 0; i < fields.length; i++){
            fields[i].setAccessible(true);
            try{
                params[i] = fields[i].get(element);
            }catch (IllegalAccessException e){
                System.out.println(e.getMessage());
                System.out.println("获取" + element + "的属性值失败");
            }
        }
        return params;
    }

    //获取插入对象的sql语句
    private String getInsertSql(String tableName, int length){
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tableName).append(" values(");
        for (int i = 0; i < length; i ++)  // 添加参数占位符?
            sql.append("?,");
        sql.deleteCharAt(sql.length()-1);
        sql.append(")");
        return sql.toString();
    }

    //根据值对象的注解获取其对应的表的名称
    private String getTableName(Class<E> clazz){
        boolean existTableAnno = clazz.isAnnotationPresent(Table.class);
        if (!existTableAnno)
            throw new RuntimeException(clazz + " 没有Table注解.");
        Table tableAnno = (Table)clazz.getAnnotation(Table.class);
        return tableAnno.name();
    }

    //更新对象到数据库
    public int update(E element) {
        if (element == null)
            throw new IllegalArgumentException("插入的元素为空.");
        Class clazz = element.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0)
            throw new RuntimeException(element + "没有属性.");
        Object[] params = new Object[fields.length];
        String sql = getUpdateSqlAndParams(element, params);
        System.out.println("update sql = " + sql);
        System.out.println("params = " + Arrays.toString(params));
        return JdbcUtils.excuteUpdate(sql, params);
    }

    private String getUpdateSqlAndParams(E element, Object[] params) {
        Class clazz = element.getClass();
        String tableName = getTableName(clazz);
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder updateSql = new StringBuilder();
        updateSql.append("update ").append(tableName).append(" set ");
        String idName = "";
        int index = 0; // 记录参数的位置
        for (int i = 0; i < fields.length; i ++){
            fields[i].setAccessible(true);
            // 找到id对应的列名和值
            if (fields[i].isAnnotationPresent(Id.class)){
                idName = fields[0].getAnnotation(Id.class).name();
                try {
                    params[params.length-1] = fields[i].get(element);  // id作为update sql 的最后一个参数
                    if (params[params.length-1] == null)
                        throw new RuntimeException(element + "没有Id属性!");
                } catch (IllegalAccessException e) {
                    System.out.println(e.getMessage());
                    System.out.println("获取" + element + "的属性值失败！");
                }
            }
            boolean isPresent = fields[i].isAnnotationPresent(Column.class);
            if (isPresent) {
                Column column = fields[i].getAnnotation(Column.class);
                String columnName = column.name();
                updateSql.append(" ").append(columnName).append( " = ? ,");
                // update sql 的参数
                try {
                    params[index++] = fields[i].get(element);  // 添加参数到数组，并更新下标
                } catch (IllegalAccessException e) {
                    System.out.println(e.getMessage());
                    System.out.println("获取" + element + "的属性值失败！");
                }
            }
        }
        updateSql.deleteCharAt(updateSql.length()-1);
        updateSql.append("where ").append(idName).append(" = ?");
        return updateSql.toString();
    }
}
