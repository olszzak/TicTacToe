package sample;

import com.sun.xml.internal.ws.message.ProblemActionHeader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    private boolean turnX = true;
    private boolean playable = true;
    private Tile[][] board = new Tile[3][3];
    private List<Combo> combos = new ArrayList<>();
    private Pane root = new Pane();

    private Parent createContent(){
        root.setPrefSize(600,600);

        for (int i=0; i<3;i++){
            for (int j=0;j<3;j++){
                Tile tile = new Tile();
                tile.setTranslateX(j*200);
                tile.setTranslateY(i*200);

                root.getChildren().add(tile);

                board[i][j] = tile;
            }
        }

        for (int y=0; y<3; y++){
            combos.add(new Combo(board[0][y], board[1][y], board[2][y]));
        }
        for (int x=0; x<3; x++){
            combos.add(new Combo(board[x][0], board[x][1], board[x][2]));
        }

        combos.add(new Combo(board[0][0], board[1][1], board[2][2]));
        combos.add(new Combo(board[0][2], board[1][1], board[2][0]));

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    private class Combo {
        private Tile[] tiles;
        public Combo(Tile... tiles) {
            this.tiles = tiles;
        }

        private boolean isComplete(){
            if (tiles[0].getValue().isEmpty())
                return false;
            return tiles[0].getValue().equals(tiles[1].getValue())
                    && tiles[0].getValue().equals(tiles[2].getValue());
        }
    }

    private void checkState(){
        for (Combo combo: combos){
            if (combo.isComplete()){
                playable = false;
                playWinAnimation(combo);
                break;
            }
        }
    }

    private void playWinAnimation(Combo combo) {
        Line line = new Line();
        line.setStartX(combo.tiles[0].getCenterX());
        line.setStartY(combo.tiles[0].getCenterY());
        line.setEndX(combo.tiles[0].getCenterX());
        line.setEndY(combo.tiles[0].getCenterY());

        root.getChildren().add(line);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                new KeyValue(line.endXProperty(), combo.tiles[2].getCenterX()),
                new KeyValue(line.endYProperty(), combo.tiles[2].getCenterY())));
        timeline.play();
    }

    private class Tile extends StackPane {
        private Text text = new Text();

        public Tile() {
            Rectangle border = new Rectangle(200,200);
            border.setFill(null);
            border.setStroke(Color.BLACK);

            text.setFont(Font.font(80));
            setAlignment(Pos.CENTER);
            getChildren().addAll(border, text);

            setOnMouseClicked(e ->{
                if (!playable)
                    return;
                if(e.getButton() == MouseButton.PRIMARY){
                    if (!turnX)
                        return;
                    drawX();
                }else if (e.getButton() == MouseButton.SECONDARY){
                    if (turnX)
                        return;
                    drawO();
                }
            });
        }

        private void drawX(){
            text.setText("X");
            turnX = false;
            checkState();
        }

        private void drawO(){
            text.setText("O");
            turnX = true;
            checkState();
        }

        public double getCenterX() {
            return getTranslateX() + 100;
        }

        public double getCenterY() {
            return getTranslateY() + 100;
        }

        public String getValue(){
            return text.getText();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
