package mu.Project.Views;

import mu.Project.Controllers.CustomerController;
import mu.Project.NotImplementedException;

import javax.swing.*;

public class CustomerView extends View {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JPanel Account;
    private JPanel Reservation;

    public CustomerView(CustomerController controller) {
        super("CustomerView", controller);
        setContentPane(panel1);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerFrame();
        setVisible(true);
    }

    public void close() {
        throw new NotImplementedException();
    }
}
