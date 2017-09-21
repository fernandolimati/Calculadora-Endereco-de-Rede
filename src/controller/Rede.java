package controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

//Define-se como número de rede o primeiro endereço da faixa e como número de broadcast o último

public class Rede {
	
	private int[] enderecoIP = new int[4];
	private int[] enderecoRede = new int[4];
	private int[] mascaraSubRede = new int[4];
	private int[] broadcast = new int[4];
	private int[] ipRangeStart = new int[4];
	private int[] ipRangeEnd = new int[4];
	private String classeRede;
	private int bitsMascara; //entrada do usuário, bits reservados para identificacao da mascara
	private int bitsSubRede; //bits dedicados para a subrede
	private int hostPorSubRede;
	private int numMaxSubRede; //2 elevado aos bits de mascara -2
	private boolean valido;
	
	
	public Rede(String stringEnderecoIP, int bitsMascara) throws IOException {
		valido = false;
		String[] stringEnderecoIP2 = stringEnderecoIP.split("\\.");
		for(int i=0;i<stringEnderecoIP2.length;i++)
			enderecoIP[i] = Integer.parseInt(stringEnderecoIP2[i]);
		this.setBitsMascara(bitsMascara);
		this.setMascaraSubRede();
		this.setBroadcast();
		this.setClasseRede();
		this.setNumMaxSubRede();
		this.setHostPorSubRede();
		this.setEnderecoRede();
		this.setIpRange();
	}
	
	public boolean isValido(){
		return this.valido;
	}
	
	
	
	public String getEnderecoIPString() {
		String enderecoIPString;
		enderecoIPString = Integer.toString(enderecoIP[0]);
		enderecoIPString += "."+Integer.toString(enderecoIP[1]);
		enderecoIPString += "."+Integer.toString(enderecoIP[2]);
		enderecoIPString += "."+Integer.toString(enderecoIP[3]);
		return enderecoIPString;
	}
	public int[] getEnderecoIPInt() {
		return this.enderecoIP;
	}
	public void setEnderecoIP(String stringEnderecoIP) {
		String[] stringEnderecoIP2 = stringEnderecoIP.split(".");
		for(int i=0;i<stringEnderecoIP2.length;i++){
			this.enderecoIP[i] = Integer.parseInt(stringEnderecoIP2[i]);
		}
	}
	public String getMascaraSubRedeString() {
		String enderecoSubRedeString;
		enderecoSubRedeString = Integer.toString(mascaraSubRede[0]);
		enderecoSubRedeString += "."+Integer.toString(mascaraSubRede[1]);
		enderecoSubRedeString += "."+Integer.toString(mascaraSubRede[2]);
		enderecoSubRedeString += "."+Integer.toString(mascaraSubRede[3]);
		return enderecoSubRedeString;
	}
	
	public void setMascaraSubRede() {
		if(bitsMascara%8==0){
			int i;
			for(i=0;i < (bitsMascara/8);i++)
				mascaraSubRede[i] = 255;
			if(i!=3) 
				for(;i<4;i++) mascaraSubRede[i] = 000;
		}else{
			int i,expoente=7;
			for(i=0;i < (bitsMascara/8);i++)
				mascaraSubRede[i] = 255;
			mascaraSubRede[i] = 0;
			for(int j=0;j<(bitsMascara%8);j++){
				mascaraSubRede[i]+=Math.pow(2, expoente);
				expoente --;
			}
			for(i=i+1;i<4;i++){
				mascaraSubRede[i] = 0;
			}
		}
	}
	
	public int[] getBroadcast() {
		return broadcast;
	}
	
	public String getBroadcastString() {
		String enderecoBroadcast;
		enderecoBroadcast = Integer.toString(broadcast[0]);
		enderecoBroadcast += "."+Integer.toString(broadcast[1]);
		enderecoBroadcast += "."+Integer.toString(broadcast[2]);
		enderecoBroadcast += "."+Integer.toString(broadcast[3]);
		return enderecoBroadcast;
	}
	
	public void setBroadcast() throws IOException {
		/*CALCULO BROADCAST
		1º passa IP para binário
		172.16.0.35 = 10101100.00010000.00000000.00100011
		2º passa MASCARA para binário
		255.255.0.0 = 11111111.11111111.00000000.00000000
		3 º operação NOT na mascara
		11111111.11111111.00000000.00000000 = 00000000.00000000.11111111.11111111
		4º faz o OR entre o IP e o NOT da máscara para achar o broadcast
		10101100.00010000.00000000.00100011  = ip
		OR
		00000000.00000000.11111111.11111111  = not mascara
		-----------------------------------
		10101100.00010000.11111111.11111111 = Broadcast
		5º passa broadcast para decimal
		10101100.00010000.11111111.11111111 = 172.16.255.255*/
				
		//FAZENDO OR NO ENDERECO IP E NO NOT MASCARA
		for(int i=0;i<4;i++)
			broadcast[i] = 256+(~(mascaraSubRede[i]))|(enderecoIP[i]);
		
	}
	
