package controller;

public class Subrede{
	String identificacao;
	String enderecoRede;
	String broadcast;
	String ipRange;
	
	public Subrede(String identificacao,String enderecoRede,String broadcast,String ipRange){
		this.identificacao = identificacao;
		this.enderecoRede = enderecoRede;
		this.broadcast = broadcast;
		this.ipRange = ipRange;
	}
	public String getIdentificacao() {
		return identificacao;
	}
	
	public String getEnderecoRede() {
		return enderecoRede;
	}
	
	public String getBroadcast() {
		return broadcast;
	}
	
	public String getIpRange() {
		return ipRange;
	}
	
	
}
