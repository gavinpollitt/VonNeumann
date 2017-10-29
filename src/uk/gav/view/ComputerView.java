package uk.gav.view;

import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import uk.gav.Computer;
import uk.gav.cpu.InterruptListener;
import uk.gav.cpu.InterruptType;

public class ComputerView extends Application implements InterruptListener {

	private final static int WIDTH = 800;
	private final static int HEIGHT = 800;
	private static Computer c;
	private static ComputerController cc;
	private static boolean proceed = true;
	private static final VBox memoryContainer = new VBox();
	private static final VBox infoContainer = new VBox();
	private static VBox cpuControl, registerHolder, aluHolder;
	private static HBox addressBusHolder, dataBusHolder;
	private static SimpleStringProperty mdrv;
	private static Text textMDR;
	private static Text textMAR;
	private static Text textPC;
	private static Text textACC;
	private static TableView<Map.Entry<Long, String>> ramTable;
	private static Text textAddBus = new Text("");
	private static Text textDataBus = new Text("");
	private static TextArea textInformation = new TextArea("");
	private static boolean played = false;
	private static int cycle = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		c = new Computer();
		cc = new ComputerController(c);
		c.getControlUnit().addInterruptsListener(this,
				new InterruptType[] { InterruptType.PRE_FETCH, InterruptType.PRE_DECODE, InterruptType.PRE_EXECUTE,
						InterruptType.POST_CYCLE, InterruptType.PRE_MAR, InterruptType.PRE_MDR,
						InterruptType.POST_DECODE, InterruptType.POST_EXECUTE, InterruptType.POST_FETCH,
						InterruptType.POST_MAR, InterruptType.POST_MDR, InterruptType.POST_PC,
						InterruptType.POST_COMMAND,InterruptType.PRE_ADD_BUS, InterruptType.PRE_DATA_BUS, InterruptType.POST_DATA_BUS});

		constructMemoryView();
		constructCPUView();
		constructInformationView();
		constructAddressBusView();
		constructDataBusView();

		// Now render the full screen
		BorderPane fullScreen = new BorderPane();

		// Memory
		fullScreen.setRight(memoryContainer);

		// CPU
		fullScreen.setLeft(cpuControl);

		// Info
		fullScreen.setCenter(infoContainer);

		// Address Bus
		fullScreen.setTop(addressBusHolder);

		// Data Bus
		fullScreen.setBottom(dataBusHolder);

		// Now put it all together under the scene
		Scene scene = new Scene(new Group());
		((Group) scene.getRoot()).getChildren().addAll(fullScreen);

