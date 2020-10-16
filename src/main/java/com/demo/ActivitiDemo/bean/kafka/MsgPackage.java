package com.demo.ActivitiDemo.bean.kafka;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public abstract class MsgPackage {
	/** 消息包的消息片数组 */
    String[] pieces = null;
    
    public MsgPackage(String... pieName) {
        pieces = pieName;
    }
    
    /**
     * 组包
     * @author Zhenwei.Zhang (2013-9-26)
     * @param charsetName
     * @return
     * @throws Exception
     */
    public byte[] pack(String charsetName) throws Exception {
        int index = 0;
        byte[] result = new byte[this.getLength()];
        for (String p : pieces) {
            PropertyDescriptor pd = new PropertyDescriptor(p, this.getClass());
            Method getterMethod = pd.getReadMethod();
            MsgPiece piece = (MsgPiece)getterMethod.invoke(this);
            if (piece == null) { //属性为空则该属性组空串
                piece = (MsgPiece) pd.getPropertyType().newInstance();
            }
            byte[] t = piece.pack(charsetName);
            System.arraycopy(t, 0, result, index, t.length);
            index += t.length;
        }
        return result;
    }
    
    /**
     * 解包
     * @author Zhenwei.Zhang (2013-9-26)
     * @param msg
     * @param charsetName
     * @throws Exception
     */
    public void unPack(byte[] msg, String charsetName) throws Exception {
        if (msg.length != this.getLength()) {
            throw new Exception("解消息出错，消息包长度不合法！");
        }

        int index = 0;
        for (String p : pieces) { // 创建一个新的属性对象，解包，赋值
            PropertyDescriptor pd = new PropertyDescriptor(p, this.getClass());
            Method writeMethod = pd.getWriteMethod();
            MsgPiece piece = (MsgPiece) pd.getPropertyType().newInstance();
            byte[] t = new byte[piece.getLength()];
            System.arraycopy(msg, index, t, 0, t.length);
            piece.unPack(t, charsetName);
            writeMethod.invoke(this, piece);
            index += t.length;
        }
    }
    
    /**
     * 获得消息包长度
     * @author Zhenwei.Zhang (2013-9-26)
     * @return
     * @throws Exception
     */
    public int getLength() throws Exception {
        int length = 0;
        try {
            for (String p : pieces) {
                PropertyDescriptor pd = new PropertyDescriptor(p, this.getClass());
                Method getterMethod = pd.getReadMethod();
                MsgPiece piece = (MsgPiece)getterMethod.invoke(this);
                if (piece == null) {
                    piece = (MsgPiece) pd.getPropertyType().newInstance();
                }
                length += piece.getLength();
            }
        } catch (Exception e) {
            throw new Exception("获得消息包长度错误：" + e.getMessage(), e);
        }
        return length;
    }

}
