package com.example.avoidobstacles;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.example.avoidobstacles.GameManager.GameManager;



import kotlinx.coroutines.Delay;


public class MainActivity extends AppCompatActivity {

    private LinearLayoutCompat gameArea;
    private ExtendedFloatingActionButton leftButton, rightButton;
    private Handler handler = new Handler();
    private ImageView[][] grid = new ImageView[6][3]; // will update later to 5 columns for additional lanes
    private ImageView[] boatImages = new ImageView[3]; // will update later to 5 columns for additional lanes
    private AppCompatImageView[] hearts = new AppCompatImageView[3];
    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initGridAndBoats();

        gameManager = new GameManager(this, handler, new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.moveBoat(-1);
                updateUI();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.moveBoat(1);
                updateUI();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameManager.resetGame(); // Reset game state when the activity starts
        gameManager.startGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameManager.stopGame();
    }

    private void initGridAndBoats() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 3; col++) {
                int resId = getResources().getIdentifier("obstacle_" + row + col, "id", getPackageName());
                grid[row][col] = findViewById(resId);
            }
        }

        for (int col = 0; col < 3; col++) { 
            int resId = getResources().getIdentifier("boat_" + col, "id", getPackageName());
            boatImages[col] = findViewById(resId);
        }
    }

    private void findViews() {
        gameArea = findViewById(R.id.game_area);
        leftButton = findViewById(R.id.main_LBL_left);
        rightButton = findViewById(R.id.main_LBL_right);
        hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };
    }

    private void updateUI() {
        // Update obstacle visibility based on the logic matrix
        int[][] logicMatrix = gameManager.getLogicMatrix();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 3; col++) {
                if (logicMatrix[row][col] == 1) {
                    grid[row][col].setVisibility(View.VISIBLE);
                } else {
                    grid[row][col].setVisibility(View.INVISIBLE);
                }
            }
        }

        // Update boat visibility based on the boat logic array
        int[] boatLogicArray = gameManager.getBoatLogicArray();
        for (int col = 0; col < 3; col++) {
            if (boatLogicArray[col] == 1) {
                boatImages[col].setVisibility(View.VISIBLE);
            } else {
                boatImages[col].setVisibility(View.INVISIBLE);
            }
        }

        // Update hearts visibility based on the number of lives
        int lives = gameManager.getLives();
        for (int i = 0; i < hearts.length; i++) {
            if (i < lives) {
                hearts[i].setVisibility(View.VISIBLE);
            } else {
                hearts[i].setVisibility(View.INVISIBLE);
            }
        }
    }
}