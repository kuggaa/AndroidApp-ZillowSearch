package com.example.zillowsearch;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ActionBar.TabListener;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class ResultActivity extends ActionBarActivity  {

	JSONObject jsonObject = null;
	JSONObject jsonResult = null, jsonChart=null;
	FrameLayout frame = null;
	ImageView chart = null;
	LinearLayout linearTab2 = null, ButtonLayout = null;
	Button left = null, right = null;
	String[] chartURL, chartHeading;
	String chartHeadingSub, propertyAddress;
	boolean nextPressed = false;
	TextView heading = null, disclaimer = null;
	ImageSwitcher imgSwitcher = null;
	TextSwitcher textSwitcher = null, textSwitcherSub = null;
	int counter = 0, width=0, height=0;
	private Session session;
	private String applicationId;
	Facebook facebook;
	String[] fbParams;
	DisplayMetrics tableMetrics;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//TextView tv = ((TextView)findViewById(R.id.editText1));
		//tv.setText(getIntent().getStringExtra("ResultString"));
		String jsonString = getIntent().getStringExtra("ResultString");

		tableMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(tableMetrics);
		width = tableMetrics.widthPixels;
		height = tableMetrics.heightPixels;
		
		try {		
			jsonObject = new JSONObject(jsonString);
			jsonResult = jsonObject.getJSONObject("result");
			jsonChart = jsonObject.getJSONObject("chart");
			Log.v("The first name is: ", jsonResult.getString("homedetails"));
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		final TextView tv = ((TextView)findViewById(R.id.textView1));
		//tv.setText(getIntent().getStringExtra("ResultString"));
		frame = (FrameLayout) findViewById(R.id.container);
		
	    android.app.ActionBar myActionBar = getActionBar(); 
	    myActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
	    
	    android.app.ActionBar.Tab tab1 = myActionBar.newTab(); 
	    tab1.setTabListener(
	    		new ActionBar.TabListener()
	    		{
					@Override
					public void onTabSelected(android.app.ActionBar.Tab tab,
							FragmentTransaction ft) {
						
						frame.removeAllViews();
						createTableTab1(frame, jsonResult);
						
						tv.setText("tab1");			
					}
					@Override
					public void onTabUnselected(android.app.ActionBar.Tab tab,
							FragmentTransaction ft) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onTabReselected(android.app.ActionBar.Tab tab,
							FragmentTransaction ft) {
						// TODO Auto-generated method stub
						
					} 
	    		});
	    tab1.setText("BASIC INFO"); 
	    myActionBar.addTab(tab1);
	    
	    
	    
	    
	    
	    
	    android.app.ActionBar.Tab tab2 = myActionBar.newTab(); 
	    tab2.setTabListener(
	    	    		new ActionBar.TabListener()
	    	    		{
	    					@Override
	    					public void onTabSelected(android.app.ActionBar.Tab tab,
	    							FragmentTransaction ft) {
	    						// TODO Auto-generated method stub
	    						tv.setText("Tab2");
	    						frame.removeAllViews();
	    						createTableTab2(frame, jsonResult);
	    					}
	    					
	    					@Override
	    					public void onTabUnselected(android.app.ActionBar.Tab tab,
	    							FragmentTransaction ft) {
	    						// TODO Auto-generated method stub
	    						
	    					}
	    					@Override
	    					public void onTabReselected(android.app.ActionBar.Tab tab,
	    							FragmentTransaction ft) {
	    						// TODO Auto-generated method stub
	    						
	    					} 
	    	    		});
	    		
	    tab2.setText("HISTORICAL ZESTIMATES"); 
	    myActionBar.addTab(tab2);		
		
	}
	
	 public void facebookFeedDisplay(String[] fbString){
		 facebook=new Facebook("759639834091936");
		 Bundle params = new Bundle();
		 params.putString("name", fbString[0]);
		 params.putString("caption", fbString[1]);
		 params.putString("description", fbString[2]);
		 params.putString("link", fbString[3]);
		 params.putString("picture", fbString[4]);
		 Log.v("hashmap", fbString[0]);
		     facebook.dialog(ResultActivity.this,"feed",params, new DialogListener() {

		 @Override
		 public void onFacebookError(FacebookError e) {
		 // TODO Auto-generated method stub
			 Toast.makeText(getApplicationContext(),"Facebook Error:",Toast.LENGTH_LONG).show();
		 }

		 @Override
		 public void onError(DialogError e) {
		 // TODO Auto-generated method stub
			 Toast.makeText(getApplicationContext(),"Facebook Dialog Error:",Toast.LENGTH_LONG).show();
		 }

		 @Override
		 public void onComplete(Bundle values) {
		 // TODO Auto-generated method stub
			 final String postId = values.getString("post_id");
			 if(postId != null) {
				 Toast.makeText(getApplicationContext(),"Posted Story ID:"+postId,Toast.LENGTH_LONG).show();
			 }
			 else {
				 Toast.makeText(getApplicationContext(),"Post cancelled",Toast.LENGTH_LONG).show();
			 }
				 
		 }
		 @Override
		 public void onCancel() {
		 // TODO Auto-generated method stub
			 Toast.makeText(getApplicationContext(),"Post cancelled",Toast.LENGTH_LONG).show();
		 }
		 });

		 }
	
	
	
	private void publishFeedDialog() {
        Session activeSession = Session.getActiveSession();
        Log.v("session id:",activeSession.getApplicationId());
        /*if (activeSession != null && activeSession.isOpened()) {
            this.session = activeSession;
        } else {
            String applicationId = Utility.getMetadataApplicationId(context);
            if (applicationId != null) {
                this.applicationId = applicationId;
            } else {
                throw new FacebookException("Attempted to create a builder without an open" +
                        " Active Session or a valid default Application ID.");
            }
        }*/
		
		
	    Bundle params = new Bundle();
	    params.putString("name", "Facebook SDK for Android");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", "https://developers.facebook.com/android");
	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(ResultActivity.this,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	            	
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(ResultActivity.this,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(ResultActivity.this.getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(ResultActivity.this.getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(ResultActivity.this.getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	
	
	/*
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Facebook SDK for Android");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", "https://developers.facebook.com/android");
	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	    WebDialog feedDialog = (
	            new WebDialog.FeedDialogBuilder(ResultActivity.this,
	                    Session.getActiveSession(),
	                    params)).build();
	    feedDialog.show();
	}*/
	
	public void createTableTab1(FrameLayout frame, JSONObject jsonResult){
		try {
		TableLayout outertable = new TableLayout(this);	
		TableRow outerRow1 = new TableRow(this);
		TableRow outerRow2 = new TableRow(this);
		
		ScrollView scrollView = new ScrollView(this);
		TableLayout table = new TableLayout(this);
	

		int tableWidthFirst = (width)*3/4;
		int tableWidthSecond = (width)/4;
		int tableWidthHalf = (width)/2;
		
		TableRow[] tRow = new TableRow[30];
        for(int i=0; i<tRow.length; i++){
        	tRow[i] = new TableRow(this);
        }
        for(int i=0; i<17; i++){
        	tRow[i].setBackgroundResource(R.layout.oddrow);
        	i++;
        	tRow[i].setBackgroundResource(R.layout.evenrow); 
        }
        
        
        TextView[] tElement = new TextView[40];
        for(int i=0; i<tElement.length; i++){
        	tElement[i] = new TextView(this);
        	tElement[i].setPadding(5, 10, 5, 10);
        }
        

        tElement[0].setWidth(tableWidthHalf);
        for(int i=2; i<tElement.length; i++){
        	tElement[i].setWidth(tableWidthFirst);
        	i++;
        	tElement[i].setWidth(tableWidthSecond); 
        }
		
        
        
        
        
        String estimateValueChangeSign="";
        String restimateValueChangeSign=""; 


        propertyAddress = jsonResult.getString("street")+","+jsonResult.getString("city")+","+jsonResult.getString("state")+"-"+jsonResult.getString("zipcode");

        
		tElement[0].setText("See more details on Zillow");

		tElement[1].setText(Html.fromHtml("<a href=\""+jsonResult.getString("homedetails")+"\">"+propertyAddress+"</a>"));
		tElement[1].setMovementMethod(LinkMovementMethod.getInstance());
		
        tElement[2].setText("Property Type");
        tElement[3].setText(jsonResult.getString("PropertyType"));
        
		tElement[4].setText("Year Built");
        tElement[5].setText(jsonResult.getString("YearBuilt")); 
		
        tElement[6].setText("Lot Size");
        tElement[7].setText(jsonResult.getString("LotSize"));
        
		tElement[8].setText("Finished Area");
        tElement[9].setText(jsonResult.getString("FinishedArea"));
        
		tElement[10].setText("Bathrooms");
        tElement[11].setText(jsonResult.getString("bathrooms"));
        
		tElement[12].setText("Bedrooms");
        tElement[13].setText(jsonResult.getString("bedrooms")); 
        
		tElement[14].setText("Tax Assessmet Year");
        tElement[15].setText(jsonResult.getString("TaxAssessmentYear"));
        
		tElement[16].setText("Tax Assessmen");
        tElement[17].setText(jsonResult.getString("TaxAssessment"));
                
        tElement[18].setText("Last Sold Price");
        tElement[19].setText(jsonResult.getString("LastSoldPrice"));
        
		tElement[20].setText("Last Sold Date");
        tElement[21].setText(jsonResult.getString("LastSoldDate")); 
		
        tElement[22].setText("Zestimate\u00AE Property Estimate as of "+jsonResult.getString("DateZestimate"));
        tElement[23].setText(jsonResult.getString("DateZestimateValue"));
        
		tElement[24].setText("30 Days Overall Change");
		
		/*
        tElement[25].setText(jsonResult.getString("zestimateValue"));
        Log.v("estimate",jsonResult.getString("estimateValueChangeSign"));
        if(jsonResult.getString("estimateValueChangeSign").trim().equals("+")) {
            Log.v("estimate","inside if");
            estimateValueChangeSign = "+";
        	tElement[25].setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.up, 0, 0, 0); 
        }
        if(jsonResult.getString("estimateValueChangeSign").trim().equals("-")) { 
        	Log.v("estimate","inside if");
        	estimateValueChangeSign = "-";
            tElement[25].setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.down, 0, 0, 0);
        }*/
        
        
        SpannableString ss = new SpannableString("  "+jsonResult.getString("zestimateValue"));
        Drawable d = null;
        Log.v("estimate",jsonResult.getString("estimateValueChangeSign"));
        if(jsonResult.getString("estimateValueChangeSign").trim().equals("+")) {
        	d = getResources().getDrawable(R.drawable.up);
            estimateValueChangeSign = "+";
        }
        if(jsonResult.getString("estimateValueChangeSign").trim().equals("-")) { 
        	d = getResources().getDrawable(R.drawable.down);
        	estimateValueChangeSign = "-";
        }
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()); 
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE); 
        ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 
        tElement[25].setText(ss);
        
        
		tElement[26].setText("All Time Property Range");
        tElement[27].setText(jsonResult.getString("PropertyRangeLow")+"-"+jsonResult.getString("PropertyRangeHigh")); 
        
		tElement[28].setText("Rent Zestimate\u00AE Rent Valuation as of "+jsonResult.getString("dateRentZestimate"));
        tElement[29].setText(jsonResult.getString("RentAmount")); 
        
		tElement[30].setText("30 days Rent Change");
        //tElement[31].setText(jsonResult.getString("rentzestimateValue"));
        SpannableString ss1 = new SpannableString("  "+jsonResult.getString("rentzestimateValue")); 
        Drawable d1 = null;
        if(jsonResult.getString("restimateValueChangeSign").trim().equals("+")) {
        	restimateValueChangeSign = "+";
        	d1 = getResources().getDrawable(R.drawable.up);
        }
        if(jsonResult.getString("restimateValueChangeSign").trim().equals("-")) { 
        	restimateValueChangeSign = "-";
        	d1 = getResources().getDrawable(R.drawable.down);
        }
        d1.setBounds(0, 0, d1.getIntrinsicWidth(), d1.getIntrinsicHeight()); 
        ImageSpan span1 = new ImageSpan(d1, ImageSpan.ALIGN_BASELINE); 
        ss1.setSpan(span1, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 
        tElement[31].setText(ss1);
        
		tElement[32].setText("All Time Rent Range");
        tElement[33].setText(jsonResult.getString("RentRangeLow")+"-"+jsonResult.getString("RentRangeHigh"));
                
        
        String LastSoldFb = "Last Sold Price: "+jsonResult.getString("LastSoldPrice")+", 30 Days Overall Change: "+estimateValueChangeSign+jsonResult.getString("zestimateValue");
        fbParams = new String[5];
        
        fbParams[0] = propertyAddress;
        fbParams[1] = "Property information from Zillow.com";
        fbParams[2] = LastSoldFb;
        fbParams[3] = jsonResult.getString("homedetails");
        fbParams[4] = jsonChart.getString("oneyear");
        
        
        //add fb button
        ImageButton imgbtn=new ImageButton(this);
        imgbtn.setBackgroundResource(R.drawable.fbbutton);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	//publishFeedDialog();
            	//facebookFeedDisplay(fbParams);
            	
            	/*alert dialog for facebook*/
            	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ResultActivity.this);
            	alertBuilder.setMessage("Post to Facebook");
            	alertBuilder.setCancelable(true);
            	alertBuilder.setPositiveButton("Post Property Details",
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	facebookFeedDisplay(fbParams);
                    }
                });
            	
            	alertBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(ResultActivity.this, "Post Cancelled",
								Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert11 = alertBuilder.create();
                alert11.show();
                
                
            }
        });
        
        
        tRow[0].setPadding(0, 20, 0, 20);
        tRow[0].addView(tElement[0]);
        tRow[0].addView(imgbtn);
        tRow[1].addView(tElement[1]);
        
        for(int i=2, j=2; i<18; i++){
        	Log.v(String.valueOf(i),String.valueOf(j));
        	tRow[i].addView(tElement[j]);
        	j++;
        	tRow[i].addView(tElement[j]);
        	tElement[j].setGravity(Gravity.RIGHT);
        	j++;
        	//table.addView(tRow[i]);
        }        
 
        for(int i=0; i<18; i++){
        	table.addView(tRow[i]);
        }
        
        /*
        
		tv1.setText("tab1----1");
		tv2.setText("tab1----2");
		
		
		row.addView(tv1);
		row.addView(tv2);
		
		table.addView(row);*/
		scrollView.addView(table);
		
		TextView disclaimer2 = new TextView(this);
		TextView disclaimer3 = new TextView(this);
		disclaimer = new TextView(this);
		
		disclaimer.setText("\u00A9 Zillow, Inc., 2006-2014.");
		disclaimer2.setText(Html.fromHtml("<html>Use is subject to<a href=\"http://www.zillow.com/corp/Terms.htm\">Terms of Use</a></html>"));
		disclaimer3.setText(Html.fromHtml("<a href=\"http://www.zillow.com/zestimate/\">What's a Zestimate?</a>"));
			
		disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
		disclaimer2.setMovementMethod(LinkMovementMethod.getInstance());
		disclaimer3.setMovementMethod(LinkMovementMethod.getInstance());
		
		disclaimer.setPadding(0, 30, 0, 0);
		
		
		LinearLayout.LayoutParams linearParams1 = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        linearParams1.gravity = Gravity.CENTER;
        disclaimer.setLayoutParams(linearParams1);
        disclaimer2.setLayoutParams(linearParams1);
        disclaimer3.setLayoutParams(linearParams1);

		LinearLayout linearDisclaimer = new LinearLayout(this);
		linearDisclaimer.setOrientation(LinearLayout.VERTICAL);
		//linearDisclaimer.setLayoutParams(linearParams1);
		
		linearDisclaimer.addView(disclaimer);
		linearDisclaimer.addView(disclaimer2);
		linearDisclaimer.addView(disclaimer3);
		
		
		//scrollView.setFillViewport(false);
		scrollView.setLayoutParams(new LinearLayout.LayoutParams(width,(height*65/100)));
		linearDisclaimer.setLayoutParams(new LinearLayout.LayoutParams(width,(height*35/100)));

        outertable.addView(scrollView);
        outertable.addView(linearDisclaimer);
        
        
        
        
        
        
        
        
        
        
        
        
        
		
		frame.addView(outertable);
		//ResultActivity.this.setContentView(scrollView);
		Log.v("element:", "kkdns");
		}
		catch(Exception e) {
			
		}
	}

	public void createTableTab2(final FrameLayout frame, JSONObject jsonResult){		
		/*disclaimer at the bottom*/
		TextView disclaimer2 = new TextView(this);
		TextView disclaimer3 = new TextView(this);
		disclaimer = new TextView(this);
		
		disclaimer.setText("\u00A9 Zillow, Inc., 2006-2014.");
		disclaimer2.setText(Html.fromHtml("<html>Use is subject to<a href=\"http://www.zillow.com/corp/Terms.htm\">Terms of Use</a></html>"));
		disclaimer3.setText(Html.fromHtml("<a href=\"http://www.zillow.com/zestimate/\">What's a Zestimate?</a>"));
			
		disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
		disclaimer2.setMovementMethod(LinkMovementMethod.getInstance());
		disclaimer3.setMovementMethod(LinkMovementMethod.getInstance());
		
		disclaimer.setPadding(0, 30, 0, 0);
		
		
		LinearLayout.LayoutParams linearParams1 = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        linearParams1.gravity = Gravity.CENTER;
        disclaimer.setLayoutParams(linearParams1);
        disclaimer2.setLayoutParams(linearParams1);
        disclaimer3.setLayoutParams(linearParams1);
		
		heading = new TextView(this);
		heading.setText("ekhfw");
		
		right = new Button(this);
		right.setText("Next");
		right.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
		right.setWidth(200);
		right.setHeight(80);
		
		left = new Button(this);
		left.setText("Previous");
		left.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
		left.setWidth(200);
		left.setHeight(80);
		
		imgSwitcher = new ImageSwitcher(this);
		imgSwitcher.setFactory(new ViewFactory() {
            public View makeView() {
                // TODO Auto-generated method stub
                
                    // Create a new ImageView set it's properties 
                    ImageView imageView = new ImageView(ResultActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
                    imageView.setPadding(0, 20, 0, 20);
                    imageView.getLayoutParams().height = (height*40/100);
                    return imageView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        
        // set the animation type to imageSwitcher
        imgSwitcher.setInAnimation(in);
        imgSwitcher.setOutAnimation(out);
        

        textSwitcher = new TextSwitcher(this);
        textSwitcherSub = new TextSwitcher(this);
        
        textSwitcher.setFactory(new ViewFactory() {
            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText = new TextView(ResultActivity.this);
                myText.setTypeface(null, Typeface.BOLD);
                myText.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                //myText.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setPadding(0, 30, 0, 0);
                //myText.setTextSize(36);
                return myText;
            }
        });
        textSwitcherSub.setFactory(new ViewFactory() {
            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText1 = new TextView(ResultActivity.this);
                //myText1.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
                myText1.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                //myText1.setPadding(0, 0, 0, 40);
                //myText1.setTextSize(36);
                return myText1;
            }
        });
        // set the animation type of textSwitcher
        textSwitcher.setInAnimation(in);
        textSwitcher.setOutAnimation(out);

        // set the animation type of textSwitcher
        textSwitcherSub.setInAnimation(in);
        textSwitcherSub.setOutAnimation(out);
        
		
		
		
		
		ButtonLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParamsButton = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        ButtonLayout.setLayoutParams(linearParamsButton);
        linearParamsButton.gravity = Gravity.CENTER;
        left.setLayoutParams(linearParamsButton);
        right.setLayoutParams(linearParamsButton);
		
		linearTab2 = new LinearLayout(this); 
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        linearParams.gravity = Gravity.CENTER;
        linearTab2.setLayoutParams(linearParams);
        linearTab2.setOrientation(LinearLayout.VERTICAL);
        
		//chart = new ImageView(ResultActivity.this);
		//chart.setLayoutParams(new LinearLayout.LayoutParams(500,500));
        

		
        
        nextPressed = false;
        chartURL = new String[4];
        chartHeading = new String[4];
        try {
        	chartHeading[0] = "Historical Zestimates for the past 1 year\n";
        	chartHeading[1] = "Historical Zestimates for the past 5 years\n";
        	chartHeading[2] = "Historical Zestimates for the past 10 years\n";
        	
        	chartHeadingSub = propertyAddress;
			
        	chartURL[0] = jsonChart.getString("oneyear");
			chartURL[1] = jsonChart.getString("fiveyears");
			chartURL[2] = jsonChart.getString("tenyears");
			
			Log.v("chart1:", chartURL[0] );
			Log.v("chart2:", chartURL[1] );
			Log.v("chart3:", chartURL[2] );
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}       
        
        counter = 0;
        /*onpress of next button*/
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	//frame.removeAllViews();
            	
            	nextPressed = true;
            	
            	if(counter >= 2){
            		counter =0;
            	}
            	else {
            		counter++;
            	}
            	Log.v("chart1:", chartURL[counter]);
        	   	URL url = null;
   				try {
   					url = new URL(chartURL[counter]);
   				} catch (MalformedURLException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}
   				MyDownloadTask d = new MyDownloadTask();
   				d.execute(url);

            }
        });
        
        /*onpress of prev button*/
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	//frame.removeAllViews();
            	
            	nextPressed = true;
            	
            	if(counter <= 0){
            		counter =2;
            	}
            	else {
            		counter--;
            	}
            	Log.v("chart1:", chartURL[counter]);
        	   	URL url = null;
   				try {
   					//url = new URL("https://www.google.com/");
   					url = new URL(chartURL[counter]);
   				} catch (MalformedURLException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}
   				//((ViewGroup)frame.getParent()).removeView(frame);
   				MyDownloadTask d = new MyDownloadTask();
   				d.execute(url);
   				//textSwitcher.setText(chartHeading[counter]);
   				//textSwitcherSub.setText(chartHeadingSub);
            }
        });
        
        
        
        //frame.removeAllViews();
        
        
        
        if(nextPressed == false) {
        	Log.v("chart1:", "inside next pressed false" );
		   URL url = null;
			try {
				//url = new URL("https://www.google.com/");
				url = new URL(chartURL[0]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			MyDownloadTask d = new MyDownloadTask();
			d.execute(url);
	        //textSwitcher.setText(chartHeading[0]);
			//textSwitcherSub.setText(chartHeadingSub);
			Log.v("heading", chartHeading[0]);
			Log.v("sub heading", chartHeadingSub);
	      
        }
  
		//chart.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //chart.setLayoutParams(new ImageSwitcher.LayoutParams(
        //        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        frame.removeAllViews();
        linearTab2.addView(textSwitcher);
        linearTab2.addView(textSwitcherSub); 
        linearTab2.addView(imgSwitcher);
        ButtonLayout.addView(left);
        ButtonLayout.addView(right);
        linearTab2.addView(ButtonLayout);
        linearTab2.addView(disclaimer);
        linearTab2.addView(disclaimer2);
        linearTab2.addView(disclaimer3);
        frame.addView(linearTab2);

        //frame.addView(linearTab2);
        
		Log.v("element:", "kkdns");
		
	}
	
	
	/*imageview chart*/
	private class MyDownloadTask extends AsyncTask<URL, Integer, Bitmap> {
	    @Override
	    protected Bitmap doInBackground(URL... params) {
	        URL url = params[0];
	        Bitmap bitmap = null;
	        try {
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            InputStream is = connection.getInputStream();
	            bitmap = BitmapFactory.decodeStream(is);

	            is.close();
	            //is.close(); THIS IS THE BROKEN LINE
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }

	        return bitmap;
	    }


	    protected void onPostExecute(Bitmap bitmap) {
	    	if (bitmap != null) {

	            //chart.setImageBitmap(bitmap);

	            Drawable drawImg =new BitmapDrawable(getResources(),bitmap);
	            imgSwitcher.setImageDrawable(drawImg);	            
   				textSwitcher.setText(chartHeading[counter]);
   				textSwitcherSub.setText(chartHeadingSub);	            

	        } else {
	            Toast.makeText(getApplicationContext(), "Failed to Download Image", Toast.LENGTH_LONG).show();
	        }

	    }       
	}
	
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_result,
					container, false);
			return rootView;
		}
	}
}
