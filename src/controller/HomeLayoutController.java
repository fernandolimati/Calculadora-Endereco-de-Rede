package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableView;

public class HomeLayoutController {
	@FXML GridPane infoGridPane;
	@FXML Button calcularBtn;
	@FXML Label classeLabel;
	@FXML Label ipLabel;
	@FXML Label mascaraLabel;
	@FXML Label enderecoRedeLabel;
	@FXML Label broadcastLabel;
	@FXML Label bitSubRedeLabel;
	@FXML Label bitMascaraLabel;
	@FXML Label naxSubRedeLabel;
	@FXML Label hostsSubRedeLabel;
	@FXML Label ipRangeStartLabel;
	@FXML Label ipRangeSeparator;
	@FXML Label ipRangeEndLabel;
	@FXML TextField ipRedeTextField;
	@FXML TextField bitMascaraTextField;
	@FXML Button gerarPDFBtn;
	
	private Rede rede;
	@FXML TableView<Subrede> tableViewSubRede;
	@FXML TableColumn<Subrede,String> colunaSubRedeNome;
	@FXML TableColumn<Subrede,String> colunaEndRede;
	@FXML TableColumn<Subrede,String> colunaBroadcast;
	@FXML TableColumn<Subrede,String> colunaIPRange;
	@FXML Label informacoesLabel;
	@FXML Button gerarPDFBtn1;
	

	@FXML
	public void initialize() {
		ipRedeTextField.setFocusTraversable(true);
		//tableViewSubRede.setVisible(false);
		gerarPDFBtn.setVisible(false);
		//infoGridPane.setVisible(false);
		//informacoesLabel.setVisible(false);
	}

	@FXML public void calcBtnClick(ActionEvent event) {
		String ipRedeInput = ipRedeTextField.getText();
		String bitMascaraInput = bitMascaraTextField.getText();
		ipRedeInput = ipRedeInput.trim();
		bitMascaraInput = bitMascaraInput.trim();
		
		if(!ipRedeTextField.getText().isEmpty() && !bitMascaraTextField.getText().isEmpty()){
			try {
				rede = new Rede(ipRedeInput,Integer.parseInt(bitMascaraInput));
				
				if(rede.isValido()){
					String[] rangeIP = rede.getIpRangeString();
					informacoesLabel.setVisible(true);
					infoGridPane.setVisible(true);
					classeLabel.setText(rede.getClasseRede());
					ipLabel.setText(rede.getEnderecoIPString());
					mascaraLabel.setText(rede.getMascaraSubRedeString());
					enderecoRedeLabel.setText(rede.getEnderecoRedeString());
					broadcastLabel.setText(rede.getBroadcastString());
					bitSubRedeLabel.setText(Integer.toString(rede.getBitsSubRede()));
					bitMascaraLabel.setText(Integer.toString(rede.getBitsMascara()));
					naxSubRedeLabel.setText(Integer.toString(rede.getNumMaxSubRede()));
					hostsSubRedeLabel.setText(Integer.toString(rede.getHostPorSubRede()));
					ipRangeStartLabel.setText(rangeIP[0]);
					ipRangeSeparator.setText(" - ");
					ipRangeEndLabel.setText(rangeIP[1]);
					
					if(rede.getClasseRede() == "C"){
						gerarPDFBtn.setVisible(true);
						tableViewSubRede.setVisible(true);
						setListWiewSubRedes();
					}else{
						gerarPDFBtn.setVisible(false);
						tableViewSubRede.setVisible(false);
					}
					
				}else{
					
				}
				
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@FXML
	public void gerarPDFBtnClick() {
		if(rede != null){
			rede.gerarArquivo(rede);
		}
		
	}

	@FXML public void ipRedeTextFieldReleased() {
		TextFieldFormatter fieldFormatter = new TextFieldFormatter();
		fieldFormatter.setMask("###.###.###.###");
		fieldFormatter.setCaracteresValidos("0123456789");
		fieldFormatter.setTf(ipRedeTextField);
		fieldFormatter.formatter();
	}

	@FXML public void bitMascaraTextFieldReleased() {
		TextFieldFormatter fieldFormatter = new TextFieldFormatter();
		fieldFormatter.setMask("##");
		fieldFormatter.setCaracteresValidos("0123456789");
		fieldFormatter.setTf(bitMascaraTextField);
		fieldFormatter.formatter();
	}
	
	@FXML public void setListWiewSubRedes(){
		String[][] subRedes = rede.calcTotasSubRedes();
		List<Subrede> subRedeList = new ArrayList<Subrede>();
		ObservableList<Subrede> listaSubRedes = FXCollections.observableArrayList(subRedeList);
		
		for(int i=0;i<subRedes.length;i++)
			listaSubRedes.add(new Subrede("Sub Rede"+(i+1),subRedes[i][0],subRedes[i][3],""+subRedes[i][1]+" até "+subRedes[i][2]));
		
		colunaSubRedeNome.setCellValueFactory(new PropertyValueFactory<Subrede,String>("identificacao"));
		colunaEndRede.setCellValueFactory(new PropertyValueFactory<Subrede,String>("enderecoRede"));
		colunaBroadcast.setCellValueFactory(new PropertyValueFactory<Subrede,String>("broadcast"));
		colunaIPRange.setCellValueFactory(new PropertyValueFactory<Subrede,String>("ipRange"));
		tableViewSubRede.setItems(listaSubRedes);
			
	}

	@FXML public void sairBtnClick(ActionEvent event) {
		((Node) (event.getSource())).getScene().getWindow().hide();
	}
}
