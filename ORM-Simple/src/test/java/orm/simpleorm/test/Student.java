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
