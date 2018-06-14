/*
@author Sarthak Sirotiya
dedicated to Mr. Britton in May of 2018
 */

import BreezySwing.*; //imports industry-standard GUI software
import javax.swing.*; //imports javax.swing for more GUI setup
import java.sql.*; //imports SQL packages in order to use an SQL database

public class ArenaScheduling extends GBFrame {

    //setup the GUI

    //creating labels
    JLabel student_ID_label = addLabel("Student ID", 1, 1, 1, 1);
    JLabel name_label = addLabel("Name", 2, 1, 1, 1);
    JLabel period_1 = addLabel("Period 1", 4, 1,1 ,1);
    JLabel period_2 = addLabel("Period 2", 5, 1,1 ,1);
    JLabel period_3 = addLabel("Period 3", 6, 1,1 ,1);
    JLabel period_4 = addLabel("Period 4", 7, 1,1 ,1);
    JLabel period_5 = addLabel("Period 5", 8, 1,1 ,1);
    JLabel period_6 = addLabel("Period 6", 9, 1,1 ,1);
    JLabel period_7 = addLabel("Period 7", 10, 1,1 ,1);
    JLabel period_8 = addLabel("Period 8", 11, 1,1 ,1);

    JLabel period_label = addLabel("Class Period", 3, 1, 1,1);
    JLabel course_label = addLabel("Available Courses", 3, 2, 1,1);
    JLabel seats_label = addLabel("Available Seats", 3, 3, 1, 1);
    JLabel seats_1 = addLabel("", 4, 3, 1,1);
    JLabel seats_2 = addLabel("", 5, 3, 1,1);
    JLabel seats_3 = addLabel("", 6, 3, 1,1);
    JLabel seats_4 = addLabel("", 7, 3, 1,1);
    JLabel seats_5 = addLabel("", 8, 3, 1,1);
    JLabel seats_6 = addLabel("", 9, 3, 1,1);
    JLabel seats_7 = addLabel("", 10, 3, 1,1);
    JLabel seats_8 = addLabel("", 11, 3, 1,1);

    //creating text boxes
    JTextField student_ID_field = addTextField("", 1, 2, 2, 1);
    JTextField name_field = addTextField("", 2, 2, 2, 1);

    //creating drop-down boxes
    JComboBox period_1_box = addComboBox(4, 2, 2, 1);
    JComboBox period_2_box = addComboBox(5, 2, 2, 1);
    JComboBox period_3_box = addComboBox(6, 2, 2, 1);
    JComboBox period_4_box = addComboBox(7, 2, 2, 1);
    JComboBox period_5_box = addComboBox(8, 2, 2, 1);
    JComboBox period_6_box = addComboBox(9, 2, 2, 1);
    JComboBox period_7_box = addComboBox(10, 2, 2, 1);
    JComboBox period_8_box = addComboBox(11, 2, 2, 1);

    //creating buttons
    JButton clear = addButton("Clear", 12, 1, 1, 1);
    JButton submit = addButton("Submit", 12, 2, 1, 1);
    JButton check = addButton("Check", 12, 3, 1, 1);

    String[] courses = new String[8]; //String array that will hold values from combo boxes

