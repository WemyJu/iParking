package com.bubble_gray.iparkingapp;


public class DataPic implements java.io.Serializable{
    public int picSize;
    public byte[] bitmap;
    public DataPic()
    {
    }
    public void setSize(int size)
    {
        picSize=size;
        bitmap=new byte[picSize];
    }
}
