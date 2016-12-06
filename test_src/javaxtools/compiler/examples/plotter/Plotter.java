package javaxtools.compiler.examples.plotter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

final public class Plotter extends JFrame {

  private static final long serialVersionUID = 1L;

  public static void main(final String[] args) {
    try {
      new Plotter().setVisible(true);
    } catch (Throwable e) {
      e.printStackTrace();
      presentException();
    }
  }

  static void presentException() {
    String title = "Unable to run the " + Plotter.class.getName() + " application.";
    String message = title
      + " \n"
      + "This may be due to a missing tools.jar or missing JFreeChart jars. \n"
      + "Please consult the docs/README file found with this application for further details.";
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public Plotter() {
    super("compiler demo");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    getContentPane().add(new PlotterPanel());
    pack();
  }
}
