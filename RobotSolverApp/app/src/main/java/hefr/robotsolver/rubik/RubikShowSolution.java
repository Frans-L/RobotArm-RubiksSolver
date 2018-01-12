package hefr.robotsolver.rubik;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hefr.robotsolver.R;
import hefr.robotsolver.utils.Util;

/**
 * Created by Frans on 11/01/2018.
 */

public class RubikShowSolution extends Activity {

    public static boolean running = false;
    private TextView output;
    private RubikCube cube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        running = true;

        Bundle params = getIntent().getExtras();
        if (params != null) {
            cube = (RubikCube) params.get("cube");
        } else {
            finish(); //if no cube, stop activity
        }

        setContentView(R.layout.show_solution); //load content
        setResult(Activity.RESULT_CANCELED); //if nothing selected, return cancelled

        output = (TextView) findViewById(R.id.solutionOutput);
        output.setText("Loading...");

        RubikSolveRequest request = new RubikSolveRequest(new AfterRequest(), cube);
        request.execute();

    }

    private class AfterRequest implements Callable {
        public void updateSolution(String solution) {
            output.setText(solution);
        }
    }


    public interface Callable {
        void updateSolution(String solution);
    }
}