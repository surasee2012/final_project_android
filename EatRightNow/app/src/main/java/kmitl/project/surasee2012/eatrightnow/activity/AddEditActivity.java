package kmitl.project.surasee2012.eatrightnow.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import kmitl.project.surasee2012.eatrightnow.R;
import kmitl.project.surasee2012.eatrightnow.sqliteDB.FoodDbAdapter;
import kmitl.project.surasee2012.eatrightnow.model.FoodItemWithTagsAndRes;
import kmitl.project.surasee2012.eatrightnow.model.Message;

public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText foodNameEt, foodCalEt;
    private TextView foodResTv;
    private Switch favSwitch;
    private CheckBox[] tagCBs;

    private FoodDbAdapter foodDbAdapter;
    private Message message;

    private boolean isEdit;
    private int[] tagIDs = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    private int foodID;
    private String foodRes = "";

    private final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        foodDbAdapter = new FoodDbAdapter(this);
        message = new Message(this);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        foodID = getIntent().getIntExtra("foodID", 0);

        initView();

        if (isEdit) {
            getFoodData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearFavResBtn:
                foodResTv.setText("");
                foodResTv.setVisibility(View.GONE);
                break;
            case  R.id.pickFavResBtn:
                startPlacePicker();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save:
                try {
                    saveFoodData();
                } catch (Exception e) {
                    message.alert("ขออภัย ข้อมูลไม่ถูกต้อง");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = builder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                foodRes = place.getName() + " " + place.getAddress();
                if (foodRes.equals(" ")) {
                    message.alert("ข้อมูลร้านอาหารไม่ถูกต้อง กรุณาลองอีกครั้ง");
                } else {
                    foodResTv.setText(foodRes);
                    foodResTv.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public String convertSingleQuote(String string) {
        String newString = "";
        for (char c: string.toCharArray()) {
            newString += c;
            if (c == '\'') {
                newString += '\'';
            }
        }
        return newString;
    }

    public void initView() {
        getSupportActionBar().setTitle("เพิ่มอาหาร");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button clearFavResBtn = findViewById(R.id.clearFavResBtn);
        Button pickFavResBtn = findViewById(R.id.pickFavResBtn);
        clearFavResBtn.setOnClickListener(this);
        pickFavResBtn.setOnClickListener(this);

        foodNameEt = findViewById(R.id.foodNameEt);
        foodCalEt = findViewById(R.id.foodCalEt);
        foodResTv = findViewById(R.id.foodResTv);

        favSwitch = findViewById(R.id.favSwitch);

        tagCBs = new CheckBox[9];
        int[] tagsCbID = {R.id.eggCb, R.id.chicDuckCb, R.id.porkCb, R.id.beefCb, R.id.fishCb
                , R.id.seafoodCb, R.id.vegCb, R.id.riceCb, R.id.noodleCb};
        for (int i=0;i<9;i++) {
            tagCBs[i] = findViewById(tagsCbID[i]);
        }
    }

    public void getFoodData() {
        getSupportActionBar().setTitle("แก้ไขอาหาร");
        FoodItemWithTagsAndRes foodItemWithTagsAndRes = foodDbAdapter.getFoodItemWithTags(foodID);
        foodNameEt.setText(foodItemWithTagsAndRes.getFood_Name(), TextView.BufferType.EDITABLE);
        foodNameEt.setKeyListener(null);
        foodCalEt.setText(String.valueOf(foodItemWithTagsAndRes.getFood_Calories()), TextView.BufferType.EDITABLE);
        if (foodItemWithTagsAndRes.getFood_Favorite() == 1) {
            favSwitch.setChecked(true);
        }
        for(int i=0;i<9;i++) {
            for(Integer foodTag : foodItemWithTagsAndRes.getTags()) {
                if (tagIDs[i] == foodTag) {
                    tagCBs[i].setChecked(true);
                    break;
                }
            }
        }
        if (foodItemWithTagsAndRes.getFood_Restaurant() != null) {
            foodResTv.setText(foodItemWithTagsAndRes.getFood_Restaurant());
            foodResTv.setVisibility(View.VISIBLE);
        }
    }

    public void saveFoodData() throws Exception {
        String action;
        String foodName = spaceLeadCheck(foodNameEt.getText().toString());
        Integer foodCal = Integer.parseInt(foodCalEt.getText().toString());
        Integer foodFav;
        if (favSwitch.isChecked()) {
            foodFav = 1;
        } else {
            foodFav = 0;
        }

        if (isEdit) {
            if (!foodRes.equals("")) {
                foodDbAdapter.updateFood(foodName, foodCal, foodFav, convertSingleQuote(foodRes));
            } else {
                foodDbAdapter.updateFood(foodName, foodCal, foodFav);
            }
            foodDbAdapter.deleteTags(foodID);
            action = "บันทึก";
        } else {
            if (!foodRes.equals("")) {
                foodDbAdapter.addFood(foodName, foodCal, foodFav, convertSingleQuote(foodRes));
            } else {
                foodDbAdapter.addFood(foodName, foodCal, foodFav);
            }
            action = "เพิ่ม";
        }

        for (int i=0;i<9;i++) {
            if (tagCBs[i].isChecked()) {
                foodDbAdapter.addTag(foodDbAdapter.getFoodID(foodName), tagIDs[i]);
            }
        }

        message.setToast(this, action + foodName + "สำเร็จแล้ว");
        finish();
    }

    public String spaceLeadCheck(String string) throws Exception{
        boolean isSpaceLead = true;
        String newString = "";
        for (char c: string.toCharArray()) {
            if (c != ' ') {
                isSpaceLead = false;
            }
            if(!isSpaceLead) {
                newString += c;
            }
        }
        if (isSpaceLead) {
            throw new Exception();
        }
        return newString;
    }
}
