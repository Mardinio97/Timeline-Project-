package Controllers;

import Models.Timeline;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class TimelinePopupController extends PopupController implements Initializable {

    Timeline timeline = null;
    boolean absoluteTimeline = true;

    @FXML
    private VBox createNewTimeline;

    @FXML
    private Label WindowTitle;

    @FXML
    private TextField timelineName;

    @FXML
    private TextArea timelineDescription;

    @FXML
    private Label timeUnitLabel;

    @FXML
    private ComboBox timeUnitBox;

    @FXML
    private Label timeFrameLabel;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button absoluteTimelineButton;

    @FXML
    private Circle absoluteCircle;

    @FXML
    private Button relativeTimelineButton;

    @FXML
    private Circle relativeCircle;

    @FXML
    private VBox relativeTimelineInputs;

    @FXML
    private VBox absoluteTimelineInputs;

    @FXML
    private TextField startInt;

    @FXML
    private TextField endInt;

    @FXML
    private TextField customTimeUnit;

    @FXML
    private HBox timelineTypeChooser;

    @FXML
    void selectAbsoluteTimeline(ActionEvent event) {
        //let choose only if it's a new timeline, else disable it
        if(timeline==null) {
            this.absoluteTimeline = true;
            absoluteCircle.setFill(Color.web("#434fe2"));
            absoluteCircle.setStroke(Color.web("#434fe2"));
            absoluteTimelineButton.setStyle("-fx-border-color: #434fe2; -fx-background-color: #FFFFFF; -fx-border-radius: 3; -fx-background-radius: 3;");
            relativeCircle.setFill(Color.web("#FFFFFF"));
            relativeCircle.setStroke(Color.web("#b9b9b9"));
            relativeTimelineButton.setStyle("-fx-border-color: #CCCCCC; -fx-background-color: #FFFFFF; -fx-border-radius: 3; -fx-background-radius: 3;");
            relativeTimelineInputs.setManaged(false);
            relativeTimelineInputs.setVisible(false);
            absoluteTimelineInputs.setManaged(true);
            absoluteTimelineInputs.setVisible(true);
            if (stage != null) {
                stage.setHeight(494);
            }
        }
    }

    @FXML
    void selectRelativeTimeline(ActionEvent event) {
        selectRelTimeline(false);
    }

    void selectRelTimeline(boolean force) {
        //let choose only if it's a new timeline, else disable it
        if(timeline==null || force) {
            this.absoluteTimeline = false;
            absoluteCircle.setFill(Color.web("#FFFFFF"));
            absoluteCircle.setStroke(Color.web("#b9b9b9"));
            absoluteTimelineButton.setStyle("-fx-border-color: #CCCCCC; -fx-background-color: #FFFFFF; -fx-border-radius: 3; -fx-background-radius: 3;");
            relativeCircle.setFill(Color.web("#434fe2"));
            relativeCircle.setStroke(Color.web("#434fe2"));
            relativeTimelineButton.setStyle("-fx-border-color: #434fe2; -fx-background-color: #FFFFFF; -fx-border-radius: 3; -fx-background-radius: 3;");
            relativeTimelineInputs.setManaged(true);
            relativeTimelineInputs.setVisible(true);
            absoluteTimelineInputs.setManaged(false);
            absoluteTimelineInputs.setVisible(false);
            if (stage != null) {
                stage.setHeight(437);
            }
        }
    }

    @FXML
    public void saveTimeline() {
        String error = errMsg();
        System.out.println("CREATE TIMELINE: "+error);
        if(!error.equals("")) {
            showError(error);
        } else {
            try {
                String title = timelineName.getText();
                String description = timelineDescription.getText();
                User owner = getUser();

                boolean updateTimeline = true; //flag to update or save object
                //create a new timeline object if it's null
                if (timeline == null) {
                    timeline = new Timeline();
                    timeline.setCreatedBy(owner);
                    updateTimeline = false;
                }

                timeline.setTitle(title);
                timeline.setDescription(description);

                if(absoluteTimeline) {
                    //is absolute timeline
                    String timeUnit = timeUnitBox.getSelectionModel().getSelectedItem().toString();
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    Date startDate = java.util.Date.from(startDatePicker.getValue().atStartOfDay(defaultZoneId).toInstant());
                    Date endDate = java.util.Date.from(endDatePicker.getValue().atStartOfDay(defaultZoneId).toInstant());
                    timeline.setAbsoluteTimeline(true);
                    timeline.setStartDate(startDate);
                    timeline.setEndDate(endDate);
                    timeline.setTimeUnit(timeUnit);
                } else {
                    int endIntInput =  Integer.parseInt(endInt.getText());
                    int startIntInput = Integer.parseInt(startInt.getText());
                    String customUnitInput =  customTimeUnit.getText();
                    timeline.setStartInt(startIntInput);
                    timeline.setEndInt(endIntInput);
                    timeline.setTimeUnit(customUnitInput);
                }

                if(updateTimeline) {
                    timeline.update();
                } else {
                    timeline.save();
                }

                parent.addTimelineToList(timeline);
                close(true);
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex.toString());
                // GraphicUtils.makeToast(stage,ex.getMessage(), ToastType.WARNING);
            } catch (Exception e) {
                System.out.println("exception : " + e.getMessage());
            }
        }
    }

    public void editTimeline(Timeline timeline) {

        this.timeline = timeline;
        WindowTitle.setText("Edit Timeline");
        createButton.setText("Save");
        deleteButton.setVisible(true);
        timelineName.setText(timeline.getTitle());
        timelineDescription.setText(timeline.getDescription());
        timeUnitBox.getSelectionModel().select(timeline.getTimeUnit());

        if(timeline.getStartDate()!=null) {
            LocalDate startDate = timeline.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            startDatePicker.setValue(startDate);
        }
        if(timeline.getEndDate()!=null) {
            LocalDate endDate = timeline.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            endDatePicker.setValue(endDate);
        }

        if(timeline.isAbsoluteTimeline() == false) {
            //select relative timeline
            selectRelTimeline(true);
            endInt.setText(""+timeline.getEndInt());
            startInt.setText(""+timeline.getStartInt());
            customTimeUnit.setText(timeline.getTimeUnit());
        }

        //disable choice of timeline
        timelineTypeChooser.setOpacity(0.5);
        //disable key changes when editing
        startInt.setDisable(true);
        endInt.setDisable(true);
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);
        timeUnitBox.setDisable(true);
    }


    @FXML
    public void deleteTimeline() {
        parent.removeFromTimelineList(timeline);
        timeline.delete();
        close(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //hide relative timeline inputs
        relativeTimelineInputs.setManaged(false);
        relativeTimelineInputs.setVisible(false);
        absoluteTimelineInputs.setManaged(true);
        absoluteTimelineInputs.setVisible(true);

        //endDate cant be before start date now
        Callback<DatePicker, DateCell> callB = new Callback<>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        setDisable(empty || item.compareTo(startDatePicker.getValue()) < 0);
                    }
                };
            }
        };
        endDatePicker.setDayCellFactory(callB);
    }

    ///////////////////////////////// GETTERS SETTERS
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    /////////////////////////////////

    ///////////////////////////////// UTILS

    public void closePopup() {
        close();
    }

    private String errMsg() {
        String msg = "";
        if (timelineName.getText().isEmpty()) {
            timelineName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            msg+="Timeline name";
        } else {
            timelineName.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }

        if(absoluteTimeline) {
            if(timeUnitBox.getSelectionModel().isEmpty()) {
                timeUnitBox.setStyle("-fx-border-color: #ff0000 ; -fx-border-width: 1px ;");
                if(!msg.equals("")) {
                    msg+=", ";
                }
                msg+="time unit";
            } else {
                timeUnitBox.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }
            if(startDatePicker.getValue()==null) {
                startDatePicker.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                if(!msg.equals("")) {
                    msg+=", ";
                }
                msg+="start date";
            }else {
                startDatePicker.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }
            if(endDatePicker.getValue()==null) {
                endDatePicker.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                if(!msg.equals("")) {
                    msg+=", ";
                }
                msg+="end date";
            }else {
                endDatePicker.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }
        }else {
            if (startInt.getText().isEmpty()) {
                startInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                if (!msg.equals("")) {
                    msg += ", ";
                }
                msg += "start time";
            } else {
                startInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            if (endInt.getText().isEmpty()) {
                endInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                if (!msg.equals("")) {
                    msg += ", ";
                }
                msg += "end time";
            } else {
                endInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            if (customTimeUnit.getText().isEmpty()) {
                customTimeUnit.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                if (!msg.equals("")) {
                    msg += ", ";
                }
                msg += "time unit";
            } else {
                customTimeUnit.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }
        }

        //empty error messages
        if(!msg.equals("")) {
            return msg.substring(0, 1).toUpperCase() + msg.substring(1)+" cannot be empty";
        }

        //validate data
        if(absoluteTimeline==false) {
            String endIntInput =  endInt.getText();
            String startIntInput =  startInt.getText();
            String customUnitInput =  customTimeUnit.getText();

            //test start time
            if (startIntInput.matches("[0-9]+") == false) {
                msg = "start time";
                startInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            }else {
                startInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            //test end time
            if (endIntInput.matches("[0-9]+") == false) {
                if (!msg.equals("")) {
                    msg += ", ";
                }
                msg = "end time";
                endInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            }else {
                endInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            if(!msg.equals("")) {
                return msg.substring(0, 1).toUpperCase() + msg.substring(1)+" should be an integer number";
            }

            //test time unit
            if (customUnitInput.matches("[a-zA-Z]+") == false) {
                customTimeUnit.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                return "Time unit should be one word without numbers";
            }else {
                customTimeUnit.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            if(startIntInput.length()>9){
                startInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                return "Start time should have less than 10 digits";
            }else{
                startInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            if(endIntInput.length()>9){
                endInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                return "End time should have less than 10 digits";
            }else{
                endInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            //verify if start time is less than end time and their difference is more than 20 units
            int startInteger, endInteger;
            startInteger = Integer.parseInt(startIntInput);
            endInteger = Integer.parseInt(endIntInput);
            if(endInteger<=startInteger) {
                endInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                startInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                return "End time must be greater than start time";
            } else {
                endInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
                startInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }
            if(endInteger-startInteger<20) {
                endInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                startInt.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                return "The start and end time must be more than 20 time units apart";
            } else {
                endInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
                startInt.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }

            return "";
        } else {
            return "";
        }
    }

    /////////////////////////////////

    @Override
    void enterPress() {
        //What to do on ENTER key press
        saveTimeline();
    }

    @Override
    void escPress() {
        //What to do on ESCAPE key press
        close();
    }
}
