import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPage extends Application {
    private String linkSTR = null;
    private int counter;
    private int level;
    private int zahod;
    List<Article> tempListLink;
    List<Article> listError;
    private Label status = new Label();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Button enterSiteName;
    private Button getSolution;
    private Task<Void> task;
    private TextArea textArea = new TextArea();
    private File file;

    public void start(Stage stage) {
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> Platform.exit());
        exitItem.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));

        MenuItem aboutProgramItem = new MenuItem("_About this program");
        aboutProgramItem.setOnAction(event ->
        {
            TextArea areaInfo = new TextArea("Determine and display the total size of all " + "\n" +
                    "www server pages and their number. Print the addresses" + "\n" +
                    "of pages with the largest and smallest sizes");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Work information");
            alert.getDialogPane().setExpandableContent(areaInfo);
            alert.showAndWait();
        });
        aboutProgramItem.setAccelerator(KeyCombination.keyCombination("Shortcut+I"));

        MenuItem aboutProgramer = new MenuItem("About _programer");
        aboutProgramer.setOnAction(event ->
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Malakhov Georgey, 6302");
            alert.showAndWait();
        });
        aboutProgramer.setAccelerator(KeyCombination.keyCombination("Shortcut+A"));

        Menu helpMenu = new Menu("_Help", null, aboutProgramItem, aboutProgramer, new SeparatorMenuItem(), exitItem);

        Button choiseSite = new Button("Choise site");
        choiseSite.setMinSize(125, 35);
        choiseSite.setOnAction(
                event -> {
                    ChoiceDialog<String> dialog = new ChoiceDialog<String>("http://imc.ssau.ru", "http://online-simpsons.ru/",
                            "https://ru.wikipedia.org/wiki/Распределение_Пуассона", "https://www.cyberforum.ru/");
                    dialog.setHeaderText("Pick a parameter");
                    dialog.showAndWait().ifPresentOrElse(
                            result -> linkSTR = dialog.getSelectedItem(),
                            () -> textArea.appendText("Canceled\n"));
                    if (linkSTR != null || !Objects.equals(linkSTR, "")) {
                        getSolution.setDisable(false);
                    }
                });

        Button choiseRoot = new Button("Choice root level");
        choiseRoot.setMinSize(125, 35);
        choiseRoot.setOnAction(
                event -> {
                    ChoiceDialog<Integer> dialog = new ChoiceDialog<Integer>(1, 2, 3, 4);
                    dialog.setHeaderText("Pick a level");
                    dialog.showAndWait().ifPresentOrElse(
                            result -> level = dialog.getSelectedItem(),
                            () -> textArea.appendText("Canceled\n"));
                });

        enterSiteName = new Button("Enter site");
        enterSiteName.setMinSize(125, 35);
        enterSiteName.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter a site name:");
            dialog.showAndWait().ifPresentOrElse(
                    result -> linkSTR = dialog.getResult(),
                    () -> textArea.appendText("Canceled\n"));
            if (linkSTR != null || !Objects.equals(linkSTR, "")) {
                getSolution.setDisable(false);
            }
            System.out.println(linkSTR);
        });

        Button clearTextArea = new Button("Clear text area");
        clearTextArea.setMinSize(125, 35);
        clearTextArea.setOnAction(event -> {
            textArea.setText("");
        });

        getSolution = new Button("Start process");
        getSolution.setMinSize(125, 35);
        getSolution.setDisable(true);
        getSolution.setOnAction(event -> {
            status.setText("");
            write(stage, level);
        });

        final double rem = new Text("").getLayoutBounds().getHeight();
        //VBox buttons = new VBox(0.8 * rem, choiseSite, choiseRoot, enterSiteName, clearTextArea, getSolution);
        HBox hDope = new HBox(0.8 * rem, choiseSite, choiseRoot, enterSiteName, clearTextArea, getSolution);
        //buttons.setPadding(new Insets(0.8 * rem));
        hDope.setPadding(new Insets(0.8 * rem));
        textArea.setMinWidth(450);

        //MenuBar bar = new MenuBar(helpMenu);
        //HBox horizontal = new HBox(textArea, buttons);
        VBox horizontal = new VBox(textArea, hDope);
        VBox root = new VBox(/*bar,*/ horizontal, status);
        stage.setScene(new Scene(root));
        stage.setWidth(750);
        stage.setTitle("Parser");
        stage.getIcons().add(new Image("Icon.png"));
        stage.show();
    }

    private void printListLinkStartPage(List<Article> tempListLink, int counterLinksFromStartPages) {
        if (tempListLink == null) {
            textArea.appendText("Host not founded or site have a bad status" + "\n");
        } else {
            for (Article tempLink : tempListLink) {
                textArea.appendText(tempLink.toString() + "\n");
            }
            textArea.appendText("Cycle is ended" + "\n");
            textArea.appendText("Count of links: " + counter + "\n");
        }
    }

    private void printListLink(List<Article> tempListLink, int k) {
        if (tempListLink == null) {
            textArea.appendText("Host not founded or site have a bad status" + "\n");
        } else {
            for (Article tempLink : tempListLink) {
                textArea.appendText(tempLink.toString() + "\n");
            }
            textArea.appendText("Cycle is ended" + "\n");
            textArea.appendText("k is processed: " + k + "\n");
        }
    }

    private void getLinkLevel(Parsing parsing, int level) throws IOException {
        if (level > 1) {
            List<List<Article>> list = parsing.getList();
            int constLen = list.size();
            System.out.println("Total size: " + list.size());
            for (int j = 0; j < constLen; j++) {
                for (int i = 0; i < list.get(j).size(); i++) {
                    tempListLink = parsing.getLinks(list.get(j).get(i).getUrl());
                    int finalI = i;
                    Platform.runLater(() ->
                    {
                        printListLink(tempListLink, finalI);
                    });
                }
            }
            getLinkLevel(parsing, level - 1);
        }
    }

    private void write(Stage stage, int level) {
        final int[] totalNumberPages = new int[1];
        final int[] summaryWeighPages = new int[1];
        final String[] minWeighLink = new String[1];
        final int[] minWeighPage = new int[1];
        final String[] maxWeighLink = new String[1];
        final int[] maxWeighPage = new int[1];
        Parsing parsing = new Parsing(linkSTR);
        textArea.clear();
        task = new Task<>() {
            @Override
            public Void call() {
                try {
                    tempListLink = parsing.getLinks(linkSTR);
                    counter = tempListLink.size();
                    Platform.runLater(() -> {
                        printListLinkStartPage(tempListLink, counter);
                    });
                    getLinkLevel(parsing, level);
                    status.textProperty().unbind();
                    Platform.runLater(() -> {
                                status.setText("Done writing");
                            });
                    task = null;
                    List<List<Article>> list = parsing.getList();
                    totalNumberPages[0] = parsing.summaryCountPages(list);
                    summaryWeighPages[0] = parsing.summaryWeighPages(list);
                    minWeighLink[0] = parsing.getMinWeighPage(list).getUrl();
                    minWeighPage[0] = parsing.getMinWeighPage(list).getLength();
                    maxWeighLink[0] = parsing.getMaxWeighPage(list).getUrl();
                    maxWeighPage[0] = parsing.getMaxWeighPage(list).getLength();

                    Platform.runLater(() -> {
                        textArea.appendText("\n" +"Total pages on site: " + totalNumberPages[0] + "\n" +
                                "Total weigh pages on site in byte: " + summaryWeighPages[0] + "\n" +
                                "Page with minimal weigh: " + minWeighLink[0] + "\n" +
                                "Minimal weigh in byte: " + minWeighPage[0] + "\n" +
                                "Page with maximal weigh: " + maxWeighLink[0] + "\n" +
                                "Maximal weigh in byte: " + maxWeighPage[0] + "\n");
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        executor.execute(task);
        /*task.setOnSucceeded(event ->
        {
            status.textProperty().unbind();
            status.setText("Done writing");
            task = null;
            List<List<Article>> list = parsing.getList();
            totalNumberPages[0] = parsing.summaryCountPages(list);
            summaryWeighPages[0] = parsing.summaryWeighPages(list);
            minWeighLink[0] = parsing.getMinWeighPage(list).getUrl();
            minWeighPage[0] = parsing.getMinWeighPage(list).getLength();
            maxWeighLink[0] = parsing.getMaxWeighPage(list).getUrl();
            maxWeighPage[0] = parsing.getMaxWeighPage(list).getLength();
            listError = parsing.getListError();
            TextArea areaInfo = new TextArea("Total pages on site: " + totalNumberPages[0] + "\n" +
                    "Total weigh pages on site in byte: " + summaryWeighPages[0] + "\n" +
                    "Page with minimal weigh: " + minWeighLink[0] + "\n" +
                    "Minimal weigh in byte: " + minWeighPage[0] + "\n" +
                    "Page with maximal weigh: " + maxWeighLink[0] + "\n" +
                    "Maximal weigh in byte: " + maxWeighPage[0] + "\n" +
                    "Error links: " + "\n");
            for (Article article : listError) {
                areaInfo.appendText(article.getUrl() + "\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Result of process");
            alert.getDialogPane().setExpandableContent(areaInfo);
            alert.showAndWait();
        });*/
    }
}