    public ArenaScheduling() //constructor
    {
        try {
            fillComboBox();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void buttonClicked(JButton button) //button clicked method to handle button click events
    {
        //TODO: button clicked method
        if(button == clear) //if the "Clear" button is clicked -- clears all the user's selections/information
        {
            student_ID_field.setText(""); //clears the "student id" field
            name_field.setText(""); //clears the "name" field
            //clears the combo boxes
            period_1_box.setSelectedIndex(-1);
            period_2_box.setSelectedIndex(-1);
            period_3_box.setSelectedIndex(-1);
            period_4_box.setSelectedIndex(-1);
            period_5_box.setSelectedIndex(-1);
            period_6_box.setSelectedIndex(-1);
            period_7_box.setSelectedIndex(-1);
            period_8_box.setSelectedIndex(-1);
            //clears the labels that contain the number of available seats
            seats_1.setText("");
            seats_2.setText("");
            seats_3.setText("");
            seats_4.setText("");
            seats_5.setText("");
            seats_6.setText("");
            seats_7.setText("");
            seats_8.setText("");
            try {
                fillComboBox();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(button == submit) //if the "Submit" button is clicked
        {
            try {
                submitSelection();
            }
            catch(SQLIntegrityConstraintViolationException e)
            {
                e.printStackTrace();
                messageBox("This user has already completed Arena Scheduling.");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(button == check) //if the "Check" button is clicked
        {
            try {
                if(checkValid()) //if the user's selection is valid
                {
                    messageBox("Your schedule is complete! Please hit submit.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkValid() throws SQLException //checks if the values are all acceptable (all courses unique, all periods filled, seats are available) and outputs number of available seats
    {
        boolean valid = true; //valid until proven otherwise
        String message = ""; //String that holds all error messages so they can be displayed in one messageBox




        //check to ensure that all values are not null
        //check to ensure that identification fields are complete
        if(student_ID_field.getText().equals("") || name_field.getText().equals(""))
        {
            messageBox("Identification fields not complete!");
            return false; //returns false because identification fields are not complete
        }
        //check to ensure that course fields are complete
        try
        {
            //adds courses from comboboxes to an array
            courses[0] = period_1_box.getSelectedItem().toString();
            courses[1] = period_2_box.getSelectedItem().toString();
            courses[2] = period_3_box.getSelectedItem().toString();
            courses[3] = period_4_box.getSelectedItem().toString();
            courses[4] = period_5_box.getSelectedItem().toString();
            courses[5] = period_6_box.getSelectedItem().toString();
            courses[6] = period_7_box.getSelectedItem().toString();
            courses[7] = period_8_box.getSelectedItem().toString();
        }catch(Exception e){
            e.printStackTrace(); //
            messageBox("Not all fields are complete!");
            return false; //returns false because not all fields are filled
        }

        //since we have checked that all fields are complete (even if not valid), we can output the number of available seats
        int[] seats = new int[8]; //holds the number of seats available
        for(int i = 0; i < courses.length; i++)
        {
            Connection c = getConnection();
            PreparedStatement statement = c.prepareStatement("SELECT available_seats FROM courses WHERE course_name='"+ courses[i] + "' AND period=" + (i+1));
            ResultSet result = statement.executeQuery();
            while(result.next())
            {
                seats[i] = result.getInt("available_seats");
            }
        }
        seats_1.setText(Integer.toString(seats[0]));
        seats_2.setText(Integer.toString(seats[1]));
        seats_3.setText(Integer.toString(seats[2]));
        seats_4.setText(Integer.toString(seats[3]));
        seats_5.setText(Integer.toString(seats[4]));
        seats_6.setText(Integer.toString(seats[5]));
        seats_7.setText(Integer.toString(seats[6]));
        seats_8.setText(Integer.toString(seats[7]));


        //check if any of the courses are the same
        for(int i = 0; i < courses.length; i++)
        {
            for(int j = 0; j < courses.length; j++)
            {

                if(i == j){} //if the same string is being compared against itself, ignore it
                else if(courses[i].equals(courses[j])){valid = false; message += "Not all selected courses are unique.\n";} //if the strings are null or the same, set valid to false and add a message
            }
        }

        //checks if seats are available
        Connection c = getConnection();
        for(int i = 0; i < courses.length; i++)
        {
            PreparedStatement statement = c.prepareStatement("SELECT available_seats FROM courses WHERE course_name='"+ courses[i] + "' AND period=" + (i+1));
            ResultSet result = statement.executeQuery();
            while(result.next())
            {
                if(result.getInt("available_seats") <=0)
                {
                    valid = false;
                    message += "The selected course (" + courses[i] + ") does not have any available seats!\n";
                }
            }
        }

        if(message.equals(""))
        {
            //will return true because user's selection is valid
        }
        else
        {
            //will return false because user's selection is not valid
            messageBox(message); //displays a messagebox of the errors in the user's selection
        }
        return valid; //returns if the user's selection is valid or not
    }

    public void submitSelection() throws SQLException //adds the user's selection to the SQL table of completed schedules AND removes an available seat
    {
        if(checkValid()) //if the user's selection is valid
        {
            Connection c = getConnection(); //establishes a connection to the SQL database
            String course_list = period_1_box.getSelectedItem().toString() + ":" + period_2_box.getSelectedItem().toString() + ":" + period_3_box.getSelectedItem().toString() + ":" + period_4_box.getSelectedItem().toString() + ":" + period_5_box.getSelectedItem().toString() + ":" + period_6_box.getSelectedItem().toString() + ":" + period_7_box.getSelectedItem().toString() + ":" + period_8_box.getSelectedItem().toString();
            PreparedStatement statement = c.prepareStatement("INSERT INTO schedules VALUES(" + student_ID_field.getText() + ", '" + name_field.getText() + "', '"+ course_list+"', NOW());");
            statement.executeUpdate(); //executes the statement, inserting the values into the SQL table

            for(int i = 0; i < courses.length; i++)
            {
                statement = c.prepareStatement("UPDATE courses SET available_seats=available_seats-1 WHERE course_name='" + courses[i] + "' AND period=" + (i+1));
                statement.executeUpdate();
            }
            messageBox("Your schedule has been submitted!"); //confirmation for user that the schedule is in the table
            clear.doClick();
        }
        else //if the user's selection is not valid
        {

        }
    }

    public void fillComboBox() throws SQLException {
        //removes all items from the combo boxes to ensure only valid items are entered
        period_1_box.removeAllItems();
        period_2_box.removeAllItems();
        period_3_box.removeAllItems();
        period_4_box.removeAllItems();
        period_5_box.removeAllItems();
        period_6_box.removeAllItems();
        period_7_box.removeAllItems();
        period_8_box.removeAllItems();

        //gets the class period of each class from the SQL database and uses this to fill the comboboxes
        Connection c = getConnection();
        PreparedStatement statement = c.prepareStatement("SELECT * FROM courses WHERE available_seats>0;");
        ResultSet set = statement.executeQuery();
        while(set.next()) //looping through the results from the SQL query
        {
            switch(set.getInt("period"))
            {
                case 1:
                    period_1_box.addItem(set.getString("course_name"));
                    break;

                case 2:
                    period_2_box.addItem(set.getString("course_name"));
                    break;

                case 3:
                    period_3_box.addItem(set.getString("course_name"));
                    break;

                case 4:
                    period_4_box.addItem(set.getString("course_name"));
                    break;

                case 5:
                    period_5_box.addItem(set.getString("course_name"));
                    break;

                case 6:
                    period_6_box.addItem(set.getString("course_name"));
                    break;

                case 7:
                    period_7_box.addItem(set.getString("course_name"));
                    break;

                case 8:
                    period_8_box.addItem(set.getString("course_name"));
                    break;
                default:
                    break;
            }
        }
        //makes all the boxes clear at the beginning of the program
        period_1_box.setSelectedIndex(-1);
        period_2_box.setSelectedIndex(-1);
        period_3_box.setSelectedIndex(-1);
        period_4_box.setSelectedIndex(-1);
        period_5_box.setSelectedIndex(-1);
        period_6_box.setSelectedIndex(-1);
        period_7_box.setSelectedIndex(-1);
        period_8_box.setSelectedIndex(-1);

    }

    public Connection getConnection()
    {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://104.188.155.28/arenascheduling?verifyServerCertificate=false&useSSL=true", "username", "password");
            return connection;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)
    {

        JFrame frm = new ArenaScheduling(); //creates the GUI using the design in the ArenaScheduling class
        frm.setTitle("Not Skyward");
        frm.setSize(960,540); //sets the size of the GUI to 960 x 540
        frm.setVisible(true); //sets the GUI to be visible so the user can interact with it
    }
}