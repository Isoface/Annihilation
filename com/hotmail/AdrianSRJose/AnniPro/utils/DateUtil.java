package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Saturday 11 July, 2020 / 02:56 PM
 */
public class DateUtil {

	public static String getFormattedCurrentDate ( ) {
		return DateTimeFormatter.ofPattern ( "dd/MM/yyyy" ).format ( LocalDate.now ( ) );
	}
}