package Controllers;

import Models.Event;
import Models.Timeline;
import Utils.FileController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.LocalTimeStringConverter;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EventCreationPopupController extends PopupController implements Initializable {

    Timeline timeline;
    Event event = new Event();
    boolean editEvent = false;

    private File imageFile;
    private SpinnerValueFactory<LocalTime> startValue;
    private SpinnerValueFactory<LocalTime> endValue;

    @FXML
    private ImageView eventImage;

    @FXML
    private Button selectImageButton;

    @FXML
    private TextField eventName;

    @FXML
    private DatePicker startingDate;

    @FXML
    private DatePicker endingDate;

    @FXML
    private Spinner startTimeSpinner;

    @FXML
    private Spinner endTimeSpinner;

    @FXML
    private TextArea eventDesc;

    @FXML
    private Button deletButton;

    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    @FXML
    private GridPane absoluteTimeBox;

    @FXML
    private VBox relativeBox;

    @FXML
    private Label eventNameLabel;

    @FXML
    private TextField timeInput;

    @FXML
    private Label timeUnit;

    @FXML
    public void saveEvent() {
        if (!editEvent) {
            event.setTimeline(this.timeline);
            event.setCreatedBy(getUser());
        }
        String error = errMsg();
        System.out.println("CREATE TIMELINE: " + error);
        if (!error.equals("")) {
            System.out.println("ERROR!!");
            showError(error);
        } else {
            System.out.println("no errors");
            try {
                event.setName(eventName.getText());
                System.out.println("65");
                event.setDescription(eventDesc.getText());
                System.out.println("67");
                event.setTimeline(timeline);
                System.out.println("69");

                if(!timeline.isAbsoluteTimeline()){
                    event.setStartInt(Integer.parseInt(timeInput.getText()));
                }
                else {
                    event.setStartDate(startValue.getValue().atDate(startingDate.getValue()));
                    System.out.println("71");
                    //only if chosen by user
                    if (endingDate.getValue() != null) {
                        System.out.println("hi");
                        event.setEndDate(endValue.getValue().atDate(endingDate.getValue()));
                    }
                }
                System.out.println("73");

                if (imageFile != null) {
                    System.out.println("image");
                    event.setImage(imageFile.getName());
                }
                System.out.println("77");
                event.setStart(startValue.getValue());
                //only if chosen by user
                if (endValue.getValue() != null)
                    event.setEnd(endValue.getValue());
                //what if the user create an event doesnt happen at current time???
                if(editEvent) {
                    event.update();
                } else {
                    event.save();
                }
                System.out.println("80");
                System.out.println("82");
                parent.openTimeline(timeline);//Reload timeline to show event
                super.close(true);
                System.out.println("84");
            } catch (Exception e) {
                //e.printStackTrace();
                errMsg();
            }
        }
    }

    //a button should be made in  Event.fxml (edit button) and set action on (e-> this.eventController.editEvent);

    public void editEvent(Event event) {
        editEvent = true;
        this.event = event;
        try {
            createButton.setText("Save");
            deletButton.setVisible(true);
            eventName.setText(event.getName());
            if (!event.getDescription().isEmpty())
                eventDesc.setText(event.getDescription());
            if(event.getTimeline().isAbsoluteTimeline()) {
                if (event.getStartDate() != null)
                    startingDate.setValue(event.getStartDate().toLocalDate());
                if (event.getEndDate() != null)
                    endingDate.setValue(event.getEndDate().toLocalDate());
            }else{
                eventNameLabel.setText("This happens/happened at");
                timeInput.setText(event.getStartInt()+"");
                timeUnit.setText(event.getTimeline().getTimeUnit());
            }
    // if(event.getImage()!=null)
    //   eventImage.setImage(new Image(event.getImage()));
    //  startTimeSpinner.setValueFactory(event.getStart().getvalueFactory());
    //endTimeSpinner.setValueFactory(event.getEndTime());

       /* createButton.setOnAction(e -> {
            event.setName(eventName.getText());
            event.setDescription(eventDesc.getText());
            event.setStartDate(startingDate.getValue().atStartOfDay());
            event.setEndDate(endingDate.getValue().atStartOfDay());
            event.setImage(imageFile.getName());

            event.update();
            closePopup();
        });*/

      /*  cancelButton.setOnAction(e -> {
            closePopup();
        });*/

        }catch(NullPointerException e){
    System.out.println(e.getMessage());

        }
    }

    @FXML
    public void fileChooser() {
        setLockFocus(true); //so we don't trigger the onFocus "event"
        String imageUrl = FileController.imageChooser();
        if(imageUrl!=null) {
            Image image = new Image(imageUrl);
            eventImage.setImage(image);
            event.setImage(imageUrl);
            setDirty(true);
        }
        setLockFocus(false); //we restore the previous state of the LockFocus
    }

    ///////////////////////////////// GETTERS SETTERS

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
        if(timeline.isAbsoluteTimeline()){
            relativeBox.setManaged(false);
            relativeBox.setVisible(false);
        } else {
            absoluteTimeBox.setManaged(false);
            absoluteTimeBox.setVisible(false);
            timeUnit.setText(timeline.getTimeUnit());
        }

       if(timeline.getStartDate()!=null) {
            //startDate can not be before start date of timeline
            startingDate.setDayCellFactory(new Callback<>() {
                @Override
                public DateCell call(final DatePicker param) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                            final LocalDate timelineStartDate = timeline.getStartDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            setDisable(empty || item.compareTo(timelineStartDate) < 0 );
                        }
                    };
                }
            });
        }
    }

    /////////////////////////////////

    ///////////////////////////////// UTILS
    public void closePopup() {
        super.close();
    }

    @FXML
    public void deleteEvent() {
        event.delete();
        //close popup
        close(true);
        //refresh timeline
        parent.openTimeline(timeline);
    }

    private String errMsg() {//it should be like this???
        String msg = "";
        if (eventName.getText().isEmpty()) {
            eventName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            msg += "Event name";
        } else {
            eventName.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }

        if(timeline.isAbsoluteTimeline()) {
            if (startingDate.getValue() == null) {
                startingDate.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                if (!msg.equals(""))
                    msg += ", ";
                msg += "Starting date";
            } else {
                startingDate.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }
        }else{
            if(timeInput.getText().isEmpty()){
                timeInput.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                if(!msg.equals(""))
                    msg += ", ";
                msg += "time input";
            }else{
                timeInput.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
            }
        }
        if (startTimeSpinner.getValue() == null) {
            startTimeSpinner.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            if (!msg.equals("")) {
                msg += ", ";
            }
            msg += "start time";
        } else {
            startTimeSpinner.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        if (!msg.equals("")) {
            return msg.substring(0, 1).toUpperCase() + msg.substring(1) + " cannot be empty";
        } else {
            //check if values are valid
            if(timeline.isAbsoluteTimeline()) {
                //TODO: check if start date is before the end date?
            }else {
                String startIntInput =  timeInput.getText();
                //test start time, only numbers allowd
                if(startIntInput.matches("[0-9]+") == false){
                    timeInput.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                    return "Time should be an integer number";
                }else{
                    timeInput.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
                }

                if(startIntInput.length()>9){
                    timeInput.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                    return "That's a big number. Write a number with less than 10 digits";
                }else{
                    timeInput.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
                }

                //should be a number between the start and end of the timeline
                int startInteger = Integer.parseInt(startIntInput);
                if(startInteger>=timeline.getStartInt() && startInteger<=timeline.getEndInt()) {
                    timeInput.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
                }else{
                    timeInput.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
                    return "Time should be between "+timeline.getStartInt()+" and "+timeline.getEndInt();
                }
            }
        }
        return "";
    }

    private SpinnerValueFactory spinnerValue() {
        SpinnerValueFactory value = new SpinnerValueFactory<LocalTime>() {

            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                setConverter(new LocalTimeStringConverter(formatter, null));
            }

            @Override
            public void decrement(int steps) {
                if (getValue() == null)
                    setValue(LocalTime.now());
                else {
                    LocalTime time = (LocalTime) getValue();
                    setValue(time.minusMinutes(steps));
                }
            }

            @Override
            public void increment(int steps) {
                if (this.getValue() == null)
                    setValue(LocalTime.now());
                else {
                    LocalTime time = (LocalTime) getValue();
                    setValue(time.plusMinutes(steps));
                }
            }
        };
        LocalTime time = LocalTime.now();
        value.setValue(time);
        return value;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        this.startValue = spinnerValue();
        //  startValue.setValue(time.get());
        startTimeSpinner.setValueFactory(startValue);
        //  startTimeSpinner.setEditable(true);

        endValue = spinnerValue();
        endTimeSpinner.setValueFactory(endValue);
        //  endTimeSpinner.setEditable(true);

        //endDate can not be before start date now
        Callback<DatePicker, DateCell> callB = new Callback<>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        setDisable(empty || (startingDate.getValue() != null && item.compareTo(startingDate.getValue()) < 0));
                    }
                };
            }
        };
        endingDate.setDayCellFactory(callB);
    }

    @Override
    void enterPress() {
        //What to do on ENTER key press
        saveEvent();
    }

    @Override
    void escPress() {
        //What to do on ESCAPE key press
        close();
    }
}