	public int getBitsMascara() {
		return bitsMascara;
	}
	
	public void setBitsMascara(int bitsMascara) {
		this.bitsMascara = bitsMascara;
	}
	
	public int getBitsSubRede() {
		return bitsSubRede;
	}
	
	public void setBitsSubRede(int bitsSubRede) {
		this.bitsSubRede = bitsSubRede;
	}
	
	public String getClasseRede() {
		return classeRede;
	}
	
	public void setClasseRede() {
		String aux = Integer.toBinaryString(enderecoIP[0]);
		String formatted = String.format("%8s", aux);
		formatted = formatted.replaceAll(" ", "0");
		if(formatted.charAt(0) == '0' && bitsMascara >= 8 && bitsMascara <= 31){
			classeRede = "A";
			this.setBitsSubRede((bitsMascara-8));
			this.valido = true;
		}
		if(formatted.charAt(0) == '1' && formatted.charAt(1)== '0' && bitsMascara >= 16 && bitsMascara <= 31){
			classeRede = "B";
			this.setBitsSubRede((bitsMascara-16));
			this.valido = true;
		}
		if(formatted.charAt(0) == '1' && formatted.charAt(1)== '1' && formatted.charAt(2)== '0' && bitsMascara >= 24 && bitsMascara < 31 ){
			classeRede = "C";
			this.setBitsSubRede((bitsMascara-24));
			this.valido = true;
		}
	}



	public int getHostPorSubRede() {
		return hostPorSubRede;
	}



	public void setHostPorSubRede() {
		if(classeRede == "A"){
			if(bitsSubRede==31)
				hostPorSubRede = 2;
			else 
				hostPorSubRede = (int) Math.pow(2, (24-bitsSubRede)) -2;
		}
		
		if(classeRede == "B"){
			if(bitsSubRede==31)
				hostPorSubRede = 2;
			else 
				hostPorSubRede = (int) Math.pow(2, (16-bitsSubRede)) -2;
		}	
		
		if(classeRede == "C"){
			if(bitsSubRede==31)
				hostPorSubRede = 2;
			else 
				hostPorSubRede = (int) Math.pow(2, (8-bitsSubRede)) -2;
		}
	}



	public int getNumMaxSubRede() {
		return numMaxSubRede;
	}



	public void setNumMaxSubRede() {
		this.numMaxSubRede = (int) Math.pow(2, bitsSubRede);
	}



	public int[] getEnderecoRede() {
		return enderecoRede;
	}
	
	public String getEnderecoRedeString() {
		String enderecoRedeString;
		enderecoRedeString = Integer.toString(enderecoRede[0]);
		enderecoRedeString += "."+Integer.toString(enderecoRede[1]);
		enderecoRedeString += "."+Integer.toString(enderecoRede[2]);
		enderecoRedeString += "."+Integer.toString(enderecoRede[3]);
		return enderecoRedeString;
	}

	public void setEnderecoRede() {
		/*CALCULO REDE
		1º passa IP para binário
		172.16.0.35 = 10101100.00010000.00000000.00100011
		2º passa MASCARA para binário
		255.255.0.0 = 11111111.11111111.00000000.00000000
		3º faz o AND entre o IP e a mascara para achar a rede
		10101100.00010000.00000000.00100011  = ip
		AND
		11111111.11111111.00000000.00000000  = mascara
		-----------------------------------
		10101100.00010000.00000000.00000000 = rede
		4º passa rede para decimal
		10101100.00010000.00000000.00000000  = 172.16.0.0*/
		for(int i=0;i<4;i++)
			enderecoRede[i] = ((enderecoIP[i])&((mascaraSubRede[i])));
	}


	public String[] getIpRangeString() {
		String[] ipRange = new String[2];
		ipRange[0] = Integer.toString(ipRangeStart[0])+ "." + Integer.toString(ipRangeStart[1])+ "." + Integer.toString(ipRangeStart[2])+ "." + Integer.toString(ipRangeStart[3]);
		ipRange[1] = Integer.toString(ipRangeEnd[0])+ "." + Integer.toString(ipRangeEnd[1])+ "." + Integer.toString(ipRangeEnd[2])+ "." + Integer.toString(ipRangeEnd[3]);
		return ipRange;
	}

