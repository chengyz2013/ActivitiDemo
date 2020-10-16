package com.demo.ActivitiDemo.bean.kafka;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class TestPackage extends MsgPackage{
	public TestPackage() {
        super("t1", "t2");
    }
    
    private TestHead t1;
    
    private TestBody t2;

    public TestHead getT1() {
        return t1;
    }

    public void setT1(TestHead t1) {
        this.t1 = t1;
    }

    public TestBody getT2() {
        return t2;
    }

    public void setT2(TestBody t2) {
        this.t2 = t2;
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException, Exception {
        TestHead head = new  TestHead();
        head.setName("name");
        head.setAmt(12.121);
        head.setSex(false);
        head.setURL("http://asdsaaaaaaaaaaaaaaaaaaaaaaa");
        head.setUAge(111);
        
        TestBody body = new TestBody();
        body.setContent("content");
        
        TestPackage packagee = new TestPackage();
        packagee.setT1(head);
        packagee.setT2(body);
        
        System.out.println(Arrays.toString(packagee.pack("GBK")));
        
        System.out.println(new String(packagee.pack("GBK"), "GBK"));
        
        TestPackage packagee2 = new TestPackage();
        String str = "name      000000true0000000111http://asd000012.121content             ";
        packagee2.unPack(str.getBytes("GBK"), "GBK");
        System.out.println(packagee2.getT1().isSex());
    }


}
