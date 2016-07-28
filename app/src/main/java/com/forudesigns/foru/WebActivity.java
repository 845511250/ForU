package com.forudesigns.foru;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.forudesigns.foru.tools.SmallImage;
import com.zuoyun.foru.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class WebActivity extends MyBaseActivity {
	Context context;
	private int windowWidth,windowHight;//获取手机屏幕宽高
	AlertDialog mydialog_fb;
	WebView wv;
	View view_top_bar;
	SwipeRefreshLayout refresh_web;
	ValueCallback<Uri> mUploadMessage;
	ValueCallback<Uri[]> mUploadCallbackAboveL;
	final static int FILECHOOSER_RESULTCODE=1;
	final static int REQUEST_CODE_ASK_PERMISSIONS=110;
	private boolean CANEXIT=false;

	String mPhotoPath;
	File mPhotoFile;
	String forupath = Environment.getExternalStorageDirectory()+"/ForUImg" ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		context = this;

		initview();
		initWebView(wv);
		wv.loadUrl("https://m.forudesigns.com");
		checkPermissions();//android 6.0 permission

		refresh_web.setColorSchemeResources(R.color.red, R.color.green,R.color.blue);
		refresh_web.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				wv.reload();
				//Log.e("e", "refreshing...");
			}
		});
	}
	//-------------------------------------------------------------onCreate finished here.

	public void initview(){
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowWidth = windowManager.getDefaultDisplay().getWidth();Log.e("windowWidth", windowWidth + "");
		windowHight = windowManager.getDefaultDisplay().getHeight();

		wv=(WebView) findViewById(R.id.wv);
		refresh_web = (SwipeRefreshLayout) findViewById(R.id.refresh_web);
	}
	public void initWebView( final WebView wv){
		wv.requestFocus();
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		WebSettings webSettings = wv.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setAllowFileAccessFromFileURLs(true);
		webSettings.setAllowUniversalAccessFromFileURLs(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setDomStorageEnabled(true);
		webSettings.setSupportMultipleWindows(true);

		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
			webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

		wv.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (v.getScrollY() == 0)
						v.scrollTo(0, 1);
				}
				return false;
			}
		});
		wv.setWebViewClient(new myWebViewClient());
		wv.setWebChromeClient(new myWebChromeClient());
	}

	class myWebViewClient extends WebViewClient{

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if(url.indexOf("basics/")!=-1)
				refresh_web.setEnabled(false);
			else
				refresh_web.setEnabled(true);

			super.onPageStarted(view, url, favicon);
		}
	}

	class myWebChromeClient extends WebChromeClient{
		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			Log.e("onCreateWindow", resultMsg.obj.toString());

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			mydialog_fb = builder.create();
			View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_facebook, null);
			mydialog_fb.setCanceledOnTouchOutside(true);

			WebView webView_facebook = (WebView) view_dialog.findViewById(R.id.wv_facebook);
			initWebView(webView_facebook);
			transport.setWebView(webView_facebook);
			resultMsg.sendToTarget();

			mydialog_fb.show();
			mydialog_fb.getWindow().setLayout(windowWidth*9/10, windowHight*7/10);
			mydialog_fb.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			mydialog_fb.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			mydialog_fb.setContentView(view_dialog);

			return true;
		}

		@Override
		public void onCloseWindow(WebView window) {
			super.onCloseWindow(window);
			mydialog_fb.dismiss();
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			if (newProgress > 70)
				refresh_web.setRefreshing(false);
			else
				refresh_web.setRefreshing(true);
		}

		//4.4+
		public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
			//Log.e("openFileChooser", "4.4--5.0");
			mUploadMessage = uploadFile;
			openChooser();
		}

		//3.0
		public void openFileChooser(ValueCallback uploadFile, String acceptType) {
			//Log.e("openFileChooser", "3.0--4.0");
			mUploadMessage = uploadFile;
			openChooser();
		}

		//3.0-
		public void openFileChooser(ValueCallback<Uri> uploadFile) {
			//Log.e("openFileChooser", "<3.0");
			mUploadMessage = uploadFile;
			openChooser();
		}

		//5.0+
		@Override
		public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
			//Log.e("openFileChooser", ">5.0");
			mUploadCallbackAboveL = filePathCallback;
			openChooser();
			return true;
		}
	}

	//
	public void openChooser(){
		//intent_select
		Intent intent_select = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//Intent intent_select = new Intent(Intent.ACTION_GET_CONTENT);
		intent_select.setType("image/*");

		//intent_capture
		makePath();
		mPhotoPath = forupath + "/" + getPhotoName();
		mPhotoFile = new File(mPhotoPath);
		Intent intent_capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent_capture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));

		//组合两个intent
		Intent intent_combine = Intent.createChooser(intent_select, "Image Chooser");
		intent_combine.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intent_capture});

		//Startactivity For Redult
		startActivityForResult(intent_combine, FILECHOOSER_RESULTCODE);
	}
	//
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==FILECHOOSER_RESULTCODE&&resultCode==RESULT_OK) {
			if (mUploadMessage != null) {                                    //<5.0
				if (data == null) {                                        //相机
					SmallImage smallImage = new SmallImage(mPhotoPath, context);
					File smallImageFile = new File(smallImage.smallimagepath);
					Uri uri = Uri.fromFile(smallImageFile);
					//Log.e("<6.0_camera", uri.toString());
					//Log.e("uploadimg_width", smallImage.bitmap.getWidth() + "");
					mUploadMessage.onReceiveValue(uri);
					mUploadMessage = null;
				} else {                                                    //相册
					Uri uri = data.getData();
					//Log.e("<6.0select", uri.toString());
					getPathFromUri(uri);
					SmallImage smallImage = new SmallImage(getPathFromUri(uri), context);
					File smallImageFile = new File(smallImage.smallimagepath);
					Uri uri1 = Uri.fromFile(smallImageFile);
					//Log.e("uploadimg_width", smallImage.bitmap.getWidth() + "");
					mUploadMessage.onReceiveValue(uri1);
					mUploadMessage = null;
				}
			} else
				onActivityResultAboveL(data);                                        //>5.0
		}
		else {
			if (mUploadMessage!=null){
				mUploadMessage.onReceiveValue(null);
				mUploadMessage = null;
			}
			if (mUploadCallbackAboveL!=null){
				mUploadCallbackAboveL.onReceiveValue(null);
				mUploadCallbackAboveL = null;
			}
		}
	}

	protected void onActivityResultAboveL(Intent data){
		if(data==null) {
			SmallImage smallImage = new SmallImage(mPhotoPath, context);
			File smallImageFile = new File(smallImage.smallimagepath);
			Uri uri = Uri.fromFile(smallImageFile);
			//Log.e(">=5.0_camera", uri.toString());
			//Log.e("image_width", smallImage.bitmap.getWidth() + "");
			mUploadCallbackAboveL.onReceiveValue(new Uri[]{uri});
			mUploadCallbackAboveL = null;
		}
		else{
			Uri uri = data.getData();
			//Log.e(">=6.0_select_uri", uri.toString());
			SmallImage smallImage = new SmallImage(getPathFromUri(uri), context);
			File smallImageFile = new File(smallImage.smallimagepath);
			Uri uri1 = Uri.fromFile(smallImageFile);
			//Log.e("image_width", smallImage.bitmap.getWidth()+"");
			mUploadCallbackAboveL.onReceiveValue(new Uri[]{uri1});
			mUploadCallbackAboveL = null;
		}
	}


	// getPathFromUri
	public String getPathFromUri(Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	// getPhotoFileName
	public String getPhotoName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'ForU'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	//
	public void makePath(){
		File file = new File(forupath);
		if(!file.exists())
			file.mkdirs();
	}

	//---------------------------------------------------------------------------------android 6.0 权限适配
	private void checkPermissions(){
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
				requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_PERMISSIONS:
				if (grantResults[0] == PackageManager.PERMISSION_DENIED)
					Toast.makeText(context,"Permission Denied!",Toast.LENGTH_SHORT).show();
				break;
		}
	}

	//--------------------------------------------------------------------------------
	public int getStatusBarHeight() {
		int result = 50;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	//onkedown
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
			wv.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK) && !wv.canGoBack()) {
			if(CANEXIT)
				finish();
			else {
				CANEXIT = true;
				Toast.makeText(context,"click again to exit",Toast.LENGTH_SHORT).show();
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						CANEXIT = false;
					}
				},2000);
			}
			return true;
		}
		if(keyCode==KeyEvent.KEYCODE_MENU){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			final AlertDialog mydialog = builder.create();
			View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_exit, null);
			mydialog.setCanceledOnTouchOutside(true);
			mydialog.show();
			mydialog.setContentView(view_dialog);
			// dialog内部的点击事件
			Button bt_exit = (Button) view_dialog.findViewById(R.id.bt_dialog_exit);
			bt_exit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mydialog.dismiss();
					finish();
				}
			});
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//
}
