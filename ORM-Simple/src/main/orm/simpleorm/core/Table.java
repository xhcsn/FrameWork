/**
 * @Description: 公用常量
 * @Date: 2021/7/14 13:33
 * @Pacakge: PACKAGE_NAME
 * @ClassName: Table
 * @Version: v1.0.0
 * @Author: ccc
 */
package simpleorm.core;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    //这个Table注解只可以使用在类上，它有一个方法，用来获得和类名对应的表的名称。
    String name();
}

