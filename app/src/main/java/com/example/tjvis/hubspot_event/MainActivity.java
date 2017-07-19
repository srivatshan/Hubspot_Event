package com.example.tjvis.hubspot_event;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TextView startDateDisplay;
    private TextView endDateDisplay;
    private Button startPickDate;
    private Button endPickDate,b3;
    private Calendar startDate;
    private Calendar endDate;
    String sts,fts;
    static final int DATE_DIALOG_ID = 0;
    private TextView activeDateDisplay;
    private Calendar activeDate;
    List<PieEntry> pieEntries=new ArrayList<>();
    PieChart chart;

    ArrayList<String> etype=new ArrayList<>();
    ArrayList<String> edate=new ArrayList<>();
    ArrayList<String> all=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startDateDisplay = (TextView) findViewById(R.id.tv1);

        chart=(PieChart)findViewById(R.id.chart);
        startPickDate = (Button) findViewById(R.id.b1);
        b3 = (Button) findViewById(R.id.b3);
        startDate = Calendar.getInstance();
        startPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(startDateDisplay, startDate);
            }
        });


        endDateDisplay = (TextView) findViewById(R.id.tv2);
        endPickDate = (Button) findViewById(R.id.b2);


        endDate = Calendar.getInstance();


        endPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(endDateDisplay, endDate);
            }
        });


        updateDisplay(startDateDisplay, startDate);
        updateDisplay(endDateDisplay, endDate);


    }
    public void b3(View v)
    {
        pieEntries.clear();
        etype.clear();
        edate.clear();
        all.clear();
        new getdata(this).execute();
    }

    private void updateDisplay(TextView dateDisplay, Calendar date) {
        if(dateDisplay.getId()==R.id.tv1) {

            dateDisplay.setText(
                    new StringBuilder()

                            .append(date.get(Calendar.DAY_OF_MONTH)).append("/")
                            .append(date.get(Calendar.MONTH) + 1).append("/")
                            .append(date.get(Calendar.YEAR)).append(" "));
            int day=(date.get(Calendar.DAY_OF_MONTH));
            int month=(date.get(Calendar.MONTH) + 1);
            int year=(date.get(Calendar.YEAR));
            GregorianCalendar calendar = new GregorianCalendar(year, (month-1), day);
            long da=calendar.getTimeInMillis();
            sts=Long.toString(da);
            Toast.makeText(this, "start"+sts, Toast.LENGTH_SHORT).show();

        }
        else if(dateDisplay.getId()==R.id.tv2)
        {
            dateDisplay.setText(
                    new StringBuilder()
                            .append(date.get(Calendar.DAY_OF_MONTH)).append("/")
                            .append(date.get(Calendar.MONTH) + 1).append("/")
                            .append(date.get(Calendar.YEAR)).append(" "));
            int day=(date.get(Calendar.DAY_OF_MONTH));
            int month=(date.get(Calendar.MONTH) + 1);
            int year=(date.get(Calendar.YEAR));
            GregorianCalendar calendar = new GregorianCalendar(year, (month-1), day);

            long ma=calendar.getTimeInMillis();
            fts=Long.toString(ma);
            Toast.makeText(this, "start"+fts, Toast.LENGTH_SHORT).show();
        }

    }

    public void showDateDialog(TextView dateDisplay, Calendar date) {
        activeDateDisplay = dateDisplay;
        activeDate = date;
        showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            activeDate.set(Calendar.YEAR, year);
            activeDate.set(Calendar.MONTH, monthOfYear);
            activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDisplay(activeDateDisplay, activeDate);
            unregisterDateDisplay();
        }
    };

    private void unregisterDateDisplay() {
        activeDateDisplay = null;
        activeDate = null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }


    class getdata extends AsyncTask<String,Void,String> {
        private Context context;
        public int res;
        public getdata(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... arg0) {
            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {
                link = "https://api.hubapi.com/calendar/v1/events?startDate="+sts+"&endDate="+fts+"&hapikey=demo";
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
                return result;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute (String result){

            String jsonStr = result;
            if (jsonStr != null) {
                try {

                    JSONArray ar=new JSONArray(jsonStr);
                    int size=ar.length();
                    for(int i=0;i<ar.length();i++)
                    {
                        JSONObject ob=ar.getJSONObject(i);
                        etype.add(ob.getString("eventType"));
                        String da=ob.getString("eventDate");
                        Date d=new Date(Long.valueOf(da));
                        String day          = (String) DateFormat.format("dd",   d);
                        String monthNumber  = (String) DateFormat.format("MM",   d);
                        String year         = (String) DateFormat.format("yyyy", d);
                        String hour         = (String) DateFormat.format("HH", d);
                        String min          = (String) DateFormat.format("mm", d);
                        edate.add(day+"-"+monthNumber+"-"+year+" "+hour+"-"+min);
                        all.add(etype.get(i)+"\n"+edate.get(i));
                        pieEntries.add(new PieEntry(1,all.get(i)));
                    }
                    Toast.makeText(context, ""+all.size(), Toast.LENGTH_SHORT).show();

                    PieDataSet dataset=new PieDataSet(pieEntries,"SCHEDULES");
                    dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    PieData data=new PieData(dataset);
                    data.setValueTextSize(2);
                    chart.setData(data);
                    chart.setTouchEnabled(true);
                    chart.invalidate();
                    chart.animateY(500);

                    chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            PieEntry pe = (PieEntry) e;
                            AlertDialog.Builder b=new  AlertDialog.Builder(context);
                            b.setTitle( Html.fromHtml("<font color='#FF7F27'>SELECTED EVENT</font>"));

                            b.setTitle("Selceted Event");
                            b.setMessage(pe.getLabel());
                            b.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            b.show();
                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in connection"+e, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Couldn't get to Database.", Toast.LENGTH_SHORT).show();
            }



        }


    }


}


