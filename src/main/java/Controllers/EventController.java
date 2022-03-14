package Controllers;

import Models.Event;
import Models.User;
import Utils.GraphicUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

import java.io.File;


public class EventController extends PopupController {
    Event event;

    @FXML
    private Button eventEditButton;

    @FXML
    private ImageView eventPicture;

    @FXML
    private Label eventTitle;

    @FXML
    private Label eventDate;

    @FXML
    private Text eventDescription;

    @FXML
    private StackPane AvatarBox;

    @FXML
    private Label AvatarLetter;

    @FXML
    private Circle UserImageCircle;

    @FXML
    private Label userName;

    @FXML
    public void editEvent(){
        //show popup with edit form
        //in order to open a popup inside a popup we need to delay the call for some ms
        close(true);
        final MainController parentContr = this.parent;
        final Event editEvent = this.event;
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                parentContr.showPopupForm(MainController.FormType.EVENT,editEvent);
            }
        });
        new Thread(sleeper).start();
    }

    void setEvent(Event e, boolean editMode) {
        this.event=e;
        //if we are in edit mode the edit button should be visible otherwise invisible

        eventTitle.setText(e.getName());

        if(e.getDescription()!=null && !e.getDescription().isEmpty()) {
            eventDescription.setText(e.getDescription());
            eventDescription.setFill(Color.web("#707070"));
            eventDescription.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.REGULAR, 13));
        } else {
            eventDescription.setText("No Description");
            eventDescription.setFill(Color.web("#ACACAC"));
            eventDescription.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, 13));
        }

        if(e.getImage()!=null && e.getImage().isEmpty() == false) {
            eventPicture.setImage(new Image(e.getImage()));
            Utils.GraphicUtils.centerImage(eventPicture);
        }

        User createdBy = e.getCreatedBy();
        if (createdBy!=null && createdBy.getImage() != null && createdBy.getImage().equals("") == false){
            AvatarLetter.setVisible(false);
            Image userImage;
            if(createdBy.getImage().startsWith("file")) {
                userImage = new Image(createdBy.getImage());
            } else {
                File userImageFile = new File(createdBy.getImage());
                userImage = new Image(userImageFile.toURI().toString());
            }
            GraphicUtils.setAvatarImage(userImage, UserImageCircle);
        }else {
            AvatarLetter.setText(String.valueOf(createdBy.getFullname().charAt(0)).toUpperCase());
            UserImageCircle.setVisible(false);
        }

        if(editMode) {
            System.out.println("timeline: "+event.getTimeline());
            System.out.println("user: "+parent.getCurrentUser());
            if(parent.getCurrentUser()!=null && event.getTimeline().canIedit(parent.getCurrentUser())) {
                eventEditButton.setVisible(true);
            } else {
                eventEditButton.setVisible(false);
            }
        } else {
            eventEditButton.setVisible(false);
        }

        if(e.getTimeline().isAbsoluteTimeline()) {
            eventDate.setText(Utils.GeneralUtils.generateTimeStr(e.getStartDate(), e.getEndDate()));
        } else {
            eventDate.setText(e.getStartInt() + " " + e.getTimeline().getTimeUnit());
        }

        userName.setText(e.getCreatedBy().getUsername());
    }

    @Override
    void enterPress() {
        //not needed here
    }

    @Override
    void escPress() {
        this.close(true);
    }
}
