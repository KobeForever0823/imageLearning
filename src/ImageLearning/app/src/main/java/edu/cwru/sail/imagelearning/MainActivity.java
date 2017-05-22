package edu.cwru.sail.imagelearning;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity{

	private ImageView iv;
	private static String imgDir;
	private Button btn1, btn2, btn3, btn4, btnForward, btnBackward, btn6;
	private CSVWriter writer;
	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 0;
	private String csvName;
	private int number;
	private String[] entry;
	private ArrayList<String> imgList;
	private String csv1Name;
	private SensorManager mSensorManager;
	private String csv2Name;
	private Sensor mAcce, mMagnetic, mGyroscope, mRotation, mLinear, mGravity;
	private static final int TAKE_PHOTO = 1;
	private static final int CROP_PHOTO = 2;
	private Uri imageUri;
	private String ac[], ma[], gy[], ro[], li[], gr[];
	private String csv3Name;
	private String x, y, velocityX, velocityY, pressure, size, x1, y1, velocityX1, velocityY1,
	               pressure1, size1;
	private String newImageName;
	private String rateNum;
//
	//Make sure that this part is dynamically defined by the Browse Folder and
	// your CSV file name is "THE_SAME_FOLDER_NAME.csv"

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						0);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						0);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}

		// assignment
		iv = (ImageView) findViewById(R.id.imageView);
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);
		btnBackward = (Button) findViewById(R.id.button);
		btnForward = (Button) findViewById(R.id.button5);
		btn6 = (Button) findViewById(R.id.button6);

		// set click listener
		btn1.setOnClickListener(new MyClickListener());
		btn2.setOnClickListener(new MyClickListener());
		btn3.setOnClickListener(new MyClickListener());
		btn4.setOnClickListener(new MyClickListener());
		btnBackward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				backwardImage(view);
			}
		});
		btnForward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				forwardImage(view);
			}
		});
		btn6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (ContextCompat.checkSelfPermission(MainActivity.this,
						Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED) {

					// Should we show an explanation?
					if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
							Manifest.permission.CAMERA)) {

						// Show an expanation to the user *asynchronously* -- don't block
						// this thread waiting for the user's response! After the user
						// sees the explanation, try again to request the permission.

					} else {

						// No explanation needed, we can request the permission.

						ActivityCompat.requestPermissions(MainActivity.this,
								new String[]{Manifest.permission.CAMERA},
								0);

						// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
						// app-defined int constant. The callback method gets the
						// result of the request.
					}
				}
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = new Date(System.currentTimeMillis());
				imgDir = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + format.format(date) + ".jpg";
				File outputImage = new File(imgDir);
				try {
					if (outputImage.exists()) {
						outputImage.delete();
					}
					outputImage.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				imageUri = Uri.fromFile(outputImage);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (intent.resolveActivity(getPackageManager()) != null) {
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, TAKE_PHOTO);
				}
			}
		});

		imgList = new ArrayList<String>();
		csv1Name = Environment.getExternalStorageDirectory() + "/DCIM/CSV/Homework1.csv";
		csv2Name = Environment.getExternalStorageDirectory() + "/DCIM/CSV/Homework2.csv";
		csv3Name = Environment.getExternalStorageDirectory() + "/DCIM/CSV/AnalysisDataWeka.csv";
		File file = new File(csv1Name);
		if (!file.exists()) {
			try {
				writer = new CSVWriter(new FileWriter(csv1Name, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
				String[] titleEntry = {"position_X", "position_Y", "velocity_X", "velocity_Y", "pressure", "size"};
				writer.writeNext(titleEntry);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		File file1 = new File(csv2Name);
		if (!file1.exists()) {
			try {
				writer = new CSVWriter(new FileWriter(csv2Name, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
				String[] titleEntry = {"TimeStamp", "TYPE_ACCELEROMETER_X", "TYPE_ACCELEROMETER_Y",
						"TYPE_ACCELEROMETER_Z", "TYPE_MAGNETIC_FIELD_X", "TYPE_MAGNETIC_FIELD_Y",
						"TYPE_MAGNETIC_FIELD_Z", "TYPE_GYROSCOPE_X", "TYPE_GYROSCOPE_Y", "TYPE_GYROSCOPE_Z",
						"TYPE_ROTATION_VECTOR_X", "TYPE_ROTATION_VECTOR_Y", "TYPE_ROTATION_VECTOR_Z",
						"TYPE_LINEAR_ACCELERATION_X", "TYPE_LINEAR_ACCELERATION_Y",
						"TYPE_LINEAR_ACCELERATION_Z", "TYPE_GRAVITY_X", "TYPE_GRAVITY_Y", "TYPE_GRAVITY_Z"};
				writer.writeNext(titleEntry);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		File file2 = new File(csv3Name);
		if (!file2.exists()) {
			try {
				writer = new CSVWriter(new FileWriter(csv3Name, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
				String[] titleEntry = {"TYPE_ACCELEROMETER_X", "TYPE_ACCELEROMETER_Y",
						"TYPE_ACCELEROMETER_Z", "TYPE_MAGNETIC_FIELD_X", "TYPE_MAGNETIC_FIELD_Y",
						"TYPE_MAGNETIC_FIELD_Z", "TYPE_GYROSCOPE_X", "TYPE_GYROSCOPE_Y", "TYPE_GYROSCOPE_Z",
						"TYPE_ROTATION_VECTOR_X", "TYPE_ROTATION_VECTOR_Y", "TYPE_ROTATION_VECTOR_Z",
						"TYPE_LINEAR_ACCELERATION_X", "TYPE_LINEAR_ACCELERATION_Y",
						"TYPE_LINEAR_ACCELERATION_Z", "TYPE_GRAVITY_X", "TYPE_GRAVITY_Y", "TYPE_GRAVITY_Z",
						"position_X", "position_Y", "velocity_X", "velocity_Y", "pressure", "size",
				        "indicator", "pattern"};
				writer.writeNext(titleEntry);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mAcce = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		mSensorManager.registerListener(mySensorListener, mAcce, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mySensorListener, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mySensorListener, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mySensorListener, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mySensorListener, mLinear, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mySensorListener, mGravity, SensorManager.SENSOR_DELAY_NORMAL);

		ac = new String[3];
		ma = new String[3];
		gy = new String[3];
		ro = new String[3];
		li = new String[3];
		gr = new String[3];
	}



	// define clickListener function
	class MyClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			if (!imgDir.substring(25, 31).equals("Camera")) {
				// get csv name
				csvName = imgDir.substring(0, imgDir.length() - 32) + ".csv";
				File file = new File(csvName);
				// generate name of new image directory
				newImageName = imgDir.substring(60, imgDir.length() - 7) + generateNewNumber(number) + ".jpg";
				if (file.exists()) {
					try {
						// new writer
						writer = new CSVWriter(new FileWriter(csvName, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
						CSVWriter newWriter = new CSVWriter(new FileWriter(csv3Name, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
						switch (view.getId()) {
							case R.id.button1:
								if (!imgList.contains(newImageName)) {
									// check if the name has been selected
									imgList.add(newImageName);
									entry = (newImageName + "," + "1").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "1";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
							case R.id.button2:
								if (!imgList.contains(newImageName)) {
									imgList.add(newImageName);
									entry = (newImageName + "," + "2").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "2";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
							case R.id.button3:
								if (!imgList.contains(newImageName)) {
									imgList.add(newImageName);
									entry = (newImageName + "," + "3").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "3";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
							case R.id.button4:
								if (!imgList.contains(newImageName)) {
									imgList.add(newImageName);
									entry = (newImageName + "," + "4").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "4";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				csvName = Environment.getExternalStorageDirectory() + "/DCIM/Camera/photo.csv";
				newImageName = imgDir.substring(32, imgDir.length());
				File file = new File(csvName);
				if (file.exists()) {
					try {
						// new writer
						writer = new CSVWriter(new FileWriter(csvName, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
						CSVWriter newWriter = new CSVWriter(new FileWriter(csv3Name, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
						switch (view.getId()) {
							case R.id.button1:
								if (!imgList.contains(newImageName)) {
									// check if the name has been selected
									imgList.add(newImageName);
									entry = (newImageName + "," + "1").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "1";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
							case R.id.button2:
								if (!imgList.contains(newImageName)) {
									imgList.add(newImageName);
									entry = (newImageName + "," + "2").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "2";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
							case R.id.button3:
								if (!imgList.contains(newImageName)) {
									imgList.add(newImageName);
									entry = (newImageName + "," + "3").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "3";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
							case R.id.button4:
								if (!imgList.contains(newImageName)) {
									imgList.add(newImageName);
									entry = (newImageName + "," + "4").split(",");
									writer.writeNext(entry);
									writer.close();
									rateNum = "4";
									writeCSV(newWriter);
									newWriter.close();
								} else {
									Toast.makeText(MainActivity.this, "You have selected! Please select other image!", Toast.LENGTH_SHORT).show();
								}
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	// define touch event
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		try {
			writer = new CSVWriter(new FileWriter(csv1Name, true), ',', CSVWriter.NO_QUOTE_CHARACTER);

			int mMaximumVelocity = ViewConfiguration.get(this).getScaledMaximumFlingVelocity();

			// initialize
			VelocityTracker velocityTracker = VelocityTracker.obtain();
			velocityTracker.addMovement(motionEvent);

			int action = motionEvent.getAction();

			switch (action) {
				case MotionEvent.ACTION_DOWN:
					x = String.valueOf(motionEvent.getX());
					y = String.valueOf(motionEvent.getY());
					pressure = String.valueOf(motionEvent.getPressure());
					size = String.valueOf(motionEvent.getSize());
					velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
					velocityX = String.valueOf(velocityTracker.getXVelocity());
					velocityY = String.valueOf(velocityTracker.getYVelocity());

					// recycle
					velocityTracker.recycle();
					String[] entry = {x, y, velocityX, velocityY, pressure, size};
					writer.writeNext(entry);
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_CANCEL:
					// recycle
					velocityTracker.recycle();
					break;
				case MotionEvent.ACTION_MOVE:
					// initialize unit of velocity
					x1 = String.valueOf(motionEvent.getX());
					y1 = String.valueOf(motionEvent.getY());
					pressure1 = String.valueOf(motionEvent.getPressure());
					size1 = String.valueOf(motionEvent.getSize());
					velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
					velocityX1 = String.valueOf(velocityTracker.getXVelocity());
					velocityY1 = String.valueOf(velocityTracker.getYVelocity());

					// recycle
					velocityTracker.recycle();
					String[] entry1 = {x1, y1, velocityX1, velocityY1, pressure1, size1};
					writer.writeNext(entry1);
					break;
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}


	// define sensor listener
	final SensorEventListener mySensorListener = new SensorEventListener() {
			@Override
			public void onSensorChanged (SensorEvent sensorEvent) {
				try {
					writer = new CSVWriter(new FileWriter(csv2Name, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
					Date date = new Date(System.currentTimeMillis());
					String dateStr = format.format(date);

					int sensorType = sensorEvent.sensor.getType();

					if (sensorType == Sensor.TYPE_ACCELEROMETER) {
						for (int i = 0; i <= 2; i++) {
							ac[i] = String.valueOf(sensorEvent.values[i]);
						}
						String[] dataEntry = {dateStr, ac[0], ac[1], ac[2], ma[0],
								ma[1], ma[2], gy[0], gy[1], gy[2], ro[0], ro[1], ro[2],
								li[0], li[1], li[2], gr[0], gr[1], gr[2]};
						writer.writeNext(dataEntry);
						writer.close();
					}
					if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
						for (int i = 0; i <= 2; i++) {
							ma[i] = String.valueOf(sensorEvent.values[i]);
						}
						String[] dataEntry = {dateStr, ac[0], ac[1], ac[2], ma[0],
								ma[1], ma[2], gy[0], gy[1], gy[2], ro[0], ro[1], ro[2],
								li[0], li[1], li[2], gr[0], gr[1], gr[2]};
						writer.writeNext(dataEntry);
						writer.close();
					}
					if (sensorType == Sensor.TYPE_GYROSCOPE) {
						for (int i = 0; i <= 2; i++) {
							gy[i] = String.valueOf(sensorEvent.values[i]);
						}
						String[] dataEntry = {dateStr, ac[0], ac[1], ac[2], ma[0],
								ma[1], ma[2], gy[0], gy[1], gy[2], ro[0], ro[1], ro[2],
								li[0], li[1], li[2], gr[0], gr[1], gr[2]};
						writer.writeNext(dataEntry);
						writer.close();
					}
					if (sensorType == Sensor.TYPE_ROTATION_VECTOR) {
						for (int i = 0; i <= 2; i++) {
							ro[i] = String.valueOf(sensorEvent.values[i]);
						}
						String[] dataEntry = {dateStr, ac[0], ac[1], ac[2], ma[0],
								ma[1], ma[2], gy[0], gy[1], gy[2], ro[0], ro[1], ro[2],
								li[0], li[1], li[2], gr[0], gr[1], gr[2]};
						writer.writeNext(dataEntry);
						writer.close();
					}
					if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION) {
						for (int i = 0; i <= 2; i++) {
							li[i] = String.valueOf(sensorEvent.values[i]);
						}
						String[] dataEntry = {dateStr, ac[0], ac[1], ac[2], ma[0],
								ma[1], ma[2], gy[0], gy[1], gy[2], ro[0], ro[1], ro[2],
								li[0], li[1], li[2], gr[0], gr[1], gr[2]};
						writer.writeNext(dataEntry);
						writer.close();
					}
					if (sensorType == Sensor.TYPE_GRAVITY) {
						for (int i = 0; i <= 2; i++) {
							gr[i] = String.valueOf(sensorEvent.values[i]);
						}
						String[] dataEntry = {dateStr, ac[0], ac[1], ac[2], ma[0],
								ma[1], ma[2], gy[0], gy[1], gy[2], ro[0], ro[1], ro[2],
								li[0], li[1], li[2], gr[0], gr[1], gr[2]};
						writer.writeNext(dataEntry);
						writer.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onAccuracyChanged (Sensor sensor,int i){
		    }

	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//Ask for permission
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {

				// Should we show an explanation?
				if (ActivityCompat.shouldShowRequestPermissionRationale(this,
						Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

					// Show an expanation to the user *asynchronously* -- don't block
					// this thread waiting for the user's response! After the user
					// sees the explanation, try again to request the permission.

				} else {

					// No explanation needed, we can request the permission.

					ActivityCompat.requestPermissions(this,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							0);

					// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
					// app-defined int constant. The callback method gets the
					// result of the request.
				}
			}

			Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
			getAlbum.setType(IMAGE_TYPE);
			startActivityForResult(getAlbum, IMAGE_CODE);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// click listener: back to former image
	public void backwardImage(View view) {
		// check if the image is the first one
		if (number == 1) {
			// get image name
			String newImgDir = imgDir.substring(0, imgDir.length() - 7) + number + ".jpg";
			displayImage(newImgDir);
			Toast.makeText(MainActivity.this, "This is the first image!", Toast.LENGTH_SHORT).show();
		} else {
			number--;
			String newImgDir = imgDir.substring(0, imgDir.length() - 7) + number + ".jpg";
			displayImage(newImgDir);
		}
	}

	// forward
	public void forwardImage(View view) {
		int num = number;
		String newImgDir = imgDir.substring(0, imgDir.length() - 7) + (++num) + ".jpg";
//		File file = new File(newImgDir);
		// check if the next image exists
		displayImage(newImgDir);
		number++;
	}


	//callback function
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		} else if (requestCode == TAKE_PHOTO) {
			Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(imageUri, "image/*");
			intent.putExtra("scale", true);

			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);

			intent.putExtra("outputX", 340);
			intent.putExtra("outputY", 340);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

			Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intentBc.setData(imageUri);
			this.sendBroadcast(intentBc);
			startActivityForResult(intent, CROP_PHOTO);
		} else if (requestCode == CROP_PHOTO) {
			if (DocumentsContract.isDocumentUri(this, imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
					String id = docId.split(":")[1];
					String selection = MediaStore.Images.Media._ID + "=" + id;
					imgDir = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
				} else if ("com.android.providers.downloads.documents".equals(imageUri.getAuthority())) {
					Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"),
							Long.valueOf(docId));
					imgDir = getImagePath(contentUri, null);
				}
			} else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
				imgDir = getImagePath(imageUri, null);
			}
		} else {
			selectImage(data);
		}
	}


	private void selectImage(Intent data) {
		Uri uri = data.getData();
		//The type of uri is document
		if (DocumentsContract.isDocumentUri(this, uri)) {
			String docId = DocumentsContract.getDocumentId(uri);
			if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
				String id = docId.split(":")[1];
				String selection = MediaStore.Images.Media._ID + "=" + id;
				imgDir = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
			} else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(docId));
				imgDir = getImagePath(contentUri, null);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			imgDir = getImagePath(uri, null);
		}
		displayImage(imgDir);
		if (imgDir.length() != 50) {
			csvName = imgDir.substring(0, imgDir.length() - 32) + ".csv";
			createCSV(csvName);
			number = Integer.parseInt(imgDir.substring(imgDir.length() - 7, imgDir.length() - 4));
			createCSV(csv3Name);
		} else {
			csvName = Environment.getExternalStorageDirectory() + "/DCIM/Camera/photo.csv";
			createCSV(csvName);
		}
	}

	private String getImagePath(Uri uri, String selection) {
		String path = null;
		Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			}
			cursor.close();
		}
		return path;
	}

	protected void displayImage(String imageDir) {
		File img = new File(imageDir);
		if (img.exists()) {
			//Loading Image from URL
			Picasso.with(MainActivity.this)
//					.load("https://www.simplifiedcoding.net/wp-content/uploads/2015/10/advertise.png")
					.load(img)
					//.placeholder(R.drawable.placeholder)   // optional
					//.error(R.drawable.error)      // optional
					.resize(1000, 1000)                        // optional
					.into(iv);
		}
	}

	// Create a csv file if not exist
	public void createCSV(String imageDir) {
		File f = new File(imageDir);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// change the former of '###' to single number
	private String generateNewNumber(int num) {
		String newNum = "";
		if (num <= 9) {
			newNum = "00" + num;
		} else if (num >= 10 && num < 100) {
			newNum = "0" + num;
		} else if (num >= 100) {
			newNum = String.valueOf(num);
		}
		return newNum;
	}

	private void writeCSV(CSVWriter newWriter) {
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
//		Date date = new Date(System.currentTimeMillis());
		String[] dataEntry = {ac[0], ac[1], ac[2], ma[0],
				ma[1], ma[2], gy[0], gy[1], gy[2], ro[0], ro[1], ro[2],
				li[0], li[1], li[2], gr[0], gr[1], gr[2], x, y, velocityX, velocityY, pressure, size,
				rateNum, "four"};
		boolean flag = true;
		for (String str: dataEntry) {
			if (str == null) {
				flag = false;
				break;
			}
		}
		if (flag == true) {
			newWriter.writeNext(dataEntry);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mySensorListener);
		super.onPause();
	}
}

