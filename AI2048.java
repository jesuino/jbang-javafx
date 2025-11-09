
//JAVA 21+
//DEPS org.openjfx:javafx-controls:21:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:21:${os.detected.jfxname}
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AI2048 extends Application {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int BOARD_COLS = 4;
    private static final int BOARD_ROWS = 4;
    private static final int TILE_BORDER = 5;

    private Map<Integer, Color> tileColors = new HashMap<>() {
        {
            put(0, Color.LIGHTGRAY);
            put(2, Color.BEIGE);
            put(4, Color.BISQUE);
            put(8, Color.ORANGE);
            put(16, Color.DARKORANGE);
            put(32, Color.CORAL);
            put(64, Color.TOMATO);
            put(128, Color.GOLD);
            put(256, Color.GOLDENROD);
            put(512, Color.YELLOWGREEN);
            put(1024, Color.LIGHTGREEN);
            put(2048, Color.GREEN);
        }
    };

    int[][] board = new int[BOARD_ROWS][BOARD_COLS];

    Random RAND;

    int TILE_WIDTH = WIDTH / BOARD_COLS;
    int TILE_HEIGHT = HEIGHT / BOARD_ROWS;
    private GraphicsContext ctx;

    @Override
    public void start(Stage stage) {
        var canvas = new Canvas(WIDTH, HEIGHT);
        var scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("2048");

        RAND = new Random(System.currentTimeMillis());
        ctx = canvas.getGraphicsContext2D();
        initBoard();
        drawBoard();
        canvas.requestFocus();

        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                initBoard();
            }

            var snapshot = boardSnapshot();

            if (e.getCode() == KeyCode.LEFT) {
                moveLeft();
            }

            if (e.getCode() == KeyCode.RIGHT) {
                moveRight();
            }

            if (e.getCode() == KeyCode.UP) {
                moveUp();
            }

            if (e.getCode() == KeyCode.DOWN) {
                moveDown();
            }

            for (int i = 0; i < snapshot.length; i++) {
                if (Arrays.compare(snapshot[i], board[i]) != 0) {
                    addValueToBoard();
                    break;
                }
            }
        });

        final var frames = 30;
        var frame = new KeyFrame(Duration.millis(1000 / frames), e -> draw());
        var timeline = new Timeline(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private int[][] boardSnapshot() {
        var snapshot = new int[BOARD_ROWS][BOARD_COLS];
        for (int i = 0; i < snapshot.length; i++) {
            snapshot[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return snapshot;
    }

    void draw() {
        ctx.clearRect(0, 0, WIDTH, HEIGHT);
        drawBoard();
    }

    void initBoard() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                board[row][col] = 0;
            }
        }
        addValueToBoard();
        addValueToBoard();
    }

    void addValueToBoard() {
        var emptyPositions = new ArrayList<Integer[]>();
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                if (board[row][col] == 0)
                    emptyPositions.add(new Integer[] { row, col });
            }
        }
        var i = RAND.nextInt(emptyPositions.size());
        var xy = emptyPositions.get(i);
        board[xy[0]][xy[1]] = Math.random() < 0.7 ? 2 : 4;
    }

    void moveLeft() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS - 1; col++) {
                shiftLeft(row, col);
                merge(row, col, row, col + 1);
            }
        }
    }

    void moveRight() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = BOARD_COLS - 1; col > -1; col--) {
                shiftRight(row, col);
                merge(row, col, row, col - 1);
            }
        }
    }

    void moveUp() {
        for (int col = 0; col < BOARD_COLS; col++) {
            for (int row = 0; row < BOARD_ROWS; row++) {
                shiftUp(row, col);
                merge(row, col, row + 1, col);
            }
        }
    }

    void moveDown() {
        for (int col = 0; col < BOARD_COLS; col++) {
            for (int row = BOARD_ROWS - 1; row > -1; row--) {
                shiftDown(row, col);
                merge(row, col, row - 1, col);
            }
        }
    }

    private void shiftLeft(int row, int fromCol) {
        for (int col = fromCol; col < board[row].length; col++) {
            for (int i = col + 1; i < board[row].length && board[row][col] == 0; i++) {
                swapZero(row, col, row, i);
            }
        }
    }

    private void shiftRight(int row, int fromCol) {
        for (int col = fromCol; col > -1; col--) {
            for (int nextCol = col - 1; nextCol >= 0 && board[row][col] == 0; nextCol--) {
                swapZero(row, col, row, nextCol);
            }
        }
    }

    private void shiftUp(int fromRow, int col) {
        for (int row = fromRow; row < BOARD_ROWS; row++) {
            for (int nextRow = row + 1; nextRow < BOARD_ROWS && board[row][col] == 0; nextRow++) {
                swapZero(row, col, nextRow, col);
            }
        }
    }

    private void shiftDown(int fromRow, int col) {
        for (int row = fromRow; row > -1; row--) {
            for (int nextRow = row - 1; nextRow > -1 && board[row][col] == 0; nextRow--) {
                swapZero(row, col, nextRow, col);
            }
        }
    }

    private void swapZero(int row, int col, int destRow, int destCol) {
        if (board[destRow][destCol] != 0) {
            board[row][col] = board[destRow][destCol];
            board[destRow][destCol] = 0;
        }
    }

    private void merge(int row, int col, int nextRow, int nextCol) {
        if (nextRow >= 0 && nextRow < BOARD_ROWS &&
                nextCol >= 0 && nextCol < BOARD_COLS) {
            var val = board[row][col];
            if (val != 0 && val == board[nextRow][nextCol]) {
                board[row][col] = board[row][col] + board[nextRow][nextCol];
                board[nextRow][nextCol] = 0;
            }
        }
    }

    void drawBoard() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                var xPos = col * TILE_WIDTH;
                var yPos = row * TILE_HEIGHT;
                var value = board[row][col];
                ctx.setFill(tileColors.get(value));
                ctx.fillRoundRect(xPos, yPos,
                        TILE_WIDTH - TILE_BORDER,
                        TILE_HEIGHT - TILE_BORDER,
                        10, 10);
                if (value != 0) {
                    ctx.setFill(Color.DARKGREY);
                    ctx.setTextAlign(TextAlignment.CENTER);
                    ctx.setTextBaseline(VPos.CENTER);
                    ctx.setFont(Font.font(35));
                    ctx.fillText("" + value,
                            xPos + TILE_WIDTH / 2,
                            yPos + TILE_HEIGHT / 2);
                    ctx.setStroke(Color.SILVER);                    
                    ctx.strokeText("" + value,
                            xPos + TILE_WIDTH / 2,
                            yPos + TILE_HEIGHT / 2);

                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

}