package com.shanjin.Jest;


public class ESClient {
	private ESClient() {

	}

	// public synchronized static Client getClient() {
	// Client client = null;
	// try {
	// client = TransportClient.builder().build().addTransportAddress(new
	// InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),
	// 9300)).addTransportAddress(new
	// InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return client;
	// }
	//
	// public static void main(String[] args) {
	// Client client = ESClient.getClient();
	// System.out.println(client);
	// }
}
