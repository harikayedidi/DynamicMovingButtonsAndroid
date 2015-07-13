package com.example.dynamicbuttons;


//import android.support.v7.app.ActionBarActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
//import android.content.SharedPreferences.Editor;
//import android.text.Editable;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.LinearLayout;


@SuppressLint("NewApi") public class MainActivity extends Activity implements OnClickListener{
	 ImageView viewImage;
	 Button b,save,retrive;
	 int count=0,tchcount=0;
	 Boolean x=false;    
	 float initialX,initialY;
	 private DBhelper DbHelper;
	 String value="";
	 String clicks="";
	 Bitmap bitmap;
	 List<ImageData> mListData;
	 int mSelectNo;
	 long mDataId = -1;
	 String mFilePath = "";
	 public final static String FILE_SAVE_PATH = android.os.Environment
             .getExternalStorageDirectory()
             + File.separator
             + "Phoenix" + File.separator + "default";
	@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.activity_main);
    DbHelper = new DBhelper(this);
    b=(Button)findViewById(R.id.btnSelectPhoto);
    viewImage=(ImageView) findViewById(R.id.viewImage);
    View buttonAdd = findViewById(R.id.add);
    buttonAdd.setOnClickListener(this);
    save=(Button)findViewById(R.id.save);
    save.setOnClickListener(new  OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			saveData();
		}
	});
    retrive=(Button)findViewById(R.id.retrieve);
    retrive.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			DbHelper.open();
			mListData = DbHelper.retriveEmpDetails();
	  		DbHelper.close();
	  		if (mListData.size() == 0){
	  			Toast.makeText(MainActivity.this, "No task" , Toast.LENGTH_LONG).show();
				return;
	  		}
			String[] names = new String[mListData.size()];
			for (int i= 0; i<mListData.size(); i++){
				names[i] = mListData.get(i).getName();
			}
			
			mSelectNo = 0;
			AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
		    ab.setTitle("Title");
		    ab.setSingleChoiceItems(names, 0,
		        new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	mSelectNo = whichButton;
		        }
		        }).setPositiveButton("Ok",
		        new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	dialog.cancel();
		            ImageData data = mListData.get(mSelectNo);
		            showDatas (data);
		            
		        }
		        }).setNegativeButton("Cancel",
		        new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            dialog.cancel();
		        }
		        });
		    ab.show();
		}

		
	});
    View buttonRemove = findViewById(R.id.remove);
    buttonRemove.setOnClickListener(this);
    
    b.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectImage();
        }
    });
    
    findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mDataId >= 0){
				DbHelper.open();
		  		DbHelper.deleteEmp(mDataId);
		  		DbHelper.close();
			}
			initDatas();
			
		}
	});
    
    File file = new File(FILE_SAVE_PATH);
    if (!file.exists()){
    	file.mkdirs();
    }
}
	String x_arrays;
	String y_arrays;
	public void saveData() {
		// TODO Auto-generated method stub
		clicks = "";
		x_arrays = "";
		y_arrays = "";
		EditText vi=new EditText(MainActivity.this);
		if(count>0)
		{
			
			for (int i = 1; i <=count; i++) {
				vi=(EditText)findViewById(10+i);
				if (vi.getText().toString().equals("")){
					clicks = clicks+" "+"@";
				}else{
					clicks = clicks+vi.getText().toString()+"@";
				}

				x_arrays = x_arrays + String.valueOf(vi.getX() - 100) + "@";
				y_arrays = y_arrays + String.valueOf(vi.getY()) + "@";
			}
			
		}
		if (bitmap == null){
			Toast.makeText(MainActivity.this, "Select Photo" , Toast.LENGTH_LONG).show();
			return;
		}
		if (mDataId >= 0){
			ImageData employee_One = new ImageData(mFilePath, "", count, clicks, x_arrays, y_arrays);
			employee_One.setId(mDataId);
        	DbHelper.open();
	  		DbHelper.updateEmpDetails(employee_One);
	  		DbHelper.close();
	  		Toast.makeText(MainActivity.this, "successed" , Toast.LENGTH_LONG).show();
	  		initDatas();
		}else{
			final EditText input = new EditText(MainActivity.this);
			new AlertDialog.Builder(MainActivity.this)
				    .setTitle("Enter Image Name To Store")
				    .setMessage("")
				    .setView(input)
				    .setCancelable(false)
				    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            String name = input.getText().toString();
				            if (!name.equals("")){
				            	try {
					            	ImageData employee_One = new ImageData(mFilePath, name, count, clicks, x_arrays, y_arrays);
					            	DbHelper.open();
							  		mDataId = DbHelper.insertEmpDetails(employee_One);
							  		DbHelper.close();
							  		if (mDataId >= 0){
								  		Toast.makeText(MainActivity.this, "successed" , Toast.LENGTH_LONG).show();
								  		initDatas();
							  		}else{
							  			Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_LONG).show();
							  		}
				            	}catch(Exception e){
				            		Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
				            	}
				            	dialog.cancel();
				            }else{
				            	
				            }
				        }
				    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            dialog.cancel();
				        }
				    }).show();
		}

	}
	public void initDatas(){
		RelativeLayout myLayout = (RelativeLayout)findViewById(R.id.rl);
		int childCnt = myLayout.getChildCount();
		for (int i= 1; i< childCnt; i++){
			myLayout.removeViewAt(1);
		}
		if (bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		mFilePath = "";
		viewImage.setImageBitmap(null);
		count = 0;
		mDataId = -1;
	}
	public void showDatas(ImageData data){
		RelativeLayout myLayout = (RelativeLayout)findViewById(R.id.rl);
		int childCnt = myLayout.getChildCount();
		for (int i= 1; i< childCnt; i++){
			myLayout.removeViewAt(1);
		}
		Log.e("x" , data.getPosx());
		Log.e("y" , data.getPosy());
		String[] labels = data.getContent().split("@");
		String[] xarrays = data.getPosx().split("@");
		String[] yarrays = data.getPosy().split("@");
		mFilePath = data.getBitmap();
		if (bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		mDataId = data.getId();
		bitmap = decodeFile(new File(mFilePath));
		viewImage.setImageBitmap(bitmap);
		count = data.getCount();
		for (int i = 0; i<data.getCount(); i++){
			int pos = i + 1;
			Button btn=new Button(myLayout.getContext());
            btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            btn.setId(pos);
            btn.setText("B"+pos);
            myLayout.addView(btn);
            EditText ed=new EditText(myLayout.getContext());
            ed.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            int ecount=10+pos;
            ed.setId(ecount);
            myLayout.addView(ed);
            MultiTouchListener touchListener=new MultiTouchListener(this);
            btn.setOnTouchListener(touchListener);
            ed.setText(labels[i]);
            try{
            	float x= Float.valueOf(xarrays[i]);
            	float y= Float.valueOf(yarrays[i]);
            	RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btn.getLayoutParams();
                layoutParams.leftMargin = (int) x;
                layoutParams.topMargin = (int) y;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                btn.setLayoutParams(layoutParams);
                ed.setX(layoutParams.leftMargin+100);
                ed.setY(layoutParams.topMargin);
            }catch(Exception e){
            	
            }
		}
		
	}
public void onClick(View v) {
    switch (v.getId()) {
    
    case R.id.add:
    	count++;
           RelativeLayout myLayout = (RelativeLayout)findViewById(R.id.rl);
          
            final Button btn=new Button(myLayout.getContext());
            btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            btn.setId(count);
            btn.setText("B"+count);
            myLayout.addView(btn);
            EditText ed=new EditText(myLayout.getContext());
            ed.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            int ecount=10+count;
            ed.setId(ecount);
            myLayout.addView(ed);
            MultiTouchListener touchListener=new MultiTouchListener(this);
            btn.setOnTouchListener(touchListener);
           
            
        break;
    
    case R.id.remove:
        View myView = findViewById(count);
        ViewGroup parent = (ViewGroup) myView.getParent();
        parent.removeView(myView);
        View edView=findViewById(10+count);
        ViewGroup edViewGroup=(ViewGroup)edView.getParent();
        edViewGroup.removeView(edView);
        count--;
        break;
  
        // More buttons go here (if any) ...

    }   
}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds options to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
}
	String mCamearPath;
  private void selectImage() {

    final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle("Add Photo!");
    builder.setItems(options, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (options[item].equals("Take Photo"))
            {
            	mCamearPath = FILE_SAVE_PATH + "/" + System.currentTimeMillis() + ".jpg" ;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(mCamearPath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);
            }
            else if (options[item].equals("Choose from Gallery"))
            {
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);

            }
            else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        }
    });
    builder.show();
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
        if (requestCode == 1) {
            mFilePath = mCamearPath;
            if (bitmap != null){
    			bitmap.recycle();
    			bitmap = null;
    		}
            bitmap = (decodeFile(new File(mFilePath)));
            Log.w("path of image from camear......******************.........", mFilePath+"");
            viewImage.setImageBitmap(bitmap);
        } else if (requestCode == 2) {

            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            mFilePath = picturePath;
            c.close();
            if (bitmap != null){
    			bitmap.recycle();
    			bitmap = null;
    		}
            bitmap = (decodeFile(new File(picturePath)));
            Log.w("path of image from gallery......******************.........", picturePath+"");
            viewImage.setImageBitmap(bitmap);
        }
    }  
}
private Bitmap decodeFile(File f){
    try {
        //decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream stream1=new FileInputStream(f);
        BitmapFactory.decodeStream(stream1,null,o);
        stream1.close();
        
        //Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE=1000;
        int width_tmp=o.outWidth, height_tmp=o.outHeight;
        int scale=1;
        while(true){
            if(width_tmp/2<REQUIRED_SIZE && height_tmp/2<REQUIRED_SIZE)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }
        
        //decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        FileInputStream stream2=new FileInputStream(f);
        Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
        stream2.close();
        return bitmap;
    } catch (FileNotFoundException e) {
    } 
    catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}

