package com.example.avoidobstacles.GameManager;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageView;
import java.util.Random;

public class GameManager {

    private final int ROWS = 6;
    private final int COLS = 3; 
    private final int UPDATE_INTERVAL = 2000; // update every 2 seconds

    private Context context;
    private Handler handler;
    private Random random;
    private int[][] logicMatrix;
    private int[] boatLogicArray;
    private int boatPosition;
    private int lives;
    private int iteration;

    private boolean isGameRunning;
    private Runnable updateCallback;

    public GameManager(Context context, Handler handler, Runnable updateCallback) {
        this.context = context;
        this.handler = handler;
        this.random = new Random();
        this.logicMatrix = new int[ROWS][COLS];
        this.boatLogicArray = new int[COLS];
        this.updateCallback = updateCallback;
        resetGame();
    }

    public void startGame() {
        if (!isGameRunning) {
            isGameRunning = true;
            handler.post(updateObstaclePosition);
        }
    }

    public void stopGame() {
        if (isGameRunning) {
            isGameRunning = false;
            handler.removeCallbacks(updateObstaclePosition);
        }
    }

    public void resetGame() {
        boatPosition = COLS / 2;
        lives = 3;
        iteration = 0;
        isGameRunning = false;

        // Clear the logic matrix
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                logicMatrix[row][col] = 0;
            }
        }

        // Clear boat logic array
        for (int col = 0; col < COLS; col++) {
            boatLogicArray[col] = 0;
        }

        boatLogicArray[boatPosition] = 1;
    }

    public void moveBoat(int direction) {
        boatLogicArray[boatPosition] = 0;
        boatPosition += direction;
        if (boatPosition < 0) boatPosition = 0;
        if (boatPosition >= COLS) boatPosition = COLS - 1;
        boatLogicArray[boatPosition] = 1;
    }

    public int[][] getLogicMatrix() {
        return logicMatrix;
    }

    public int[] getBoatLogicArray() {
        return boatLogicArray;
    }

    public int getLives() {
        return lives;
    }

    private Runnable updateObstaclePosition = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning) return;

            moveObstaclesDown();
            clearTopRow();
            if (iteration % 2 == 0) {
                addNewObstacles();
            }
            iteration++;
            checkCollisions();

            // Trigger the UI update in MainActivity
            updateCallback.run();

            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    private void moveObstaclesDown() {
        for (int row = ROWS - 1; row > 0; row--) {
            for (int col = 0; col < COLS; col++) {
                logicMatrix[row][col] = logicMatrix[row - 1][col];
            }
        }
    }

    private void clearTopRow() {
        for (int col = 0; col < COLS; col++) {
            logicMatrix[0][col] = 0;
        }
    }

    private void addNewObstacles() {
        int randomCol = random.nextInt(COLS);
        logicMatrix[0][randomCol] = 1;
    }

    private void checkCollisions() {
        for (int col = 0; col < COLS; col++) {
            if (logicMatrix[ROWS - 1][col] == 1 && boatLogicArray[col] == 1) {
                lives--;
                toastAndVibrate();
                if (lives <= 0) {
                    stopGame();
                }
            }
        }
    }
    private void toastAndVibrate() {
        Toast.makeText(context, "hit", Toast.LENGTH_SHORT).show();
        vibrate();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }
}
