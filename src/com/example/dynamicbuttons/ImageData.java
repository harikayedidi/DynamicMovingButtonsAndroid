package com.example.dynamicbuttons;
import android.graphics.Bitmap;

public class ImageData {
	private long id;
	private String bmp;
	private String name;
	private int btncount;
	private String content;
	private String posx;
	private String posy;
	public ImageData(String b, String n,int k, String c, String x, String y) {
		bmp = b;
		name = n;
		btncount = k;
		content = c;
		posx = x;
		posy = y;
	}

	public String getBitmap() {
		return bmp;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return btncount;
	}

	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPosx() {
		return posx;
	}

	public void setPosx(String posx) {
		this.posx = posx;
	}

	public String getPosy() {
		return posy;
	}

	public void setPosy(String posy) {
		this.posy = posy;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
}