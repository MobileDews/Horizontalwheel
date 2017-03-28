# Horizontalwheel


Horizontal wheel to select values in horizontal direction.

Demo

![Alt text](https://github.com/MobileDews/Horizontalwheel/blob/master/horizontalwheel.png?raw=true "Optional Title")

GRADLE

Step 1. Add the JitPack repository to your build file


	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
Step 2. Add the dependency
	
	dependencies {
    	        compile 'com.github.MobileDews:Horizontalwheel:-SNAPSHOT'
    	}
    	
    	
    	
USAGE
    	
    	<com.techdew.lib.HorizontalWheel.HorizontalView
                android:id="@+id/HorizontalView"
                android:layout_centerInParent="true"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
                
                
 Add the following code to the MainActivity.java
                
                public class MainActivity extends AppCompatActivity implements OnWheelScrollListener {
                
                    String[] values;
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.activity_main);
                
                        AbstractWheel abstractWheel = (AbstractWheel) findViewById(R.id.HorizontalView);
                        values = new String[100];
                        for (int i = 0; i < 100; i++) {
                            values[i] = String.valueOf(25 * (i));
                        }
                
                        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(MainActivity.this, values);
                        ampmAdapter.setItemResource(R.layout.horizontal_wheel_text_centered);
                        ampmAdapter.setItemTextResource(R.id.text);
                        abstractWheel.setViewAdapter(ampmAdapter);
                        abstractWheel.addScrollingListener(this);
                    }
                
                    @Override
                    public void onScrollingStarted(AbstractWheel wheel) {
                
                    }
                
                    @Override
                    public void onScrollingFinished(AbstractWheel wheel) {
                
                        Toast.makeText(getApplicationContext(),""+values[wheel.getCurrentItem()],Toast.LENGTH_LONG).show();
                    }
                }
                