		stage.setTitle("Von Neuman Demo");
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);
		stage.setScene(scene);
		stage.show();

	}

	public void interupted(InterruptType type) {
		this.interrupted(type,null);
	}
	@Override
	public void interrupted(InterruptType type, String description) {
		System.out.println("Interrupted");
		if (type == InterruptType.POST_CYCLE) {

			maintainRegisterView();
			maintainMemoryTable();
			maintainALUView();
			
			cycle++;
			textInformation.appendText("Cycle " + cycle + " completed\n\n");


			proceed = false;
			while (!proceed) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
		} else if (type == InterruptType.PRE_MAR) {
			textInformation.appendText("Updating the Memory Address Register\n");
			maintainMARView();
			maintainAddressBusView();

			textAddBus.textProperty().setValue("");

		} else if (type == InterruptType.PRE_ADD_BUS) {
			textInformation.appendText("Loading Address Bus with " + description + "\n");
		} else if (type == InterruptType.PRE_DATA_BUS) {
			textInformation.appendText("Loading Data Bus with " + description + "\n");
		} else if (type == InterruptType.POST_DATA_BUS) {
			textInformation.appendText("Unloading " + description + " from Data Bus\n");
		} else if (type == InterruptType.POST_MAR) {
			textInformation.appendText("Updating the Memory Address Register\n");
			maintainMARView();
		} else if (type == InterruptType.PRE_MDR) {
			textInformation.appendText("Updating the Memory Data Register and add to bus\n");
			maintainMDRView();
			maintainDataBusView(true);

			textDataBus.textProperty().setValue("");

		} else if (type == InterruptType.POST_MDR) {
			textInformation.appendText("Updating the Memory Data Register from the bus\n");
			maintainDataBusView();
			maintainMDRView();

			textDataBus.textProperty().setValue("");

		} else if (type == InterruptType.POST_PC) {
			textInformation.appendText("Updating the Program Counter\n");
			maintainPCView();
			
		} else if (type == InterruptType.POST_DECODE) {
			textInformation.appendText("Successfully decoded command: " + textMDR.getText() + "\n");
			textInformation.appendText("Completed Decode Cycle\n");
			proceed = false;
			while (!proceed) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}

			textDataBus.textProperty().setValue("");
		} else if (type == InterruptType.POST_FETCH) {
			maintainAddressBusView();
			maintainDataBusView();
			maintainRegisterView();
			textAddBus.textProperty().setValue("");
			textDataBus.textProperty().setValue("");

			textInformation.appendText("Completed Fetch Cycle\n");
			proceed = false;
			while (!proceed) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
		} else if (type == InterruptType.PRE_FETCH) {
			textInformation.appendText("Commencing Fetch Cycle...\n");
		} else if (type == InterruptType.PRE_DECODE) {
			textInformation.appendText("Commencing Decode Cycle...\n");
		} else if (type == InterruptType.PRE_EXECUTE) {
			textInformation.appendText("Commencing Execute Cycle...\n");
		} else if (type == InterruptType.POST_EXECUTE) {
			textInformation.appendText("Completed Execute Cycle...\n");
		} else if (type == InterruptType.POST_COMMAND) {
			textInformation.appendText(description);
		}

	}

	private static void maintainMemoryTable() {
		ObservableList<Map.Entry<Long, String>> items = FXCollections
				.observableArrayList(c.getMemory().getRAM().entrySet());
		ramTable.setItems(items);
		ramTable.refresh();
	}

	private static void constructMemoryView() {
		// Set the memory table display
		ramTable = new TableView<>();
		ramTable.setEditable(false);
		ramTable.setSortPolicy(null);
		ramTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		maintainMemoryTable();

		TableColumn<Map.Entry<Long, String>, Long> addressCol = new TableColumn<>("Address");
		TableColumn<Map.Entry<Long, String>, String> contentCol = new TableColumn<>("Content");

		addressCol.setSortable(false);
		addressCol.setCellValueFactory((p) -> {
			return new ReadOnlyObjectWrapper<Long>(p.getValue().getKey());
		});
		contentCol.setSortable(false);
		contentCol.setCellValueFactory((p) -> {
			return new SimpleStringProperty(p.getValue().getValue());
		});

		ramTable.getColumns().setAll(addressCol, contentCol);

		// Set the label for memory
		final Label label = new Label("RAM");
		label.setFont(new Font("Arial", 20));

		// Set the buttons for memory
		Button startButton = new Button("Start");
		startButton.setOnAction((ActionEvent e) -> {
			try {
				memoryContainer.getChildren().removeAll(startButton);
				proceed = false;
				Button proceedButton = new Button("Proceed");
				memoryContainer.getChildren().add(proceedButton);
				proceedButton.setOnAction((ActionEvent ae) -> {
					if (cc.executing) {
						proceed = true;
					}
					else {
						Platform.exit();
					}						
				});

				cc.start();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}

		});

		memoryContainer.setSpacing(3);
		memoryContainer.setPadding(new Insets(10, 0, 0, 10));
		memoryContainer.getChildren().addAll(label, ramTable, startButton);

	}

	private static void constructCPUView() {
		cpuControl = new VBox();
		cpuControl.setPadding(new Insets(10, 20, 5, 10));
		cpuControl.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

		constructRegisterView();
		constructALUView();

		cpuControl.getChildren().addAll(registerHolder, aluHolder);

	}

	private static void maintainRegisterView() {
		maintainMARView();
		maintainMDRView();
		maintainPCView();
	}

	private static void maintainMARView() {
		if (c.getControlUnit().getRh().getMar().getValue() != null) {
			textMAR.textProperty().setValue(c.getControlUnit().getRh().getMar().getValue().toString());
		} else {
			textMAR.textProperty().setValue("");
		}

	}
	
	private static void maintainMDRView() {
		mdrv = new SimpleStringProperty(c.getControlUnit().getRh().getMdr().getValue());
		textMDR.textProperty().bindBidirectional(mdrv);
	}

	private static void maintainPCView() {
		if (c.getControlUnit().getPC().getValue() != null) {
			textPC.textProperty().setValue(c.getControlUnit().getPC().getValue().toString());
		} else {
			textPC.textProperty().setValue("");
		}		
	}

	private static void constructRegisterView() {
		final Label labelReg = new Label("Registers");
		labelReg.setFont(new Font("Arial", 20));

		registerHolder = new VBox();
		registerHolder.setPadding(new Insets(10, 20, 15, 10));
		registerHolder.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

		HBox PC = new HBox();
		PC.setStyle("-fx-border-style: dashed inside;" + "-fx-border-width: 1;" + "-fx-border-color: red;");
		PC.setPadding(new Insets(5, 0, 5, 0));
		final Label labelPC = new Label("PC");
		labelPC.setFont(new Font("Arial", 12));
		textPC = new Text();

		PC.setSpacing(5);
		PC.getChildren().addAll(labelPC, textPC);

		HBox MAR = new HBox();
		MAR.setStyle("-fx-border-style: dashed inside;" + "-fx-border-width: 1;" + "-fx-border-color: red;");
		MAR.setPadding(new Insets(5, 0, 5, 0));
		final Label labelMAR = new Label("MAR");
		labelMAR.setFont(new Font("Arial", 12));
		textMAR = new Text();

		MAR.setSpacing(5);
		MAR.getChildren().addAll(labelMAR, textMAR);

		HBox MDR = new HBox();
		MDR.setStyle("-fx-border-style: dashed inside;" + "-fx-border-width: 1;" + "-fx-border-color: red;");
		MDR.setPadding(new Insets(5, 0, 5, 0));
		final Label labelMDR = new Label("MDR");
		labelMDR.setFont(new Font("Arial", 12));
		textMDR = new Text();

		maintainRegisterView();

		MDR.setSpacing(5);
		MDR.getChildren().addAll(labelMDR, textMDR);
		registerHolder.getChildren().addAll(labelReg, PC, MAR, MDR);

	}

	private static void maintainALUView() {
		if (c.getControlUnit().getAlu().getAcc().getValue() != null) {
			textACC.textProperty().setValue(c.getControlUnit().getAlu().getAcc().getValue().toString());
		} else {
			textACC.textProperty().setValue("");
		}

	}

	private static void constructALUView() {
		final Label labelReg = new Label("ALU");
		labelReg.setFont(new Font("Arial", 20));

		aluHolder = new VBox();
		aluHolder.setPadding(new Insets(10, 20, 25, 10));
		aluHolder.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

		HBox ACC = new HBox();
		ACC.setStyle("-fx-border-style: dashed inside;" + "-fx-border-width: 1;" + "-fx-border-color: red;");
		ACC.setPadding(new Insets(5, 0, 5, 0));
		final Label labelACC = new Label("ACC");
		labelACC.setFont(new Font("Arial", 12));
		textACC = new Text();

		maintainALUView();

		ACC.setSpacing(5);
		ACC.getChildren().addAll(labelACC, textACC);
		aluHolder.getChildren().addAll(labelReg, ACC);

	}

	private static void maintainAddressBusView() {
		textAddBus.textProperty().setValue(c.getControlUnit().getRh().getMar().getValue() + "");

		KeyValue initKeyValue = new KeyValue(textAddBus.translateXProperty(), 0);
		KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);

		KeyValue endKeyValue = new KeyValue(textAddBus.translateXProperty(), WIDTH * 0.7);
		KeyFrame endFrame = new KeyFrame(Duration.seconds(2), endKeyValue);

		Timeline timeline = new Timeline(initFrame, endFrame);
		played = false;
		timeline.setOnFinished(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				played = true;
			}
		});

		timeline.setCycleCount(1);
		timeline.play();

		while (!played) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}

	}

	private static void constructAddressBusView() {
		addressBusHolder = new HBox();

		final Label labelBus = new Label("Address Bus");
		labelBus.setFont(new Font("Arial", 20));

		HBox addBUS = new HBox();
		addBUS.setPrefWidth(WIDTH * 0.8);
		addBUS.setStyle("-fx-width:200;-fx-height:80;-fx-padding: 10;" + "-fx-border-style: solid inside;"
				+ "-fx-border-width: 2;" + "-fx-border-insets: 5;" + "-fx-border-radius: 5;"
				+ "-fx-border-color: red;");

		addBUS.getChildren().addAll(textAddBus);

		addressBusHolder.getChildren().addAll(addBUS, labelBus);

	}

	private static void maintainDataBusView() {
		maintainDataBusView(false);
	}

	private static void maintainDataBusView(boolean reverse) {
		textDataBus.textProperty().setValue(c.getControlUnit().getRh().getMdr().getValue());

		KeyValue initKeyValue = new KeyValue(textDataBus.translateXProperty(), reverse ? 0 : WIDTH * 0.7);
		KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);

		KeyValue endKeyValue = new KeyValue(textDataBus.translateXProperty(), reverse ? WIDTH * 0.7 : 0);
		KeyFrame endFrame = new KeyFrame(Duration.seconds(2), endKeyValue);

		Timeline timeline = new Timeline(initFrame, endFrame);
		played = false;
		timeline.setOnFinished(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				played = true;
			}
		});

		timeline.setCycleCount(1);
		timeline.play();

		while (!played) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}

	}

	private static void constructDataBusView() {
		dataBusHolder = new HBox();

		final Label labelBus = new Label("Data Bus");
		labelBus.setFont(new Font("Arial", 20));

		HBox dataBUS = new HBox();
		dataBUS.setPrefWidth(WIDTH * 0.8);
		dataBUS.setStyle("-fx-width:200;-fx-height:80;-fx-padding: 10;" + "-fx-border-style: solid inside;"
				+ "-fx-border-width: 2;" + "-fx-border-insets: 5;" + "-fx-border-radius: 5;"
				+ "-fx-border-color: red;");

		dataBUS.getChildren().addAll(textDataBus);

		dataBusHolder.getChildren().addAll(dataBUS, labelBus);

	}

	private static void constructInformationView() {
		final Label labelInfo = new Label("Information");
		labelInfo.setFont(new Font("Arial", 20));
		infoContainer.setStyle(
				"-fx-width: 100; -fx-padding: 10;" + "-fx-border-style: dashed inside;" + "-fx-border-width: 2;"
						+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: green;");
		infoContainer.setSpacing(5);
		textInformation.setEditable(false);
		textInformation.setPrefWidth(WIDTH * 0.3);
		textInformation.setPrefHeight(HEIGHT * 0.7);
		infoContainer.getChildren().addAll(labelInfo, textInformation);

	}

	private static class ComputerController implements Runnable, InterruptListener {

		private Computer computer;
		private Long pMemorySlot;
		private final Thread thread = new Thread(this);
		private boolean executing = false;

		public ComputerController(Computer c) throws Exception {
			this.computer = c;
			c.getControlUnit().addInterruptsListener(this, new InterruptType[] { InterruptType.POST_EXIT });
			pMemorySlot = computer.loadProgram();
		}

		@Override
		public void run() {
			while (executing) {
				try {
					computer.runProgram(pMemorySlot);
				} catch (Exception e) {
					executing = false;
				}
			}
		}

		public void start() {
			executing = true;
			thread.start();
		}

		@Override
		public void interrupted(InterruptType type, String description) {
			if (type == InterruptType.POST_EXIT && executing) {
				System.out.println("Thread is complete");
				executing = false;
			}
			
		}
	}
}