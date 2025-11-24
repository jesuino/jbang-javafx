//JAVA 25+
//DEPS org.openjfx:javafx-controls:25.0.1:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:25.0.1:${os.detected.jfxname}
//DEPS org.openjfx:javafx-web:25.0.1:${os.detected.jfxname}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.scene.layout.StackPane;

public class P5JS extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
	    var webView = new WebView();
	    stage.setScene(new Scene(new StackPane(webView), 400, 400));
	    stage.show();

	    webView.getEngine().loadContent("""
		<html lang="en">
              <head>
                <style>
                  html, body {
                    margin: 0;
                    padding: 0;
                  }
                  canvas {
                    display: block;
                  }

                </style>
                <script src="https://cdn.jsdelivr.net/npm/p5@1.11.11/lib/p5.js"></script>
                <meta charset="utf-8" />
              </head>
              <body>
                <main>
                </main>
                <script>
                    function setup() {
                        createCanvas(400, 400);
                    }
                    function draw() {
                        circle(100, 100, 10); 
                    }
              </script>
              </body>
            </html>		    
			    
	    """);
    }


}
