/**
 * @Description: 公用常量
 * @Date: 2021/7/14 13:42
 * @Pacakge: PACKAGE_NAME
 * @ClassName: Column
 * @Version: v1.0.0
 * @Author: ccc
 */

package simpleorm.core;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String name();
    String type() default "string";
    int length() default 20;
}