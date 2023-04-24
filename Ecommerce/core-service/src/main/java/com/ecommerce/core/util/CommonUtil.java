
package com.ecommerce.core.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.DigestUtils;

import com.ecommerce.core.entities.PasswordPolicies;

/**
 * This utility is using for working with handling Message , ID , List ,
 * Validation ...
 * 
 */
public class CommonUtil {

	public static List<String> rejectFromList(List<String> listOne, List<String> listTwo) {
		for (String str : listTwo) {
			if (listOne.contains(str)) {
				listOne.remove(str);
			}
		}
		return listOne;
	}

	public List<String> getAddedMember(List<String> listOne, List<String> listTwo) {
		List<String> addedMembers = new ArrayList<String>();
		for (String s : listTwo) {
			if (!listOne.contains(s)) {
				addedMembers.add(s);
			}
		}
		return addedMembers;
	}

	public List<String> getDeletedMember(List<String> listOne, List<String> listTwo) {
		List<String> deletedMembers = new ArrayList<String>();
		for (int i = 0; i < listOne.size(); i++) {
			if (!listTwo.contains(listOne.get(i))) {
				deletedMembers.add(listOne.get(i));
			}
		}
		return deletedMembers;
	}

	public static Boolean validateWithValidPattern(String id, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(id);
		boolean b = m.matches();
		return b;
	}

	/**
	 * validation of location data
	 * 
	 * @return true when valid
	 * 
	 */
	public static Boolean validLocation(String location) {
		try {
			String validPattern = "^\\d*[.]?\\d*,\\d*[.]?\\d*[,]?\\d*[.]?\\d*$";

			if (!validateWithValidPattern(location, validPattern)) {
				return false;
			}
			String arr[] = location.split(",");
			Double lat = Double.parseDouble(arr[0]);
			Double lon = Double.parseDouble(arr[1]);
			Boolean isValid = false;
			double latAbs = Math.abs(lat);
			double lonAbs = Math.abs(lon);
			if (latAbs >= 90.0D) {
				isValid = false;
			} else if (lonAbs >= 180.0D) {
				isValid = false;
			} else {
				isValid = latAbs > 1.0E-4D || lonAbs > 1.0E-4D;
			}
			return isValid;
		} catch (NumberFormatException e) {

			return false;
		}
	}

	public static InetAddress getLocalAddress() {
		try {
			Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
			while (b.hasMoreElements()) {
				for (InterfaceAddress f : b.nextElement().getInterfaceAddresses())
					if (f.getAddress().isSiteLocalAddress())
						return f.getAddress();
			}
		} catch (SocketException e) {

		}
		return null;
	}

	public static String getMd5(String input) {
		try {
			byte[] bytesOfMessage = input.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < thedigest.length; i++) {
				sb.append(Integer.toString((thedigest[i] & 0xff) + 0x10, 16).substring(1));
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
		} catch (NoSuchAlgorithmException e) {

		}
		return null;
	}

	/**
	 * Generates an random ID based on SecureRandom & MD5 library
	 * 
	 * @param prefix  - prefix of the resource ID
	 * @param postfix - postfix of the resource ID
	 * @return generated resource ID , CSE ID , AE ID , Node ID ...
	 */
	public static String generateId(String prefix, String postfix) {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		SecureRandom secureRandom = new SecureRandom();
		String input = randomUUIDString + String.valueOf(secureRandom.nextInt(999999999));
		try {
			byte[] bytesOfMessage = input.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < thedigest.length; i++) {
				sb.append(Integer.toString((thedigest[i] & 0xff) + 0x10, 16).substring(1));
			}
			return prefix + sb.toString() + postfix;
		} catch (UnsupportedEncodingException e) {

		} catch (NoSuchAlgorithmException e) {

		}
		return null;
	}

	public static String generateMD5(String input) {
		DigestUtils.md5Digest(input.getBytes());
		byte[] thedigest = DigestUtils.md5Digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < thedigest.length; i++) {
			sb.append(String.format("%02x", thedigest[i]));
		}
		return sb.toString();
	}

	// Check passwordPolicy
	public static Boolean checkPasswordPolicy(PasswordPolicies policy, String password) {
		if (password.length() < policy.getPasswordMinLength()) {
			return false;
		}

		int numberOfLowercase = 0;
		int numberOfUpperCase = 0;
		int numberOfNumber = 0;
		int numberOfSpecial = 0;

		for (int i = 0; i < password.length(); i++) {
			String c = password.charAt(i) + "";
			if (c.matches("[A-Z]")) {
				numberOfUpperCase++;
			} else if (c.matches("[a-z]")) {
				numberOfLowercase++;
			} else if (c.matches("[0-9]")) {
				numberOfNumber++;
			} else {
				numberOfSpecial++;
			}
		}

		if (numberOfLowercase < policy.getMinLowercaseCharacter()) {
			return false;
		}

		if (numberOfUpperCase < policy.getMinUppercaseCharacter()) {
			return false;
		}

		if (numberOfNumber < policy.getMinNumberCharacter()) {
			return false;
		}

		if (numberOfSpecial < policy.getMinSpecialCharacter()) {
			return false;
		}

		return true;
	}
	
	public static String generatePassword(PasswordPolicies policy) {

		String password = "";

		Random random = new Random();
		if(policy.getMinUppercaseCharacter() > 0) {
			password += random.ints(65, 90)
				      .limit(policy.getMinUppercaseCharacter())
				      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				      .toString();
		}
		
		if(policy.getMinLowercaseCharacter() > 0) {
			password += random.ints(97, 122)
				      .limit(policy.getMinLowercaseCharacter())
				      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				      .toString();
		}
		
		if(policy.getMinNumberCharacter() > 0) {
			password += random.ints(48, 57)
				      .limit(policy.getMinNumberCharacter())
				      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				      .toString();
		}
		
		if(policy.getMinSpecialCharacter() > 0) {
			password += random.ints(35, 39)
				      .limit(policy.getMinSpecialCharacter())
				      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				      .toString();
		}
		
		if(policy.getPasswordMinLength() - password.length() > 0) {
			password += random.ints(97, 122)
				      .limit(policy.getPasswordMinLength() - password.length())
				      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				      .toString();
		}
		
		return password;
	}

	public static String trim0(String value){
		if(value.startsWith("0") && value.length() > 2){
			return trim0(value.replaceFirst("0", ""));
		}else { return value; }
	}

}
