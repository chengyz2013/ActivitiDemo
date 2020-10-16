package com.demo.ActivitiDemo.bean.kafka;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.demo.ActivitiDemo.bean.kafka.MsgField.FillSide;

public abstract class MsgPiece {
	
	/** 消息域列表 */
    private List<MsgField> itemList = new LinkedList<MsgField>();
    
    public MsgPiece(MsgField[] items) {
        itemList = new LinkedList<MsgField>();
        for (int i = 0; i < items.length; i++) {
            itemList.add(items[i]);
        }
    }
    
    /**
     * 组消息
     * @author cheng.chen
     * @param charsetName
     * @return
     * @throws Exception
     */
    public byte[] pack(String charsetName) throws Exception {
        StringBuffer result = new StringBuffer();
        byte[] b = null;
        try {
            for (MsgField item : itemList) {
                PropertyDescriptor pd = new PropertyDescriptor(item.getName(), this.getClass());
                Method readMethod = pd.getReadMethod();
                Object gotVal = readMethod.invoke(this);
                String strFill = (gotVal == null ? "" : gotVal.toString());
                result.append(this.autoFill(strFill, item.getFillChar(), item.getFillSide(), item.getLength(), charsetName));
            }
            b = result.toString().getBytes(charsetName);
        } catch (Exception e) {
            throw new Exception("组消息出错：" + e.getMessage(), e);
        }
        
        return b;
    }
    
    /**
     * 解消息
     * @author Zhenwei.Zhang (2013-9-25)
     * @param msg
     * @param charsetName
     * @throws Exception
     */
    public void unPack(byte[] msg, String charsetName) throws Exception {
        if (msg.length != this.getLength()) {
            throw new Exception("解消息出错，消息长度不合法！");
        }
        
        int index = 0;
        try {
            for (MsgField item : itemList) {
                String value = new String(msg, index, item.getLength(), charsetName);
                value = this.getRealVal(value, item.getFillSide(), item.getFillChar());
                
                PropertyDescriptor pd = new PropertyDescriptor(item.getName(), this.getClass());
                Method setMethod = pd.getWriteMethod();
                if (pd.getPropertyType().equals(byte.class) || pd.getPropertyType().equals(Byte.class)) {
                    setMethod.invoke(this, Byte.parseByte(value));
                }else if (pd.getPropertyType().equals(short.class) || pd.getPropertyType().equals(Short.class)) {
                    setMethod.invoke(this, Short.parseShort(value));
                }else if (pd.getPropertyType().equals(int.class) || pd.getPropertyType().equals(Integer.class)) {
                    setMethod.invoke(this, Integer.parseInt(value));
                }else if (pd.getPropertyType().equals(long.class) || pd.getPropertyType().equals(Long.class)) {
                    setMethod.invoke(this, Long.parseLong(value));
                }else if (pd.getPropertyType().equals(float.class) || pd.getPropertyType().equals(Float.class)) {
                    setMethod.invoke(this, Float.parseFloat(value));
                }else if (pd.getPropertyType().equals(double.class) || pd.getPropertyType().equals(Double.class)) {
                    setMethod.invoke(this, Double.parseDouble(value));
                }else if (pd.getPropertyType().equals(char.class) || pd.getPropertyType().equals(Character.class)) {
                    setMethod.invoke(this, value.toCharArray()[0]);
                }else if (pd.getPropertyType().equals(boolean.class) || pd.getPropertyType().equals(Boolean.class)) {
                    setMethod.invoke(this, Boolean.valueOf(value));
                }else {
                    setMethod.invoke(this, value);
                }
                index += item.getLength();
            }
        } catch (Exception e) {
            throw new Exception("解消息出错：" + e.getMessage(), e);
        }
    }
    
    /**
     * 获得消息片长
     * @author Zhenwei.Zhang (2013-9-25)
     * @return
     */
    public int getLength() {
        int length = 0;
        for (MsgField item : itemList) {
            length += item.getLength();
        }
        return length;
    }
    
    /**
     * 获得消息域的真实值
     * @author Zhenwei.Zhang (2013-9-26)
     * @param value
     * @param side 填充位置
     * @param fillChar 填充字符
     * @return
     * @throws Exception
     */
    private String getRealVal(String value, FillSide side, char fillChar) throws Exception {
        char[] chars = value.toCharArray();
        if (FillSide.LEFT == side) {
            int index = 0;
            for (int i = 0; i < chars.length; i++) {
                if (fillChar == chars[i]) {
                    index ++;
                } else {
                    continue;
                }
            }
            return value.substring(index);
        } else if (FillSide.RIGHT == side) {
            int index = chars.length - 1;
            for (int i = index; i >= 0; i--) {
                if (fillChar == chars[i]) {
                    index --;
                } else {
                    continue;
                }
            }
            return value.substring(0, index + 1);
        } else {
            throw new Exception("无效的填充位置");
        }
    }
    
    /**
     * 格式化真实值为指定长度字符串，超长自动截取
     * @author Zhenwei.Zhang (2013-9-26)
     * @param value
     * @param fillChar 填充字符
     * @param side 填充位置
     * @param totalLen 域定义的长度
     * @param charsetName 编码
     * @return
     */
    private String autoFill(String value, char fillChar, FillSide side, int totalLen, String charsetName) {
        try {
            if (value == null) {
                value = "";
            }
            StringBuffer sbuffBuffer = new StringBuffer();
            // 长度超过指定长度，截取
            if (value.getBytes(charsetName).length > totalLen) {
                byte[] data = new byte[totalLen];
                System.arraycopy(value.getBytes(charsetName), 0, data, 0, totalLen);
                sbuffBuffer.append(new String(data, charsetName));
                return sbuffBuffer.toString();
            }
            if (side == FillSide.RIGHT) {
                sbuffBuffer.append(value);
            }
            for (int i = value.getBytes(charsetName).length; i < totalLen; i++) {
                sbuffBuffer.append(fillChar);
            }
            if (side == FillSide.LEFT) {
                sbuffBuffer.append(value);
            }
            return sbuffBuffer.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("系统不支持" + charsetName + "编码");
        }
    }
    
    

}