	public void setIpRange() {
		ipRangeStart[0] = enderecoRede[0];
		ipRangeStart[1] = enderecoRede[1];
		ipRangeStart[2] = enderecoRede[2];
		ipRangeStart[3] = enderecoRede[3]+1;
		
		ipRangeEnd[0] = broadcast[0];
		ipRangeEnd[1] = broadcast[1];
		ipRangeEnd[2] = broadcast[2];
		ipRangeEnd[3] = broadcast[3]-1;
	}
	
	public String[][] calcTotasSubRedes(){
		String[][] infoSubRedesString = new String[numMaxSubRede][4];
		int[][] infoSubRedes = new int[numMaxSubRede][4];			//as linhas sao as quantidades de sub redes e coluna os dados na ordem: 0-rede 1-startip 2-endIP 3-broadcast
		int incrementadorSub = hostPorSubRede+2;
		if(this.classeRede == "C"){
			infoSubRedes[0][0] = enderecoRede[3];//DIGITO DA REDE
			infoSubRedes[0][1] = ipRangeStart[3];//STARTIP
			infoSubRedes[0][2] = ipRangeEnd[3];//ENDIP
			infoSubRedes[0][3] = broadcast[3];//BROADCAST
			for(int i=1;i<numMaxSubRede;i++){
				infoSubRedes[i][0] = infoSubRedes[i-1][0]+incrementadorSub;//DIGITO DA REDE
				infoSubRedes[i][1] = infoSubRedes[i-1][1]+incrementadorSub;//STARTIP
				infoSubRedes[i][2] = infoSubRedes[i-1][2]+incrementadorSub;//ENDIP
				infoSubRedes[i][3] = infoSubRedes[i-1][3]+incrementadorSub;//BROADCAST
			}
			for(int i=0;i<numMaxSubRede;i++){
				infoSubRedesString[i][0] =""+enderecoRede[0]+"."+enderecoRede[1]+"."+enderecoRede[2]+"."+infoSubRedes[i][0];
			    infoSubRedesString[i][1] =""+enderecoRede[0]+"."+enderecoRede[1]+"."+enderecoRede[2]+"."+infoSubRedes[i][1];
			    infoSubRedesString[i][2] =""+enderecoRede[0]+"."+enderecoRede[1]+"."+enderecoRede[2]+"."+infoSubRedes[i][2];
			    infoSubRedesString[i][3] =""+enderecoRede[0]+"."+enderecoRede[1]+"."+enderecoRede[2]+"."+infoSubRedes[i][3];
			}	
		}
		return infoSubRedesString;
	}
	
