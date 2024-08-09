package eu.joaorodrigo.demos.blockchain.hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;

import eu.joaorodrigo.demos.blockchain.Block;
import eu.joaorodrigo.demos.blockchain.Report;

public class SHA256 {

	private static 		 MessageDigest digest;
	
	static {
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String bytesToHex(byte[] hash) {
	    StringBuilder hexString = new StringBuilder(2 * hash.length);
	    for (int i = 0; i < hash.length; i++) {
	        String hex = Integer.toHexString(0xff & hash[i]);
	        if(hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	public static String encode(Block block) {
		String json = Block.blockAdaptedGson.toJson(block);
//		Report.log(json);
		return bytesToHex(digest.digest(json.getBytes(StandardCharsets.UTF_8)));
	}
}
