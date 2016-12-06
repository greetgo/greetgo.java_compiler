package javaxtools.compiler.examples.plotter;

import javaxtools.compiler.CharSequenceCompiler;
import javaxtools.compiler.CharSequenceCompilerException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import static javax.swing.SpringLayout.*;

final public class PlotterPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private static final String DEFAULT_FUNCTION = "x * (sin(x) + cos(x))";
  private static final int PAD = 5;
  private final CharSequenceCompiler<Function> compiler = new CharSequenceCompiler<>(
    getClass().getClassLoader(), Arrays.asList("-target", "1.8"));
  private int classNameSuffix = 0;
  private static final String PACKAGE_NAME = "kz.pompei.compiler.examples.plotter.runtime";
  private static final Random random = new Random();
  private String template;

  private Function function;
  private final PlotPanel plotPanel = new PlotPanel();
  private final JTextArea errors = new JTextArea();
  private final JTextField plotFunctionText = new JTextField(DEFAULT_FUNCTION, 40);

  public static void main(final String[] args) {
    new PlotterPanel().setVisible(true);
  }

  public PlotterPanel() {
    Container c = this;
    SpringLayout layout = new SpringLayout();
    c.setLayout(layout);
    JLabel label = new JLabel("f(x)=");
    JButton plotButton = new JButton("Plot this function");
    c.add(label);
    c.add(plotFunctionText);
    c.add(plotButton);
    ActionListener plot = action -> generateAndPlotFunction();
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent evt) {
        generateAndPlotFunction();
      }
    });
    plotButton.addActionListener(plot);
    plotFunctionText.addActionListener(plot);
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(errors);
    add(plotPanel);
    c.add(scrollPane);

    layout.putConstraint(NORTH, label, PAD, NORTH, c);
    layout.putConstraint(NORTH, plotButton, PAD, NORTH, c);
    layout.putConstraint(NORTH, plotFunctionText, PAD, NORTH, c);
    layout.putConstraint(WEST, label, PAD, WEST, c);
    layout.putConstraint(EAST, plotButton, -PAD, EAST, c);
    layout.putConstraint(WEST, plotFunctionText, PAD, EAST, label);
    layout.putConstraint(EAST, plotFunctionText, -PAD, WEST, plotButton);
    layout.putConstraint(EAST, plotPanel, -PAD, EAST, c);
    layout.putConstraint(WEST, plotPanel, PAD, WEST, c);
    layout.putConstraint(NORTH, plotPanel, PAD, SOUTH, plotButton);
    layout.putConstraint(SOUTH, plotPanel, -PAD, NORTH, scrollPane);
    layout.putConstraint(NORTH, scrollPane, PAD, SOUTH, plotPanel);
    layout.putConstraint(EAST, scrollPane, -PAD, EAST, c);
    layout.putConstraint(WEST, scrollPane, PAD, WEST, c);
    layout.putConstraint(SOUTH, scrollPane, -PAD, SOUTH, c);
    layout.putConstraint(NORTH, scrollPane, -40, SOUTH, c);
    setPreferredSize(new Dimension(800, 600));
  }

  @SuppressWarnings("ConstantConditions")
  void generateAndPlotFunction() {
    final String source = plotFunctionText.getText();
    function = newFunction(source);
    final XYSeries series = new XYSeries(source);
    for (int i = -100; i <= 100; i++) {
      double x = i / 10.0;
      series.add(x, function.f(x));
    }
    final XYDataset xyDataset = new XYSeriesCollection(series);

    boolean legend = false;
    boolean tooltips = true;
    boolean urls = false;
    JFreeChart chart = ChartFactory.createXYLineChart( //
      "f(x)=" + source, // Title
      "x", // X-Axis label
      "f(x)", // Y-Axis label
      xyDataset, PlotOrientation.VERTICAL, legend, tooltips, urls);
    final BufferedImage image = chart.createBufferedImage(plotPanel.getWidth(),
      plotPanel.getHeight());
    final JLabel plotComponent = new JLabel();
    plotComponent.setIcon(new ImageIcon(image));
    plotPanel.image = image;
    plotPanel.repaint();
  }

  static class PlotPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    BufferedImage image;

    @Override
    public void paint(final Graphics g) {
      if (image != null) {
        g.drawImage(image, 0, 0, this);
      } else {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, getWidth(), getHeight());
      }
    }
  }

  Function newFunction(final String expr) {
    errors.setText("");
    try {
      final String packageName = PACKAGE_NAME + digits();
      final String className = "Fx_" + (classNameSuffix++) + digits();
      final String qName = packageName + '.' + className;
      final String source = fillTemplate(packageName, className, expr);
      final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<>();
      Class<Function> compiledFunction = compiler.compile(qName, source, errs, Function.class);
      log(errs);
      return compiledFunction.newInstance();
    } catch (CharSequenceCompilerException e) {
      e.printStackTrace();
      log(e.getDiagnostics());
    } catch (InstantiationException | IllegalAccessException | IOException e) {
      e.printStackTrace();
      errors.setText(e.getMessage());
    }
    return NULL_FUNCTION;
  }

  private String digits() {
    return '_' + Long.toHexString(random.nextLong());
  }

  private String fillTemplate(String packageName, String className, String expression)
    throws IOException {
    if (template == null)
      template = readTemplate();
    String source = template.replace("$packageName", packageName)//
      .replace("$className", className)//
      .replace("$expression", expression);
    return source;
  }

  private String readTemplate() throws IOException {
    InputStream is = PlotterPanel.class.getResourceAsStream("Function.java.template");
    int size = is.available();
    byte bytes[] = new byte[size];
    if (size != is.read(bytes, 0, size))
      throw new IOException();
    return new String(bytes, "US-ASCII");
  }

  private void log(final DiagnosticCollector<JavaFileObject> diagnostics) {
    final StringBuilder sb = new StringBuilder();
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
      .getDiagnostics()) {
      sb.append(diagnostic.getMessage(null)).append("\n");
    }
    errors.setText(sb.toString());

  }

  static final Function NULL_FUNCTION = x -> 0.0;
}
