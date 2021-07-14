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