	public void gerarArquivo(Rede rede){
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		String nomeArquivoPDF = "RelatorioRede-"+(dateFormat.format(cal.getTime()).toString())+".pdf";
		Document documentoPDF = new Document();
		try {
			PdfWriter.getInstance(documentoPDF, new FileOutputStream(nomeArquivoPDF));
			documentoPDF.open();
			documentoPDF.addSubject("Relatório de Rede");
			documentoPDF.addKeywords("Creator - Fernando Lima");
			documentoPDF.addCreator("Calculadora de Rede");
			documentoPDF.addAuthor("Fernando de Lima Santos");
			
			
			Font fontTitulo = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.BLACK);
			Font fontSubTitulo = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.BOLD, new BaseColor(255,255,255));
			Font fontSubTitulo2 = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.BOLD, new BaseColor(27,40,49));
			Font fontDados = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.NORMAL, new BaseColor(27,40,49));
			Paragraph titulo = new Paragraph("Relatório da Rede",fontTitulo);
			titulo.setAlignment(Element.ALIGN_CENTER);
			documentoPDF.add(titulo);
			
			String[][] resultado = this.calcTotasSubRedes();
			
			//PDF COMECO SOBRE A REDE
			PdfPTable table = new PdfPTable(new float[] { 1,1 });
	        PdfPCell cell;
	        String texto;
	        
	        texto = "Informações sobre o IP";
        	cell = new PdfPCell(new Phrase(texto,fontSubTitulo) );
	        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        	cell.setBackgroundColor(new BaseColor(27,40,49));
        	cell.setUseAscender(true);
        	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        	cell.setColspan(2);
        	cell.setBorder(Rectangle.NO_BORDER);
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Classe",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(rede.getClasseRede(),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("IP",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(rede.getEnderecoIPString(),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	
        	cell = new PdfPCell(new Phrase("Máscara sub-rede",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(rede.getMascaraSubRedeString(),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	   	
        	table.setSpacingBefore(40.0f);
        	table.setWidthPercentage(50.0f);
        	table.setHorizontalAlignment(Element.ALIGN_CENTER);
        	documentoPDF.add(table);
	        
        	//LEGENDA
        	titulo = new Paragraph("*Informações geradas para a subrede específica do IP de entrada.",new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL, new BaseColor(27,40,49)));
        	titulo.setSpacingAfter(0.0f);
        	titulo.setSpacingBefore(0.0f);
        	titulo.setAlignment(Element.ALIGN_CENTER);
        	documentoPDF.add(titulo);
        	
        	//INFORMACOES DA REDE
        	table = new PdfPTable(new float[] { 1,1 });
	        texto = "Informações sobre a Rede";
        	cell = new PdfPCell(new Phrase(texto,fontSubTitulo) );
	        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        	cell.setBackgroundColor(new BaseColor(27,40,49));
        	cell.setUseAscender(true);
        	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        	cell.setColspan(2);
        	cell.setBorder(Rectangle.NO_BORDER);
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Classe",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(rede.getClasseRede(),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Endereço Rede",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(rede.getEnderecoRedeString(),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Máscara sub-rede",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(rede.getMascaraSubRedeString(),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Broadcast",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(rede.getBroadcastString(),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Bits SubRede",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(Integer.toString(rede.getBitsSubRede()),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Bits Máscara",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(Integer.toString(rede.getBitsMascara()),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Max SubRedes",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(Integer.toString(rede.getNumMaxSubRede()),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("Hosts por SubRede",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	cell = new PdfPCell(new Phrase(Integer.toString(rede.getHostPorSubRede()),fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("IP Range",fontSubTitulo2));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	String[] rangeIP = rede.getIpRangeString();
        	cell = new PdfPCell(new Phrase((rangeIP[0]+" - "+rangeIP[1]) ,fontDados));
        	cell.setBorder(Rectangle.NO_BORDER);
        	cell.setBackgroundColor(new BaseColor(245,222,179));
        	table.addCell(cell);
        	
        	table.setSpacingBefore(20.0f);
        	table.setSpacingAfter(30.0f);
        	table.setWidthPercentage(50.0f);
        	table.setHorizontalAlignment(Element.ALIGN_CENTER);
        	documentoPDF.add(table);
        	
        	//REPRESENTACAO DA REDE
        	table = new PdfPTable(35);
        	texto = "Representação Binária da Rede";
        	cell = new PdfPCell(new Phrase(texto,fontSubTitulo) );
	        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        	cell.setBackgroundColor(new BaseColor(27,40,49));
        	cell.setColspan(35);
        	cell.setUseAscender(true);
        	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        	cell.setBorder(Rectangle.NO_BORDER);
        	table.addCell(cell);
        	
        	for(int j=1;j<=3;j++){
        		for(int i=1;i<=8;i++){
            		cell = new PdfPCell(new Phrase("N" ,fontDados));
                	cell.setBorder(Rectangle.NO_BORDER);
                	cell.setUseAscender(true);
                	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                	cell.setBackgroundColor(BaseColor.GREEN);
                	table.addCell(cell);
            	}
        		cell = new PdfPCell(new Phrase("." ,fontDados));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setUseAscender(true);
            	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell);	
        	}
        	for(int i=1;i<=this.bitsSubRede;i++){
        		cell = new PdfPCell(new Phrase("S" ,fontDados));
            	cell.setBorder(Rectangle.NO_BORDER);
            	cell.setUseAscender(true);
            	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            	cell.setBackgroundColor(BaseColor.YELLOW);
            	table.addCell(cell);
        	}
        	for(int i=1;i<=(8-this.bitsSubRede);i++){
        		cell = new PdfPCell(new Phrase("H" ,fontDados));
            	cell.setBorder(Rectangle.NO_BORDER);
            	cell.setUseAscender(true);
            	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            	cell.setBackgroundColor(BaseColor.GRAY);
            	table.addCell(cell);
        	}
        	
        	cell = new PdfPCell(new Phrase("*Legenda: N = Network (bits p/ rede)",new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL, new BaseColor(27,40,49))) );
	        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        	cell.setBackgroundColor(new BaseColor(255,255,255));
        	cell.setColspan(35);
        	cell.setUseAscender(true);
        	cell.setVerticalAlignment(Element.ALIGN_LEFT);
        	cell.setBorder(Rectangle.NO_BORDER);
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("                  S = Sub Network (bits p/ subrede)",new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL, new BaseColor(27,40,49))) );
	        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        	cell.setBackgroundColor(new BaseColor(255,255,255));
        	cell.setColspan(35);
        	cell.setUseAscender(true);
        	cell.setVerticalAlignment(Element.ALIGN_LEFT);
        	cell.setBorder(Rectangle.NO_BORDER);
        	table.addCell(cell);
        	
        	cell = new PdfPCell(new Phrase("                  H = Host (bits p/ host)",new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL, new BaseColor(27,40,49))) );
	        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        	cell.setBackgroundColor(new BaseColor(255,255,255));
        	cell.setColspan(35);
        	cell.setUseAscender(true);
        	cell.setVerticalAlignment(Element.ALIGN_LEFT);
        	cell.setBorder(Rectangle.NO_BORDER);
        	table.addCell(cell);
        	
        	table.setSpacingBefore(0.0f);
        	table.setWidthPercentage(65.0f);
        	table.setHorizontalAlignment(Element.ALIGN_CENTER);
        	documentoPDF.add(table);
        	
        	titulo = new Paragraph("Listagem completa de todas as subredes disponíveis para essa classe de rede, mostrando o range, broadcast e endereço de rede."
        			+ "Caso encontre algum erro nos cálculos, por favor, entre em contato com o desenvolvedor do programa.",fontDados);
        	titulo.setSpacingAfter(20.0f);
        	titulo.setSpacingBefore(25.0f);
        	titulo.setAlignment(Element.ALIGN_JUSTIFIED_ALL);
        	documentoPDF.add(titulo);
			
	        for(int i=0;i<resultado.length;i++){
	        	// a table with three columns
		        PdfPTable table1 = new PdfPTable(new float[] { 2, 3 });
		        PdfPCell cell1;
		        String texto1;
		        boolean listra = false;
		        if(i%2==0)
		        	listra = true;
		        
		        texto1 = "Sub rede nº"+(i+1)+" de "+resultado.length;
	        	cell1 = new PdfPCell(new Phrase(texto1,fontSubTitulo) );
		        cell1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	        	cell1.setBackgroundColor(new BaseColor(27,40,49));
	        	cell1.setBorder(Rectangle.NO_BORDER);
	        	cell1.setColspan(2); 
	        	cell1.setUseAscender(true);
	        	cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        	table1.addCell(cell1);
	        	
	        	cell1 = new PdfPCell(new Phrase("Endereco de Rede",fontSubTitulo2));
	        	cell1.setBorder(Rectangle.NO_BORDER);
	        	if(listra) cell1.setBackgroundColor(new BaseColor(230,230,250));
	        	table1.addCell(cell1);
	        	cell1 = new PdfPCell(new Phrase(resultado[i][0],fontDados));
	        	cell1.setBorder(Rectangle.NO_BORDER);
	        	if(listra) cell1.setBackgroundColor(new BaseColor(230,230,250));
	        	table1.addCell(cell1);
	        	
	        	cell1 = new PdfPCell(new Phrase("Broadcast",fontSubTitulo2));
	        	cell1.setBorder(Rectangle.NO_BORDER);
	        	if(listra) cell1.setBackgroundColor(new BaseColor(230,230,250));
	        	table1.addCell(cell1);
	        	cell1 = new PdfPCell(new Phrase(resultado[i][3],fontDados));
	        	cell1.setBorder(Rectangle.NO_BORDER);
	        	if(listra) cell1.setBackgroundColor(new BaseColor(230,230,250));
	        	table1.addCell(cell1);
	        	
	        	cell1 = new PdfPCell(new Phrase("IP Range",fontSubTitulo2));
	        	cell1.setBorder(Rectangle.NO_BORDER);
	        	if(listra) cell1.setBackgroundColor(new BaseColor(230,230,250));
	        	table1.addCell(cell1);
	        	cell1 = new PdfPCell(new Phrase(resultado[i][1]+" - "+resultado[i][2],fontDados));
	        	cell1.setBorder(Rectangle.NO_BORDER);
	        	if(listra) cell1.setBackgroundColor(new BaseColor(230,230,250));
	        	table1.addCell(cell1);
	        	
	        	table1.setSpacingAfter(40.0f);
	        	table1.setWidthPercentage(55.0f);
	        	table1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        	documentoPDF.add(table1);
	        }
	        
			
		} catch (FileNotFoundException | DocumentException e) {
			System.out.println("Erro: "+e);
		} finally{
			documentoPDF.close();
		}
		
		try {
			Desktop.getDesktop().open(new File(nomeArquivoPDF));
		} catch (IOException e) {
			System.out.println("Erro: "+e);
		}
		
	}


	
}
