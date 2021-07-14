### ORM实现流程

#### 一、创建数据库和表
这一步是在mysql数据库中实现的，我的mysql的数据库版本是5.5。

先创建一张关于学生基本信息的表，包括学生基本信息，包括学生id，姓名，年龄和生日。

```sql
-- 先登录进入数据库
-- 创建出dbpipe数据库和一张student的空表
create database if not exists dbpipe;
create table if not exists student(
    student_id int(11) not null  auto_increment,
    name varchar(32) not null,
    age int(11),
    birthday date,
    primary key (student_id)
);
```

#### 二、创建表所对应的类

空表创建完成以后需要创建该表所对应的类，目的是为了通过对象的形式来进行操作数据库中的表

```java
public class Student {
    private int studentNo;
    private String name;
    private int age;
    private Calendar birthday;
    public Student(){
        super();
    }
    public Student(int studentNo, String name, int age){
        this.studentNo = studentNo;
        this.name = name;
        this.age = age;
    }

    public int getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(int studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }
}
```

#### 三、关联表与类——使用注解的方式

为了能够实现ORM，我们的类需要和表关联起来，其中需要关联的信息包括：
- 表名和类名
- 表中的字段和类中的属性

为了能够使操作数据库的程序通过类获得表的名称，我们需要开发一个使用在类名的注解，这个注解将实现类名和表名的一一对应的关系。

```java
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    //这个Table注解只可以使用在类上，它有一个方法，用来获得和类名对应的表的名称。
    String name();
}
```

接下来，我们需要实现第二点，把表中的字段和类中的属性关联起来。在数据库中键，尤其是id，是有别于普通的字段的，据此我们可以把字段分为两种类型：id和普通的字段。

我们可以开发一个标识id的注解:

```Java
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    String name();
    String type() default "int";
    int length() default 20;
    int increment() default 1;
}
```

@Id注解包括四个方法，通过它们可以获得表中id的名称，id 的类型，以及id字段的长度，如果是id类型是int型的整数，可以获得自动增长量。

另外，对于一般的字段，我们也可以开发一个如下的注解标识:

```java
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String name();
    String type() default "string";
    int length() default 20;
}

```

@Column只包含有获得表的名称、类型和长度的三个方法。


#### 四、实现表和类的关联
目前已经创建了数据表、数据表对应的类、关联表与类的注解，接下来需要将注解配置到代码中去。


```Java
package orm.simpleorm.test;

import jdk.internal.vm.annotation.Stable;
import simpleorm.core.Column;
import simpleorm.core.Id;
import simpleorm.core.Table;

import java.util.Calendar;

/**
 * @Description: 公用常量
 * @Date: 2021/7/14 13:22
 * @Pacakge: orm.simpleorm.test
 * @ClassName: Student
 * @Version: v1.0.0
 * @Author: ccc
 */


@Table(name="student")
public class Student {
    @Id(name = "student_id")
    private int studentNo;
    @Column(name = "name")
    private String name;
    @Column(name = "age", type = "int")
    private int age;
    @Column(name = "birthday", type = "Calendar")
    private Calendar birthday;
    public Student(){
        super();
    }
    public Student(int studentNo, String name, int age){
        this.studentNo = studentNo;
        this.name = name;
        this.age = age;
    }

    public int getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(int studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }
}

```



#### 五、操作数据库——实现JdbcUtils工具类




为了连接以及操作数据库中的表，我们需要一个访问数据库的类。这个类中应当包含连接数据库以及操作数据库、释放资源的方法。

将这些方法提取出来写在jdbcUtils工具类中。

```java
public class JdbcUtils {

    //获取连接
    public static Connection getConn(){
        Connection conn =null;
        try{
            Class.forName("com.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://dbpipe", "root", "admin");
        } catch (Exception e){
            System.out.println("获取连接对象失败");
            e.printStackTrace();
        }
        return conn;
    }

    //更新操作
    public static int excuteUpdate(String sql, Object[] params){
        Connection connection = null;
        PreparedStatement pstmt = null;
        int result = -1;
        try{
            connection = getConn();
            pstmt = connection.prepareStatement(sql);
            for(int i = 0; i < params.length; i++){
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeUpdate();
        }catch (SQLException e){
            System.out.println("更新数据出现异常");
            System.out.println(e.getMessage());
        }finally {
            //释放连接
            release(pstmt, connection);
        }
        return result;
    }

    public static void release(Statement stmt, Connection conn){
        if(conn != null){
            try {
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            stmt = null;
        }
        if(conn != null){
            try{
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            conn = null;
        }
    }
}
```



#### 六、 简单的ORM框架

将一切准备工作做完之后可以开始实现简单的ORM框架。

实现以对象的形式往数据库中添加数据的方法

以对象的形式插入一条记录，基本操作如下：获取预编译的插入记录的SQL语句sql；获取预编译SQL语句中占位符对应的参数params；最后执行JdbcUtils.excuteUpdate(sql, params) ，就完成了以对象的形式向数据库中插入一条记录。

```java
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
```


#### 七、使用ORM框架（单元测试）

```java
package orm.simpleorm.test;

import org.junit.Test;
import simpleorm.core.SimpleDbPipe;

/**
 * @Description: 公用常量
 * @Date: 2021/7/14 15:09
 * @Pacakge: orm.simpleorm.test
 * @ClassName: SimpleDbPipeTest
 * @Version: v1.0.0
 * @Author: ccc
 */
public class SimpleDbPipeTest {
    private static SimpleDbPipe<Student> dbPipe = new SimpleDbPipe<Student>();

    @Test
    public void addTest(){
        Student student = null;
        for(int i = 0; i < 10; i++){
            student = new Student(i, "cjl_" +i, i *2);
            dbPipe.add(student);
        }
    }

    @Test
    public void updateTest(){
        Student student = null;
        for (int i = 0; i < 10; i ++){
            student = new Student(i, "new_cjl_" + i, i * 2);
            dbPipe.update(student);
        }
    }
}
```