@SuppressLint("NewApi") public class MultiTouchListener implements OnTouchListener
{

//private float mPrevX;
//private float mPrevY;
private int _xDelta;
private int _yDelta;
private float intX,intY,finX,finY;
public MainActivity mainActivity;
public int count;
public MultiTouchListener(MainActivity mainActivity1) {
    mainActivity = mainActivity1;

}
public int getCount()
{
	 return count;
}
@SuppressLint("ClickableViewAccessibility") public boolean onTouch(View view, MotionEvent event) {
    final int X = (int) event.getRawX();
    final int Y = (int) event.getRawY();
    new PointF();
	    new PointF();
    int btnid=view.getId();
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
      	 
        	intX=event.getX();
        	intY=event.getY();
            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            _xDelta = X - lParams.leftMargin;
            _yDelta = Y - lParams.topMargin;
            break;
        case MotionEvent.ACTION_UP:
      	  
        	finX=event.getX();
        	finY=event.getY();
        	if(intX==finX&&intY==finY)
        	{
        		count++;
        		int eid=10+view.getId();
        		EditText edx=(EditText)findViewById(eid);
        		edx.setText("C"+count);
        		edx.setKeyListener(null);
        		edx.setCursorVisible(false);
        		edx.setPressed(false);
        		edx.setFocusable(false);
        	}
        	Log.d("Iam Here", "Button"+view.getId()+" Click Count"+count);
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            break;
        case MotionEvent.ACTION_POINTER_UP:
      	  
            break;
        case MotionEvent.ACTION_MOVE:
        	RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = X - _xDelta;
            layoutParams.topMargin = Y - _yDelta;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
            view.setLayoutParams(layoutParams);
            EditText ed=(EditText)findViewById(10+btnid);
            ed.setX(layoutParams.leftMargin+100);
            ed.setY(layoutParams.topMargin);
           break;
    }
    return true;
}

} 
}