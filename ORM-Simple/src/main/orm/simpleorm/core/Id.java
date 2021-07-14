/**
 * @Description: 公用常量
 * @Date: 2021/7/14 13:40
 * @Pacakge: PACKAGE_NAME
 * @ClassName: Id
 * @Version: v1.0.0
 * @Author: ccc
 */

package simpleorm.core;
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