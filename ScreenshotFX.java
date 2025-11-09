//JAVA 25+
//DEPS org.openjfx:javafx-controls:25.0.1:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:25.0.1:${os.detected.jfxname}

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.Toolkit;
import java.util.concurrent.atomic.AtomicReference;

// On Fedora you must run with the following system properties:
// jbang -Dawt.robot.screenshotDebug=true -Dawt.robot.screenshotMethod=dbusScreencast ScreenshotFX.java 

public class ScreenshotFX extends Application {

    private static final double APP_WIDTH = 400;
    private static final double APP_HEIGHT = 300;
    private final Robot robot = new Robot();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        var btnCapture = new Button("Click to Capture");
        var parent = new BorderPane();
        parent.setTop(btnCapture);
        stage.setScene(new Scene(parent, APP_WIDTH, APP_HEIGHT));
        stage.setTitle("Print Screen");
        stage.show();

        var dimensions = Toolkit.getDefaultToolkit().getScreenSize();
        var captureWindow = new Stage();
        var dragRect = new Rectangle();
        var captureRoot = new AnchorPane(dragRect);

        captureWindow.initOwner(stage);
        captureWindow.initStyle(StageStyle.TRANSPARENT);
        dragRect.setVisible(false);
        captureRoot.setCursor(Cursor.CROSSHAIR);
        captureRoot.setPrefSize(dimensions.getWidth(), dimensions.getHeight());
        captureWindow.setScene(new Scene(captureRoot,
                dimensions.getWidth(),
                dimensions.getHeight(),
                Color.TRANSPARENT));
        var initX = new AtomicReference<Double>();
        var initY = new AtomicReference<Double>();

        captureWindow.setOnShown(e -> captureRoot.setOpacity(0.3));
        captureRoot.setOnMousePressed(e -> {            
            initX.set(e.getScreenX());
            initY.set(e.getScreenY());
        });
        captureRoot.setOnMouseDragged(e -> {
            dragRect.setVisible(true);
            var xDiff = e.getScreenX() - initX.get();
            var yDiff = e.getScreenY() - initY.get();
            if (xDiff >= 0) {
                dragRect.setX(initX.get());
                dragRect.setWidth(xDiff);
            } else {
                dragRect.setX(e.getScreenX());
                dragRect.setWidth(Math.abs(xDiff));
            }
            if (yDiff >= 0) {
                dragRect.setY(initY.get());
                dragRect.setHeight(yDiff);
            } else {
                dragRect.setY(e.getScreenY());
                dragRect.setHeight(Math.abs(yDiff));
            }

        });
        captureRoot.setOnMouseReleased(e -> {
            dragRect.setVisible(false);
            captureRoot.setOpacity(0.0);
            var pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(_ -> {
                var img = capture(dragRect.getX(), dragRect.getY(), dragRect.getWidth(), dragRect.getHeight());
                parent.setCenter(img);
                stage.show();
                captureWindow.hide();
            });
            pause.play();
        });

        btnCapture.setOnAction(e -> {
            stage.hide();
            captureWindow.show();
            captureWindow.requestFocus();
        });
    }

    private ScrollPane capture(double x, double y, double width, double height) {
        var writableImage = robot.getScreenCapture(null, x, y, width, height);
        return new ScrollPane(new ImageView(writableImage));
    }

}
