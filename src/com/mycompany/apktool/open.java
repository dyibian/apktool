package com.mycompany.apktool;

import android.content.*;
import android.net.*;
import java.io.*;
import java.nio.channels.*;
import java.text.*;

public class open
{
	Context c;
	Intent i,ii;
	public open(Context c){
		this.c=c;
		i=new Intent(Intent.ACTION_VIEW);
	}
	public void install(File path){
		ii=i;
		ii.setDataAndType(Uri.fromFile(path),"");
	}
	public static void fileChannelCopy(File s, File t)
	{
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try
		{
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        }
		catch (IOException e)
		{
            e.printStackTrace();
        }
		finally
		{
            try
			{
                fi.close();
                in.close();
                fo.close();
                out.close();
            }
			catch (IOException e)
			{
                e.printStackTrace();
            }
        }
    }
	public static String FormetFileSize(long fileS)
	{//转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		}
		else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "K";
		}
		else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		}
		else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
}
