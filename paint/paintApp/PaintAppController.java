package paintApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;

/*******************************************************************************
 * This class is the controller for the GUI of the Painter application (whose
 * main routine is defined in the PaintApp class).
 * The Paint application is a GUI-based program to draw simple shapes on a
 * drawing pane. For more details regarding its features - see the documentation
 * of the main PaintApp class.
 * 
 * @author Nadav Kahlon
 */
public class PaintAppController {
    /***************************************************************************
     * ShapeOption - an enumeration type that represents possible shapes that
     *  the user may choose to draw
     */
    private enum ShapeOption {LINE, RECTANGLE, ELLIPSE};
    
    /***************************************************************************
     * FXML ttributes of the controller
     */
    
    // color radio buttons and toggle group:
    @FXML private RadioButton blackRadioButton;
    @FXML private RadioButton redRadioButton;
    @FXML private RadioButton greenRadioButton;
    @FXML private RadioButton blueRadioButton;
    @FXML private ToggleGroup colorToggleGroup;
    
    // shape radio buttons and toggle group
    @FXML private RadioButton lineRadioButton;
    @FXML private RadioButton rectangleRadioButton;
    @FXML private RadioButton ellipseRadioButton;
    @FXML private ToggleGroup shapeToggleGroup;
    
    // additional elements
    @FXML private Button undoButton;
    @FXML private Button clearButton;
    @FXML private CheckBox fillCheckbox;
    
    // the drawing pane
    @FXML private Pane drawingAreaPane;
    
    /***************************************************************************
     * Additional variables the controller uses 
     */
    
    // coordinates of mouse on the drawing pane when it started dragging (used
    // to determine how to draw each shape when the mouse is released)
    private double startX = 0, startY= 0; 
    
    // drawing color - starting with black 
    private Paint colorSelected = Color.BLACK;
    // shape to draw - starting with a rectangle
    private ShapeOption shapeSelected = ShapeOption.RECTANGLE;
    
    /***************************************************************************
     * Initializes the controller.
     * Runs right after the window is loaded.
     */
    @FXML
    public void initialize()
    {
        // attach shape to each shape radio button
        lineRadioButton.setUserData(ShapeOption.LINE);
        rectangleRadioButton.setUserData(ShapeOption.RECTANGLE);
        ellipseRadioButton.setUserData(ShapeOption.ELLIPSE);
        
        // attach color to each color radio button
        blackRadioButton.setUserData(Color.BLACK);
        redRadioButton.setUserData(Color.RED);
        greenRadioButton.setUserData(Color.GREEN);
        blueRadioButton.setUserData(Color.BLUE);
    }
    

    /***************************************************************************
     * Handles events in which a radio button specifying a shape is selected:
     * selects the corresponding shape to draw.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML
    private void shapeRadioButtonSelected(ActionEvent event) {
        // store the shape selected for future drawings
        shapeSelected =
                (ShapeOption) shapeToggleGroup.getSelectedToggle().getUserData();
    }

    /***************************************************************************
     * Handles events in which a radio button specifying a color is selected:
     * selects the corresponding color to draw with.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML
    private void colorRadioButtonSelected(ActionEvent event) {
        // store the color selected for future drawings
        colorSelected =
                (Color) colorToggleGroup.getSelectedToggle().getUserData();
    }
    
    /***************************************************************************
     * Handles events in which the mouse is pressed down on the drawing pane:
     * records the beginning location of the mouse (before it is released) for
     * later use.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML
    private void panePressed(MouseEvent event) {
        // when the mouse is first pressed down (before dragging)  - record its
        // starting position so we can use it to draw a shape when its dropped
        startX = event.getX();
        startY = event.getY();
    }
    
    /***************************************************************************
     * Handles events in which the user releases the mouse on the drawing pane:
     * draws a new shape as required.
     * 
     * @param event the event that caused the handler to be called.
     */
    @FXML
    private void paneReleased(MouseEvent event) {        
        // get the coordinates of the mouse when it dropped
        double endX = event.getX();
        double endY = event.getY();
        
        // make sure the end point is inside the drawing pane
        if (0 <= endX && endX < drawingAreaPane.getWidth() &&
            0 <= endY && endY < drawingAreaPane.getHeight())
        {
            // if so - we shall add a shape
            Shape shape; // to hold the shape we will add 
            
            if (shapeSelected == ShapeOption.LINE) {
                // if line is selected - create a line
                shape = new Line(startX, startY, endX, endY);
            }
            else {
                // otherwise - it is a rectangle or an ellipse - get positioning
                double x = Math.min(startX, endX);
                double y = Math.min(startY, endY);
                double width = Math.abs(startX - endX);
                double height = Math.abs(startY - endY);
                // create the appropriate shape
                if (shapeSelected == ShapeOption.RECTANGLE)
                    shape = new Rectangle(x, y, width, height);
                else
                    shape = new Ellipse(x+width/2, y+height/2, width/2, height/2);
            }

            // set stroke color and fill color (according to the fill checkbox)
            shape.setStroke(colorSelected);
            shape.setFill(fillCheckbox.isSelected()? colorSelected : null);

            // add new shape to the drawing pane
            drawingAreaPane.getChildren().add(shape);
        }
    }
    
    /**************************************************************************
     * Handles events in which the "undo" button is pressed: deletes the last
     * shape drawn.
     * 
     * @param event the event that caused the handler to be called.
    */
    @FXML
    private void undoButtonPressed(ActionEvent event) {
        // read the number of shapes on the drawing pane
        int count = drawingAreaPane.getChildren().size();
        
        // if there are any shapes left - remove the last one added
        if (count > 0)
            drawingAreaPane.getChildren().remove(count-1);
    }
    
    /**************************************************************************
     * Handles events in which the "Clear" button is pressed: clears the
     * drawing board.
     * 
     * @param event the event that caused the handler to be called.
    */
    @FXML
    private void clearButtonPressed(ActionEvent event) {
        // clear all the shapes on the drawing pane
        drawingAreaPane.getChildren().clear();
    }

}
