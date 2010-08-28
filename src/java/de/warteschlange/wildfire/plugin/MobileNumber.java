package de.warteschlange.wildfire.plugin;


public class MobileNumber {
   	// short message
	// wrong provider
	// wrong country code
	
     //private static int[] prefix = { 160, 170, 171, 175, 1511, 1515, 162, 172, 173, 174, 1520, 163, 177, 178, 157, 179, 176};  
        
        public static String validate( String number ) throws MobileNumberException
        {
          if( number == null || number.length() < 1  ) throw( new NoNumberException() );
          number = number.trim();
          if( number.indexOf( "+" ) == 0 ) number = number.replaceFirst( "\\+", "00" );
          number = number.replace( "(", "" );
          number = number.replace( ")", "" );
          number = number.replace( "/", "" );
          number = number.replace( "-", "" );
          number = number.replace( ".", "" );
          number = number.replace( " ", "" );
          number = number.trim();
          if( !isNumeric( number ) ) throw( new NoNumberException() );
          if( number.indexOf( '0' ) != 0 ) throw( new NoNumberException() );
          if( number.indexOf( "00" ) != 0 ) number = number.replaceFirst( "0", "0049" );
          //test for country code
          //test for mobile code
          if( number.length() < 10 ) throw( new ShortNumberException() );
          return number;
        }
        
        
        private static final boolean isNumeric( final String s) {
        	  final char[] numbers = s.toCharArray();
        	  for (int x = 0; x < numbers.length; x++) {      
        	    final char c = numbers[x];
        	    if ((c >= '0') && (c <= '9')) continue;
        	    return false; // invalid
        	  }
        	  return true; // valid
        	}
        
        /*
        class Country{
         private String name;
        private int code;
        private int length;
        privat int[] prefix;
        }*/
        
}  
      