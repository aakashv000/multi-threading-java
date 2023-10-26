package org.example.thread.lock;

import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class PriceUpdator extends Thread {
    private Random random = new Random();

    private PricesContainer pricesContainer;
    public PriceUpdator(PricesContainer pricesContainer) {
        this.pricesContainer = pricesContainer;
    }

    @Override
    public void run() {
        while(true) {
            pricesContainer.getLockObj().lock();
            // will remain suspended until is able to acquire lock

            try {
                // simulate network delays, getting and setting all cryptocurrencies' values, etc.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { throw new RuntimeException(e); }

                pricesContainer.setBitcoinPrice(random.nextInt(222));
                pricesContainer.setEtherPrice(random.nextInt(444));
                pricesContainer.setRipplePrice(random.nextDouble());
            }
            finally {
                pricesContainer.getLockObj().unlock();
            }

            // simulate update intervals
            try { Thread.sleep(2000); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
    }
}



/**
 * this is the shared resource,
 * for both UI and backend value updater
 */
class PricesContainer {
    private Lock lockObj = new ReentrantLock();

    private double bitcoinPrice;
    private double etherPrice;
    private double ripplePrice;

    // getters and setters

    public Lock getLockObj() {
        return lockObj;
    }

    public void setLockObj(Lock lockObj) {
        this.lockObj = lockObj;
    }

    public double getBitcoinPrice() {
        return bitcoinPrice;
    }

    public void setBitcoinPrice(double bitcoinPrice) {
        this.bitcoinPrice = bitcoinPrice;
    }

    public double getEtherPrice() {
        return etherPrice;
    }

    public void setEtherPrice(double etherPrice) {
        this.etherPrice = etherPrice;
    }

    public double getRipplePrice() {
        return ripplePrice;
    }

    public void setRipplePrice(double ripplePrice) {
        this.ripplePrice = ripplePrice;
    }
}



// -------------------------------------------------------------------
// UI
public class ReentrantLock_UI extends Application {
    // run - works only by - ./gradlew run
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start (Stage primaryStage) {
        primaryStage.setTitle("Crypto Prices");

        GridPane gridPane = createGrid();
        Map<String, Label> cryptoLabelsMap = createCryptoPriceLabels();
        addLabelsToGrid(cryptoLabelsMap, gridPane);

        double width = 300, height = 250;
        Rectangle rectBg = createBackgroundRectWithAnimation(width, height);

        StackPane rootStackPane = new StackPane();
        rootStackPane.getChildren().add(rectBg);
        rootStackPane.getChildren().add(gridPane);

        primaryStage.setScene(new Scene(rootStackPane, width, height));

        addWindowResizeListener(primaryStage, rectBg);

        // read from PricesContainer
        PricesContainer pricesContainer = new PricesContainer();

        // handle method to be called at every frame
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                /**
                 * toggle (151, 152) - lagging application UI vs non-blocking UI.
                 * lock suspends UI thread until lock is released from producer thread,
                 * hence creating the lag
                 */
                pricesContainer.getLockObj().lock();
//                if (pricesContainer.getLockObj().tryLock()) {
                    try {
                        cryptoLabelsMap.get("BTC").setText(String.valueOf(pricesContainer.getBitcoinPrice()));
                        cryptoLabelsMap.get("ETH").setText(String.valueOf(pricesContainer.getEtherPrice()));
                        cryptoLabelsMap.get("XRP").setText(String.valueOf(pricesContainer.getRipplePrice()));
                    }
                    finally {
                        pricesContainer.getLockObj().unlock();
                    }
//                }
            }
        };
        animationTimer.start();

        // start updating prices
        PriceUpdator priceUpdator = new PriceUpdator(pricesContainer);
        priceUpdator.start();

        primaryStage.show();
    }

    // UI - helpers

    private GridPane createGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        return gridPane;
    }

    private Map<String, Label> createCryptoPriceLabels() {
        Label bitcoinPriceLabel = new Label("0");
        bitcoinPriceLabel.setId("BTC");
        Label etherPriceLabel = new Label("0");
        etherPriceLabel.setId("ETH");
        Label ripplePriceLabel = new Label("0");
        ripplePriceLabel.setId("XRP");

        Map<String, Label> cryptoLabelMap = new HashMap<>();
        cryptoLabelMap.put("BTC", bitcoinPriceLabel);
        cryptoLabelMap.put("ETH", etherPriceLabel);
        cryptoLabelMap.put("XRP", ripplePriceLabel);
        return cryptoLabelMap;
    }

    private void addLabelsToGrid (Map<String, Label> idLableMap, GridPane gridPane) {
        int row = 0;
        for (Map.Entry<String, Label> entry : idLableMap.entrySet()) {
            Label nameLabel = new Label(entry.getKey());
            nameLabel.setTextFill(Color.BLUE);
            nameLabel.setOnMousePressed(event -> nameLabel.setTextFill(Color.RED));
            nameLabel.setOnMouseReleased((EventHandler) event -> nameLabel.setTextFill(Color.BLUE));

            gridPane.add(nameLabel, 0, row);
            gridPane.add(entry.getValue(), 1, row);
            row++;
        }
    }

    private Rectangle createBackgroundRectWithAnimation (double width, double height) {
        Rectangle bgRect = new Rectangle(width, height);
        FillTransition fillTransition = new FillTransition(Duration.millis(1000), bgRect, Color.LIGHTGREEN, Color.LIGHTBLUE);
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
        return bgRect;
    }

    private void addWindowResizeListener (Stage stage, Rectangle rectangleBg) {
        ChangeListener<Number> stageSizeChangeListener = (observable, oldVal, newVal) -> {
            rectangleBg.setHeight(stage.getHeight());
            rectangleBg.setWidth(stage.getWidth());
        };
        stage.widthProperty().addListener(stageSizeChangeListener);
        stage.heightProperty().addListener(stageSizeChangeListener);
    }
}