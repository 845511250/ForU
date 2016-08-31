package com.forudesigns.foru.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

public class SmallImage {
	public String smallimagepath;
	public Bitmap bitmap;
	
	
	public SmallImage(String imagepath,Context context) {
		try {
			int size=1000;

			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;// 不读入内存，至获取长宽
			Bitmap bm = BitmapFactory.decodeFile(imagepath, option);
			int oldwidth = option.outWidth;
			int oldheight = option.outHeight;
			option.inJustDecodeBounds = false;// 以下decode读入内存中
			option.inSampleSize = 1;// 分辨率缩小为几倍

			if (oldwidth > size || oldheight > size) {
				if (oldwidth >= oldheight)

					option.inSampleSize = (int) (oldheight / size);
				else
					option.inSampleSize = (int) (oldwidth / size);
			}
			bm = BitmapFactory.decodeFile(imagepath, option);
			//
			FileOutputStream out = new FileOutputStream(new File(context.getFilesDir().getPath()+"/1.jpg"));
			bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
			out.flush();
			out.close();
		
			//////////////////////////////////////////////////////////////////
			this.smallimagepath = context.getFilesDir().getPath()+"/1.jpg";
			
			this.bitmap = bm;
		} catch (Exception e) {
			this.smallimagepath = "";
		}
	}


	/*
	public Bitmap getSquareImage(Bitmap bitmap) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();

		int width = w > h ? h : w;// 裁切后所取的正方形区域边长

		int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
		int retY = w > h ? 0 : (h - w) / 2;

		return Bitmap.createBitmap(bitmap, retX, retY, width, width);
	}*/
}
